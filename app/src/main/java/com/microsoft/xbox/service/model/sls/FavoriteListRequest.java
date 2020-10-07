package com.microsoft.xbox.service.model.sls;

import com.microsoft.xbox.toolkit.GsonUtil;

import java.util.ArrayList;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class FavoriteListRequest {
    public ArrayList<String> xuids;

    public FavoriteListRequest(ArrayList<String> userIds) {
        xuids = userIds;
    }

    public static String getFavoriteListRequestBody(FavoriteListRequest favoriteListRequest) {
        return GsonUtil.toJsonString(favoriteListRequest);
    }
}
