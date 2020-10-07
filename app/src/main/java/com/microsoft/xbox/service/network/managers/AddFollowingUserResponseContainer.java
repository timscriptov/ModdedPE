package com.microsoft.xbox.service.network.managers;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class AddFollowingUserResponseContainer {

    public static class AddFollowingUserResponse {
        public int code;
        public String description;
        private boolean mSuccess;

        public boolean getAddFollowingRequestStatus() {
            return mSuccess;
        }

        public void setAddFollowingRequestStatus(boolean success) {
            mSuccess = success;
        }
    }
}
