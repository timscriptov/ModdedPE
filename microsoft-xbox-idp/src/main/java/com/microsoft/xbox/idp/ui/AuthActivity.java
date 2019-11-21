package com.microsoft.xbox.idp.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseActivity;
import com.microsoft.xbox.idp.interop.Interop.AuthFlowScreenStatus;

public abstract class AuthActivity extends BaseActivity {
    public static final int RESULT_PROVIDER_ERROR = 2;

    public static int toActivityResult(AuthFlowScreenStatus status) {
        switch (status) {
            case NO_ERROR:
                return -1;
            case ERROR_USER_CANCEL:
                return 0;
            default:
                return 2;
        }
    }

    public static AuthFlowScreenStatus fromActivityResult(int result) {
        switch (result) {
            case -1:
                return AuthFlowScreenStatus.NO_ERROR;
            case 0:
                return AuthFlowScreenStatus.ERROR_USER_CANCEL;
            default:
                return AuthFlowScreenStatus.PROVIDER_ERROR;
        }
    }

    public void showBodyFragment(Fragment bodyFragment, Bundle args, boolean showHeader) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();
        if (!showHeader) {
            Fragment headerFragment = fragmentManager.findFragmentById(R.id.xbid_header_fragment);
            if (headerFragment != null) {
                tx.remove(headerFragment);
            }
        } else if (fragmentManager.findFragmentById(R.id.xbid_header_fragment) == null) {
            Fragment headerFragment2 = new HeaderFragment();
            headerFragment2.setArguments(args);
            tx.add(R.id.xbid_header_fragment, headerFragment2);
        }
        if (bodyFragment != null) {
            bodyFragment.setArguments(args);
        }
        tx.replace(R.id.xbid_body_fragment, bodyFragment);
        tx.commit();
    }

    public void finishCompat() {
        finish();
    }
}
