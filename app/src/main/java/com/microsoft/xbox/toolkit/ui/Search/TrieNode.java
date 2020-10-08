package com.microsoft.xbox.toolkit.ui.Search;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * 08.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class TrieNode {
    public boolean IsWord;
    public Hashtable<Character, TrieNode> MoreNodes = new Hashtable<>(26);
    public List<String> Words = new ArrayList();

    public void accept(ITrieNodeVisitor visitor) {
        if (visitor != null) {
            visitor.visit(this);
        }
        if (MoreNodes != null) {
            Enumeration<Character> keys = MoreNodes.keys();
            while (keys.hasMoreElements()) {
                MoreNodes.get(Character.valueOf(keys.nextElement().charValue())).accept(visitor);
            }
        }
    }
}
