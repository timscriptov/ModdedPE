package com.mojang.minecraftpe;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.Page;
import android.graphics.pdf.PdfDocument.PageInfo;
import android.graphics.pdf.PdfDocument.PageInfo.Builder;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.http.HttpStatus;

public class PDFWriter {
    private Rect mImageRect;
    private PdfDocument mOpenDocument;
    private Rect mPageRect = new Rect(0, 0, 612, 792);
    private TextPaint mPageTextPaint;
    private Rect mTextRect;
    private Rect mTitleRect = new Rect(0, 0, mPageRect.width(), (int) (((float) mPageRect.height()) * (1.0f - 0.3f)));
    private TextPaint mTitleTextPaint;

    public PDFWriter() {
        mTitleRect.offset(0, (int) (((float) mPageRect.height()) * 0.3f));
        mTextRect = new Rect(mPageRect);
        mTextRect.inset(20, 20);
        mImageRect = new Rect(0, 0, HttpStatus.SC_INTERNAL_SERVER_ERROR, HttpStatus.SC_INTERNAL_SERVER_ERROR);
        mImageRect.offset(this.mPageRect.centerX() - mImageRect.centerX(), mPageRect.centerY() - mImageRect.centerY());
        Typeface titleFont = Typeface.DEFAULT_BOLD;
        try {
            titleFont = Typeface.createFromAsset(MainActivity.mInstance.getAssets(), "fonts/Mojangles.ttf");
        } catch (Exception e) {
            System.out.println("Failed to load mojangles font: " + e.getMessage());
        }
        mTitleTextPaint = new TextPaint();
        mTitleTextPaint.setAntiAlias(true);
        mTitleTextPaint.setTextSize(64.0f);
        mTitleTextPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        mTitleTextPaint.setTypeface(titleFont);
        mPageTextPaint = new TextPaint();
        mPageTextPaint.setAntiAlias(true);
        mPageTextPaint.setTextSize(32.0f);
        mPageTextPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean createDocument(String[] orderedFilenames, String title) {
        if (mOpenDocument != null) {
            mOpenDocument.close();
        }
        mOpenDocument = new PdfDocument();
        Page titlePage = mOpenDocument.startPage(_getPageInfo(1));
        _drawTextInRect(title, titlePage, mTitleTextPaint, mTitleRect, Alignment.ALIGN_CENTER);
        mOpenDocument.finishPage(titlePage);
        int i = 0;
        while (i < orderedFilenames.length) {
            String filename = orderedFilenames[i];
            Page page = mOpenDocument.startPage(_getPageInfo(i + 2));
            try {
                String fileExtension = _getExtension(filename);
                if (fileExtension.equals("txt")) {
                    _drawTextInRect(_readFileToString(filename), page, mPageTextPaint, mTextRect, Alignment.ALIGN_NORMAL);
                } else if (fileExtension.equals("jpeg")) {
                    page.getCanvas().drawBitmap(BitmapFactory.decodeFile(filename), null, mImageRect, null);
                } else {
                    throw new UnsupportedOperationException("Unsupported extension from file: " + filename);
                }
                this.mOpenDocument.finishPage(page);
                i++;
            } catch (Exception e) {
                System.out.println("Failed to write page: " + e.getMessage());
                closeDocument();
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean writeDocumentToFile(String destinationFilename) {
        try {
            mOpenDocument.writeTo(new FileOutputStream(destinationFilename));
            return true;
        } catch (Exception e) {
            System.out.println("Failed to write pdf file: " + e.getMessage());
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void closeDocument() {
        if (mOpenDocument != null) {
            mOpenDocument.close();
            mOpenDocument = null;
        }
    }

    public String getPicturesDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private PageInfo _getPageInfo(int pageNumber) {
        return new Builder(mPageRect.width(), mPageRect.height(), pageNumber).create();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void _drawTextInRect(String text, Page page, TextPaint textPaint, Rect rect, Alignment alignment) {
        StaticLayout textLayout = new StaticLayout(text, textPaint, rect.width(), alignment, 1.0f, 0.0f, false);
        Canvas pageCanvas = page.getCanvas();
        pageCanvas.translate((float) rect.left, (float) rect.top);
        textLayout.draw(pageCanvas);
    }

    private String _readFileToString(String filename) throws FileNotFoundException, IOException {
        File textFile = new File(filename);
        FileInputStream textStream = new FileInputStream(textFile);
        byte[] textBytes = new byte[((int) textFile.length())];
        textStream.read(textBytes);
        textStream.close();
        return new String(textBytes);
    }

    private String _getExtension(String filename) {
        int index = filename.lastIndexOf(46);
        if (index < 0 || index + 1 >= filename.length()) {
            return "";
        }
        return filename.substring(index + 1).toLowerCase();
    }
}
