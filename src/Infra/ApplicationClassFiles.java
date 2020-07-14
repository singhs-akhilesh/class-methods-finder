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

    private static final String DOT_CLASS_FILE = ".class";
    private static final String DOT_JAR_FILE = ".jar";
    private static final String DOT_WAR_FILE = ".war";
    private static final String DOT_EAR_FILE = ".ear";

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
    }

    private boolean isSupportedFileType(String fileName){
        return fileName.endsWith(DOT_CLASS_FILE)
               || fileName.endsWith(DOT_JAR_FILE)
               || fileName.endsWith(DOT_WAR_FILE)
               || fileName.endsWith(DOT_EAR_FILE);
    }

}
