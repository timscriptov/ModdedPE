package com.microsoft.xbox.service.network.managers;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public final class NeverListResultContainer {

    public static class NeverListResult {
        public ArrayList<NeverUser> users = new ArrayList<>();

        public void add(String xuid) {
            users.add(new NeverUser(xuid));
        }

        public NeverUser remove(String xuid) {
            Iterator<NeverUser> it = users.iterator();
            while (it.hasNext()) {
                NeverUser user = it.next();
                if (user.xuid.equalsIgnoreCase(xuid)) {
                    users.remove(user);
                    return user;
                }
            }
            return null;
        }

        public boolean contains(String xuid) {
            Iterator<NeverUser> it = users.iterator();
            while (it.hasNext()) {
                if (it.next().xuid.equalsIgnoreCase(xuid)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class NeverUser {
        public String xuid;

        public NeverUser(String xuid2) {
            xuid = xuid2;
        }
    }
}
