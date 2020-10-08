package com.microsoft.xbox.toolkit.ui.Search;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class TrieInput {
    public Object Context;
    public String Text;

    public TrieInput(String text, Object context) {
        Text = text;
        Context = context;
    }
}
