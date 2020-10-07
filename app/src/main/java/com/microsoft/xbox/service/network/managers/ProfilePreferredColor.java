package com.microsoft.xbox.service.network.managers;

import com.google.gson.annotations.SerializedName;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class ProfilePreferredColor {
    private int primary = -1;
    @SerializedName("primaryColor")
    private String primaryColorString;
    private int secondary = -1;
    @SerializedName("secondaryColor")
    private String secondaryColorString;
    private int tertiary = -1;
    @SerializedName("tertiaryColor")
    private String tertiaryColorString;

    public static int convertColorFromString(String colorString) {
        if (colorString == null) {
            return 0;
        }
        if (colorString.startsWith("#")) {
            colorString = colorString.substring(1);
        }
        int color = Integer.parseInt(colorString, 16);
        if ((color >> 24) == 0) {
            return color | -16777216;
        }
        return color;
    }

    public int getPrimaryColor() {
        if (primary < 0) {
            primary = convertColorFromString(primaryColorString);
        }
        return primary;
    }

    public int getSecondaryColor() {
        if (secondary < 0) {
            secondary = convertColorFromString(secondaryColorString);
        }
        return secondary;
    }

    public int getTertiaryColor() {
        if (tertiary < 0) {
            tertiary = convertColorFromString(tertiaryColorString);
        }
        return tertiary;
    }
}