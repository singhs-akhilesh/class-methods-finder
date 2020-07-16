package domain;

import Infra.*;
import com.sun.tools.classfile.ClassFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeprecatedMethodFinder {

    private static final String PACKAGE_JAVA = "java/";
    private static final String PACKAGE_JAVAX = "javax/";
    private static final String PACKAGE_JAVAFX = "javafx/";
    private static final String PACKAGE_COM_SUN = "com/sun/";
    private static final String PACKAGE_SUN_MISC = "com/misc";
    private static final String PACKAGE_JDK = "JDK/";

    private static final String DOT_JAR_FILE = ".jar";
    private static final String DOT_WAR_FILE = ".war";
    private static final String DOT_EAR_FILE = ".ear";

    private static final String JAR_EXTRACT_LOCATION = "extract" + System.currentTimeMillis() + File.separator;
    private List<String> supportedPackages = Arrays.asList(PACKAGE_JAVA, PACKAGE_JAVAX, PACKAGE_COM_SUN, PACKAGE_JDK, PACKAGE_SUN_MISC, PACKAGE_JAVAFX);

    Map<String, Set<String>> methodsClassMap = new LinkedHashMap<>();
    private static Properties prop = null;

    static {
        try (InputStream input = new FileInputStream("resources/config.properties")) {
            prop = new Properties();
            prop.load(input);
        } catch (IOException ex) {
            System.err.println("Unable to load the configuration");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) throws Exception {
        DeprecatedMethodFinder deprecatedMethodFinder = new DeprecatedMethodFinder();
        deprecatedMethodFinder.processArguments(args);
        if(!new File(args[0]).exists()){
            System.out.println("Invalid application path");
            return;
        }
        if(!deprecatedMethodFinder.isSupportedVersion(args[1])){
            System.out.println("Invalid jdk migration version, supported versions are:");
            String version = "";
            for(Object configVersion : prop.keySet()){
                version = version + configVersion + " | ";
            }
            System.out.println(version.substring(0, version.length() - 2));
            return;
        }

        long total = System.currentTimeMillis();
        final Map<String, Map<String, String>> deprecatedAPI = new DeprecatedAPIWebCrawler().getPageDeprecatedItemAndRecommendation(prop.getProperty(args[1]));

        Map<String, String> deprecatedMethodsDB = null;
        if(args[1].equals("7") || args[1].equals("8") || args[1].equals("9")){
            deprecatedMethodsDB = deprecatedAPI.get("Deprecated Methods");
        } else{
            deprecatedMethodsDB = deprecatedAPI.get("Methods");
        }

        final Map<String, Set<String>> fromApp = deprecatedMethodFinder.findFromApp(args[0]);

        final Map<String, Set<String>> deprecatedMethodsOfApp = deprecatedMethodFinder.searchAppExtractedMethodInDeprecatedDBList(fromApp, deprecatedMethodsDB.keySet());

        deprecatedMethodFinder.printDeprecatedMethodAndSuggestion(deprecatedMethodsOfApp, deprecatedMethodsDB);

        System.out.println("Total Time taken "+((System.currentTimeMillis()-total))/1000 + " seconds");
    }

    public Map<String, Set<String>> findFromApp(String applicationPath) throws Exception {
        ApplicationClassFiles classFiles = new ApplicationClassFiles();
        long start = System.currentTimeMillis();
        List<String> classFileList = classFiles.get(applicationPath, JAR_EXTRACT_LOCATION);
        System.out.println("Time taken to extract jars & read .class file locations from the specified path: "+((System.currentTimeMillis()-start))/1000+ " seconds");
        System.out.println("No of .class files found: "+classFileList.size());
        start = System.currentTimeMillis();
        MethodReference classMethodReference = new ClassMethodReference();
        MethodReference interfaceMethodReference = new InterfaceMethodReference();
        for (String fileName : classFileList) {
            try {
                ClassFile cf = ClassFile.read(new File(fileName));
                String packageClassName = ClassInfo.getPackageAndClassName(cf);
                List<Method> extractedMethods = classMethodReference.get(cf);
                extractedMethods.addAll(interfaceMethodReference.get(cf));
                for (Method method : extractedMethods) {
                    String methodSignature = filterAndBuildMethodSignature(method);
                    if (methodSignature != null) {
                        addToMap(packageClassName, methodSignature);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error while processing the file :: " + fileName);
                e.printStackTrace();
            }
        }

        System.out.println("Time taken to extract methods from .class file: "+((System.currentTimeMillis()-start))/1000+ " seconds");
        //delete jar extracted folder
        deleteTempFolders();
        //deleteTempFolders(new File(JAR_EXTRACT_LOCATION));
        return methodsClassMap;
    }

    private void addToMap(String packageClassName, String methodSignature) {
        methodsClassMap.computeIfAbsent(methodSignature, value -> new HashSet<String>());
        if (methodsClassMap.containsKey(methodSignature)) {
            methodsClassMap.get(methodSignature).add(packageClassName);
        }
    }

    private String filterAndBuildMethodSignature(Method method) {
        String finalMethod = null;
        if (isPackageSupported(method.getMethodClass())) {
            finalMethod = method.getMethodClass().replace("/", ".") + "." + method.getMethodName() + MethodParamsFormatter.format(method.getMethodParams());
            if (isConstructorMethod(method.getMethodName())) {
                finalMethod = finalMethod.replace(".<init>", "");
            }
        }
        return finalMethod;
    }

    private boolean isPackageSupported(String packageName) {
        return supportedPackages.stream().anyMatch(packageName::startsWith);
    }

    private boolean isConstructorMethod(String methodName) {
        return methodName.contains("<init>");
    }

    /*private void deleteTempFolders(File file){
        if (!file.exists())
            return;

        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                //call recursively
                deleteTempFolders(f);
            }
        }
        String fileName = file.getName();
        boolean isRead = file.canRead();
        boolean isWrite = file.canWrite();
        boolean isExecute = file.canExecute();
        if(!file.delete())
            System.out.println(fileName +" Read :"+isRead+" Write :"+isWrite+" Execute :"+isExecute);
    }*/
    private void deleteTempFolders() throws IOException {
        long start = System.currentTimeMillis();
        Path pathToBeDeleted = Paths.get(JAR_EXTRACT_LOCATION);
        if (pathToBeDeleted.toFile().exists()) {
            Files.walk(pathToBeDeleted)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        System.out.println("Time taken for deleting temp folders :" + ((System.currentTimeMillis() - start)/1000)+ " seconds");
    }

    private Map<String, Set<String>> searchAppExtractedMethodInDeprecatedDBList(final Map<String, Set<String>> extractedMethods, final Set<String> deprecatedMethodList) {
        long start = System.currentTimeMillis();
        final Map<String, Set<String>> deprecatedMethodsInApplication = new HashMap<>();
        for (String extractedMethod : extractedMethods.keySet()) {
            if (deprecatedMethodList.contains(extractedMethod)) {
                deprecatedMethodsInApplication.put(extractedMethod, extractedMethods.get(extractedMethod));
            }
        }
        System.out.println("Time taken to search deprecated method in DB" + ((System.currentTimeMillis() - start)/1000)+ " seconds");
        return deprecatedMethodsInApplication;
    }

    private void printDeprecatedMethodAndSuggestion(final Map<String, Set<String>> deprecatedMethodsOfApp, final Map<String, String> deprecatedMethodsDB){
        System.out.println("Total Deprecated method found: "+deprecatedMethodsOfApp.size());
        for(String deprecatedMethod : deprecatedMethodsOfApp.keySet()){
            System.out.println("Deprecated Method\n-----------------\n"+deprecatedMethod+"\n");
            System.out.println("Suggestion(if any)\n------------------\n"+deprecatedMethodsDB.get(deprecatedMethod)+"\n");
            System.out.println("Occurrence\n----------\n"+deprecatedMethodsOfApp.get(deprecatedMethod).size()+"\n");
            System.out.println("Used Places\n----------- \n"+deprecatedMethodsOfApp.get(deprecatedMethod).toString());
        }
    }

    private boolean isSupportedVersion(String jdkMigrationVersion){
        return prop.keySet().contains(jdkMigrationVersion);
    }

    private void processArguments(String... args){
        if(args.length != 2){
            System.out.println("Invalid arguments provided");
            System.out.println("Please provide <application-path> and <migration version>");
            System.out.println("E.g. migration-util c:/project/xyx 14");
            System.exit(1);
        }
    }

    private List<String> getAllJarTypesFileFromExtractedLocation() throws Exception{
            try (Stream<Path> paths = Files.walk(Paths.get(JAR_EXTRACT_LOCATION))) {
                return paths
                        .filter(Files::isRegularFile)
                        .map(Path::toString)
                        .filter(this::isSupportedFileType)
                        .collect(Collectors.toList());
            }
    }

    private boolean isSupportedFileType(String fileName){
        return fileName.endsWith(DOT_JAR_FILE)
               || fileName.endsWith(DOT_WAR_FILE)
               || fileName.endsWith(DOT_EAR_FILE);
    }

}
