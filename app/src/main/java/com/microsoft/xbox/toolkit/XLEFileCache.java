package com.microsoft.xbox.toolkit;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class XLEFileCache {
    private static final String TAG = XLEFileCache.class.getSimpleName();
    final int maxFileNumber;
    private final long expiredTimer;
    int size;
    private boolean enabled;
    private int readAccessCnt;
    private int readSuccessfulCnt;
    private int writeAccessCnt;

    XLEFileCache() {
        size = 0;
        enabled = true;
        readAccessCnt = 0;
        writeAccessCnt = 0;
        readSuccessfulCnt = 0;
        expiredTimer = Long.MAX_VALUE;
        maxFileNumber = 0;
        enabled = false;
    }

    XLEFileCache(String dir, int maxFileNumber2) {
        this(dir, maxFileNumber2, Long.MAX_VALUE);
    }

    XLEFileCache(String dir, int maxFileNumber2, long expiredDurationInSeconds) {
        size = 0;
        enabled = true;
        readAccessCnt = 0;
        writeAccessCnt = 0;
        readSuccessfulCnt = 0;
        maxFileNumber = maxFileNumber2;
        expiredTimer = expiredDurationInSeconds;
    }

    public static int readInt(@NotNull InputStream is) throws IOException {
        int ch1 = is.read();
        int ch2 = is.read();
        int ch3 = is.read();
        int ch4 = is.read();
        if ((ch1 | ch2 | ch3 | ch4) >= 0) {
            return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
        }
        throw new EOFException();
    }

    public int getItemsInCache() {
        return size;
    }

    public synchronized boolean contains(XLEFileCacheItemKey cachedItem) {
        boolean exists;
        if (!enabled) {
            exists = false;
        } else {
            exists = new File(XLEFileCacheManager.getCacheRootDir(this), getCachedItemFileName(cachedItem)).exists();
        }
        return exists;
    }

    public synchronized OutputStream getOuputStreamForSave(XLEFileCacheItemKey cachedItem) throws IOException {
        OutputStream cachedFileOutputStreamItem;
        if (!enabled) {
            cachedFileOutputStreamItem = new OutputStream() {
                public void write(int oneByte) throws IOException {
                }
            };
        } else {
            XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
            writeAccessCnt++;
            checkAndEnsureCapacity();
            File outputFile = new File(XLEFileCacheManager.getCacheRootDir(this), getCachedItemFileName(cachedItem));
            if (outputFile.exists()) {
                outputFile.delete();
                size--;
            }
            if (outputFile.createNewFile()) {
                size++;
            }
            cachedFileOutputStreamItem = new CachedFileOutputStreamItem(cachedItem, outputFile);
        }
        return cachedFileOutputStreamItem;
    }

    public synchronized void save(XLEFileCacheItemKey fileItem, InputStream is) {
        try {
            OutputStream os = getOuputStreamForSave(fileItem);
            StreamUtil.CopyStream(os, is);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized InputStream getInputStreamForRead(XLEFileCacheItemKey cachedItem) {
        InputStream inputStream;
        if (!enabled) {
            inputStream = null;
        } else {
            XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
            readAccessCnt++;
            File cacheFile = new File(XLEFileCacheManager.getCacheRootDir(this), getCachedItemFileName(cachedItem));
            if (cacheFile.exists()) {
                if (cacheFile.lastModified() < System.currentTimeMillis() - expiredTimer) {
                    cacheFile.delete();
                    size--;
                    inputStream = null;
                } else {
                    try {
                        inputStream = new CachedFileInputStreamItem(cachedItem, cacheFile).getContentInputStream();
                        readSuccessfulCnt++;
                    } catch (IOException e) {
                    }
                }
            }
            inputStream = null;
        }
        return inputStream;
    }

    public String toString() {
        return "Size=" + size + "\tRootDir=" + XLEFileCacheManager.getCacheRootDir(this) + "\tMaxFileNumber=" + maxFileNumber + "\tExpiredTimerInSeconds=" + expiredTimer + "\tWriteAccessCnt=" + writeAccessCnt + "\tReadAccessCnt=" + readAccessCnt + "\tReadSuccessfulCnt=" + readSuccessfulCnt;
    }

    private void checkAndEnsureCapacity() {
        if (size >= maxFileNumber && enabled) {
            File[] files = XLEFileCacheManager.getCacheRootDir(this).listFiles();
            files[new Random().nextInt(files.length)].delete();
            size = files.length - 1;
        }
    }

    @NotNull
    private String getCachedItemFileName(@NotNull XLEFileCacheItemKey fileItem) {
        return String.valueOf(fileItem.getKeyString().hashCode());
    }

    private class CachedFileInputStreamItem {
        private byte[] computedMd5;
        private InputStream contentInputStream;
        private MessageDigest mDigest = null;
        private byte[] savedMd5;

        public CachedFileInputStreamItem(XLEFileCacheItemKey key, File file) throws IOException {
            FileInputStream wrappedFileInputStream = new FileInputStream(file);
            try {
                mDigest = MessageDigest.getInstance("MD5");
                savedMd5 = new byte[mDigest.getDigestLength()];
                if (wrappedFileInputStream.read(savedMd5) != mDigest.getDigestLength()) {
                    wrappedFileInputStream.close();
                    throw new IOException("Ddigest lengh check failed!");
                }
                int keyLength = XLEFileCache.readInt(wrappedFileInputStream);
                byte[] cacheItemKey = new byte[keyLength];
                if (keyLength != wrappedFileInputStream.read(cacheItemKey) || !key.getKeyString().equals(new String(cacheItemKey))) {
                    file.delete();
                    throw new IOException("File key check failed because keyLength != readKeyLength or !key.getKeyString().equals(new String(urlOrSomething))");
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                StreamUtil.CopyStream(baos, wrappedFileInputStream);
                wrappedFileInputStream.close();
                byte[] content = baos.toByteArray();
                mDigest.update(content);
                computedMd5 = mDigest.digest();
                if (isMd5Error()) {
                    file.delete();
                    throw new IOException(wrappedFileInputStream.getFD() + "the saved md5 is not equal computed md5.ComputedMd5:" + computedMd5 + "     SavedMd5:" + savedMd5);
                } else {
                    contentInputStream = new ByteArrayInputStream(content);
                }
            } catch (NoSuchAlgorithmException e) {
                wrappedFileInputStream.close();
                throw new IOException("File digest failed! " + e.getMessage());
            } catch (OutOfMemoryError e2) {
                wrappedFileInputStream.close();
                throw new IOException("File digest failed! Out of memory: " + e2.getMessage());
            }
        }

        public InputStream getContentInputStream() {
            return contentInputStream;
        }

        private boolean isMd5Error() {
            for (int i = 0; i < mDigest.getDigestLength(); i++) {
                if (savedMd5[i] != computedMd5[i]) {
                    return true;
                }
            }
            return false;
        }
    }

    private class CachedFileOutputStreamItem extends FileOutputStream {
        private File destFile;
        private MessageDigest mDigest = null;
        private boolean startDigest = false;
        private boolean writeMd5Finished = false;

        public CachedFileOutputStreamItem(@NotNull XLEFileCacheItemKey key, File file) throws IOException {
            super(file);
            destFile = file;
            try {
                mDigest = MessageDigest.getInstance("MD5");
                write(new byte[mDigest.getDigestLength()]);
                byte[] urlOrSomething = key.getKeyString().getBytes();
                writeInt(urlOrSomething.length);
                write(urlOrSomething);
                startDigest = true;
            } catch (NoSuchAlgorithmException e) {
                throw new IOException("File digest failed!" + e.getMessage());
            }
        }

        public void close() throws IOException {
            super.close();
            if (!writeMd5Finished) {
                writeMd5Finished = true;
                RandomAccessFile raf = new RandomAccessFile(destFile, "rw");
                byte[] md5Hash = mDigest.digest();
                raf.seek(0);
                raf.write(md5Hash);
                raf.close();
            }
        }

        public void write(byte[] buffer, int offset, int byteCount) throws IOException {
            super.write(buffer, offset, byteCount);
            if (startDigest) {
                mDigest.update(buffer, offset, byteCount);
            }
        }

        private final void writeInt(int v) throws IOException {
            write((v >>> 24) & 255);
            write((v >>> 16) & 255);
            write((v >>> 8) & 255);
            write((v >>> 0) & 255);
        }
    }
}