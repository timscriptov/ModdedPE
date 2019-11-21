package com.microsoft.xbox.idp.interop;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.microsoft.xbox.idp.interop.Interop.AuthFlowScreenStatus;
import com.microsoft.xbox.idp.interop.Interop.ErrorCallback;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

public final class XsapiUser {
    private static final String TAG = XsapiUser.class.getSimpleName();
    private static XsapiUser instance;
    private static final Object instanceLock = new Object();
    private final long id = create();
    private final UserImpl userImpl = new UserImpl(getUserImpl(this.id));

    public interface FinishSignInCallback extends VoidCallback {
    }

    private interface LongCallback extends ErrorCallback {
        void onSuccess(long j);
    }

    public interface SignInSilentlyCallback extends ErrorCallback {
        void onSuccess(SignInStatus signInStatus);
    }

    private interface SignInSilentlyCallbackInternal extends ErrorCallback {
        void onSuccess(int i);
    }

    public enum SignInStatus {
        SUCCESS(0),
        USER_INTERACTION_REQUIRED(1),
        USER_CANCEL(3);

        public final int id;

        private SignInStatus(int id2) {
            this.id = id2;
        }

        public static SignInStatus fromId(int id2) {
            switch (id2) {
                case 0:
                    return SUCCESS;
                case 1:
                    return USER_INTERACTION_REQUIRED;
                default:
                    return USER_CANCEL;
            }
        }
    }

    public interface SignOutCallback extends VoidCallback {
    }

    public interface StartSignInCallback extends VoidCallback {
    }

    public interface TokenAndSignatureCallback extends ErrorCallback {
        void onSuccess(TokenAndSignature tokenAndSignature);
    }

    private static class TokenAndSignatureCallbackWithResult implements TokenAndSignatureCallback {
        private int errorCode;
        private String errorMessage;
        private int httpStatusCode;
        private TokenAndSignature tokenAndSignature;

        private TokenAndSignatureCallbackWithResult() {
        }

        public void onSuccess(TokenAndSignature tokenAndSignature2) {
            this.tokenAndSignature = tokenAndSignature2;
        }

        public void onError(int httpStatusCode2, int errorCode2, String errorMessage2) {
            this.httpStatusCode = httpStatusCode2;
            this.errorCode = errorCode2;
            this.errorMessage = errorMessage2;
        }

        public TokenAndSignature getTokenAndSignature() {
            return this.tokenAndSignature;
        }

        public int getHttpStatusCode() {
            return this.httpStatusCode;
        }

        public int getErrorCode() {
            return this.errorCode;
        }

        public String getErrorMessage() {
            return this.errorMessage;
        }
    }

    public static class UserImpl implements Parcelable {
        public static final Creator<UserImpl> CREATOR = new Creator<UserImpl>() {
            public UserImpl createFromParcel(Parcel in) {
                return new UserImpl(in);
            }

            public UserImpl[] newArray(int size) {
                return new UserImpl[size];
            }
        };
        private final long id;

        public UserImpl(long id2) {
            this.id = id2;
        }

        protected UserImpl(Parcel in) {
            this.id = in.readLong();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.id);
        }

        private long getId() {
            return this.id;
        }

        public long getUserImplPtr() {
            return this.id;
        }
    }

    public interface VoidCallback extends ErrorCallback {
        void onSuccess();
    }

    private static native long create();

    private static native void delete(long j);

    private static native void finishSignIn(long j, FinishSignInCallback finishSignInCallback, int i, String str);

    private static native String getPrivileges(long j);

    private static native void getTokenAndSignature(long j, String str, String str2, String str3, String str4, LongCallback longCallback);

    private static native long getUserImpl(long j);

    private static native String getXuid(long j);

    private static native boolean isProd(long j);

    private static native boolean isSignedIn(long j);

    private static native void signInSilently(long j, SignInSilentlyCallbackInternal signInSilentlyCallbackInternal);

    private static native void signOut(long j, SignOutCallback signOutCallback);

    private static native void startSignIn(long j, StartSignInCallback startSignInCallback);

    public static XsapiUser getInstance() {
        if (instance == null) {
            synchronized (instanceLock) {
                if (instance == null) {
                    instance = new XsapiUser();
                }
            }
        }
        return instance;
    }

    private XsapiUser() {
    }

    public UserImpl getUserImpl() {
        return this.userImpl;
    }

    public void startSignIn(StartSignInCallback callback) {
        startSignIn(this.id, callback);
    }

    public void finishSignIn(FinishSignInCallback callback, AuthFlowScreenStatus authStatus, String cid) {
        finishSignIn(this.id, callback, authStatus.getId(), cid);
    }

    public void signInSilently(final SignInSilentlyCallback callback) {
        signInSilently(this.id, new SignInSilentlyCallbackInternal() {
            public void onSuccess(int signInStatus) {
                callback.onSuccess(SignInStatus.fromId(signInStatus));
            }

            public void onError(int httpStatusCode, int errorCode, String errorMessage) {
                callback.onError(httpStatusCode, errorCode, errorMessage);
            }
        });
    }

    public void signOut(SignOutCallback callback) {
        signOut(this.id, callback);
    }

    public void getTokenAndSignature(String httpMethod, String url, String headers, TokenAndSignatureCallback callback) {
        getTokenAndSignature(httpMethod, url, headers, null, callback);
    }

    public void getTokenAndSignature(String httpMethod, String url, String headers, String requestBody, final TokenAndSignatureCallback callback) {
        getTokenAndSignature(this.id, httpMethod, url, headers, requestBody, new LongCallback() {
            public void onSuccess(long id) {
                callback.onSuccess(new TokenAndSignature(id));
            }

            public void onError(int httpStatusCode, int errorCode, String errorMessage) {
                callback.onError(httpStatusCode, errorCode, errorMessage);
            }
        });
    }

    public TokenAndSignature getTokenAndSignatureSync(String httpMethod, String url, String headers) {
        return getTokenAndSignatureSync(httpMethod, url, headers, null);
    }

    public TokenAndSignature getTokenAndSignatureSync(String httpMethod, String url, String headers, String requestBody) {
        final CountDownLatch latch = new CountDownLatch(1);
        TokenAndSignatureCallbackWithResult callback = new TokenAndSignatureCallbackWithResult() {
            public void onSuccess(TokenAndSignature tokenAndSignature) {
                super.onSuccess(tokenAndSignature);
                latch.countDown();
            }

            public void onError(int httpStatusCode, int errorCode, String errorMessage) {
                super.onError(httpStatusCode, errorCode, errorMessage);
                latch.countDown();
            }
        };
        getTokenAndSignature(httpMethod, url, headers, requestBody, callback);
        try {
            latch.await();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return callback.getTokenAndSignature();
    }

    public boolean isProd() {
        return isProd(this.id);
    }

    public boolean isSignedIn() {
        return isSignedIn(this.id);
    }

    public String getXuid() {
        return getXuid(this.id);
    }

    public int[] getPrivileges() {
        return convertPrivileges(getPrivileges(this.id));
    }

    public void finalize() throws Throwable {
        delete(this.id);
        super.finalize();
    }

    public static int[] convertPrivileges(String privileges) {
        String[] split;
        LinkedList<Integer> list = new LinkedList<>();
        for (String s : privileges.split(" ")) {
            try {
                list.add(Integer.valueOf(Integer.parseInt(s)));
            } catch (NumberFormatException e) {
                Log.d(TAG, "Cannot convert " + s + " to integer");
            }
        }
        int[] buf = new int[list.size()];
        int idx = -1;
        Iterator it = list.iterator();
        while (it.hasNext()) {
            idx++;
            buf[idx] = ((Integer) it.next()).intValue();
        }
        return buf;
    }
}
