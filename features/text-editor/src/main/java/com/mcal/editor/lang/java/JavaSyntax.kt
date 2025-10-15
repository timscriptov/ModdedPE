package com.mcal.editor.lang.java

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.SyntaxPattern

fun getJavaSyntaxPatterns(theme: SyntaxHighlightingTheme): List<SyntaxPattern> {
    return listOf(
        // Keywords (including modern Java features)
        SyntaxPattern(
            Regex("\\b(abstract|assert|boolean|break|byte|case|catch|char|class|const|continue|default|do|double|else|enum|exports|extends|final|finally|float|for|goto|if|implements|import|instanceof|int|interface|long|module|native|new|non-sealed|open|opens|package|permits|private|protected|provides|public|record|requires|return|sealed|short|static|strictfp|super|switch|synchronized|this|throw|throws|to|transient|transitive|try|uses|var|void|volatile|while|with|true|false|null|yield)\\b"),
            theme.getKeywordStyle()
        ),
        // Strings
        SyntaxPattern(
            Regex("\"[^\"]*\""),
            theme.getStringStyle()
        ),
        // Text blocks (Java 15+)
        SyntaxPattern(
            Regex("\"\"\"[\\s\\S]*?\"\"\""),
            theme.getStringStyle()
        ),
        // Character literals
        SyntaxPattern(
            Regex("'[^']*'"),
            theme.getStringStyle()
        ),
        // Numbers (all formats)
        SyntaxPattern(
            Regex("\\b([0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?|0x[0-9a-fA-F]+|0b[01]+|0[0-7]+|[0-9]+_[0-9_]+)\\b"),
            theme.getNumbersStyle()
        ),
        // Single-line comments
        SyntaxPattern(Regex("//.*"), theme.getCommentStyle()),
        // Multi-line comments
        SyntaxPattern(Regex("/\\*[\\s\\S]*?\\*/"), theme.getCommentStyle()),
        // Javadoc comments
        SyntaxPattern(
            Regex("/\\*\\*[\\s\\S]*?\\*/"),
            theme.getLanguageSpecificStyle("java_javadoc")
        ),
        // Class definitions
        SyntaxPattern(
            Regex("\\b(class|record)\\s+(\\w+)"),
            theme.getLanguageSpecificStyle("java_class")
        ),
        // Interface definitions
        SyntaxPattern(
            Regex("\\b(interface|@?interface)\\s+(\\w+)"),
            theme.getLanguageSpecificStyle("java_interface")
        ),
        // Enum definitions
        SyntaxPattern(
            Regex("\\benum\\s+(\\w+)"),
            theme.getLanguageSpecificStyle("java_enum")
        ),
        // Method declarations
        SyntaxPattern(
            Regex("\\b(\\w+(?:<[^>]*>)?)\\s+(\\w+)\\s*\\("),
            theme.getLanguageSpecificStyle("java_method")
        ),
        // Constructor declarations
        SyntaxPattern(
            Regex("\\b(\\w+)\\s*\\("),
            theme.getLanguageSpecificStyle("java_constructor")
        ),
        // Annotations
        SyntaxPattern(
            Regex("@[\\w.]+(\\([^)]*\\))?"),
            theme.getLanguageSpecificStyle("java_annotation")
        ),
        // Type references (user-defined types)
        SyntaxPattern(
            Regex("\\b([A-Z][A-Za-z0-9_]*)\\b"),
            theme.getLanguageSpecificStyle("java_type")
        ),
        // Generic types
        SyntaxPattern(
            Regex("<[^>]*>"),
            theme.getLanguageSpecificStyle("java_generic")
        ),
        // Constants (UPPER_CASE naming convention)
        SyntaxPattern(
            Regex("\\b[A-Z][A-Z0-9_]+\\b"),
            theme.getLanguageSpecificStyle("java_constant")
        ),
        // Operators
        SyntaxPattern(
            Regex("[+\\-*/=%&|^~<>!?]+|::|->|\\+\\+|--"),
            theme.getLanguageSpecificStyle("java_operator")
        ),
        // Brackets, parentheses, and braces
        SyntaxPattern(
            Regex("[\\[\\]]"),
            theme.getLanguageSpecificStyle("java_bracket")
        ),
        SyntaxPattern(
            Regex("[()]"),
            theme.getLanguageSpecificStyle("java_parenthesis")
        ),
        SyntaxPattern(
            Regex("[{}]"),
            theme.getLanguageSpecificStyle("java_brace")
        ),
        // Semicolons
        SyntaxPattern(
            Regex(";"),
            theme.getLanguageSpecificStyle("java_semicolon")
        ),
        // Function parameters
        SyntaxPattern(
            Regex("\\b(\\w+(?:<[^>]*>)?)\\s+(\\w+)\\s*(?=,|\\))"),
            theme.getLanguageSpecificStyle("java_parameter")
        ),
        // Local variables (including var)
        SyntaxPattern(
            Regex("\\b(var|\\w+(?:<[^>]*>)?)\\s+(\\w+)\\s*="),
            theme.getLanguageSpecificStyle("java_local_variable")
        ),
        // Field declarations
        SyntaxPattern(
            Regex("\\b(public|private|protected|static|final)\\s+(\\w+(?:<[^>]*>)?)\\s+(\\w+)\\s*[;=]"),
            theme.getLanguageSpecificStyle("java_field")
        ),
        // Lambda expressions
        SyntaxPattern(
            Regex("\\([^)]*\\)\\s*->"),
            theme.getLanguageSpecificStyle("java_lambda")
        ),
        // Pattern matching (instanceof)
        SyntaxPattern(
            Regex("instanceof\\s+(\\w+(?:<[^>]*>)?)\\s+(\\w+)"),
            theme.getLanguageSpecificStyle("java_pattern_matching")
        ),
        // Module system (Java 9+)
        SyntaxPattern(
            Regex("\\b(module|requires|exports|opens|provides|uses|to|with|transitive)\\b"),
            theme.getLanguageSpecificStyle("java_module")
        ),
        // Sealed classes/interfaces (Java 17+)
        SyntaxPattern(
            Regex("\\b(sealed|non-sealed|permits)\\b"),
            theme.getLanguageSpecificStyle("java_sealed")
        )
    )
}
