package com.microsoft.xbox.idp.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.idp.compat.BaseActivity;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.model.Const;
import com.microsoft.xbox.telemetry.helpers.UTCError;
import com.microsoft.xbox.telemetry.helpers.UTCPageView;

import org.jetbrains.annotations.Nullable;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ErrorActivity extends BaseActivity implements HeaderFragment.Callbacks, ErrorButtonsFragment.Callbacks {
    public static final String ARG_ERROR_TYPE = "ARG_ERROR_TYPE";
    public static final String ARG_GAMER_TAG = "ARG_GAMER_TAG";
    public static final int RESULT_TRY_AGAIN = 1;
    private static final String TAG = ErrorActivity.class.getSimpleName();
    private int activityResult = 0;

    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xbid_activity_error);
        Intent intent = getIntent();
        UiUtil.ensureHeaderFragment(this, R.id.xbid_header_fragment, intent.getExtras());
        if (intent.hasExtra(ARG_ERROR_TYPE)) {
            ErrorScreen errorScreen = ErrorScreen.fromId(intent.getIntExtra(ARG_ERROR_TYPE, -1));
            if (errorScreen != null) {
                UiUtil.ensureErrorFragment(this, errorScreen);
                UiUtil.ensureErrorButtonsFragment(this, errorScreen);
                UTCError.trackPageView(errorScreen, getTitle());
                return;
            }
            Log.e(TAG, "Incorrect error type was provided");
            return;
        }
        Log.e(TAG, "No error type was provided");
    }

    public void onClickCloseHeader() {
        Log.d(TAG, "onClickCloseHeader");
        UTCError.trackClose(ErrorScreen.fromId(getIntent().getIntExtra(ARG_ERROR_TYPE, -1)), getTitle());
        finish();
    }

    public void onClickedLeftButton() {
        Log.d(TAG, "onClickedLeftButton");
        ErrorScreen errorScreen = ErrorScreen.fromId(getIntent().getIntExtra(ARG_ERROR_TYPE, -1));
        if (errorScreen == ErrorScreen.BAN) {
            UTCError.trackGoToEnforcement(errorScreen, getTitle());
            try {
                startActivity(new Intent("android.intent.action.VIEW", Const.URL_ENFORCEMENT_XBOX_COM));
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            UTCError.trackTryAgain(errorScreen, getTitle());
            activityResult = 1;
            setResult(activityResult);
            finish();
        }
    }

    public void onClickedRightButton() {
        Log.d(TAG, "onClickedRightButton");
        UTCError.trackRightButton(ErrorScreen.fromId(getIntent().getIntExtra(ARG_ERROR_TYPE, -1)), getTitle());
        finish();
    }

    public void finish() {
        UTCPageView.removePage();
        super.finish();
    }

    public enum ErrorScreen {
        BAN(Interop.ErrorType.BAN, BanErrorFragment.class, R.string.xbid_more_info),
        CREATION(Interop.ErrorType.CREATION, CreationErrorFragment.class, R.string.xbid_try_again),
        OFFLINE(Interop.ErrorType.OFFLINE, OfflineErrorFragment.class, R.string.xbid_try_again),
        CATCHALL(Interop.ErrorType.CATCHALL, CatchAllErrorFragment.class, R.string.xbid_try_again);

        public final Class<? extends BaseFragment> errorFragmentClass;
        public final int leftButtonTextId;
        public final Interop.ErrorType type;

        private ErrorScreen(Interop.ErrorType type2, Class<? extends BaseFragment> errorFragmentClass2, int leftButtonId) {
            type = type2;
            errorFragmentClass = errorFragmentClass2;
            leftButtonTextId = leftButtonId;
        }

        @Nullable
        public static ErrorScreen fromId(int id) {
            for (ErrorScreen t : values()) {
                if (t.type.getId() == id) {
                    return t;
                }
            }
            return null;
        }
    }
}