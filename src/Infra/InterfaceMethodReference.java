package Infra;

import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.ConstantPool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InterfaceMethodReference implements MethodReference {
    @Override
    public List<Method> get(ClassFile cf) {
        List<Method> classMethodRefList = new ArrayList<>();
        try {
            ConstantPoolEntries constantPoolEntries = ConstantPoolEntries.loadFrom(cf);
            ConstantPool constant_pool = cf.constant_pool;
            List<ConstantPool.CONSTANT_InterfaceMethodref_info> interfaceMethodRefs = constantPoolEntries.intfMethodRefs;
            for(ConstantPool.CONSTANT_InterfaceMethodref_info cimi : interfaceMethodRefs){
                int classIndex = cimi.class_index;
                int typeIndex = cimi.name_and_type_index;
                ConstantPool.CONSTANT_Class_info classInfo = constant_pool.getClassInfo(classIndex);
                ConstantPool.CONSTANT_NameAndType_info nameAndTypeInfo = constant_pool.getNameAndTypeInfo(typeIndex);

                String className = constant_pool.getUTF8Value(classInfo.name_index);
                String methodName = constant_pool.getUTF8Value(nameAndTypeInfo.name_index);
                String paramAndReturn = constant_pool.getUTF8Value(nameAndTypeInfo.type_index);

                Method method = new Method(className,methodName,paramAndReturn);
                classMethodRefList.add(method);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classMethodRefList;
    }

    @Override
    public List<Method> get(String classFilePath) {
        List<Method> classMethodRefList = new ArrayList<>();
        File cfPath = new File(classFilePath);
        try {
            ClassFile cf = ClassFile.read(cfPath);
            classMethodRefList.addAll(get(cf));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classMethodRefList;
    }
}
