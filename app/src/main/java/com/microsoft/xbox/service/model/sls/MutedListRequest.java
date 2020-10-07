package com.microsoft.xbox.service.model.sls;

import com.microsoft.xbox.toolkit.GsonUtil;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class MutedListRequest {
    public long xuid;

    public MutedListRequest(long xuid2) {
        xuid = xuid2;
    }

    public static String getNeverListRequestBody(MutedListRequest mutedListRequest) {
        return GsonUtil.toJsonString(mutedListRequest);
    }
}
