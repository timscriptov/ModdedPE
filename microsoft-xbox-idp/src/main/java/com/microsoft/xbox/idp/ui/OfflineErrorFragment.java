package com.microsoft.xbox.idp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;

public class OfflineErrorFragment extends BaseFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xbid_fragment_error_offline, container, false);
    }
}
