/*
 * Copyright (C) 2018-2019 Тимашков Иван
 */
package com.mojang.minecraftpe.store;

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
