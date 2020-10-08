package com.microsoft.xbox.toolkit.ui.Search;

import com.microsoft.xbox.toolkit.JavaUtil;

import org.jetbrains.annotations.NotNull;

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

public class TrieSearch {
    private static String ComponentName = TrieSearch.class.getName();
    private static int DefaultTrieDepth = 4;
    public TrieNode RootTrieNode;
    public int TrieDepth;
    public Hashtable<String, List<Object>> WordsDictionary;

    public TrieSearch() {
        WordsDictionary = new Hashtable<>();
        RootTrieNode = new TrieNode();
        TrieDepth = DefaultTrieDepth;
    }

    public TrieSearch(int depth) {
        WordsDictionary = new Hashtable<>();
        RootTrieNode = new TrieNode();
        TrieDepth = depth;
    }

    @NotNull
    public static Hashtable<String, List<Object>> getWordsDictionary(List<TrieInput> trieInputs) {
        String[] words;
        Hashtable<String, List<Object>> wordsDictionary = new Hashtable<>();
        if (trieInputs != null) {
            for (TrieInput trieInput : trieInputs) {
                if (JavaUtil.isNullOrEmpty(trieInput.Text)) {
                    words = new String[0];
                } else {
                    words = trieInput.Text.split(" ");
                }
                for (String findWordIndex : words) {
                    int wordIndex = findWordIndex(trieInput.Text, findWordIndex);
                    if (wordIndex != -1) {
                        String word = trieInput.Text.substring(wordIndex).toUpperCase();
                        if (!wordsDictionary.containsKey(word)) {
                            List<Object> contexts = new ArrayList<>();
                            contexts.add(trieInput.Context);
                            wordsDictionary.put(word, contexts);
                        } else if (!wordsDictionary.get(word).contains(trieInput.Context)) {
                            wordsDictionary.get(word).add(trieInput.Context);
                        }
                    }
                }
            }
        }
        return wordsDictionary;
    }

    public static int findWordIndex(String text, String word) {
        int index = -1;
        if (!JavaUtil.isNullOrEmpty(text) && !JavaUtil.isNullOrEmpty(word)) {
            index = text.toLowerCase().indexOf(word.toLowerCase());
            while (index != -1 && index != 0 && !isNullOrWhitespace(text.substring(index - 1, index))) {
                index = text.toLowerCase().indexOf(word.toLowerCase(), index + 1);
            }
        }
        return index;
    }

    private static boolean isNullOrWhitespace(String text) {
        return JavaUtil.isNullOrEmpty(text) || text.trim().isEmpty();
    }

    public static TrieNode getTrieNodes(Hashtable<String, List<Object>> wordsDictionary, int trieDepth) {
        if (wordsDictionary == null) {
            return null;
        }
        TrieNode rootTrieNode = new TrieNode();
        Enumeration<String> keys = wordsDictionary.keys();
        while (keys.hasMoreElements()) {
            String word = keys.nextElement();
            TrieNode node = rootTrieNode;
            int level = 0;
            while (level < word.length() && level <= trieDepth) {
                char charAtIndex = word.charAt(level);
                if (node.MoreNodes == null) {
                    node.MoreNodes = new Hashtable<>(26);
                }
                if (!node.MoreNodes.containsKey(Character.valueOf(charAtIndex))) {
                    node.MoreNodes.put(Character.valueOf(charAtIndex), new TrieNode());
                }
                node = node.MoreNodes.get(Character.valueOf(charAtIndex));
                level++;
            }
            if (level > trieDepth) {
                if (node.Words == null) {
                    node.Words = new ArrayList();
                }
                node.Words.add(word);
            }
            if (level == word.length()) {
                node.IsWord = true;
            }
        }
        return rootTrieNode;
    }

    @NotNull
    public static List<String> getWordMatches(TrieNode root, int trieDepth, String searchText) {
        List<String> wordMatches = new ArrayList<>();
        if (!JavaUtil.isNullOrEmpty(searchText)) {
            String prefix = "";
            TrieNode node = root;
            boolean hasMatches = true;
            String text = searchText.toUpperCase();
            int level = 0;
            while (true) {
                if (level >= text.length() || level > trieDepth) {
                    break;
                }
                char charAtIndex = text.charAt(level);
                prefix = prefix + charAtIndex;
                if (node.MoreNodes == null || !node.MoreNodes.containsKey(Character.valueOf(charAtIndex))) {
                    hasMatches = false;
                } else {
                    node = node.MoreNodes.get(Character.valueOf(charAtIndex));
                    level++;
                }
            }
            if (level > trieDepth) {
                if (node.Words != null) {
                    for (String word : node.Words) {
                        if (word.toLowerCase().startsWith(searchText.toLowerCase())) {
                            wordMatches.add(word);
                        }
                    }
                }
            } else if (hasMatches) {
                wordMatches.addAll(getRemainingWordMatches(node, trieDepth, prefix));
            }
        }
        return wordMatches;
    }

    @NotNull
    public static List<String> getRemainingWordMatches(TrieNode node, int trieDepth, String prefix) {
        List<String> words = new ArrayList<>();
        if (node != null && !JavaUtil.isNullOrEmpty(prefix)) {
            if (node.IsWord && prefix.length() <= trieDepth) {
                words.add(prefix);
            }
            if (node.MoreNodes != null) {
                Enumeration<Character> keys = node.MoreNodes.keys();
                while (keys.hasMoreElements()) {
                    char key = keys.nextElement().charValue();
                    words.addAll(getRemainingWordMatches(node.MoreNodes.get(Character.valueOf(key)), trieDepth, prefix + key));
                }
            }
            if (node.Words != null) {
                for (String word : node.Words) {
                    if (word.toLowerCase().startsWith(prefix.toLowerCase())) {
                        words.add(word);
                    }
                }
            }
        }
        return words;
    }

    public void initialize(List<TrieInput> trieInputs) {
        WordsDictionary = getWordsDictionary(trieInputs);
        RootTrieNode = getTrieNodes(WordsDictionary, TrieDepth);
    }

    public List<String> search(String searchText) {
        return getWordMatches(RootTrieNode, TrieDepth, searchText);
    }
}
