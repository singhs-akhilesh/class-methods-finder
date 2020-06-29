package Infra;

import com.sun.tools.classfile.ClassFile;

import java.util.List;

public interface MethodReference {
    List<Method> get(ClassFile cf);
    List<Method> get(String classFilePath);
}
