package com.microsoft.xbox.service.model.sls;

import com.microsoft.xbox.toolkit.GsonUtil;

import java.util.ArrayList;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class AddShareIdentityRequest {
    public ArrayList<String> xuids;

    public AddShareIdentityRequest(ArrayList<String> xuids2) {
        xuids = xuids2;
    }

    public static String getAddShareIdentityRequestBody(AddShareIdentityRequest addShareIdentityRequest) {
        return GsonUtil.toJsonString(addShareIdentityRequest);
    }
}
