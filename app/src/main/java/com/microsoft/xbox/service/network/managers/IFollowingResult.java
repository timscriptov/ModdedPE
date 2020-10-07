package com.microsoft.xbox.service.network.managers;

import java.util.ArrayList;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public interface IFollowingResult {

    public static class FollowingResult {
        public ArrayList<People> people;
        public int totalCount;
    }

    public static class People {
        public boolean isFavorite;
        public String xuid;
    }
}
