package Infra;

import com.sun.tools.classfile.ClassFile;
import com.sun.tools.classfile.ConstantPool;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

import static com.sun.tools.classfile.ConstantPool.CPInfo;

class ConstantPoolEntries {
    final List<ConstantPool.CONSTANT_Class_info> classes = new ArrayList<>();
    final List<ConstantPool.CONSTANT_Fieldref_info> fieldRefs = new ArrayList<>();
    final List<ConstantPool.CONSTANT_Methodref_info> methodRefs = new ArrayList<>();
    final List<ConstantPool.CONSTANT_InterfaceMethodref_info> intfMethodRefs = new ArrayList<>();

    public static ConstantPoolEntries loadFrom(ClassFile cf) {
        ConstantPoolEntries entries = new ConstantPoolEntries();
        for (CPInfo cpi : cf.constant_pool.entries()) {
            cpi.accept(new ConstantPoolSelector(), entries);
        }
        return entries;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb, Locale.getDefault());
        f.format("Classes:%n");
        f.format("%s%n", classes);
        f.format("FieldRefs:%n");
        f.format("%s%n", fieldRefs);
        f.format("MethodRefs:%n");
        f.format("%s%n", methodRefs);
        f.format("InterfaceMethodRefs:%n");
        f.format("%s%n", intfMethodRefs);
        f.flush();
        return sb.toString();
    }
}