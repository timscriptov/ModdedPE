package com.mcal.editor.lang.python

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.SyntaxPattern

fun getPythonSyntaxPatterns(theme: SyntaxHighlightingTheme): List<SyntaxPattern> {
    return listOf(
        // Keywords (Python 3.6+ including modern features)
        SyntaxPattern(
            Regex("\\b(and|as|assert|async|await|break|case|class|continue|def|del|elif|else|except|finally|for|from|global|if|import|in|is|lambda|match|nonlocal|not|or|pass|raise|return|try|while|with|yield|True|False|None|NotImplemented|Ellipsis)\\b"),
            theme.getKeywordStyle()
        ),
        // Strings (all types including f-strings and raw strings)
        SyntaxPattern(
            Regex("([fFrRuU]?\"\"\"[\\s\\S]*?\"\"\"|[fFrRuU]?\"[^\"]*\"|[fFrRuU]?'[^']*')"),
            theme.getStringStyle()
        ),
        // F-strings placeholders
        SyntaxPattern(
            Regex("\\{[^}]*\\}"),
            theme.getLanguageSpecificStyle("python_fstring")
        ),
        // Numbers (all formats)
        SyntaxPattern(
            Regex("\\b([0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?|0x[0-9a-fA-F]+|0b[01]+|0o[0-7]+|[0-9]+_[0-9_]+[jJ]?)\\b"),
            theme.getNumbersStyle()
        ),
        // Single-line comments
        SyntaxPattern(Regex("#.*"), theme.getCommentStyle()),
        // Multi-line comments (docstrings used as comments)
        SyntaxPattern(
            Regex("(\"\"\"[\\s\\S]*?\"\"\"|'''[\\s\\S]*?''')"),
            theme.getCommentStyle()
        ),
        // Class definitions
        SyntaxPattern(
            Regex("\\b(class)\\s+(\\w+)"),
            theme.getLanguageSpecificStyle("python_class")
        ),
        // Function definitions
        SyntaxPattern(
            Regex("\\b(def|async\\s+def)\\s+(\\w+)\\s*\\("),
            theme.getLanguageSpecificStyle("python_function")
        ),
        // Method definitions
        SyntaxPattern(
            Regex("\\bdef\\s+(\\w+)\\s*\\([^)]*\\w+\\s*,\\s*self[^)]*\\)"),
            theme.getLanguageSpecificStyle("python_method")
        ),
        // Variables (assignments)
        SyntaxPattern(
            Regex("\\b(\\w+)\\s*="),
            theme.getLanguageSpecificStyle("python_variable")
        ),
        // Annotations and decorators
        SyntaxPattern(
            Regex("@[\\w.]+"),
            theme.getLanguageSpecificStyle("python_decorator")
        ),
        // Type hints
        SyntaxPattern(
            Regex(":\\s*([A-Z][A-Za-z0-9_]*|\\w+\\s*\\[[^]]*\\])"),
            theme.getLanguageSpecificStyle("python_type")
        ),
        // Return type hints
        SyntaxPattern(
            Regex("->\\s*([A-Z][A-Za-z0-9_]*|\\w+\\s*\\[[^]]*\\])"),
            theme.getLanguageSpecificStyle("python_return_type")
        ),
        // Constants (UPPER_CASE convention)
        SyntaxPattern(
            Regex("\\b[A-Z][A-Z0-9_]+\\b"),
            theme.getLanguageSpecificStyle("python_constant")
        ),
        // Built-in functions and types
        SyntaxPattern(
            Regex("\\b(abs|all|any|ascii|bin|bool|breakpoint|bytearray|bytes|callable|chr|classmethod|compile|complex|delattr|dict|dir|divmod|enumerate|eval|exec|filter|float|format|frozenset|getattr|globals|hasattr|hash|help|hex|id|input|int|isinstance|issubclass|iter|len|list|locals|map|max|memoryview|min|next|object|oct|open|ord|pow|print|property|range|repr|reversed|round|set|setattr|slice|sorted|staticmethod|str|sum|super|tuple|type|vars|zip|__import__)\\b"),
            theme.getLanguageSpecificStyle("python_builtin")
        ),
        // Magic methods and dunder methods
        SyntaxPattern(
            Regex("\\b(__[a-z__]+__)\\b"),
            theme.getLanguageSpecificStyle("python_magic_method")
        ),
        // Operators
        SyntaxPattern(
            Regex("[+\\-*/=%&|^~<>!?]+|//|\\*\\*|:=|->"),
            theme.getLanguageSpecificStyle("python_operator")
        ),
        // Brackets, parentheses, and braces
        SyntaxPattern(
            Regex("[\\[\\]]"),
            theme.getLanguageSpecificStyle("python_bracket")
        ),
        SyntaxPattern(
            Regex("[()]"),
            theme.getLanguageSpecificStyle("python_parenthesis")
        ),
        SyntaxPattern(
            Regex("[{}]"),
            theme.getLanguageSpecificStyle("python_brace")
        ),
        // Semicolons (rare in Python but possible)
        SyntaxPattern(
            Regex(";"),
            theme.getLanguageSpecificStyle("python_semicolon")
        ),
        // Function parameters
        SyntaxPattern(
            Regex("\\b(\\w+)\\s*(?=,|:|\\))"),
            theme.getLanguageSpecificStyle("python_parameter")
        ),
        // Self parameter (convention)
        SyntaxPattern(
            Regex("\\bself\\b"),
            theme.getLanguageSpecificStyle("python_self")
        ),
        // Class variables
        SyntaxPattern(
            Regex("\\b(\\w+)\\s*=\\s*[^=](?![^(]*\\))"),
            theme.getLanguageSpecificStyle("python_class_variable")
        ),
        // Import statements
        SyntaxPattern(
            Regex("\\b(import|from)\\s+(\\w+)"),
            theme.getLanguageSpecificStyle("python_import")
        ),
        // Exception handling
        SyntaxPattern(
            Regex("\\b(try|except|finally|raise|assert)\\b"),
            theme.getLanguageSpecificStyle("python_exception")
        ),
        // Async/await keywords
        SyntaxPattern(
            Regex("\\b(async|await)\\b"),
            theme.getLanguageSpecificStyle("python_async")
        ),
        // Pattern matching (Python 3.10+)
        SyntaxPattern(
            Regex("\\b(match|case)\\b"),
            theme.getLanguageSpecificStyle("python_pattern_matching")
        )
    )
}
