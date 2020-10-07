package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.network.managers.IPeopleHubResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.app.XLEUtil;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class RecommendationsPeopleData extends FollowersData {
    private IPeopleHubResult.PeopleHubRecommendation recommendationInfo;

    public RecommendationsPeopleData(IPeopleHubResult.PeopleHubPersonSummary person) {
        super(person);
        XLEAssert.assertNotNull(person.recommendation);
        recommendationInfo = person.recommendation;
    }

    public RecommendationsPeopleData(boolean isDummy, FollowersData.DummyType type) {
        super(isDummy, type);
    }

    public String getRecommendationFirstReason() {
        return XLEUtil.isNullOrEmpty(recommendationInfo.Reasons) ? "" : recommendationInfo.Reasons.get(0);
    }

    public boolean getIsFacebookFriend() {
        return recommendationInfo.getRecommendationType() == IPeopleHubResult.RecommendationType.FacebookFriend;
    }

    public IPeopleHubResult.RecommendationType getRecommendationType() {
        return recommendationInfo.getRecommendationType();
    }
}
