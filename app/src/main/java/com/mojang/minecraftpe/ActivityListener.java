/*
 * Copyright (C) 2018-2019 Тимашков Иван
 */
package com.mojang.minecraftpe;

import android.content.Intent;

public interface ActivityListener {
    void onActivityResult(int i, int i2, Intent intent);

    void onDestroy();
}
