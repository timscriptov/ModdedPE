package com.mojang.minecraftpe;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.mcal.mcpelauncher.R;

public class FloatButton extends PopupWindow {
    private final Context mContext;

    public FloatButton(Context ctx) {
        mContext = ctx;
        init();
    }

    public void showHoverMenu() {
        ListView optionsListView = new ListView(mContext);
        String[] values = {"AlertDialog", "Toast"};
        optionsListView.setAdapter(new ArrayAdapter<>(mContext,
                android.R.layout.simple_list_item_1, android.R.id.text1, values));

        optionsListView.setOnItemClickListener((parent, view, position, id) -> {
            TextView textView = (TextView) view;
            String text = textView.getText().toString();

            if (text.equals("AlertDialog")) {
                new AlertDialog.Builder(mContext)
                        .setTitle("About")
                        .setMessage("ModdedPE - open source Minecraft launcher")
                        .show();
            } else if(text.equals("Toast")) {
                Toast.makeText(mContext, "ModdedPE - open source Minecraft launcher", Toast.LENGTH_SHORT).show();
            }
        });

        new AlertDialog.Builder(mContext).setTitle("Menu")
                .setView(optionsListView).show();

    }

    public void init() {
        // Create layout and the button.
        LinearLayout layout = new LinearLayout(mContext);
        AppCompatButton button = new AppCompatButton(mContext);
        button.setBackgroundResource(R.mipmap.ic_launcher_round);
        button.setOnClickListener(v -> showHoverMenu());
        layout.addView(button);
        setContentView(layout);

        // Set dimensions.
        setWidth(128);
        setHeight(128);
    }
}