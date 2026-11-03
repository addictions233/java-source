package com.tuling.classloader;

import java.util.concurrent.locks.ReentrantLock;

import com.sun.nio.zipfs.ZipInfo;

/**
 * @author Fox
 */
public class ClassLoaderDemo {

    public static void main(String[] args) {
        // BootstrapClassLoader   rt.jar
        System.out.println(ReentrantLock.class.getClassLoader());
        // ExtClassLoader
        System.out.println(ZipInfo.class.getClassLoader());
        // AppClassLoader
        System.out.println(ClassLoaderDemo.class.getClassLoader());

        // java.util.concurrent.locks.ReentrantLock   java.lang.String

        // AppClassLoader
        System.out.println(ClassLoader.getSystemClassLoader());
        // ExtClassLoader
        System.out.println(ClassLoader.getSystemClassLoader().getParent());
        // BootstrapClassLoader
        System.out.println(ClassLoader.getSystemClassLoader().getParent().getParent());

    }
}
