package com.microsoft.xbox.idp.ui;

import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.idp.compat.BaseActivity;
import com.microsoft.xbox.idp.compat.BaseFragment;

import org.jetbrains.annotations.NotNull;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public final class UiUtil {
    private static final String TAG = "UiUtil";

    public static boolean ensureHeaderFragment(BaseActivity activity, int fragmentId, Bundle args) {
        return ensureFragment(HeaderFragment.class, activity, fragmentId, args);
    }

    public static boolean ensureErrorFragment(@NotNull BaseActivity activity, ErrorActivity.ErrorScreen errorScreen) {
        if (!activity.hasFragment(R.id.xbid_body_fragment)) {
            return ensureFragment(errorScreen.errorFragmentClass, activity, R.id.xbid_body_fragment, activity.getIntent().getExtras());
        }
        return false;
    }

    public static boolean ensureErrorButtonsFragment(@NotNull BaseActivity activity, ErrorActivity.ErrorScreen errorScreen) {
        if (activity.hasFragment(R.id.xbid_error_buttons)) {
            return false;
        }
        Bundle args = new Bundle();
        args.putInt(ErrorButtonsFragment.ARG_LEFT_ERROR_BUTTON_STRING_ID, errorScreen.leftButtonTextId);
        return ensureFragment(ErrorButtonsFragment.class, activity, R.id.xbid_error_buttons, args);
    }

    public static void ensureClickableSpanOnUnderlineSpan(@NotNull AppCompatTextView text, int stringId, ClickableSpan clickableSpan) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(Html.fromHtml(text.getResources().getString(stringId)));
        UnderlineSpan[] spans = ssb.getSpans(0, ssb.length(), UnderlineSpan.class);
        if (spans != null && spans.length > 0) {
            UnderlineSpan underlineSpan = spans[0];
            ssb.setSpan(clickableSpan, ssb.getSpanStart(underlineSpan), ssb.getSpanEnd(underlineSpan), 33);
            text.setMovementMethod(LinkMovementMethod.getInstance());
        }
        text.setText(ssb);
    }

    public static boolean canScroll(@NotNull ScrollView scrollView) {
        View scrollChild = scrollView.getChildAt(0);
        if (scrollChild == null) {
            return false;
        }
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) scrollChild.getLayoutParams();
        if (scrollView.getHeight() < lp.topMargin + scrollChild.getHeight() + lp.bottomMargin) {
            return true;
        }
        return false;
    }

    private static boolean ensureFragment(Class<? extends BaseFragment> cls, @NotNull BaseActivity activity, int fragmentId, Bundle args) {
        if (!activity.hasFragment(fragmentId)) {
            try {
                BaseFragment fragment = cls.newInstance();
                fragment.setArguments(args);
                activity.addFragment(fragmentId, fragment);
                return true;
            } catch (InstantiationException | IllegalAccessException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return false;
    }
}
