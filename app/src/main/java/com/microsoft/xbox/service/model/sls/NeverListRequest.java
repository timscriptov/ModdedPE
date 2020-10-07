package com.microsoft.xbox.service.model.sls;

import com.microsoft.xbox.toolkit.GsonUtil;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class NeverListRequest {
    public long xuid;

    public NeverListRequest(long xuid2) {
        xuid = xuid2;
    }

    public static String getNeverListRequestBody(NeverListRequest neverListRequest) {
        return GsonUtil.toJsonString(neverListRequest);
    }
}
