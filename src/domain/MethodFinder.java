package domain;

import Infra.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.tools.classfile.ClassFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MethodFinder {

    private static final String PACKAGE_JAVA = "java/";
    private static final String PACKAGE_JAVAX = "javax/";
    private static final String PACKAGE_COM_SUN = "com/sun/";
    private static final String PACKAGE_JDK = "JDK/";
    private static final String JAR_EXTRACT_LOCATION= "extract"+System.currentTimeMillis()+File.separator;
    private List<String> supportedPackages = Arrays.asList(PACKAGE_JAVA, PACKAGE_JAVAX, PACKAGE_COM_SUN, PACKAGE_JDK);

    Map<String, Set<String>> methodsClassMap = new LinkedHashMap<>();

    public static void main(String[] args) throws Exception {
        MethodFinder methodFinder = new MethodFinder();
        //final Map<String, Set<String>> fromApp = methodFinder.findFromApp("C:\\projects\\netbeans\\main\\netbeans\\java\\debugger.jpda");
        //final Map<String, Set<String>> fromApp = methodFinder.findFromApp("C:\\Users\\aksinsin\\Downloads\\SampleWebApp.war");
        final Map<String, Set<String>> fromApp = methodFinder.findFromApp("C:\\Users\\aksinsin\\Downloads\\SampleWebApp");
        //final Map<String, Set<String>> fromApp = methodFinder.findFromApp("C:\\Users\\aksinsin\\Downloads\\gson-2.2.2.jar\\gson-2.2.2.jar");
        methodFinder.printInJson(fromApp);

    }

    private void printInJson(Map<String, Set<String>> fromApp){
        GsonBuilder gsonMapBuilder = new GsonBuilder();

        Gson gsonObject = gsonMapBuilder.create();

        String JSONObject = gsonObject.toJson(fromApp);

        Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
        String prettyJson = prettyGson.toJson(fromApp);
        System.out.println(prettyJson);
    }
    public Map<String, Set<String>> findFromApp(String applicationPath) throws Exception {
        ApplicationClassFiles classFiles = new ApplicationClassFiles();
        //List<String> classFileList = classFiles.find(applicationPath);
        List<String> classFileList = classFiles.get(applicationPath,JAR_EXTRACT_LOCATION);
        MethodReference classMethodReference = new ClassMethodReference();
        MethodReference interfaceMethodReference = new InterfaceMethodReference();
        for (String fileName : classFileList) {
            ClassFile cf = ClassFile.read(new File(fileName));
            String packageClassName = ClassInfo.getPackageAndClassName(cf);
            List<Method> classMethods = classMethodReference.get(cf);
            List<Method> interfaceMethods = interfaceMethodReference.get(cf);
            for (Method method : classMethods) {
                String methodSignature = filterAndBuildMethodSignature(method);
                if(methodSignature != null){
                    addToMap(packageClassName, methodSignature);
                }
            }

            for (Method method : interfaceMethods) {
                String methodSignature = filterAndBuildMethodSignature(method);
                if(methodSignature != null){
                    addToMap(packageClassName, methodSignature);
                }
            }

        }
        //delete jar extracted folder
        Path pathToBeDeleted = Paths.get(JAR_EXTRACT_LOCATION);

        Files.walk(pathToBeDeleted)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);

        return methodsClassMap;
    }

    private void addToMap(String packageClassName, String methodSignature){
        methodsClassMap.computeIfAbsent(methodSignature, value -> new HashSet<String>());
        if (methodsClassMap.containsKey(methodSignature)) {
            methodsClassMap.get(methodSignature).add(packageClassName);
        }
    }

    private String filterAndBuildMethodSignature(Method method){
        String finalMethod = null;
        if(isPackageSupported(method.getMethodClass())){
            finalMethod = method.getMethodClass()+"."+method.getMethodName()+method.getMethodParams();
            if(isConstructorMethod(method.getMethodName())){
                finalMethod = finalMethod.replace(".<init>","");
            }
        }
        return finalMethod;
    }

    private boolean isPackageSupported(String packageName){
        return supportedPackages.stream().anyMatch(packageName::startsWith);
    }

    private boolean isConstructorMethod(String methodName){
        return methodName.contains("<init>");
    }
}
