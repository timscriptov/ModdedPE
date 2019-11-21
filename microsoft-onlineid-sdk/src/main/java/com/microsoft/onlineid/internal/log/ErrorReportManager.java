package com.microsoft.onlineid.internal.log;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Debug;
import android.os.Environment;
import android.os.Process;
import android.view.View;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.sdk.BuildConfig;
import com.microsoft.onlineid.sdk.R;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.nio.CharBuffer;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class ErrorReportManager {
    private static final String ConfirmationTitle = "Report a problem?";
    private static final String CrashReportEmailTitleFormat = "MSA Android Application Crash Report - %s";
    private static final String CrashReportExtension = ".stacktrace";
    private static WeakReference<Context> CurrentActivityContext = null;
    private static Context CurrentAppContext = null;
    private static final String DontAskAgainMessage = "No, don't ask again";
    private static final DateFormat EmailTitleDateFormat = DateFormat.getDateTimeInstance(2, 2, Locale.getDefault());
    private static final String IgnoreCrashReportingStorageKeyName = "isIgnoreCrashReporting";
    private static final int LogCatNumberLines = 5000;
    private static final String ScreenshotFileName = "com.microsoft.msa.authenticator.screenshot.jpg";
    private static final String SendCrashReportConfirmation = "A problem occurred last time you ran this application. Would you like to report it?";
    private static final String SendEmailTo = "WS-MSACLIENT-AFB@microsoft.com";
    private File _contextFilePath;
    private boolean _sendLogs = true;
    private boolean _sendScreenshot = false;

    public ErrorReportManager(Context applicationContext) {
        if (applicationContext != null) {
            init(applicationContext);
        }
    }

    public ErrorReportManager() {
        CurrentAppContext = null;
    }

    public void init(Context applicationContext) {
        try {
            CurrentAppContext = applicationContext;
            if (this._contextFilePath == null && CurrentAppContext != null) {
                this._contextFilePath = CurrentAppContext.getFilesDir();
            }
        } catch (Exception ex) {
            Logger.warning("Error in init: ", ex);
        }
    }

    protected boolean isIgnoreCrashReportingFlagSet() {
        return Settings.getInstance(CurrentAppContext).isSettingEnabled(IgnoreCrashReportingStorageKeyName);
    }

    public void setSendScreenshot(boolean sendScreenshotNewValue) {
        this._sendScreenshot = sendScreenshotNewValue;
    }

    public void setSendLogs(boolean sendLogsNewValue) {
        this._sendLogs = sendLogsNewValue;
    }

    public void generateAndSaveCrashReport(Throwable e) {
        try {
            if (!isIgnoreCrashReportingFlagSet()) {
                if (this._sendScreenshot) {
                    saveScreenshot(CurrentActivityContext);
                }
                PrintWriter printWriter = new PrintWriter(CurrentAppContext.openFileOutput("stack-" + System.currentTimeMillis() + CrashReportExtension, 0));
                constructReport(e, true, null, printWriter);
                printWriter.close();
            }
        } catch (Exception ex) {
            Logger.warning("Error in generateAndSaveCrashReport: ", ex);
        }
    }

    public void generateAndSendReportWithUserPermission(Context activityContext) {
        generateAndSendReportWithUserPermission(activityContext, null);
    }

    public void generateAndSendReportWithUserPermission(Context activityContext, String userFeedback) {
        try {
            CurrentActivityContext = new WeakReference(activityContext);
            if (this._sendScreenshot) {
                saveScreenshot(CurrentActivityContext);
            }
            emailLogs(userFeedback);
        } catch (Exception ex) {
            Logger.error("!Error generateAndSendReportWithUserPermission:", ex);
        }
    }

    public void checkAndSendCrashReportWithUserPermission(Context activityContext) {
        try {
            if (!isIgnoreCrashReportingFlagSet()) {
                CurrentActivityContext = new WeakReference(activityContext);
                File[] reportFilesList = getCrashErrorFileList();
                if (reportFilesList != null && reportFilesList.length > 0) {
                    askUserPermissionToEmailCrashReport();
                }
            }
        } catch (Exception ex) {
            Logger.error("!Error checkAndSendCrashReportWithUserPermission:", ex);
        }
    }

    protected void constructReport(Throwable e, boolean shouldFilterByPID, String userFeedback, PrintWriter printWriter) {
        try {
            String delimiter = "-------------------- \n";
            if (!(userFeedback == null || userFeedback.isEmpty())) {
                printWriter.append(userFeedback);
                printWriter.append("\n\n");
            }
            AuthenticatorAccountManager accountManager = new AuthenticatorAccountManager(CurrentAppContext);
            if (accountManager.hasAccounts()) {
                for (AuthenticatorUserAccount account : accountManager.getAccounts()) {
                    appendValue(printWriter, "PUID", account.getPuid(), false);
                    appendValue(printWriter, "Username", account.getUsername(), false);
                    appendValue(printWriter, "GcmRegistrationID", account.getGcmRegistrationID(), false);
                    printWriter.append("\n");
                }
            }
            printWriter.append(new Date().toString());
            printWriter.append("\n\n");
            getDeviceInfo(printWriter);
            if (e != null) {
                printWriter.append("Stack : \n");
                printWriter.append("-------------------- \n");
                e.printStackTrace(printWriter);
                int depthCounter = 0;
                Throwable cause = e.getCause();
                while (cause != null && depthCounter < 5) {
                    printWriter.append("Cause :");
                    printWriter.append(String.valueOf(depthCounter));
                    printWriter.append("-------------------- \n");
                    cause.printStackTrace(printWriter);
                    cause = cause.getCause();
                    depthCounter++;
                }
            }
            if (this._sendLogs) {
                printWriter.append("-------------------- \n");
                printWriter.append("\nLogcat:\n\n");
                collectLogCatLogs(printWriter, true);
                printWriter.append("\n");
                printWriter.append("-------------------- \n");
            }
        } catch (Exception ex) {
            Logger.error("Exception in constructReport:", ex);
        }
    }

    protected void saveScreenshot(WeakReference<Context> activityContext) {
        Exception e;
        Throwable th;
        deleteScreenshot();
        FileOutputStream fos = null;
        try {
            View view = ((Activity) activityContext.get()).getWindow().getDecorView();
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap bitmap = view.getDrawingCache();
            FileOutputStream fos2 = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + ScreenshotFileName);
            try {
                bitmap.compress(CompressFormat.JPEG, 10, fos2);
                if (fos2 != null) {
                    try {
                        fos2.close();
                        fos = fos2;
                        return;
                    } catch (Exception e2) {
                        e = e2;
                        fos = fos2;
                        Logger.warning("Exception in saveScreenshot:", e);
                    }
                }
            } catch (Exception e3) {
                e = e3;
                fos = fos2;
                try {
                    Logger.warning("Exception in saveScreenshot:", e);
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (Exception e4) {
                            e = e4;
                            Logger.warning("Exception in saveScreenshot:", e);
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    if (fos != null) {
                        fos.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fos = fos2;
                if (fos != null) {
                    fos.close();
                }
                throw th;
            }
        } catch (Exception e5) {
            e = e5;
            Logger.warning("Exception in saveScreenshot:", e);
            if (fos != null) {
                fos.close();
            }
        }
    }

    protected File getScreenshotFile() {
        Exception e;
        File screenshotFile = null;
        try {
            if (this._sendScreenshot) {
                File screenshotFile2 = new File(Environment.getExternalStorageDirectory() + File.separator + ScreenshotFileName);
                try {
                    if (screenshotFile2.exists()) {
                        screenshotFile = screenshotFile2;
                        return screenshotFile2;
                    }
                    screenshotFile = screenshotFile2;
                    return null;
                } catch (Exception e2) {
                    e = e2;
                    screenshotFile = screenshotFile2;
                    Logger.warning("Exception in getScreenshotFile:", e);
                    return screenshotFile;
                }
            }
        } catch (Exception e3) {
            e = e3;
            Logger.warning("Exception in getScreenshotFile:", e);
            return screenshotFile;
        }
        return screenshotFile;
    }

    protected void deleteScreenshot() {
        try {
            File reportFile = getScreenshotFile();
            if (reportFile != null) {
                deleteFileNoThrow(reportFile);
            }
        } catch (Exception e) {
            Logger.warning("Exception in deleteScreenshot", e);
        }
    }

    protected static void collectLogCatLogs(PrintWriter printWriter, boolean shouldFilterByPID) {
        String pidFilter = null;
        if (shouldFilterByPID) {
            try {
                int pid = Process.myPid();
                if (pid > 0) {
                    pidFilter = Integer.toString(pid) + "):";
                }
            } catch (Exception e) {
                Logger.error("Exception in collectLogCat", e);
                return;
            }
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(new String[]{"logcat", "-t", Integer.toString(LogCatNumberLines), "-v", "time", Logger.getLogTag() + ":*", "*:S"}).getInputStream()));
        while (true) {
            String line = bufferedReader.readLine();
            if (line != null) {
                if (pidFilter == null || line.contains(pidFilter)) {
                    printWriter.append(line);
                    printWriter.append("\n");
                }
            } else {
                return;
            }
        }
    }

    protected void getDeviceInfo(PrintWriter printWriter) {
        try {
            appendValue(printWriter, "Package", CurrentAppContext.getPackageName(), false);
            appendValue(printWriter, "FilePath", this._contextFilePath.getAbsolutePath(), false);
            try {
                appendValue(printWriter, "Version", CurrentAppContext.getPackageManager().getPackageInfo(CurrentAppContext.getPackageName(), 0).versionName, false);
            } catch (Exception e) {
            }
            printWriter.append("\nPackage Data\n");
            appendValue(printWriter, "OS version", VERSION.RELEASE);
            appendValue(printWriter, "SDK level", String.valueOf(VERSION.SDK_INT));
            appendValue(printWriter, "Board", Build.BOARD);
            appendValue(printWriter, "Brand", Build.BRAND);
            appendValue(printWriter, "Phone model", Build.MODEL);
            appendValue(printWriter, "Device", Build.DEVICE);
            appendValue(printWriter, "Display", Build.DISPLAY);
            appendValue(printWriter, "Fingerprint", Build.FINGERPRINT);
            appendValue(printWriter, "Host", Build.HOST);
            appendValue(printWriter, "ID", Build.ID);
            appendValue(printWriter, "Model", Build.MODEL);
            appendValue(printWriter, "Product", Build.PRODUCT);
            appendValue(printWriter, "Tags", Build.TAGS);
            appendValue(printWriter, "Type", String.valueOf(Build.TYPE));
            appendValue(printWriter, "User", String.valueOf(Build.USER));
            appendValue(printWriter, "Locale", Locale.getDefault().toString());
            appendValue(printWriter, "Screen density", String.valueOf(CurrentAppContext.getResources().getDisplayMetrics().density));
            appendValue(printWriter, "Screen size", getScreenSize());
            appendValue(printWriter, "Screen orientation", getOrientation());
            printWriter.append("Internal Memory\n");
            appendValue(printWriter, "Total", String.valueOf(Environment.getDataDirectory().getTotalSpace() / 1024) + "KB");
            appendValue(printWriter, "Available", String.valueOf(Environment.getDataDirectory().getUsableSpace() / 1024) + "KB");
            printWriter.append("Native Memory\n");
            appendValue(printWriter, "Allocated heap size", String.valueOf(Debug.getNativeHeapAllocatedSize() / 1024) + "KB");
            appendValue(printWriter, "Free size", String.valueOf(Debug.getNativeHeapFreeSize() / 1024) + "KB");
            appendValue(printWriter, "Heap size", String.valueOf(Debug.getNativeHeapSize() / 1024) + "KB");
        } catch (Exception e2) {
            Logger.warning("Error in getDeviceInfo: ", e2);
        }
        printWriter.append("\n");
    }

    private void appendValue(PrintWriter writer, String name, String value, boolean indent) {
        String format = "%s : %s\n";
        if (indent) {
            format = "      " + format;
        }
        writer.append(String.format(Locale.US, format, new Object[]{name, value}));
    }

    private void appendValue(PrintWriter writer, String name, String value) {
        appendValue(writer, name, value, true);
    }

    private static String getScreenSize() {
        switch (CurrentAppContext.getResources().getConfiguration().screenLayout & 15) {
            case R.styleable.StyledTextView_isUnderlined /*1*/:
                return "Small";
            case ApiResult.ResultUINeeded /*2*/:
                return "Normal";
            case 3:
                return "Large";
            case 4:
                return "Xlarge";
            default:
                return "Undefined";
        }
    }

    private static String getOrientation() {
        return CurrentAppContext.getResources().getConfiguration().orientation == 1 ? "Portrait" : "Landscape";
    }

    protected File[] getCrashErrorFileList() {
        File[] fileList = new File[0];
        try {
            if (this._contextFilePath != null) {
                fileList = this._contextFilePath.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String fileName) {
                        return fileName.endsWith(ErrorReportManager.CrashReportExtension);
                    }
                });
            }
        } catch (Exception e) {
            Logger.warning("Exception in getCrashErrorFileList", e);
        }
        return fileList;
    }

    protected void notifyUserOfNoMailApp() {
        OnClickListener closeDialogListener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };
        Builder noMailDialogBuilder = new Builder((Context) CurrentActivityContext.get());
        noMailDialogBuilder.setTitle(getStringResourceIdAtRuntime("send_feedback_no_email_app_header"));
        noMailDialogBuilder.setMessage(getStringResourceIdAtRuntime("send_feedback_no_email_app_body"));
        noMailDialogBuilder.setPositiveButton(getStringResourceIdAtRuntime("popup_button_close"), closeDialogListener);
        noMailDialogBuilder.show();
    }

    private static int getStringResourceIdAtRuntime(String identifier) {
        return CurrentAppContext.getResources().getIdentifier(identifier, "string", CurrentAppContext.getPackageName());
    }

    protected void emailLogs(String userFeedback) {
        PrintWriter printWriter;
        Exception e;
        String subjectFormat;
        String subjectTag;
        String subject;
        Throwable th;
        Writer result = null;
        PrintWriter printWriter2 = null;
        try {
            String str = BuildConfig.VERSION_NAME;
            try {
                Writer result2 = new CharArrayWriter();
                try {
                    printWriter = new PrintWriter(result2);
                } catch (Exception e2) {
                    e = e2;
                    result = result2;
                    try {
                        Logger.warning("Exception in emailLogs", e);
                        printWriter2.close();
                        result.close();
                        subjectFormat = "[%s] %s";
                        subjectTag = CurrentAppContext.getResources().getString(getStringResourceIdAtRuntime("send_feedback_subject_tag"));
                        subject = EmailTitleDateFormat.format(new Date());
                        subject = subject + " : " + userFeedback.substring(0, Math.min(userFeedback.length(), 50));
                        sendEmail(CurrentActivityContext, str, SendEmailTo, String.format(Locale.US, "[%s] %s", new Object[]{subjectTag, subject}));
                    } catch (Throwable th2) {
                        th = th2;
                        printWriter2.close();
                        result.close();
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    result = result2;
                    printWriter2.close();
                    result.close();
                    throw th;
                }
                try {
                    constructReport(null, false, userFeedback, printWriter);
                    printWriter.close();
                    str = result2.toString();
                    try {
                        printWriter.close();
                        result2.close();
                        printWriter2 = printWriter;
                        result = result2;
                    } catch (Exception e3) {
                        e = e3;
                        printWriter2 = printWriter;
                        result = result2;
                        Logger.warning("Exception in emailLogs", e);
                    }
                } catch (Exception e4) {
                    e = e4;
                    printWriter2 = printWriter;
                    result = result2;
                    Logger.warning("Exception in emailLogs", e);
                    printWriter2.close();
                    result.close();
                    subjectFormat = "[%s] %s";
                    subjectTag = CurrentAppContext.getResources().getString(getStringResourceIdAtRuntime("send_feedback_subject_tag"));
                    subject = EmailTitleDateFormat.format(new Date());
                    subject = subject + " : " + userFeedback.substring(0, Math.min(userFeedback.length(), 50));
                    sendEmail(CurrentActivityContext, str, SendEmailTo, String.format(Locale.US, "[%s] %s", new Object[]{subjectTag, subject}));
                } catch (Throwable th4) {
                    th = th4;
                    printWriter2 = printWriter;
                    result = result2;
                    printWriter2.close();
                    result.close();
                    throw th;
                }
            } catch (Exception e5) {
                e = e5;
                Logger.warning("Exception in emailLogs", e);
                printWriter2.close();
                result.close();
                subjectFormat = "[%s] %s";
                subjectTag = CurrentAppContext.getResources().getString(getStringResourceIdAtRuntime("send_feedback_subject_tag"));
                subject = EmailTitleDateFormat.format(new Date());
                subject = subject + " : " + userFeedback.substring(0, Math.min(userFeedback.length(), 50));
                sendEmail(CurrentActivityContext, str, SendEmailTo, String.format(Locale.US, "[%s] %s", new Object[]{subjectTag, subject}));
            }
            subjectFormat = "[%s] %s";
            subjectTag = CurrentAppContext.getResources().getString(getStringResourceIdAtRuntime("send_feedback_subject_tag"));
            subject = EmailTitleDateFormat.format(new Date());
            if (!(userFeedback == null || userFeedback.isEmpty())) {
                subject = subject + " : " + userFeedback.substring(0, Math.min(userFeedback.length(), 50));
            }
            sendEmail(CurrentActivityContext, str, SendEmailTo, String.format(Locale.US, "[%s] %s", new Object[]{subjectTag, subject}));
        } catch (Exception e6) {
            e = e6;
            Logger.warning("Exception in emailLogs", e);
        }
    }

    protected void askUserPermissionToEmailCrashReport() {
        OnClickListener permissionListener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                ErrorReportManager.this.sendMailAndDeleteFiles(id == -1);
                if (id == -3) {
                    Settings.getInstance(ErrorReportManager.CurrentAppContext).setSetting(ErrorReportManager.IgnoreCrashReportingStorageKeyName, "true");
                }
            }
        };
        Builder permissionDialogBuilder = new Builder((Context) CurrentActivityContext.get());
        permissionDialogBuilder.setTitle(ConfirmationTitle);
        permissionDialogBuilder.setMessage(SendCrashReportConfirmation);
        permissionDialogBuilder.setPositiveButton(17039379, permissionListener);
        permissionDialogBuilder.setNegativeButton(17039369, permissionListener);
        permissionDialogBuilder.setNeutralButton(DontAskAgainMessage, permissionListener);
        permissionDialogBuilder.show();
    }

    protected void sendMailAndDeleteFiles(boolean sendMail) {
        Exception e;
        File[] reportFilesList = getCrashErrorFileList();
        CharBuffer buffer = null;
        Arrays.sort(reportFilesList);
        int bufferCapacity = 0;
        for (File file : reportFilesList) {
            if (sendMail) {
                bufferCapacity = (int) (((long) bufferCapacity) + file.length());
            } else {
                deleteFileNoThrow(file);
            }
        }
        if (bufferCapacity > 0) {
            buffer = CharBuffer.allocate(bufferCapacity);
            for (File file2 : reportFilesList) {
                FileReader input = null;
                try {
                    FileReader input2 = new FileReader(file2.getAbsoluteFile());
                    do {
                        try {
                        } catch (Exception e2) {
                            e = e2;
                            input = input2;
                        }
                    } while (input2.read(buffer) > 0);
                    buffer.flip();
                    input = input2;
                } catch (Exception e3) {
                    e = e3;
                    Logger.warning("Error reading the report file", e);
                    input.close();
                }
                try {
                    input.close();
                } catch (Exception e4) {
                    Logger.error("Error closing the report file", e4);
                    try {
                    } catch (Exception e42) {
                        Logger.warning("Error in sendMailAndDeleteFiles: ", e42);
                        return;
                    }
                } finally {
                    deleteFileNoThrow(file2);
                }
            }
        }
        if (sendMail && buffer != null) {
            sendEmail(CurrentActivityContext, buffer.toString(), SendEmailTo, String.format(Locale.US, CrashReportEmailTitleFormat, new Object[]{EmailTitleDateFormat.format(new Date())}));
        }
    }

    protected void deleteFileNoThrow(File file) {
        if (file != null) {
            try {
                file.delete();
            } catch (Exception e) {
                Logger.error("deleteFileNoThrow failed", e);
            }
        }
    }

    protected void sendEmail(WeakReference<Context> context, String errorContent, String mailTo, String emailSubject) {
        try {
            File file = getScreenshotFile();
            Intent sendIntent = new Intent("android.intent.action.SEND");
            sendIntent.putExtra("android.intent.extra.EMAIL", new String[]{mailTo});
            sendIntent.putExtra("android.intent.extra.SUBJECT", emailSubject);
            sendIntent.putExtra("android.intent.extra.TEXT", errorContent + "\n");
            sendIntent.setType("message/rfc822");
            if (file != null) {
                sendIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
            }
            ((Context) context.get()).startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            notifyUserOfNoMailApp();
            Logger.warning("ActivityNotFoundException in sendEmail.", e);
        } catch (Exception ex) {
            Logger.warning("Exception in sendEmail.", ex);
        }
    }
}
