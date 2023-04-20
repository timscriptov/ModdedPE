package com.mojang.minecraftpe.store;

/**
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */
public class Purchase {
    public String mProductId;
    public boolean mPurchaseActive;
    public String mReceipt;

    public Purchase(String productId, String receipt, boolean purchaseActive) {
        mProductId = productId;
        mReceipt = receipt;
        mPurchaseActive = purchaseActive;
    }
}