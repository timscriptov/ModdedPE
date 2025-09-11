package com.microsoft.xbox.toolkit;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

/**
 * 07.01.2021
 *
 * @author <a href="https://github.com/timscriptov">timscriptov</a>
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
        this.size = 0;
        this.enabled = true;
        this.readAccessCnt = 0;
        this.writeAccessCnt = 0;
        this.readSuccessfulCnt = 0;
        this.expiredTimer = Long.MAX_VALUE;
        this.maxFileNumber = 0;
        this.enabled = false;
    }

    XLEFileCache(String str, int i) {
        this(str, i, Long.MAX_VALUE);
    }

    XLEFileCache(String str, int i, long j) {
        this.size = 0;
        this.enabled = true;
        this.readAccessCnt = 0;
        this.writeAccessCnt = 0;
        this.readSuccessfulCnt = 0;
        this.maxFileNumber = i;
        this.expiredTimer = j;
    }

    public static int readInt(@NotNull InputStream inputStream) throws IOException {
        int read = inputStream.read();
        int read2 = inputStream.read();
        int read3 = inputStream.read();
        int read4 = inputStream.read();
        if ((read | read2 | read3 | read4) >= 0) {
            return (read << 24) + (read2 << 16) + (read3 << 8) + (read4 << 0);
        }
        throw new EOFException();
    }

    public int getItemsInCache() {
        return this.size;
    }

    public synchronized boolean contains(XLEFileCacheItemKey xLEFileCacheItemKey) {
        if (!this.enabled) {
            return false;
        }
        return new File(XLEFileCacheManager.getCacheRootDir(this), getCachedItemFileName(xLEFileCacheItemKey)).exists();
    }

    public synchronized OutputStream getOuputStreamForSave(XLEFileCacheItemKey xLEFileCacheItemKey) throws IOException {
        if (!this.enabled) {
            return new OutputStream() {
                public void write(int i) throws IOException {
                }
            };
        }
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        this.writeAccessCnt++;
        checkAndEnsureCapacity();
        File file = new File(XLEFileCacheManager.getCacheRootDir(this), getCachedItemFileName(xLEFileCacheItemKey));
        if (file.exists()) {
            file.delete();
            this.size--;
        }
        if (file.createNewFile()) {
            this.size++;
        }
        return new CachedFileOutputStreamItem(xLEFileCacheItemKey, file);
    }

    public synchronized void save(XLEFileCacheItemKey xLEFileCacheItemKey, InputStream inputStream) {
        try {
            OutputStream ouputStreamForSave = getOuputStreamForSave(xLEFileCacheItemKey);
            StreamUtil.CopyStream(ouputStreamForSave, inputStream);
            ouputStreamForSave.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized InputStream getInputStreamForRead(XLEFileCacheItemKey xLEFileCacheItemKey) {
        if (!this.enabled) {
            return null;
        }
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        this.readAccessCnt++;
        File file = new File(XLEFileCacheManager.getCacheRootDir(this), getCachedItemFileName(xLEFileCacheItemKey));
        if (file.exists()) {
            if (file.lastModified() < System.currentTimeMillis() - this.expiredTimer) {
                file.delete();
                this.size--;
                return null;
            }
            try {
                InputStream contentInputStream = new CachedFileInputStreamItem(xLEFileCacheItemKey, file).getContentInputStream();
                this.readSuccessfulCnt++;
                return contentInputStream;
            } catch (IOException unused) {
                return null;
            }
        }
        return null;
    }

    public @NotNull String toString() {
        return "Size=" + this.size + "\tRootDir=" + XLEFileCacheManager.getCacheRootDir(this) + "\tMaxFileNumber=" + this.maxFileNumber + "\tExpiredTimerInSeconds=" + this.expiredTimer + "\tWriteAccessCnt=" + this.writeAccessCnt + "\tReadAccessCnt=" + this.readAccessCnt + "\tReadSuccessfulCnt=" + this.readSuccessfulCnt;
    }

    private void checkAndEnsureCapacity() {
        if (this.size >= this.maxFileNumber && this.enabled) {
            File[] listFiles = XLEFileCacheManager.getCacheRootDir(this).listFiles();
            listFiles[new Random().nextInt(listFiles.length)].delete();
            this.size = listFiles.length - 1;
        }
    }

    private @NotNull String getCachedItemFileName(@NotNull XLEFileCacheItemKey xLEFileCacheItemKey) {
        return String.valueOf(xLEFileCacheItemKey.getKeyString().hashCode());
    }

    private class CachedFileInputStreamItem {
        private final InputStream contentInputStream;
        private byte[] computedMd5;
        private byte[] savedMd5;
        private MessageDigest mDigest = null;

        public CachedFileInputStreamItem(XLEFileCacheItemKey xLEFileCacheItemKey, File file) throws IOException {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                initializeDigest();
                readAndValidateMd5(fileInputStream);
                validateFileKey(xLEFileCacheItemKey, fileInputStream);
                byte[] content = readContent(fileInputStream);
                validateContentIntegrity(content, file);
                this.contentInputStream = new ByteArrayInputStream(content);
            } catch (NoSuchAlgorithmException e) {
                throw new IOException("MD5 algorithm not available: " + e.getMessage(), e);
            } catch (OutOfMemoryError e) {
                throw new IOException("Out of memory while processing file: " + e.getMessage(), e);
            }
        }

        private void initializeDigest() throws NoSuchAlgorithmException {
            this.mDigest = MessageDigest.getInstance("MD5");
            this.savedMd5 = new byte[this.mDigest.getDigestLength()];
        }

        private void readAndValidateMd5(@NotNull FileInputStream fileInputStream) throws IOException {
            int bytesRead = fileInputStream.read(this.savedMd5);
            if (bytesRead != this.mDigest.getDigestLength()) {
                throw new IOException("Digest length check failed! Expected: " +
                        this.mDigest.getDigestLength() + ", got: " + bytesRead);
            }
        }

        private void validateFileKey(XLEFileCacheItemKey cacheItemKey, FileInputStream fileInputStream)
                throws IOException {
            int keyLength = XLEFileCache.readInt(fileInputStream);
            byte[] keyBytes = new byte[keyLength];

            int bytesRead = fileInputStream.read(keyBytes);
            if (bytesRead != keyLength) {
                throw new IOException("Key length mismatch. Expected: " + keyLength + ", got: " + bytesRead);
            }

            String actualKey = new String(keyBytes);
            if (!cacheItemKey.getKeyString().equals(actualKey)) {
                throw new IOException("File key validation failed. Expected: " +
                        cacheItemKey.getKeyString() + ", got: " + actualKey);
            }
        }

        private byte @NotNull [] readContent(FileInputStream fileInputStream) throws IOException {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            StreamUtil.CopyStream(byteArrayOutputStream, fileInputStream);
            return byteArrayOutputStream.toByteArray();
        }

        private void validateContentIntegrity(byte[] content, File file) throws IOException {
            this.mDigest.update(content);
            this.computedMd5 = this.mDigest.digest();

            if (isMd5Error()) {
                file.delete();
                throw new IOException("MD5 validation failed. Computed: " + Arrays.toString(this.computedMd5) +
                        ", Saved: " + Arrays.toString(this.savedMd5));
            }
        }

        public InputStream getContentInputStream() {
            return this.contentInputStream;
        }

        private boolean isMd5Error() {
            return !Arrays.equals(this.savedMd5, this.computedMd5);
        }
    }

    private class CachedFileOutputStreamItem extends FileOutputStream {
        private final File destFile;
        private final MessageDigest mDigest;
        private final boolean startDigest;
        private boolean writeMd5Finished = false;

        public CachedFileOutputStreamItem(@NotNull XLEFileCacheItemKey xLEFileCacheItemKey, File file) throws IOException {
            super(file);
            this.destFile = file;
            try {
                MessageDigest instance = MessageDigest.getInstance("MD5");
                this.mDigest = instance;
                write(new byte[instance.getDigestLength()]);
                byte[] bytes = xLEFileCacheItemKey.getKeyString().getBytes();
                writeInt(bytes.length);
                write(bytes);
                this.startDigest = true;
            } catch (NoSuchAlgorithmException e) {
                throw new IOException("File digest failed!" + e.getMessage());
            }
        }

        public void close() throws IOException {
            super.close();
            if (!this.writeMd5Finished) {
                this.writeMd5Finished = true;
                RandomAccessFile randomAccessFile = new RandomAccessFile(this.destFile, "rw");
                byte[] digest = this.mDigest.digest();
                randomAccessFile.seek(0);
                randomAccessFile.write(digest);
                randomAccessFile.close();
            }
        }

        public void write(byte[] bArr, int i, int i2) throws IOException {
            super.write(bArr, i, i2);
            if (this.startDigest) {
                this.mDigest.update(bArr, i, i2);
            }
        }

        private final void writeInt(int i) throws IOException {
            write((i >>> 24) & 255);
            write((i >>> 16) & 255);
            write((i >>> 8) & 255);
            write((i >>> 0) & 255);
        }
    }
}
