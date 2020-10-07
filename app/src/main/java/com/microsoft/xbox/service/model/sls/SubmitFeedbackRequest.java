package com.microsoft.xbox.service.model.sls;

import com.microsoft.xbox.toolkit.GsonUtil;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SubmitFeedbackRequest {
    public String evidenceId;
    public FeedbackType feedbackType;
    public String sessionRef;
    public String textReason;
    public String voiceReasonId;
    public long xuid;

    public SubmitFeedbackRequest(long xuid2, String sessionRef2, FeedbackType feedbackType2, String textReason2, String voiceReasonId2, String evidenceId2) {
        xuid = xuid2;
        sessionRef = sessionRef2;
        feedbackType = feedbackType2;
        textReason = textReason2;
        voiceReasonId = voiceReasonId2;
        evidenceId = evidenceId2;
    }

    public static String getSubmitFeedbackRequestBody(SubmitFeedbackRequest request) {
        return GsonUtil.toJsonString(request);
    }
}
