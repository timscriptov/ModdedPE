package com.microsoft.onlineid.internal.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.transport.Transport;
import com.microsoft.onlineid.internal.transport.TransportFactory;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.KnownEnvironment;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadProfileImageTask extends AsyncTask<Void, Void, Bitmap> {
    public static final String UserTileExtension = ".png";
    protected static final String UserTileUrlFormat = "https://storage.%2$s/users/0x%1$s/myprofile/expressionprofile/profilephoto:UserTileStatic";
    private final AuthenticatorUserAccount _account;
    private final Context _applicationContext;
    private final ImageView _imageView;
    private boolean _newImageDownloaded;
    private final ServerConfig _serverConfig;
    private final Transport _transport;
    private final String _userTileFileName;

    public DownloadProfileImageTask(Context applicationContext, AuthenticatorUserAccount account, ImageView imageView) {
        this._applicationContext = applicationContext;
        this._account = account;
        this._transport = new TransportFactory(applicationContext).createTransport();
        this._serverConfig = new ServerConfig(applicationContext);
        this._imageView = imageView;
        this._userTileFileName = this._account.getPuid() + UserTileExtension;
        this._newImageDownloaded = false;
    }

    DownloadProfileImageTask(AuthenticatorUserAccount account, Transport transport, ServerConfig serverConfig) {
        this._applicationContext = null;
        this._account = account;
        this._transport = transport;
        this._serverConfig = serverConfig;
        this._imageView = null;
        this._userTileFileName = null;
        this._newImageDownloaded = false;
    }

    protected void onPreExecute() {
        Bitmap lastSavedUserTile = getSavedUserTileImage();
        if (lastSavedUserTile != null) {
            this._imageView.setImageBitmap(lastSavedUserTile);
        }
    }

    protected Bitmap doInBackground(Void... params) {
        InputStream responseStream = null;
        try {
            String str = UserTileUrlFormat;
            Object[] objArr = new Object[2];
            objArr[0] = this._account.getCid();
            objArr[1] = this._serverConfig.getEnvironment().equals(KnownEnvironment.Production.getEnvironment()) ? "live.com" : "live-int.com";
            this._transport.openGetRequest(new URL(String.format(str, objArr)));
            this._transport.setUseCaches(true);
            responseStream = this._transport.getResponseStream();
            Bitmap image = BitmapFactory.decodeStream(responseStream);
            saveUserTileImage(image);
            this._newImageDownloaded = true;
            this._transport.closeConnection();
            if (responseStream == null) {
                return image;
            }
            try {
                responseStream.close();
                return image;
            } catch (IOException e) {
                Logger.error("Error closing response stream.", e);
                return image;
            }
        } catch (MalformedURLException e2) {
            Logger.error("Error downloading image from url.", e2);
            this._transport.closeConnection();
            if (responseStream != null) {
                try {
                    responseStream.close();
                } catch (IOException e3) {
                    Logger.error("Error closing response stream.", e3);
                }
            }
        } catch (NetworkException e4) {
            Logger.error("Error downloading image from url.", e4);
            this._transport.closeConnection();
            if (responseStream != null) {
                try {
                    responseStream.close();
                } catch (IOException e32) {
                    Logger.error("Error closing response stream.", e32);
                }
            }
        } catch (Throwable th) {
            this._transport.closeConnection();
            if (responseStream != null) {
                try {
                    responseStream.close();
                } catch (IOException e322) {
                    Logger.error("Error closing response stream.", e322);
                }
            }
        }
        return null;
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            HttpResponseCache httpResponseCache = HttpResponseCache.getInstalled();
            if (httpResponseCache != null) {
                ClientAnalytics.get().logEvent(ClientAnalytics.PerformanceCategory, ClientAnalytics.LoadProfileImage, httpResponseCache.getHitCount() == 0 ? "from network" : "from cache");
            }
            this._imageView.setImageBitmap(result);
            return;
        }
        File file = this._applicationContext.getFileStreamPath(this._userTileFileName);
        if (file.exists() && this._newImageDownloaded) {
            ClientAnalytics.get().logEvent(ClientAnalytics.PerformanceCategory, ClientAnalytics.LoadProfileImage, "deleted");
            file.delete();
            this._imageView.setImageDrawable(this._applicationContext.getResources().getDrawable(this._applicationContext.getResources().getIdentifier("msa_default_user_tile", "drawable", this._applicationContext.getPackageName())));
        }
    }

    Bitmap getSavedUserTileImage() {
        Bitmap savedUserTileImage = null;
        FileInputStream userTileInputStream = null;
        try {
            userTileInputStream = this._applicationContext.openFileInput(this._userTileFileName);
            savedUserTileImage = BitmapFactory.decodeStream(userTileInputStream);
            if (userTileInputStream != null) {
                try {
                    userTileInputStream.close();
                } catch (IOException e) {
                    Logger.error("Error closing file input stream.", e);
                }
            }
        } catch (FileNotFoundException e2) {
            if (userTileInputStream != null) {
                try {
                    userTileInputStream.close();
                } catch (IOException e3) {
                    Logger.error("Error closing file input stream.", e3);
                }
            }
        } catch (Throwable th) {
            if (userTileInputStream != null) {
                try {
                    userTileInputStream.close();
                } catch (IOException e32) {
                    Logger.error("Error closing file input stream.", e32);
                }
            }
        }
        return savedUserTileImage;
    }

    void saveUserTileImage(Bitmap image) {
        if (image != null) {
            FileOutputStream userTileOutputStream = null;
            try {
                userTileOutputStream = this._applicationContext.openFileOutput(this._userTileFileName, 0);
                image.compress(CompressFormat.PNG, 100, userTileOutputStream);
                if (userTileOutputStream != null) {
                    try {
                        userTileOutputStream.close();
                    } catch (IOException e) {
                        Logger.error("Error closing file output stream.", e);
                    }
                }
            } catch (FileNotFoundException e2) {
                Logger.error("Error saving user tile image.", e2);
                if (userTileOutputStream != null) {
                    try {
                        userTileOutputStream.close();
                    } catch (IOException e3) {
                        Logger.error("Error closing file output stream.", e3);
                    }
                }
            } catch (Throwable th) {
                if (userTileOutputStream != null) {
                    try {
                        userTileOutputStream.close();
                    } catch (IOException e32) {
                        Logger.error("Error closing file output stream.", e32);
                    }
                }
            }
        }
    }
}
