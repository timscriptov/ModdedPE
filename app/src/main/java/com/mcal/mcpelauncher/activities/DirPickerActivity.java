/*
 * Copyright (C) 2018-2020 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.mcpelauncher.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;

import com.mcal.mcpelauncher.R;
import com.mcal.mcpelauncher.data.Constants;
import com.mcal.mcpelauncher.utils.ScopedStorage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class DirPickerActivity extends BaseActivity {
    public static final int REQUEST_PICK_DIR = 3;
    public final static String TAG_DIR_PATH = "dir_path";
    private static final int MSG_SELECT = 1;
    private File currentPath;
    private ArrayList<File> filesInCurrentPath;
    private SelectHandler mSelectHandler = new SelectHandler();

    public static void startThisActivity(AppCompatActivity context, @NotNull File path) {
        startThisActivity(context, path.getPath());
    }

    public static void startThisActivity(AppCompatActivity context, String path) {
        Intent intent = new Intent(context, DirPickerActivity.class);
        Bundle extras = new Bundle();
        extras.putString(TAG_DIR_PATH, path);
        intent.putExtras(extras);
        context.startActivityForResult(intent, REQUEST_PICK_DIR);
    }

    public static void startThisActivity(AppCompatActivity context) {
        startThisActivity(context, ScopedStorage.getStorageDirectory().getPath());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.moddedpe_dir_picker);

        findViewById(R.id.moddedpe_dir_picker_fab_reset).setOnClickListener(p1 -> onResetClicked());
        findViewById(R.id.moddedpe_dir_picker_fab_select).setOnClickListener(p1 -> onSelectThisClicked());

        setResult(RESULT_CANCELED, new Intent());
        setActionBarButtonCloseRight();

        String pathString = null;
        try {
            pathString = getIntent().getExtras().getString(TAG_DIR_PATH);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (pathString == null)
            pathString = ScopedStorage.getStorageDirectory().getAbsolutePath();
        currentPath = new File(pathString);

        openDirectory(currentPath);
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private boolean isValidParent() {
        return currentPath.getParentFile() != null && currentPath.getParentFile().exists() && currentPath.getParentFile().listFiles() != null && currentPath.getParentFile().listFiles().length > 0;
    }

    private void onSelectThisClicked() {
        Intent data = new Intent();
        Bundle extras = new Bundle();
        extras.putString(TAG_DIR_PATH, currentPath.getAbsolutePath());
        data.putExtras(extras);
        setResult(RESULT_OK, data);
        finish();
    }

    private void onResetClicked() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        dialog.setTitle(R.string.dir_picker_reset_warning_title);
        dialog.setMessage(R.string.dir_picker_reset_warning_message);
        dialog.setPositiveButton(android.R.string.ok, (p1, p2) -> {
            p1.dismiss();
            Intent data = new Intent();
            Bundle extras = new Bundle();
            extras.putString(TAG_DIR_PATH, Constants.STRING_VALUE_DEFAULT);
            data.putExtras(extras);
            setResult(RESULT_OK, data);
            finish();
        });
        dialog.setNegativeButton(android.R.string.cancel, (p1, p2) -> p1.dismiss());
        dialog.show();
    }

    private void select(File file_arg) {
        final File file = file_arg;
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(450);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message mMessage = new Message();
                mMessage.what = MSG_SELECT;
                mMessage.obj = file;
                mSelectHandler.sendMessage(mMessage);
            }
        }.start();
    }

    private void openDirectory(File directory) {
        currentPath = directory;
        filesInCurrentPath = new ArrayList<>();

        File[] unmanagedFilesInCurrentDirectory = currentPath.listFiles();
        if (unmanagedFilesInCurrentDirectory != null) {
            for (File fileItem : unmanagedFilesInCurrentDirectory) {
                if (fileItem.isDirectory())
                    filesInCurrentPath.add(fileItem);
            }
        }

        Collections.sort(filesInCurrentPath, (o1, o2) -> {
            if (o1.isDirectory() & o2.isFile()) {
                return -1;
            } else if (o1.isFile() & o2.isDirectory()) {
                return 1;
            } else return o1.getName().compareToIgnoreCase(o2.getName());
        });

        ListView fileListView = findViewById(R.id.picker_dir_list_view);
        fileListView.setAdapter(new FileAdapter());
    }

    private class FileAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (isValidParent())
                return filesInCurrentPath.size() + 1;
            if (filesInCurrentPath.size() == 0)
                return 1;
            return filesInCurrentPath.size();
        }

        @Override
        public Object getItem(int p1) {
            return p1;
        }

        @Override
        public long getItemId(int p1) {
            return p1;
        }

        @Override
        public View getView(int p1, View p2, ViewGroup p3) {
            @SuppressLint("ViewHolder") CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.nmod_picker_file_item, null);

            if (!currentPath.getPath().endsWith(File.separator)) {
                if (p1 == 0) {
                    AppCompatImageView fileImage = cardView.findViewById(R.id.nmod_picker_item_card_view_image_view);
                    fileImage.setImageResource(R.drawable.ic_folder_outline);

                    AppCompatTextView textFileName = cardView.findViewById(R.id.nmod_picker_item_card_view_text_name);
                    textFileName.setText("...");

                    cardView.setOnClickListener(p11 -> select(null));
                } else {
                    final File currentCardViewFile = filesInCurrentPath.get(--p1);
                    AppCompatImageView fileImage = cardView.findViewById(R.id.nmod_picker_item_card_view_image_view);
                    fileImage.setImageResource(R.drawable.ic_folder);

                    AppCompatTextView textFileName = cardView.findViewById(R.id.nmod_picker_item_card_view_text_name);
                    textFileName.setText(currentCardViewFile.getName());

                    cardView.setOnClickListener(p112 -> select(currentCardViewFile));
                }
            } else {
                final File currentCardViewFile = filesInCurrentPath.get(p1);
                AppCompatImageView fileImage = cardView.findViewById(R.id.nmod_picker_item_card_view_image_view);
                fileImage.setImageResource(R.drawable.ic_folder);
                AppCompatTextView textFileName = cardView.findViewById(R.id.nmod_picker_item_card_view_text_name);
                textFileName.setText(currentCardViewFile.getName());

                cardView.setOnClickListener(p113 -> select(currentCardViewFile));
            }
            return cardView;
        }
    }

    @SuppressLint("HandlerLeak")
    private class SelectHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_SELECT) {
                File file = (File) msg.obj;
                if (file == null) {
                    File lastFile = currentPath.getParentFile();
                    if (isValidParent())
                        openDirectory(lastFile);
                } else if (file.isDirectory())
                    openDirectory(file);
            }
        }
    }
}