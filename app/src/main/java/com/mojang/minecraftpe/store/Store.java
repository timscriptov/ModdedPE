package com.mojang.minecraftpe.store;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public interface Store {
    void acknowledgePurchase(String str, String str2);

    void destructor();

    ExtraLicenseResponseData getExtraLicenseData();

    String getProductSkuPrefix();

    String getRealmsSkuPrefix();

    String getStoreId();

    boolean hasVerifiedLicense();

    void purchase(String str, boolean z, String str2);

    void purchaseGame();

    void queryProducts(String[] strArr);

    void queryPurchases();

    boolean receivedLicenseResponse();
}