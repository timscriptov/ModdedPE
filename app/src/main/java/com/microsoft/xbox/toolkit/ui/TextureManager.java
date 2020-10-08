package com.microsoft.xbox.toolkit.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.widget.ImageView;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.MemoryMonitor;
import com.microsoft.xbox.toolkit.MultiMap;
import com.microsoft.xbox.toolkit.StreamUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.ThreadSafePriorityQueue;
import com.microsoft.xbox.toolkit.TimeMonitor;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEFileCache;
import com.microsoft.xbox.toolkit.XLEFileCacheManager;
import com.microsoft.xbox.toolkit.XLEMemoryCache;
import com.microsoft.xbox.toolkit.XLEThread;
import com.microsoft.xbox.toolkit.network.HttpClientFactory;
import com.microsoft.xbox.toolkit.network.XLEHttpStatusAndStream;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import com.microsoft.xboxtcui.XboxTcuiSdk;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class TextureManager {
    private static final int ANIM_TIME = 100;
    private static final int BITMAP_CACHE_MAX_FILE_SIZE_IN_BYTES = 5242880;
    private static final String BMP_FILE_CACHE_DIR_NAME = "texture";
    private static final int BMP_FILE_CACHE_SIZE = 2000;
    private static final int DECODE_THREAD_WAIT_TIMEOUT_MS = 3000;
    private static final int TEXTURE_TIMEOUT_MS = 15000;
    private static final long TIME_TO_RETRY_MS = 300000;
    public static TextureManager instance = new TextureManager();
    public XLEMemoryCache<TextureManagerScaledNetworkBitmapRequest, XLEBitmap> bitmapCache = new XLEMemoryCache<>(Math.min(getNetworkBitmapCacheSizeInMB(), 50) * 1048576, BITMAP_CACHE_MAX_FILE_SIZE_IN_BYTES);
    public XLEFileCache bitmapFileCache = XLEFileCacheManager.createCache(BMP_FILE_CACHE_DIR_NAME, BMP_FILE_CACHE_SIZE);
    public HashSet<TextureManagerScaledNetworkBitmapRequest> inProgress = new HashSet<>();
    public Object listLock = new Object();
    public HashMap<TextureManagerScaledNetworkBitmapRequest, RetryEntry> timeToRetryCache = new HashMap<>();
    public ThreadSafePriorityQueue<TextureManagerDownloadRequest> toDecode = new ThreadSafePriorityQueue<>();
    public MultiMap<TextureManagerScaledNetworkBitmapRequest, ImageView> waitingForImage = new MultiMap<>();
    private Thread decodeThread = null;
    private HashMap<TextureManagerScaledResourceBitmapRequest, XLEBitmap> resourceBitmapCache = new HashMap<>();
    private TimeMonitor stopwatch = new TimeMonitor();

    public TextureManager() {
        stopwatch.start();
        decodeThread = new XLEThread(new TextureManagerDecodeThread(), "XLETextureDecodeThread");
        decodeThread.setDaemon(true);
        decodeThread.setPriority(4);
        decodeThread.start();
    }

    public static TextureManager Instance() {
        return instance;
    }

    private static boolean invalidUrl(String url) {
        return url == null || url.length() == 0;
    }

    private static boolean validResizeDimention(int width, int height) {
        if (width != 0 && height != 0) {
            return width > 0 && height > 0;
        }
        throw new UnsupportedOperationException();
    }

    private int getNetworkBitmapCacheSizeInMB() {
        return (Math.max(0, MemoryMonitor.instance().getMemoryClass() - 64) / 2) + 12;
    }

    public void unsafeClearBitmapCache() {
    }

    private void load(@NotNull TextureManagerScaledNetworkBitmapRequest key) {
        if (!invalidUrl(key.url)) {
            XLEThreadPool.textureThreadPool.run(new TextureManagerDownloadThreadWorker(new TextureManagerDownloadRequest(key)));
        }
    }

    public XLEBitmap.XLEBitmapDrawable loadScaledResourceDrawable(int resourceId) {
        XLEBitmap bitmap = loadResource(resourceId);
        if (bitmap == null) {
            return null;
        }
        return bitmap.getDrawable();
    }

    public BitmapFactory.Options computeInSampleSizeOptions(int desiredw, int desiredh, BitmapFactory.Options options) {
        boolean z = true;
        BitmapFactory.Options scaleoptions = new BitmapFactory.Options();
        int scale = 1;
        if (validResizeDimention(desiredw, desiredh) && options.outWidth > desiredw && options.outHeight > desiredh) {
            scale = (int) Math.pow(2.0d, (double) Math.min((int) Math.floor(Math.log((double) (((float) options.outWidth) / ((float) desiredw))) / Math.log(2.0d)), (int) Math.floor(Math.log((double) (((float) options.outHeight) / ((float) desiredh))) / Math.log(2.0d))));
            if (scale < 1) {
                z = false;
            }
            XLEAssert.assertTrue(z);
        }
        scaleoptions.inSampleSize = scale;
        return scaleoptions;
    }

    public XLEBitmap loadResource(int resourceId) {
        TextureManagerScaledResourceBitmapRequest request = new TextureManagerScaledResourceBitmapRequest(resourceId);
        XLEBitmap bitmap = resourceBitmapCache.get(request);
        if (bitmap == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(XboxTcuiSdk.getResources(), request.resourceId, options);
            bitmap = XLEBitmap.decodeResource(XboxTcuiSdk.getResources(), request.resourceId);
            resourceBitmapCache.put(request, bitmap);
        }
        XLEAssert.assertNotNull(bitmap);
        return bitmap;
    }

    public void preload(int resourceId) {
    }

    public void preload(URI uri) {
    }

    public void preloadFromFile(String filePath) {
    }

    public void bindToView(int resourceId, ImageView view, int width, int height) {
        bindToView(resourceId, view, width, height, (OnBitmapSetListener) null);
    }

    public void bindToView(int resourceId, ImageView view, int width, int height, OnBitmapSetListener listener) {
        boolean z;
        boolean z2 = true;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        XLEBitmap bitmap = loadResource(resourceId);
        if (bitmap == null) {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
        if (view instanceof XLEImageView) {
            ((XLEImageView) view).TEST_loadingOrLoadedImageUrl = Integer.toString(resourceId);
        }
        setImage(view, bitmap);
    }

    public void bindToViewFromFile(String filePath, ImageView view, TextureBindingOption option) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        bindToViewInternal(filePath, view, option);
    }

    public void bindToViewFromFile(String filePath, ImageView view, int width, int height) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (width == 0 || height == 0) {
            throw new UnsupportedOperationException();
        }
        bindToViewInternal(filePath, view, new TextureBindingOption(width, height));
    }

    public void bindToView(URI uri, ImageView view, int width, int height) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (width == 0 || height == 0) {
            throw new UnsupportedOperationException();
        }
        bindToViewInternal(uri == null ? null : uri.toString(), view, new TextureBindingOption(width, height));
    }

    public void bindToView(URI uri, ImageView view, TextureBindingOption option) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        bindToViewInternal(uri == null ? null : uri.toString(), view, option);
    }

    public void setCachingEnabled(boolean enabled) {
        bitmapCache = new XLEMemoryCache<>(enabled ? getNetworkBitmapCacheSizeInMB() : 0, BITMAP_CACHE_MAX_FILE_SIZE_IN_BYTES);
        bitmapFileCache = XLEFileCacheManager.createCache(BMP_FILE_CACHE_DIR_NAME, BMP_FILE_CACHE_SIZE, enabled);
        resourceBitmapCache = new HashMap<>();
    }

    public boolean isBusy() {
        boolean z;
        synchronized (listLock) {
            z = !inProgress.isEmpty();
        }
        return z;
    }

    private void bindToViewInternal(String url, ImageView view, TextureBindingOption option) {
        boolean needToDownload;
        TextureManagerScaledNetworkBitmapRequest key = new TextureManagerScaledNetworkBitmapRequest(url, option);
        XLEBitmap bitmap = null;
        synchronized (listLock) {
            if (waitingForImage.containsValue(view)) {
                waitingForImage.removeValue(view);
            }
            if (!invalidUrl(url)) {
                bitmap = bitmapCache.get(key);
                if (bitmap == null) {
                    RetryEntry retryEntry = timeToRetryCache.get(key);
                    if (retryEntry == null) {
                        needToDownload = true;
                    } else if (retryEntry.isExpired()) {
                        needToDownload = true;
                    } else {
                        if (option.resourceIdForError != -1) {
                            bitmap = loadResource(option.resourceIdForError);
                        }
                        needToDownload = false;
                    }
                } else {
                    needToDownload = false;
                }
            } else if (option.resourceIdForError != -1) {
                bitmap = loadResource(option.resourceIdForError);
                needToDownload = false;
                XLEAssert.assertNotNull(bitmap);
            } else {
                needToDownload = false;
            }
            if (needToDownload) {
                if (option.resourceIdForLoading != -1) {
                    bitmap = loadResource(option.resourceIdForLoading);
                    XLEAssert.assertTrue(bitmap != null);
                }
                this.waitingForImage.put(key, view);
                if (!inProgress.contains(key)) {
                    inProgress.add(key);
                    load(key);
                }
            }
        }
        setImage(view, bitmap);
        if (view instanceof XLEImageView) {
            ((XLEImageView) view).TEST_loadingOrLoadedImageUrl = url;
        }
    }

    public XLEBitmap createScaledBitmap(XLEBitmap bitmapsrc, int width, int height) {
        XLEBitmap bitmap = bitmapsrc;
        if (!validResizeDimention(width, height) || bitmapsrc.getBitmap() == null) {
            return bitmap;
        }
        float bitmapAR = ((float) bitmapsrc.getBitmap().getHeight()) / ((float) bitmapsrc.getBitmap().getWidth());
        if (((float) height) / ((float) width) < bitmapAR) {
            width = Math.max(1, (int) (((float) height) / bitmapAR));
        } else {
            height = Math.max(1, (int) (((float) width) * bitmapAR));
        }
        return XLEBitmap.createScaledBitmap8888(bitmapsrc, width, height, true);
    }

    public void drainWaitingForImage(TextureManagerScaledNetworkBitmapRequest key, XLEBitmap bitmap) {
        if (waitingForImage.containsKey(key)) {
            Iterator<ImageView> it = waitingForImage.get(key).iterator();
            while (it.hasNext()) {
                ImageView view = it.next();
                if (view != null) {
                    if (view instanceof XLEImageView) {
                        setXLEImageView(key, (XLEImageView) view, bitmap);
                    } else {
                        setView(key, view, bitmap);
                    }
                }
            }
        }
    }

    private void setView(final TextureManagerScaledNetworkBitmapRequest key, final ImageView view, final XLEBitmap bitmap) {
        ThreadManager.UIThreadPost(() -> {
            boolean stillValid;
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            synchronized (listLock) {
                stillValid = waitingForImage.keyValueMatches(key, view);
            }
            if (stillValid) {
                setImage(view, bitmap);
                synchronized (listLock) {
                    waitingForImage.removeValue(view);
                }
            }
        });
    }

    private void setXLEImageView(final TextureManagerScaledNetworkBitmapRequest key, final XLEImageView view, final XLEBitmap bitmap) {
        ThreadManager.UIThreadPost((Runnable) () -> {
            boolean stillValid;
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            synchronized (listLock) {
                stillValid = waitingForImage.keyValueMatches(key, view);
            }
            if (stillValid) {
                final float finalAlpha = view.getAlpha();
                if (view.getShouldAnimate()) {
                    view.animate().alpha(0.0f).setDuration(100).setListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            view.setFinal(true);
                            setImage(view, bitmap);
                            view.animate().alpha(finalAlpha).setDuration(100).setListener((Animator.AnimatorListener) null);
                        }
                    });
                } else {
                    setImage(view, bitmap);
                }
                synchronized (listLock) {
                    waitingForImage.removeValue(view);
                }
            }
        });
    }

    public void logMemoryUsage() {
    }

    public void purgeResourceBitmapCache() {
        resourceBitmapCache.clear();
    }

    public void setImage(@NotNull ImageView img, XLEBitmap bitmap) {
        Bitmap bmp = bitmap == null ? null : bitmap.getBitmap();
        OnBitmapSetListener listener = (OnBitmapSetListener) img.getTag(R.id.image_callback);
        if (listener != null) {
            listener.onBeforeImageSet(img, bmp);
        }
        img.setImageBitmap(bmp);
        img.setTag(R.id.image_bound, true);
        if (listener != null) {
            listener.onAfterImageSet(img, bmp);
        }
    }

    private static class RetryEntry {
        private static final long SEC = 1000;
        private static final long[] TIMES_MS = {5000, 9000, 19000, 37000, 75000, 150000, 300000};
        private int curIdx = 0;
        private long currStart = System.currentTimeMillis();

        public boolean isExpired() {
            return currStart + TIMES_MS[curIdx] < System.currentTimeMillis();
        }

        public void startNext() {
            if (curIdx < TIMES_MS.length - 1) {
                curIdx++;
            }
            currStart = System.currentTimeMillis();
        }
    }

    private class TextureManagerDecodeThread implements Runnable {
        private TextureManagerDecodeThread() {
        }

        public void run() {
            while (true) {
                TextureManagerDownloadRequest request = (TextureManagerDownloadRequest) toDecode.pop();
                XLEBitmap bitmap = null;
                if (request.stream != null) {
                    BackgroundThreadWaitor.getInstance().waitForReady(TextureManager.DECODE_THREAD_WAIT_TIMEOUT_MS);
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        StreamUtil.CopyStream(baos, request.stream);
                        byte[] buffer = baos.toByteArray();
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(new ByteArrayInputStream(buffer), (Rect) null, options);
                        BitmapFactory.Options scaleoptions = computeInSampleSizeOptions(request.key.bindingOption.width, request.key.bindingOption.height, options);
                        int i = (options.outWidth / scaleoptions.inSampleSize) * (options.outHeight / scaleoptions.inSampleSize) * 4;
                        XLEBitmap bitmapsrc = XLEBitmap.decodeStream(new ByteArrayInputStream(buffer), scaleoptions);
                        if (request.key.bindingOption.useFileCache && !bitmapFileCache.contains(request.key)) {
                            bitmapFileCache.save(request.key, new ByteArrayInputStream(buffer));
                        }
                        bitmap = createScaledBitmap(bitmapsrc, request.key.bindingOption.width, request.key.bindingOption.height);
                    } catch (Exception e) {
                        bitmap = null;
                    }
                }
                BackgroundThreadWaitor.getInstance().waitForReady(TextureManager.DECODE_THREAD_WAIT_TIMEOUT_MS);
                synchronized (listLock) {
                    if (bitmap != null) {
                        bitmapCache.add(request.key, bitmap, bitmap.getByteCount());
                        timeToRetryCache.remove(request.key);
                    } else if (request.key.bindingOption.resourceIdForError != -1) {
                        bitmap = loadResource(request.key.bindingOption.resourceIdForError);
                        RetryEntry retryEntry = (RetryEntry) timeToRetryCache.get(request.key);
                        if (retryEntry != null) {
                            retryEntry.startNext();
                        } else {
                            timeToRetryCache.put(request.key, new RetryEntry());
                        }
                    }
                    drainWaitingForImage(request.key, bitmap);
                    inProgress.remove(request.key);
                }
            }
        }
    }

    private class TextureManagerDownloadThreadWorker implements Runnable {
        private TextureManagerDownloadRequest request;

        public TextureManagerDownloadThreadWorker(TextureManagerDownloadRequest request2) {
            request = request2;
        }

        public void run() {
            XLEAssert.assertTrue((request.key == null || request.key.url == null) ? false : true);
            request.stream = null;
            try {
                if (!request.key.url.startsWith(HttpHost.DEFAULT_SCHEME_NAME)) {
                    request.stream = downloadFromAssets(request.key.url);
                } else if (request.key.bindingOption.useFileCache) {
                    request.stream = bitmapFileCache.getInputStreamForRead(request.key);
                    if (request.stream == null) {
                        request.stream = downloadFromWeb(request.key.url);
                    }
                } else {
                    request.stream = downloadFromWeb(request.key.url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            synchronized (listLock) {
                toDecode.push(request);
            }
        }

        @Nullable
        private InputStream downloadFromWeb(String requestUrl) {
            try {
                XLEHttpStatusAndStream statusAndStream = HttpClientFactory.textureFactory.getHttpClient(TextureManager.TEXTURE_TIMEOUT_MS).getHttpStatusAndStreamInternal(new HttpGet(URI.create(requestUrl)), false);
                if (statusAndStream.statusCode == 200) {
                    return statusAndStream.stream;
                }
                return null;
            } catch (Exception e) {
                return null;
            }
        }

        @Nullable
        private InputStream downloadFromAssets(String requestUrl) {
            try {
                return XboxTcuiSdk.getAssetManager().open(requestUrl);
            } catch (IOException e) {
                return null;
            }
        }
    }
}
