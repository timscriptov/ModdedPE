package com.mojang.minecraftpe.store;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
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