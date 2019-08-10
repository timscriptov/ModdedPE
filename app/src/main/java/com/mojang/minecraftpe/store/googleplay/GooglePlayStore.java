package com.mojang.minecraftpe.store.googleplay;

import com.mojang.minecraftpe.MainActivity;
import com.mojang.minecraftpe.store.ExtraLicenseResponseData;
import com.mojang.minecraftpe.store.Store;
import com.mojang.minecraftpe.store.StoreListener;

public class GooglePlayStore implements Store {
    static final String IAB_BROADCAST_ACTION = "com.android.vending.billing.PURCHASES_UPDATED";
    MainActivity mActivity;
    StoreListener mListener;
    int mPurchaseRequestCode;

    public GooglePlayStore(MainActivity activity, String licenseKey, StoreListener listener) {
        mActivity = activity;
        System.out.println("GooglePlayStore:" + activity + ":" + licenseKey + ":" + listener);
        mListener = listener;
        mListener.onStoreInitialized(true);
    }

    public String getStoreId() {
        return "android.googleplay";
    }

    public boolean hasVerifiedLicense() {
        return true;
    }

    public ExtraLicenseResponseData getExtraLicenseData() {
        long[] data = new long[]{60000, 0, 0};
        return new ExtraLicenseResponseData(data[0], data[1], data[2]);
    }

    public void queryProducts(String[] productIds) {
        System.out.println("GooglePlayStore: Query products");
    }

    public void acknowledgePurchase(String receipt, String productType) {
    }

    public void queryPurchases() {
        System.out.println("GooglePlayStore: Query purchases");
    }

    public String getProductSkuPrefix() {
        return "";
    }

    public String getRealmsSkuPrefix() {
        return "";
    }

    public boolean receivedLicenseResponse() {
        return true;
    }

    public void destructor() {
        System.out.println("GooglePlayStore: Destructor");
    }

    public void purchase(String productId, boolean isSubscription, String payload) {
    }

    public void purchaseGame() {
    }
}
