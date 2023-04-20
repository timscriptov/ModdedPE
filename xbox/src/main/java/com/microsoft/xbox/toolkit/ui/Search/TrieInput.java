package com.microsoft.xbox.toolkit.ui.Search;

/**
 * 07.01.2021
 *
 * @author <a href="https://github.com/TimScriptov">TimScriptov</a>
 */

public class TrieInput {
    public Object Context;
    public String Text;

    public TrieInput(String str, Object obj) {
        this.Text = str;
        this.Context = obj;
    }
}
