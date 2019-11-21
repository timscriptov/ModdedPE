package com.microsoft.onlineid.internal.log;

import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.sdk.R;
import java.util.Locale;

public class Redactor {
    protected static final String RedactedStringEmptyReplacement = "";
    protected static final String RedactedStringNullReplacement = "(null)";
    protected static final String RedactedStringReplacement = "*(%d)*";
    protected static final String RedactedStringStarReplacement = "***";

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$onlineid$internal$log$Redactor$RedactionType = new int[RedactionType.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$onlineid$internal$log$Redactor$RedactionType[RedactionType.Email.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$log$Redactor$RedactionType[RedactionType.Password.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$log$Redactor$RedactionType[RedactionType.String.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private enum RedactionType {
        Email,
        Password,
        String
    }

    public static boolean shouldRedact() {
        return Logger.shouldRedact();
    }

    public static String redactEmail(String str) {
        return doRedact(str, RedactionType.Email);
    }

    public static String redactPassword(String str) {
        return doRedact(str, RedactionType.Password);
    }

    public static String redactString(String str) {
        return doRedact(str, RedactionType.String);
    }

    private static String doRedact(String stringToRedact, RedactionType type) {
        if (stringToRedact == null) {
            return RedactedStringNullReplacement;
        }
        if (stringToRedact.isEmpty()) {
            return RedactedStringEmptyReplacement;
        }
        String redactedStr = RedactedStringEmptyReplacement;
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$internal$log$Redactor$RedactionType[type.ordinal()]) {
            case R.styleable.StyledTextView_isUnderlined /*1*/:
                return String.format(Locale.getDefault(), RedactedStringReplacement, new Object[]{Integer.valueOf(stringToRedact.length())});
            case ApiResult.ResultUINeeded /*2*/:
                return RedactedStringStarReplacement;
            case 3:
                return String.format(Locale.getDefault(), RedactedStringReplacement, new Object[]{Integer.valueOf(stringToRedact.length())});
            default:
                return RedactedStringStarReplacement;
        }
    }
}
