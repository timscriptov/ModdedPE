package com.mcal.pesdk3.dex;

import android.content.res.AssetManager;
import android.os.Build;
import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;
import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://github.com/listerily/ModdedBE/blob/main/ModdedBE-Android/endercore/src/main/java/org/endercore/android/utils/Patcher.java">https://github.com/listerily/ModdedBE</a>
 */
public class Patcher {
    public static void patchDexFile(ClassLoader classLoader, String dexFilePath, String dexOptFilePath) throws NoSuchFieldException, IllegalAccessException {
        new File(dexOptFilePath).mkdirs();
        Field field1 = BaseDexClassLoader.class.getDeclaredField("pathList");
        field1.setAccessible(true);
        Object dexPathList = field1.get(classLoader);
        Field field2 = dexPathList.getClass().getDeclaredField("dexElements");
        field2.setAccessible(true);
        Object dexElements = field2.get(dexPathList);

        DexClassLoader dcl = new DexClassLoader(dexFilePath, dexOptFilePath, null, classLoader);
        Object patchDexPathList = field1.get(dcl);
        Object patchDexElements = field2.get(patchDexPathList);
        Object concatArray;

        int len1 = Array.getLength(patchDexElements);
        int len2 = Array.getLength(dexElements);
        int totalLen = len1 + len2;
        concatArray = Array.newInstance(patchDexElements.getClass().getComponentType(), totalLen);
        for (int i = 0; i < len1; i++) {
            Array.set(concatArray, i, Array.get(patchDexElements, i));
        }
        for (int j = 0; j < len2; j++) {
            Array.set(concatArray, len1 + j, Array.get(dexElements, j));
        }
        field2.set(dexPathList, concatArray);
    }

    public static void patchMultipleDexFiles(ClassLoader classLoader, String @NotNull [] dexFilePaths, String dexOptFilePath) throws NoSuchFieldException, IllegalAccessException {
        new File(dexOptFilePath).mkdirs();
        Field field1 = BaseDexClassLoader.class.getDeclaredField("pathList");
        field1.setAccessible(true);
        Object dexPathList = field1.get(classLoader);
        Field field2 = dexPathList.getClass().getDeclaredField("dexElements");
        field2.setAccessible(true);
        Object dexElements = field2.get(dexPathList);

        List<Object> allDexElements = new ArrayList<>();

        for (String dexFilePath : dexFilePaths) {
            DexClassLoader dcl = new DexClassLoader(dexFilePath, dexOptFilePath, null, classLoader);
            Object patchDexPathList = field1.get(dcl);
            Object patchDexElements = field2.get(patchDexPathList);

            int len = Array.getLength(patchDexElements);
            for (int i = 0; i < len; i++) {
                allDexElements.add(Array.get(patchDexElements, i));
            }
        }

        int originalLen = Array.getLength(dexElements);
        for (int j = 0; j < originalLen; j++) {
            allDexElements.add(Array.get(dexElements, j));
        }

        Object concatArray = Array.newInstance(dexElements.getClass().getComponentType(), allDexElements.size());
        for (int i = 0; i < allDexElements.size(); i++) {
            Array.set(concatArray, i, allDexElements.get(i));
        }

        field2.set(dexPathList, concatArray);
    }

    public static void patchNativeLibraryDir(ClassLoader classLoader, String nativeLibraryPath) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        PathClassLoader pathClassLoader = (PathClassLoader) classLoader;
        if (Build.VERSION.SDK_INT <= 22) {
            Field fieldPathList = Class.forName("dalvik.system.BaseDexClassLoader").getDeclaredField("pathList");
            fieldPathList.setAccessible(true);
            Object pathList = fieldPathList.get(pathClassLoader);
            Field nativeLibraryDirectories = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
            nativeLibraryDirectories.setAccessible(true);
            File[] files = (File[]) nativeLibraryDirectories.get(pathList);
            Object newFiles = Array.newInstance(File.class, files.length + 1);
            Array.set(newFiles, 0, new File(nativeLibraryPath));
            for (int i = 1; i < files.length + 1; i++) {
                Array.set(newFiles, i, files[i - 1]);
            }
            nativeLibraryDirectories.set(pathList, newFiles);
        } else if (Build.VERSION.SDK_INT <= 25) {
            Class<?> classBaseDexClassLoader = Class.forName("dalvik.system.BaseDexClassLoader");
            Field fieldPathList = classBaseDexClassLoader.getDeclaredField("pathList");
            fieldPathList.setAccessible(true);
            Object pathList = fieldPathList.get(pathClassLoader);

            Class<?> nativeLibraryElementClass = Class.forName("dalvik.system.DexPathList$Element");
            Constructor<?> element = null;
            element = nativeLibraryElementClass.getConstructor(File.class, boolean.class, File.class, DexFile.class);
            Field systemNativeLibraryDirectories = pathList.getClass()
                    .getDeclaredField("systemNativeLibraryDirectories");
            Field nativeLibraryDirectories = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
            Field nativeLibraryPathElements = pathList.getClass().getDeclaredField("nativeLibraryPathElements");
            systemNativeLibraryDirectories.setAccessible(true);
            nativeLibraryDirectories.setAccessible(true);
            nativeLibraryPathElements.setAccessible(true);
            List<File> systemFiles = (List<File>) systemNativeLibraryDirectories.get(pathList);
            List<File> nativeFiles = (List<File>) nativeLibraryDirectories.get(pathList);
            Object[] elementFiles = (Object[]) nativeLibraryPathElements.get(pathList);
            Object newElementFiles = Array.newInstance(nativeLibraryElementClass, elementFiles.length + 1);

            systemFiles.add(new File(nativeLibraryPath));
            nativeFiles.add(new File(nativeLibraryPath));

            systemNativeLibraryDirectories.set(pathList, systemFiles);
            nativeLibraryDirectories.set(pathList, nativeFiles);
            if (element != null) {
                element.setAccessible(true);
                Object newInstance = element.newInstance(new File(nativeLibraryPath), true, null, null);
                Array.set(newElementFiles, 0, newInstance);
                for (int i = 1; i < elementFiles.length + 1; i++) {
                    Array.set(newElementFiles, i, elementFiles[i - 1]);
                }
                nativeLibraryPathElements.set(pathList, newElementFiles);
            }
        } else {
            Class<?> classBaseDexClassLoader = Class.forName("dalvik.system.BaseDexClassLoader");
            Field fieldPathList = classBaseDexClassLoader.getDeclaredField("pathList");
            fieldPathList.setAccessible(true);
            Object pathList = fieldPathList.get(pathClassLoader);

            Class<?> nativeLibraryElementClass = Class.forName("dalvik.system.DexPathList$NativeLibraryElement");
            Constructor<?> element = null;
            element = nativeLibraryElementClass.getConstructor(File.class);
            Field systemNativeLibraryDirectories = pathList.getClass()
                    .getDeclaredField("systemNativeLibraryDirectories");
            Field nativeLibraryDirectories = pathList.getClass().getDeclaredField("nativeLibraryDirectories");
            Field nativeLibraryPathElements = pathList.getClass().getDeclaredField("nativeLibraryPathElements");
            systemNativeLibraryDirectories.setAccessible(true);
            nativeLibraryDirectories.setAccessible(true);
            nativeLibraryPathElements.setAccessible(true);
            List<File> systemFiles = (List<File>) systemNativeLibraryDirectories.get(pathList);
            List<File> nativeFiles = (List<File>) nativeLibraryDirectories.get(pathList);
            Object[] elementFiles = (Object[]) nativeLibraryPathElements.get(pathList);
            Object newElementFiles = Array.newInstance(nativeLibraryElementClass, elementFiles.length + 1);

            systemFiles.add(new File(nativeLibraryPath));
            nativeFiles.add(new File(nativeLibraryPath));

            systemNativeLibraryDirectories.set(pathList, systemFiles);
            nativeLibraryDirectories.set(pathList, nativeFiles);
            if (element != null) {
                element.setAccessible(true);
                Object newInstance = element.newInstance(new File(nativeLibraryPath));
                Array.set(newElementFiles, 0, newInstance);
                for (int i = 1; i < elementFiles.length + 1; i++) {
                    Array.set(newElementFiles, i, elementFiles[i - 1]);
                }
                nativeLibraryPathElements.set(pathList, newElementFiles);
            }
        }
    }

    public static void patchAssetsFile(AssetManager assetManager, String filePath) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method addAssetPath = AssetManager.class.getMethod("addAssetPath", String.class);
        addAssetPath.invoke(assetManager, filePath);
    }

    public static @NotNull AssetManager createNewAssetManager() throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return AssetManager.class.getDeclaredConstructor().newInstance();
    }

    public static void patchAssetsFile(AssetManager assetManager, @NotNull ArrayList<String> filePaths) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        int size = filePaths.size();
        for (int i = 0; i < size; ++i) {
            patchAssetsFile(assetManager, filePaths.get(i));
        }
    }
}
