package Infra;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationClassFiles {
    List<String> fileNameList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        //final List<String> stringList = new ListOfApplicationClassFiles().walkAllDirectoryFiles("C:\\Users\\aksinsin\\Desktop\\testfolder");
        //System.out.println(stringList);
        //File file = new File("C:\\projects\\ear\\HelloWorldApp.ear");

        //System.out.println(file.listFiles());
        //walk();
        final List<String> classFileList = new ApplicationClassFiles().get("C:\\Users\\aksinsin\\Downloads\\SampleWebApp.war","extract//");
        System.out.println("**************List of .class Files****************"+classFileList.size());
        classFileList.forEach(System.out::println);

    }
    static void walk() throws IOException{
        try (Stream<Path> paths = Files.walk(Paths.get("C:\\Users\\aksinsin\\Desktop\\testfolder"))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(System.out::println);
        }
    }

    private static final String DOT_CLASS_FILE = ".class";
    private static final String DOT_JAR_FILE = ".jar";
    private static final String DOT_WAR_FILE = ".war";
    private static final String DOT_EAR_FILE = ".ear";


    public List<String> find(String path) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {
            return walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(DOT_CLASS_FILE)).collect(Collectors.toList());

        } catch (IOException e) {
            throw e;
        }
    }

    public List<String> get(String filePath, String jarExtractLocation) throws IOException {
        if(filePath == null){
            return fileNameList;
        }
        File destinationFile = new File(filePath);
        if(destinationFile.isDirectory()){
            for(String fileName : walkAllDirectoryFiles(filePath)){
                get(processZipAndClassFiles(fileName, jarExtractLocation), jarExtractLocation);
            }
        }
        else if(destinationFile.isFile() && isSupportedFileType(destinationFile.getName())){
            get(processZipAndClassFiles(filePath, jarExtractLocation), jarExtractLocation);
        }

        return fileNameList;
    }

    private List<String> walkAllDirectoryFiles1(String directoryPath) throws IOException {
        try (Stream<Path> walk = Files.walk(Paths.get(directoryPath))) {
            return walk.map(Path::toString)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw e;
        }
    }
    private List<String> walkAllDirectoryFiles(String directoryPath) throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(directoryPath))) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(file-> isSupportedFileType(file))
                    .collect(Collectors.toList());
        }
    }



    private String processZipAndClassFiles(String jarPath, String jarExtractLocation) throws IOException{

        if (jarPath.toLowerCase().endsWith(DOT_CLASS_FILE)) {
           fileNameList.add(jarPath);
        } else {
            String extractPath = jarExtractLocation + new File(jarPath).getName() +System.currentTimeMillis();
            UnzipJar.unzipJar(extractPath, jarPath);
            return extractPath;
        }
        return null;

//        if (destinationFile.toLowerCase().endsWith(DOT_CLASS_FILE)) {
//            fileNameList.add(destinationFile);
//        } else {
//            ZipInputStream zipIn = new ZipInputStream(new FileInputStream(destinationFile));
//            ZipEntry entry = zipIn.getNextEntry();
//            // iterates over entries in the zip file
//            while (entry != null) {
//                System.out.println(entry.getName());
//                /*if (!entry.isDirectory()) {
//                    // if the entry is a file, extracts it
//                    extractFile(zipIn, filePath);
//                } else {
//                    // if the entry is a directory, make the directory
//                    File dir = new File(filePath);
//                    dir.mkdir();
//                }*/
//                zipIn.closeEntry();
//                entry = zipIn.getNextEntry();
//            }
//            zipIn.close();
//        }
    }

    private boolean isSupportedFileType(String fileName){
        return fileName.endsWith(DOT_CLASS_FILE)
               || fileName.endsWith(DOT_JAR_FILE)
               || fileName.endsWith(DOT_WAR_FILE)
               || fileName.endsWith(DOT_EAR_FILE);
    }

}
