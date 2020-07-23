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
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;

import com.mcal.mcpelauncher.R;

import org.jetbrains.annotations.NotNull;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class NModFilePickerActivity extends BaseActivity {
    public static final int REQUEST_PICK_FILE = 2;
    public final static String TAG_FILE_PATH = "file_path";
    private static final int MSG_SELECT = 1;
    private File currentPath;
    private ArrayList<File> filesInCurrentPath;
    private SelectHandler mSelectHandler = new SelectHandler();

    public static void startThisActivity(Activity context, @NotNull File path) {
        startThisActivity(context, path.getPath());
    }

    public static void startThisActivity(Activity context, String path) {
        Intent intent = new Intent(context, NModFilePickerActivity.class);
        Bundle extras = new Bundle();
        extras.putString(TAG_FILE_PATH, path);
        intent.putExtras(extras);
        context.startActivityForResult(intent, REQUEST_PICK_FILE);
    }

    public static void startThisActivity(Activity context) {
        startThisActivity(context, Environment.getExternalStorageDirectory().getPath());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nmod_picker_file);

        setResult(RESULT_CANCELED, new Intent());
        setActionBarButtonCloseRight();

        String pathString = null;
        try {
            pathString = getIntent().getExtras().getString(TAG_FILE_PATH);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (pathString == null)
            pathString = Environment.getExternalStorageDirectory().getAbsolutePath();
        currentPath = new File(pathString);

        openDirectory(currentPath);
    }

    private boolean isValidParent() {
        return currentPath.getParentFile() != null && currentPath.getParentFile().exists() && currentPath.getParentFile().listFiles() != null && currentPath.getParentFile().listFiles().length > 0;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
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
        filesInCurrentPath = new ArrayList<File>();

        File[] unmanagedFilesInCurrentDirectory = currentPath.listFiles();
        if (unmanagedFilesInCurrentDirectory != null) {
            for (File fileItem : unmanagedFilesInCurrentDirectory) {
                if (fileItem.isDirectory())
                    filesInCurrentPath.add(fileItem);
            }
            for (File fileItem : unmanagedFilesInCurrentDirectory) {
                if (!fileItem.isDirectory())
                    filesInCurrentPath.add(fileItem);
            }
        }
        ListView fileListView = findViewById(R.id.nmod_picker_file_list_view);
        fileListView.setAdapter(new FileAdapter());
    }

    private void selectFile(@NotNull File file) {
        Intent data = new Intent();
        Bundle extras = new Bundle();
        extras.putString(TAG_FILE_PATH, file.getPath());
        data.putExtras(extras);
        setResult(RESULT_OK, data);
        finish();
    }

    private class FileAdapter extends BaseAdapter {
        public FileAdapter() {
            Collections.sort(filesInCurrentPath, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() & o2.isFile()) {
                        return -1;
                    } else if (o1.isFile() & o2.isDirectory()) {
                        return 1;
                    } else return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
        }

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

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View p1) {
                            select(null);
                        }
                    });
                } else {
                    final File currentCardViewFile = filesInCurrentPath.get(--p1);
                    AppCompatImageView fileImage = cardView.findViewById(R.id.nmod_picker_item_card_view_image_view);

                    if (currentCardViewFile.isDirectory())
                        fileImage.setImageResource(R.drawable.ic_folder);
                    else if (currentCardViewFile.getName().endsWith(".nmod")) {
                        byte[] bytes = ZipUtil.unpackEntry(currentCardViewFile, "icon.png");
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        fileImage.setImageBitmap(bmp);
                    } else fileImage.setImageResource(R.drawable.ic_file);

                    AppCompatTextView textFileName = cardView.findViewById(R.id.nmod_picker_item_card_view_text_name);
                    textFileName.setText(currentCardViewFile.getName());

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View p1) {
                            select(currentCardViewFile);
                        }
                    });
                }
            } else {
                if (filesInCurrentPath.size() > 0) {
                    final File currentCardViewFile = filesInCurrentPath.get(p1);
                    AppCompatImageView fileImage = cardView.findViewById(R.id.nmod_picker_item_card_view_image_view);
                    if (currentCardViewFile.isDirectory())
                        fileImage.setImageResource(R.drawable.ic_folder);
                    else
                        fileImage.setImageResource(R.drawable.ic_file);

                    AppCompatTextView textFileName = cardView.findViewById(R.id.nmod_picker_item_card_view_text_name);
                    textFileName.setText(currentCardViewFile.getName());

                    cardView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View p1) {
                            select(currentCardViewFile);
                        }
                    });
                } else {
                    AppCompatImageView fileImage = cardView.findViewById(R.id.nmod_picker_item_card_view_image_view);
                    fileImage.setImageResource(R.drawable.ic_folder_outline);

                    AppCompatTextView textFileName = cardView.findViewById(R.id.nmod_picker_item_card_view_text_name);
                    textFileName.setText(android.R.string.cancel);

                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View p1) {
                            openDirectory(Environment.getExternalStorageDirectory());
                        }
                    });
                }
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
                else
                    selectFile(file);
            }
        }
    }
}