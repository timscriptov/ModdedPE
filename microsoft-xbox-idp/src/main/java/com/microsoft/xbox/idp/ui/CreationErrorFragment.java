package com.microsoft.xbox.idp.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.model.Const;

public class CreationErrorFragment extends BaseFragment {
    public static final String TAG = CreationErrorFragment.class.getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xbid_fragment_error_creation, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UiUtil.ensureClickableSpanOnUnderlineSpan((TextView) view.findViewById(R.id.xbid_error_message), R.string.xbid_creation_error_android, new ClickableSpan() {
            public void onClick(View widget) {
                Log.d(CreationErrorFragment.TAG, "onClick");
                try {
                    CreationErrorFragment.this.startActivity(new Intent("android.intent.action.VIEW", Const.URL_XBOX_COM));
                } catch (ActivityNotFoundException e) {
                    Log.e(CreationErrorFragment.TAG, e.getMessage());
                }
            }
        });
    }
}
