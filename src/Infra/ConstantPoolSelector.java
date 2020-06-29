package Infra;

import com.sun.tools.classfile.ConstantPool;

class ConstantPoolSelector implements ConstantPool.Visitor<Void, ConstantPoolEntries> {
    @Override
    public Void visitClass(ConstantPool.CONSTANT_Class_info info, ConstantPoolEntries p) {
        p.classes.add(info);
        return null;
    }

    @Override
    public Void visitDouble(ConstantPool.CONSTANT_Double_info info, ConstantPoolEntries p) {
        return null;
    }

    @Override
    public Void visitFieldref(ConstantPool.CONSTANT_Fieldref_info info, ConstantPoolEntries p) {
        p.fieldRefs.add(info);
        return null;
    }

    @Override
    public Void visitFloat(ConstantPool.CONSTANT_Float_info info, ConstantPoolEntries p) {
        return null;
    }

    @Override
    public Void visitInteger(ConstantPool.CONSTANT_Integer_info info, ConstantPoolEntries p) {
        return null;
    }

    @Override
    public Void visitInterfaceMethodref(ConstantPool.CONSTANT_InterfaceMethodref_info info, ConstantPoolEntries p) {
        p.intfMethodRefs.add(info);
        return null;
    }

    @Override
    public Void visitInvokeDynamic(ConstantPool.CONSTANT_InvokeDynamic_info info, ConstantPoolEntries p) {
        return null;
    }

    @Override
    public Void visitLong(ConstantPool.CONSTANT_Long_info info, ConstantPoolEntries p) {
        return null;
    }

    @Override
    public Void visitMethodref(ConstantPool.CONSTANT_Methodref_info info, ConstantPoolEntries p) {
        p.methodRefs.add(info);
        return null;
    }

    @Override
    public Void visitMethodHandle(ConstantPool.CONSTANT_MethodHandle_info info, ConstantPoolEntries p) {
        return null;
    }

    @Override
    public Void visitMethodType(ConstantPool.CONSTANT_MethodType_info info, ConstantPoolEntries p) {
        return null;
    }

    @Override
    public Void visitNameAndType(ConstantPool.CONSTANT_NameAndType_info info, ConstantPoolEntries p) {
        return null;
    }


    @Override
    public Void visitString(ConstantPool.CONSTANT_String_info info, ConstantPoolEntries p) {
        return null;
    }

    @Override
    public Void visitUtf8(ConstantPool.CONSTANT_Utf8_info info, ConstantPoolEntries p) {
        return null;
    }
}