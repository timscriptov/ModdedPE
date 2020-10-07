package com.microsoft.xbox.service.network.managers;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public final class MutedListResultContainer {

    public static class MutedListResult {
        public ArrayList<MutedUser> users = new ArrayList<>();

        public void add(String xuid) {
            users.add(new MutedUser(xuid));
        }

        public MutedUser remove(String xuid) {
            Iterator<MutedUser> it = users.iterator();
            while (it.hasNext()) {
                MutedUser user = it.next();
                if (user.xuid.equalsIgnoreCase(xuid)) {
                    users.remove(user);
                    return user;
                }
            }
            return null;
        }

        public boolean contains(String xuid) {
            Iterator<MutedUser> it = users.iterator();
            while (it.hasNext()) {
                if (it.next().xuid.equalsIgnoreCase(xuid)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class MutedUser {
        public String xuid;

        public MutedUser(String xuid2) {
            xuid = xuid2;
        }
    }
}
