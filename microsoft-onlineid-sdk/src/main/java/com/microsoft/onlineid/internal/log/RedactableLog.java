package com.microsoft.onlineid.internal.log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RedactableLog implements IRedactable {
    private static final Pattern RedactedPattern = Pattern.compile("*(%d)*".replace("*", "\\*").replace("(", "\\(").replace(")", "\\)").replace("%d", "\\d+"));
    private final Pattern[] _patternsToRedact;
    private final String _unredactedString;

    public RedactableLog(String unredactedString, Pattern... patternsToRedact) {
        this._unredactedString = unredactedString;
        this._patternsToRedact = patternsToRedact;
    }

    public String getRedactedString() {
        String redactedString = this._unredactedString;
        for (Pattern pattern : this._patternsToRedact) {
            Matcher matcher = pattern.matcher(redactedString);
            while (matcher.find()) {
                String stringToRedact = matcher.group(matcher.groupCount() == 0 ? 0 : 1);
                if (!RedactedPattern.matcher(stringToRedact).matches()) {
                    redactedString = redactedString.replace(stringToRedact, Redactor.redactString(stringToRedact));
                }
            }
        }
        return redactedString;
    }

    public String getUnredactedString() {
        return this._unredactedString;
    }
}
