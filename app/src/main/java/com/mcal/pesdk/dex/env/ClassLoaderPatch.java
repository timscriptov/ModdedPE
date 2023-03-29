package com.mcal.pesdk.dex.env;

import android.content.Context;
import android.util.Log;

import com.mcal.pesdk.dex.utils.ReflectionHelper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexFile;

public class ClassLoaderPatch {
    private static final List<Object> patchedObjects = new ArrayList();

    private List<File> dexFiles;

    public static @NotNull Object newGenericArrayOfType(Class cls, int i) {
        return Array.newInstance(cls, i);
    }

    public static synchronized void addNativeLibraryPath(ClassLoader classLoader, File file) {
        Object obj;
        synchronized (ClassLoaderPatch.class) {
            try {
                Field declaredField = BaseDexClassLoader.class.getDeclaredField("pathList");
                declaredField.setAccessible(true);
                Object obj2 = declaredField.get(classLoader);
                Field declaredField2 = obj2.getClass().getDeclaredField("nativeLibraryDirectories");
                declaredField2.setAccessible(true);
                try {
                    ((List) declaredField2.get(obj2)).add(0, file);
                } catch (ClassCastException unused) {
                    File[] fileArr = (File[]) declaredField2.get(obj2);
                    File[] fileArr2 = new File[(fileArr.length + 1)];
                    fileArr2[0] = file;
                    System.arraycopy(fileArr, 0, fileArr2, 1, fileArr.length);
                    declaredField2.set(obj2, fileArr2);
                }
                patchedObjects.add(file);
                try {
                    Field declaredField3 = obj2.getClass().getDeclaredField("nativeLibraryPathElements");
                    declaredField3.setAccessible(true);
                    Object[] objArr = (Object[]) declaredField3.get(obj2);
                    try {
                        Constructor<?> declaredConstructor = objArr[0].getClass().getDeclaredConstructor(File.class);
                        declaredConstructor.setAccessible(true);
                        obj = declaredConstructor.newInstance(file);
                    } catch (NoSuchMethodException e) {
                        Constructor<?> declaredConstructor2 = objArr[0].getClass().getDeclaredConstructor(File.class, Boolean.TYPE, File.class, DexFile.class);
                        declaredConstructor2.setAccessible(true);
                        obj = declaredConstructor2.newInstance(file, true, null, null);
                        try {
                            obj.getClass().getDeclaredMethod("maybeInit", new Class[0]).invoke(obj);
                        } catch (NoSuchMethodException e1) {
                            e.printStackTrace();
                        }
                    }
                    Object newGenericArrayOfType = newGenericArrayOfType(objArr[0].getClass(), objArr.length + 1);
                    ((Object[]) newGenericArrayOfType)[0] = obj;
                    System.arraycopy(objArr, 0, newGenericArrayOfType, 1, objArr.length);
                    declaredField3.set(obj2, newGenericArrayOfType);
                    patchedObjects.add(obj);
                } catch (NoSuchFieldException e) {
                    Log.e("ClassLoaderPatch", "Failed to modify nativeLibraryPathElements, no field found, it may be old version or something else");
                    Log.i("ClassLoaderPatch", "Showing class loader inner structure:");
                    ReflectionHelper.printClassStructure(classLoader.getClass(), classLoader, "ClassLoaderPatch", "    ", true);
                    Log.i("ClassLoaderPatch", "Showing path list inner structure:");
                    ReflectionHelper.printClassStructure(obj2.getClass(), obj2, "ClassLoaderPatch", "    ", true);
                }
                Log.d("ClassLoaderPatch", "modified class loader (native path, hash: " + classLoader.hashCode() + "): " + classLoader);
            } catch (Exception e) {
                PrintStream printStream = System.out;
                printStream.println("ClassLoaderPatch: Showing class loader inner structure: \n" + ReflectionHelper.getClassStructureString(classLoader.getClass(), classLoader, "    ", true));
                throw new RuntimeException("failed to patch classloader with following error", e);
            }
        }
    }

    public static synchronized void addDexPath(ClassLoader classLoader, File file) {
        Object obj;
        synchronized (ClassLoaderPatch.class) {
            try {
                Field declaredField = BaseDexClassLoader.class.getDeclaredField("pathList");
                declaredField.setAccessible(true);
                Object obj2 = declaredField.get(classLoader);
                Field declaredField2 = obj2.getClass().getDeclaredField("dexElements");
                declaredField2.setAccessible(true);
                Object[] objArr = (Object[]) declaredField2.get(obj2);
                try {
                    Constructor<?> declaredConstructor = objArr[0].getClass().getDeclaredConstructor(DexFile.class, File.class);
                    declaredConstructor.setAccessible(true);
                    obj = declaredConstructor.newInstance(new DexFile(file), null);
                } catch (NoSuchMethodException e) {
                    try {
                        Constructor<?> declaredConstructor2 = objArr[0].getClass().getDeclaredConstructor(File.class, ZipFile.class, DexFile.class);
                        declaredConstructor2.setAccessible(true);
                        obj = declaredConstructor2.newInstance(null, null, new DexFile(file));
                    } catch (NoSuchMethodException e1) {
                        Constructor<?> declaredConstructor3 = objArr[0].getClass().getDeclaredConstructor(File.class, Boolean.TYPE, File.class, DexFile.class);
                        declaredConstructor3.setAccessible(true);
                        obj = declaredConstructor3.newInstance(null, false, null, new DexFile(file));
                    }
                }
                Object newGenericArrayOfType = newGenericArrayOfType(objArr[0].getClass(), objArr.length + 1);
                ((Object[]) newGenericArrayOfType)[objArr.length] = obj;
                System.arraycopy(objArr, 0, newGenericArrayOfType, 0, objArr.length);
                declaredField2.set(obj2, newGenericArrayOfType);
                patchedObjects.add(obj);
                Log.d("ClassLoaderPatch", "modified class loader (dex path, hash: " + classLoader.hashCode() + "): " + classLoader);
            } catch (Exception e) {
                PrintStream printStream = System.out;
                printStream.println("ClassLoaderPatch: Showing class loader inner structure: \n" + ReflectionHelper.getClassStructureString(classLoader.getClass(), classLoader, "    ", true));
                throw new RuntimeException("failed to patch classloader with following error", e);
            }
        }
    }

    private static @NotNull Object removePatchesFromGenericArray(Object @NotNull [] objArr, Class cls) {
        ArrayList<Object> arrayList = new ArrayList();
        int i = 0;
        for (Object obj : objArr) {
            if (!patchedObjects.contains(obj)) {
                arrayList.add(obj);
            }
        }
        Object newGenericArrayOfType = newGenericArrayOfType(cls, arrayList.size());
        for (Object obj2 : arrayList) {
            ((Object[]) newGenericArrayOfType)[i] = obj2;
            i++;
        }
        return newGenericArrayOfType;
    }

    public static synchronized void revertClassLoaderPatches(ClassLoader classLoader) {
        synchronized (ClassLoaderPatch.class) {
            PrintStream printStream = System.out;
            printStream.println("CLASSLOADER BEFORE REVERTED: " + classLoader);
            try {
                Field declaredField = BaseDexClassLoader.class.getDeclaredField("pathList");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(classLoader);
                Field declaredField2 = obj.getClass().getDeclaredField("nativeLibraryDirectories");
                declaredField2.setAccessible(true);
                List list = (List) declaredField2.get(obj);
                for (Object obj2 : patchedObjects) {
                    if (obj2 instanceof File) {
                        list.remove(obj2);
                    }
                }
                Field declaredField3 = obj.getClass().getDeclaredField("dexElements");
                declaredField3.setAccessible(true);
                Object[] objArr = (Object[]) declaredField3.get(obj);
                declaredField3.set(obj, removePatchesFromGenericArray(objArr, objArr[0].getClass()));
                Field declaredField4 = obj.getClass().getDeclaredField("nativeLibraryPathElements");
                declaredField4.setAccessible(true);
                Object[] objArr2 = (Object[]) declaredField4.get(obj);
                declaredField4.set(obj, removePatchesFromGenericArray(objArr2, objArr2[0].getClass()));
                PrintStream printStream2 = System.out;
                printStream2.println("CLASSLOADER AFTER REVERTED: " + classLoader);
            } catch (Exception e) {
                throw new RuntimeException("failed to revert patched classloader with following error", e);
            }
        }
    }

    public void initialize(Context context) {
        for (File file : this.dexFiles) {
            ClassLoaderPatch.addDexPath(context.getClassLoader(), file);
        }
    }
}