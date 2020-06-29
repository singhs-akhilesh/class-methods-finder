package Infra;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.Enumeration;
 
public class UnzipJar {

	private static final String DOT_CLASS_FILE = ".class";
	private static final String DOT_JAR_FILE = ".jar";
	private static final String DOT_WAR_FILE = ".war";
	private static final String DOT_EAR_FILE = ".ear";

//	public static void main(String[] args) throws IOException {
//		unzipJar("C:\\projects\\ear\\extract\\", "C:\\projects\\ear\\HelloWorldApp.ear");
//	}
//
	public static void unzipJar(String destinationDir, String jarPath) throws IOException {
		File file = new File(jarPath);
		System.out.println(jarPath);
		JarFile jar = new JarFile(file);

		for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();) {
			JarEntry entry = (JarEntry) enums.nextElement();
 
			String fileName = destinationDir + File.separator + entry.getName();
			File f = new File(fileName);
 
			if (fileName.endsWith("/")) {
				f.mkdirs();
			}
 
		}
 
		for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();) {
			JarEntry entry = (JarEntry) enums.nextElement();
 
			String fileName = destinationDir+ File.separator + entry.getName();
			File f = new File(fileName);

			if (!fileName.endsWith("/") && isSupportedFileType(fileName)) {
				InputStream is = jar.getInputStream(entry);
				FileOutputStream fos = new FileOutputStream(f);
				while (is.available() > 0) {
					fos.write(is.read());
				}
 
				fos.close();
				is.close();
			}
		}
	}

	private static boolean isSupportedFileType(String fileName){
		return fileName.endsWith(DOT_CLASS_FILE)
			   || fileName.endsWith(DOT_JAR_FILE)
			   || fileName.endsWith(DOT_WAR_FILE)
			   || fileName.endsWith(DOT_EAR_FILE);
	}
}