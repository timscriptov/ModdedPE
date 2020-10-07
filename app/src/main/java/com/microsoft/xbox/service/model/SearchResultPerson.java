package com.microsoft.xbox.service.model;

import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.Search.TrieSearch;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 07.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class SearchResultPerson {
    public String GamertagAfter;
    public String GamertagBefore;
    public String GamertagMatch;
    public String RealNameAfter;
    public String RealNameBefore;
    public String RealNameMatch;
    public String SearchText;
    public String StatusAfter;
    public String StatusBefore;
    public String StatusMatch;

    public SearchResultPerson(FollowersData person, String searchText) {
        if (isNullOrWhitespace(searchText)) {
            throw new IllegalArgumentException(searchText);
        }
        SearchText = searchText;
        setInlineRuns(person);
    }

    @NotNull
    private static List<String> getRuns(String text, String searchText) {
        List<String> runs = new ArrayList<>(3);
        int startIndex = TrieSearch.findWordIndex(text, searchText);
        int postIndex = startIndex + searchText.length();
        if (startIndex != -1) {
            runs.add(text.substring(0, startIndex));
            runs.add(text.substring(startIndex, searchText.length() + startIndex));
            runs.add(text.substring(postIndex, text.length()));
        } else {
            runs.add(text);
            runs.add("");
            runs.add("");
        }
        return runs;
    }

    private static boolean isNullOrWhitespace(String text) {
        return JavaUtil.isNullOrEmpty(text) || text.trim().isEmpty();
    }

    private void setInlineRuns(@NotNull FollowersData person) {
        List<String> runs = getRuns(person.getGamertag(), SearchText);
        if (runs.size() == 3) {
            GamertagBefore = runs.get(0);
            GamertagMatch = runs.get(1);
            GamertagAfter = runs.get(2);
        }
        List<String> runs2 = getRuns(person.getGamerRealName(), SearchText);
        if (runs2.size() == 3) {
            RealNameBefore = runs2.get(0);
            RealNameMatch = runs2.get(1);
            RealNameAfter = runs2.get(2);
        }
        List<String> runs3 = getRuns(person.presenceString, SearchText);
        if (runs3.size() == 3) {
            StatusBefore = runs3.get(0);
            StatusMatch = runs3.get(1);
            StatusAfter = runs3.get(2);
        }
    }
}
