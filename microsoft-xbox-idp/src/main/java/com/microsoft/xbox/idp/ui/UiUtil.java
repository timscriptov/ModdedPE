package com.microsoft.xbox.idp.ui;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseActivity;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;

public final class UiUtil {
    private static final String TAG = UiUtil.class.getSimpleName();

    public static boolean ensureHeaderFragment(BaseActivity activity, int fragmentId, Bundle args) {
        return ensureFragment(HeaderFragment.class, activity, fragmentId, args);
    }

    public static boolean ensureWelcomeFragment(BaseActivity activity, int fragmentId, boolean firstTime, Bundle args) {
        if (firstTime) {
            return ensureFragment(IntroducingFragment.class, activity, fragmentId, args);
        }
        return ensureFragment(WelcomeFragment.class, activity, fragmentId, args);
    }

    public static boolean ensureGamerTagCreationFragment(BaseActivity activity, int fragmentId, Bundle args) {
        return ensureFragment(SignUpFragment.class, activity, fragmentId, args);
    }

    public static boolean ensureErrorFragment(BaseActivity activity, ErrorScreen errorScreen) {
        if (!activity.hasFragment(R.id.xbid_body_fragment)) {
            return ensureFragment(errorScreen.errorFragmentClass, activity, R.id.xbid_body_fragment, activity.getIntent().getExtras());
        }
        return false;
    }

    public static boolean ensureErrorButtonsFragment(BaseActivity activity, ErrorScreen errorScreen) {
        if (activity.hasFragment(R.id.xbid_error_buttons)) {
            return false;
        }
        Bundle args = new Bundle();
        args.putInt(ErrorButtonsFragment.ARG_LEFT_ERROR_BUTTON_STRING_ID, errorScreen.leftButtonTextId);
        return ensureFragment(ErrorButtonsFragment.class, activity, R.id.xbid_error_buttons, args);
    }

    public static void ensureClickableSpanOnUnderlineSpan(TextView text, int stringId, ClickableSpan clickableSpan) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(Html.fromHtml(text.getResources().getString(stringId)));
        UnderlineSpan[] spans = (UnderlineSpan[]) ssb.getSpans(0, ssb.length(), UnderlineSpan.class);
        if (spans != null && spans.length > 0) {
            UnderlineSpan underlineSpan = spans[0];
            ssb.setSpan(clickableSpan, ssb.getSpanStart(underlineSpan), ssb.getSpanEnd(underlineSpan), 33);
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }
        text.setText(ssb);
    }

    public static boolean canScroll(ScrollView scrollView) {
        View scrollChild = scrollView.getChildAt(0);
        if (scrollChild == null) {
            return false;
        }
        MarginLayoutParams lp = (MarginLayoutParams) scrollChild.getLayoutParams();
        if (scrollView.getHeight() < lp.topMargin + scrollChild.getHeight() + lp.bottomMargin) {
            return true;
        }
        return false;
    }

    private static boolean ensureFragment(Class<? extends BaseFragment> cls, BaseActivity activity, int fragmentId, Bundle args) {
        if (!activity.hasFragment(fragmentId)) {
            try {
                BaseFragment fragment = (BaseFragment) cls.newInstance();
                fragment.setArguments(args);
                activity.addFragment(fragmentId, fragment);
                return true;
            } catch (InstantiationException e) {
                Log.e(TAG, e.getMessage());
            } catch (IllegalAccessException e2) {
                Log.e(TAG, e2.getMessage());
            }
        }
        return false;
    }
}
