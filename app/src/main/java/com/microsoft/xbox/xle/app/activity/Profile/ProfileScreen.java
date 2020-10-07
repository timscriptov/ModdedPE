package com.microsoft.xbox.xle.app.activity.Profile;

import android.content.Context;
import android.util.AttributeSet;

import com.mcal.mcpelauncher.R;
import com.microsoft.xbox.telemetry.helpers.UTCPeopleHub;
import com.microsoft.xbox.xle.app.activity.ActivityBase;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ProfileScreen extends ActivityBase {
    public ProfileScreen() {
    }

    public ProfileScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        ProfileScreenViewModel psVM = new ProfileScreenViewModel(this);
        viewModel = psVM;
        UTCPeopleHub.trackPeopleHubView(getActivityName(), psVM.getXuid(), psVM.isMeProfile());
    }

    public void onCreateContentView() {
        setContentView(R.layout.profile_screen);
    }

    public String getActivityName() {
        return "PeopleHub Info";
    }
}
