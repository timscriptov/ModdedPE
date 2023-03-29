package com.mcal.mcpelauncher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import androidx.preference.PreferenceViewHolder;

import com.mcal.mcpelauncher.R;

public class SwitchPreference extends androidx.preference.SwitchPreference implements
        android.widget.CompoundButton.OnCheckedChangeListener {
    public Switch content = null;
    protected OnCheckedChangeListener listener = null;

    public SwitchPreference(Context context) {
        super(context);
        setWidgetLayoutResource(R.layout.switch_preference);
    }

    public SwitchPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.switch_preference);
    }

    public SwitchPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setWidgetLayoutResource(R.layout.switch_preference);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        content = (Switch) holder.findViewById(R.id.switch_widget);
        if (content != null) {
            content.setChecked(getPersistedBoolean(false));
            //content.setOnCheckedChangeListener(this);
            content.setOnCheckedChangeListener(null);
        } else {
            System.err.println("SwitchPreference Switch is null");
        }
    }

    public void setListener(OnCheckedChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        persistBoolean(isChecked);
        if (listener != null) {
            listener.onCheckedChange(content);
        }
    }

    public interface OnCheckedChangeListener {
        void onCheckedChange(Switch data);
    }
}