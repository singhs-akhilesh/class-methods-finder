package Infra;

import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.ConstantPool;

import java.io.File;

public class ClassInfo {
    public static String getPackageAndClassName(ClassFile cf) {
        ConstantPoolEntries constantPoolEntries = ConstantPoolEntries.loadFrom(cf);
        ConstantPool constant_pool = cf.constant_pool;
        int thisClassIndex = cf.this_class;
        try {
            final ConstantPool.CONSTANT_Class_info classInfo = constant_pool.getClassInfo(thisClassIndex);
            return constant_pool.getUTF8Value(classInfo.name_index);
        } catch (Exception invalidIndex) {
            invalidIndex.printStackTrace();
        }
        return null;
    }

    public static String getPackageAndClassName(String classFilePath) {
        File cfPath = new File(classFilePath);
        try {
            ClassFile cf = ClassFile.read(cfPath);
            return getPackageAndClassName(cf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
