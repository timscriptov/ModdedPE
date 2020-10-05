package com.microsoft.xbox.idp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.idp.compat.BaseFragment;

import org.jetbrains.annotations.NotNull;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ErrorButtonsFragment extends BaseFragment implements View.OnClickListener {
    public static final String ARG_LEFT_ERROR_BUTTON_STRING_ID = "ARG_LEFT_ERROR_BUTTON_STRING_ID";
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onClickedLeftButton() {
        }

        public void onClickedRightButton() {
        }
    };
    private Callbacks callbacks = NO_OP_CALLBACKS;

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (Callbacks) activity;
    }

    public void onDetach() {
        super.onDetach();
        callbacks = NO_OP_CALLBACKS;
    }

    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.xbid_fragment_error_buttons, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppCompatButton leftButton = view.findViewById(R.id.xbid_error_left_button);
        leftButton.setOnClickListener(this);
        view.findViewById(R.id.xbid_error_right_button).setOnClickListener(this);
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_LEFT_ERROR_BUTTON_STRING_ID)) {
            leftButton.setText(args.getInt(ARG_LEFT_ERROR_BUTTON_STRING_ID));
        }
    }

    public void onClick(@NotNull View v) {
        int id = v.getId();
        if (id == R.id.xbid_error_left_button) {
            callbacks.onClickedLeftButton();
        } else if (id == R.id.xbid_error_right_button) {
            callbacks.onClickedRightButton();
        }
    }

    public interface Callbacks {
        void onClickedLeftButton();

        void onClickedRightButton();
    }
}
