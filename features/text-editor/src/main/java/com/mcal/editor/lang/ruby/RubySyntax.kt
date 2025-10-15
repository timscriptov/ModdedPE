package com.mcal.editor.lang.ruby

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.SyntaxPattern

fun getRubySyntaxPatterns(theme: SyntaxHighlightingTheme): List<SyntaxPattern> {
    return listOf(
        // Keywords
        SyntaxPattern(
            Regex("\\b(alias|and|begin|break|case|class|def|defined\\?|do|else|elsif|end|ensure|false|for|if|in|module|next|nil|not|or|redo|rescue|retry|return|self|super|then|true|undef|unless|until|when|while|yield)\\b"),
            theme.getKeywordStyle()
        ),
        // Built-in functions
        SyntaxPattern(
            Regex("\\b(puts|gets|require|include|extend|each|map|select|reject|reduce|inject|find|detect|all\\?|any\\?|none\\?|one\\?|sort|sort_by|reverse|length|size|count|first|last|min|max|sum|uniq|join|split|gsub|sub|chomp|strip|lstrip|rstrip|to_s|to_i|to_f|to_a|to_h|to_sym|nil\\?|empty\\?|include\\?|key\\?|value\\?|merge|fetch|delete|clear|push|pop|shift|unshift|concat|flatten|compact|zip|cycle|take|drop|take_while|drop_while|group_by|partition|slice|sample|shuffle|rand|exit|abort|raise|catch|throw|lambda|proc|call|eval|instance_eval|class_eval|define_method|attr_accessor|attr_reader|attr_writer|private|protected|public)\\b"),
            theme.getLanguageSpecificStyle("ruby_builtin_function")
        ),
        // Strings
        SyntaxPattern(
            Regex("(\"\"\".*?\"\"\"|\"[^\"]*\")", RegexOption.DOT_MATCHES_ALL),
            theme.getStringStyle()
        ),
        // Single-Quoted Strings
        SyntaxPattern(
            Regex("'([^'\\\\]|\\\\.)*'"), // Correctly escaped regex for Kotlin
            theme.getStringStyle()
        ),
        // Multi-line Strings
        SyntaxPattern(
            Regex("\"\"\"(.*?)\"\"\"", RegexOption.DOT_MATCHES_ALL),
            theme.getStringStyle()
        ),
        // Symbols
        SyntaxPattern(
            Regex("(:[\\w_]+)"),
            theme.getLanguageSpecificStyle("ruby_symbol")
        ),
        // Numbers
        SyntaxPattern(Regex("\\b([0-9]+(\\.[0-9]+)?|0x[0-9a-fA-F]+)\\b"), theme.getNumbersStyle()),
        // Single-line comments
        SyntaxPattern(Regex("#.*"), theme.getCommentStyle()),
        // Multi-line comments (Ruby doesn't have multi-line comments, but you can use multiple single-line comments)
        // Class definitions
        SyntaxPattern(
            Regex("\\bclass\\s+(\\w+)"),
            theme.getLanguageSpecificStyle("ruby_class")
        ),
        // Method declarations
        SyntaxPattern(
            Regex("\\bdef\\s+(\\w+)\\s*\\("),
            theme.getLanguageSpecificStyle("ruby_method")
        ),
        // Variables (Ruby doesn't have explicit variable declarations, but we can highlight assignments)
        SyntaxPattern(
            Regex("\\b(\\w+)\\s*="),
            theme.getLanguageSpecificStyle("ruby_variable")
        ),
        // Constants (Ruby constants start with an uppercase letter)
        SyntaxPattern(
            Regex("\\b[A-Z][A-Z0-9_]+\\b"),
            theme.getLanguageSpecificStyle("ruby_constant")
        ),
        // Operators
        SyntaxPattern(
            Regex("[+\\-*/=%&|^~<>!?]+"),
            theme.getLanguageSpecificStyle("ruby_operator")
        ),
        // Brackets, parentheses, and braces
        SyntaxPattern(
            Regex("[\\[\\]]"),
            theme.getLanguageSpecificStyle("ruby_bracket")
        ),
        SyntaxPattern(
            Regex("[()]"),
            theme.getLanguageSpecificStyle("ruby_parenthesis")
        ),
        SyntaxPattern(
            Regex("[{}]"),
            theme.getLanguageSpecificStyle("ruby_brace")
        ),
        // Method parameters
        SyntaxPattern(
            Regex("\\((\\s*\\w+\\s*:)"),
            theme.getLanguageSpecificStyle("ruby_parameter")
        ),
        // Local variables (Ruby doesn't have explicit local variable declarations)
        SyntaxPattern(
            Regex("\\b(\\w+)\\s*="),
            theme.getLanguageSpecificStyle("ruby_local_variable")
        ),
        // Global variables (Ruby global variables start with $)
        SyntaxPattern(
            Regex("\\$\\w+"),
            theme.getLanguageSpecificStyle("ruby_global_variable")
        ),
        // Instance variables (Ruby instance variables start with @)
        SyntaxPattern(
            Regex("@\\w+"),
            theme.getLanguageSpecificStyle("ruby_instance_variable")
        ),
        // Class variables (Ruby class variables start with @@)
        SyntaxPattern(
            Regex("@@\\w+"),
            theme.getLanguageSpecificStyle("ruby_class_variable")
        )
    )
}
