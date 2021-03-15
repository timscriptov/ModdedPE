package com.balsikandar.crashreporter.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.balsikandar.crashreporter.R;
import com.balsikandar.crashreporter.ui.LogMessageActivity;
import com.balsikandar.crashreporter.utils.FileUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by bali on 10/08/17.
 */

public class CrashLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private ArrayList<File> crashFileList;

    public CrashLogAdapter(Context context, ArrayList<File> allCrashLogs) {
        this.context = context;
        crashFileList = allCrashLogs;
    }

    @Override
    public RecyclerView.@NotNull ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_item, null);
        return new CrashLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CrashLogViewHolder) holder).setUpViewHolder(context, crashFileList.get(position));
    }

    @Override
    public int getItemCount() {
        return crashFileList.size();
    }


    public void updateList(ArrayList<File> allCrashLogs) {
        crashFileList = allCrashLogs;
        notifyDataSetChanged();
    }


    private static class CrashLogViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView textViewMsg;
        private final AppCompatTextView messageLogTime;

        CrashLogViewHolder(View itemView) {
            super(itemView);
            messageLogTime = itemView.findViewById(R.id.messageLogTime);
            textViewMsg = itemView.findViewById(R.id.textViewMsg);
        }

        void setUpViewHolder(final Context context, final @NotNull File file) {
            final String filePath = file.getAbsolutePath();
            messageLogTime.setText(file.getName().replaceAll("[a-zA-Z_.]", ""));
            textViewMsg.setText(FileUtils.readFirstLineFromFile(new File(filePath)));

            textViewMsg.setOnClickListener(v -> {
                Intent intent = new Intent(context, LogMessageActivity.class);
                intent.putExtra("LogMessage", filePath);
                context.startActivity(intent);
            });
        }
    }
}