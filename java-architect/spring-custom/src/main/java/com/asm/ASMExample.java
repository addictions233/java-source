package com.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;

public class ASMExample {
    public static void main(String[] args) throws IOException {
        // 假设我们有一个类文件SampleClass.class
        // 使用ClassReader读取这个类文件
        ClassReader classReader = new ClassReader("com.asm.Product");
        // 使用ClassVisitor来访问类信息
        classReader.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                // 这里可以访问到类的信息，例如类名、版本、访问修饰符等
                System.out.println("Class Name: " + name);
                System.out.println("Version: " + version);
                System.out.println("Access: " + access);
                // 输出其他信息...
            }
 
            // 重写其他需要访问的方法...
        }, 0);
    }
}