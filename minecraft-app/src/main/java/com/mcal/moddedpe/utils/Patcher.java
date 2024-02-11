package com.mcal.moddedpe.utils;

import android.annotation.SuppressLint;
import android.os.Build;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class Patcher {
    public static void patchNativeLibraryDir(ClassLoader classLoader, File nativeLibraryPath) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        final PathClassLoader pathClassLoader = (PathClassLoader) classLoader;
        if (Build.VERSION.SDK_INT <= 22) {
            @SuppressLint("DiscouragedPrivateApi") Field fieldPathList = Class.forName("dalvik.system.BaseDexClassLoader").getDeclaredField("pathList");
            fieldPathList.setAccessible(true);
            final Object pathList = fieldPathList.get(pathClassLoader);
            if (pathList != null) {
                final Field nativeLibraryDirectories = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
                nativeLibraryDirectories.setAccessible(true);
                final File[] files = (File[]) nativeLibraryDirectories.get(pathList);
                if (files != null) {
                    final Object newFiles = Array.newInstance(File.class, files.length + 1);
                    Array.set(newFiles, 0, nativeLibraryPath);
                    for (int i = 1; i < files.length + 1; i++) {
                        Array.set(newFiles, i, files[i - 1]);
                    }
                    nativeLibraryDirectories.set(pathList, newFiles);
                }
            }
        } else if (Build.VERSION.SDK_INT <= 25) {
            final Class<?> classBaseDexClassLoader = Class.forName("dalvik.system.BaseDexClassLoader");
            @SuppressLint("DiscouragedPrivateApi") Field fieldPathList = classBaseDexClassLoader.getDeclaredField("pathList");
            fieldPathList.setAccessible(true);
            final Object pathList = fieldPathList.get(pathClassLoader);
            if (pathList != null) {
                final Class<?> nativeLibraryElementClass = Class.forName("dalvik.system.DexPathList$Element");
                final Constructor<?> element = nativeLibraryElementClass.getConstructor(File.class, boolean.class, File.class, DexFile.class);
                final Field systemNativeLibraryDirectories = pathList.getClass().getDeclaredField("systemNativeLibraryDirectories");
                final Field nativeLibraryDirectories = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
                final Field nativeLibraryPathElements = pathList.getClass().getDeclaredField("nativeLibraryPathElements");
                systemNativeLibraryDirectories.setAccessible(true);
                nativeLibraryDirectories.setAccessible(true);
                nativeLibraryPathElements.setAccessible(true);
                final List<File> systemFiles = (List<File>) systemNativeLibraryDirectories.get(pathList);
                if (systemFiles != null) {
                    final List<File> nativeFiles = (List<File>) nativeLibraryDirectories.get(pathList);
                    if (nativeFiles != null) {
                        final Object[] elementFiles = (Object[]) nativeLibraryPathElements.get(pathList);
                        if (elementFiles != null) {
                            final Object newElementFiles = Array.newInstance(nativeLibraryElementClass, elementFiles.length + 1);

                            systemFiles.add(nativeLibraryPath);
                            nativeFiles.add(nativeLibraryPath);

                            systemNativeLibraryDirectories.set(pathList, systemFiles);
                            nativeLibraryDirectories.set(pathList, nativeFiles);
                            if (element != null) {
                                element.setAccessible(true);
                                final Object newInstance = element.newInstance(nativeLibraryPath, true, null, null);
                                Array.set(newElementFiles, 0, newInstance);
                                for (int i = 1; i < elementFiles.length + 1; i++) {
                                    Array.set(newElementFiles, i, elementFiles[i - 1]);
                                }
                                nativeLibraryPathElements.set(pathList, newElementFiles);
                            }
                        }
                    }
                }
            }
        } else {
            final Class<?> classBaseDexClassLoader = Class.forName("dalvik.system.BaseDexClassLoader");
            @SuppressLint("DiscouragedPrivateApi") final Field fieldPathList = classBaseDexClassLoader.getDeclaredField("pathList");
            fieldPathList.setAccessible(true);
            final Object pathList = fieldPathList.get(pathClassLoader);
            if (pathList != null) {
                final Class<?> nativeLibraryElementClass = Class.forName("dalvik.system.DexPathList$NativeLibraryElement");
                final Constructor<?> element = nativeLibraryElementClass.getConstructor(File.class);
                final Field systemNativeLibraryDirectories = pathList.getClass().getDeclaredField("systemNativeLibraryDirectories");
                final Field nativeLibraryDirectories = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
                final Field nativeLibraryPathElements = pathList.getClass().getDeclaredField("nativeLibraryPathElements");
                systemNativeLibraryDirectories.setAccessible(true);
                nativeLibraryDirectories.setAccessible(true);
                nativeLibraryPathElements.setAccessible(true);
                final List<File> systemFiles = (List<File>) systemNativeLibraryDirectories.get(pathList);
                if (systemFiles != null) {
                    final List<File> nativeFiles = (List<File>) nativeLibraryDirectories.get(pathList);
                    if (nativeFiles != null) {
                        final Object[] elementFiles = (Object[]) nativeLibraryPathElements.get(pathList);
                        if (elementFiles != null) {
                            final Object newElementFiles = Array.newInstance(nativeLibraryElementClass, elementFiles.length + 1);

                            systemFiles.add(nativeLibraryPath);
                            nativeFiles.add(nativeLibraryPath);

                            systemNativeLibraryDirectories.set(pathList, systemFiles);
                            nativeLibraryDirectories.set(pathList, nativeFiles);
                            if (element != null) {
                                element.setAccessible(true);
                                final Object newInstance = element.newInstance(nativeLibraryPath);
                                Array.set(newElementFiles, 0, newInstance);
                                for (int i = 1; i < elementFiles.length + 1; i++) {
                                    Array.set(newElementFiles, i, elementFiles[i - 1]);
                                }
                                nativeLibraryPathElements.set(pathList, newElementFiles);
                            }
                        }
                    }
                }
            }
        }
    }
}
