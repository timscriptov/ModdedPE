package com.microsoft.onlineid.internal.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Resources;

public class BaseScreenFragment extends Fragment {
    private ProgressView _progress;

    public enum ArgumentsKey {
        Header,
        Body
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Resources resources = new Resources(getActivity().getApplicationContext());
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(resources.getLayout("base_screen"), container, false);
        this._progress = (ProgressView) view.findViewById(resources.getId("baseScreenProgressView"));
        TextView header = (TextView) view.findViewById(resources.getId("baseScreenHeader"));
        TextView body = (TextView) view.findViewById(resources.getId("baseScreenBody"));
        Bundle arguments = getArguments();
        String headerArgumentKey = ArgumentsKey.Header.name();
        String bodyArgumentKey = ArgumentsKey.Body.name();
        Objects.verifyArgumentNotNull(arguments.getString(headerArgumentKey), headerArgumentKey);
        Objects.verifyArgumentNotNull(arguments.getString(bodyArgumentKey), bodyArgumentKey);
        header.setText(arguments.getString(headerArgumentKey));
        body.setText(arguments.getString(bodyArgumentKey));
        return view;
    }

    protected void showProgressViewAnimation() {
        this._progress.setVisibility(0);
        this._progress.startAnimation();
    }

    protected void stopProgressAnimation() {
        this._progress.stopAnimation();
        this._progress.setVisibility(8);
    }

    public static <T extends BaseScreenFragment> T buildWithBaseScreen(String header, String body, Class<T> genericClass) {
        Bundle baseScreenArguments = new Bundle();
        try {
            BaseScreenFragment fragment = (BaseScreenFragment) genericClass.newInstance();
            baseScreenArguments.putString(ArgumentsKey.Header.name(), header);
            baseScreenArguments.putString(ArgumentsKey.Body.name(), body);
            fragment.setArguments(baseScreenArguments);
            return fragment;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e2) {
            throw new RuntimeException(e2);
        }
    }
}
