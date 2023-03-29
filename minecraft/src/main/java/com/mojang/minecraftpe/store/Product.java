package com.mojang.minecraftpe.store;

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class Product {
    public String mCurrencyCode;
    public String mId;
    public String mPrice;
    public String mUnformattedPrice;

    public Product(String id, String price, String currencyCode, String unformattedPrice) {
        mId = id;
        mPrice = price;
        mCurrencyCode = currencyCode;
        mUnformattedPrice = unformattedPrice;
    }
}