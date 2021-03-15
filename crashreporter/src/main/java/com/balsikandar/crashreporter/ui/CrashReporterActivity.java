package com.balsikandar.crashreporter.ui;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.balsikandar.crashreporter.CrashReporter;
import com.balsikandar.crashreporter.R;
import com.balsikandar.crashreporter.adapter.MainPagerAdapter;
import com.balsikandar.crashreporter.utils.Constants;
import com.balsikandar.crashreporter.utils.CrashUtil;
import com.balsikandar.crashreporter.utils.FileUtils;
import com.balsikandar.crashreporter.utils.SimplePageChangeListener;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class CrashReporterActivity extends AppCompatActivity {

    private MainPagerAdapter mainPagerAdapter;
    private int selectedTabPosition = 0;

    //region activity callbacks
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.log_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == R.id.delete_crash_logs) {
            clearCrashLog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_reporter_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.crash_reporter));
        toolbar.setSubtitle(getApplicationName());
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    //endregion

    private void clearCrashLog() {
        new Thread(() -> {
            String crashReportPath = TextUtils.isEmpty(CrashReporter.getCrashReportPath()) ?
                    CrashUtil.getDefaultPath() : CrashReporter.getCrashReportPath();

            File[] logs = new File(crashReportPath).listFiles();
            for (File file : logs) {
                FileUtils.delete(file);
            }
            runOnUiThread(() -> mainPagerAdapter.clearLogs());
        }).start();
    }

    private void setupViewPager(@NotNull ViewPager viewPager) {
        String[] titles = {getString(R.string.crashes), getString(R.string.exceptions)};
        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), titles);
        viewPager.setAdapter(mainPagerAdapter);

        viewPager.addOnPageChangeListener(new SimplePageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                selectedTabPosition = position;
            }
        });

        Intent intent = getIntent();
        if (intent != null && !intent.getBooleanExtra(Constants.LANDING, false)) {
            selectedTabPosition = 1;
        }
        viewPager.setCurrentItem(selectedTabPosition);
    }

    private String getApplicationName() {
        ApplicationInfo applicationInfo = getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : getString(stringId);
    }
}