package com.mcal.editor.lang.cpp

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.SyntaxPattern

fun getCppSyntaxPatterns(theme: SyntaxHighlightingTheme): List<SyntaxPattern> {
    return listOf(
        // Keywords (C++11/14/17/20/23)
        SyntaxPattern(
            Regex("\\b(alignas|alignof|and|and_eq|asm|auto|bitand|bitor|bool|break|case|catch|char|char8_t|char16_t|char32_t|class|co_await|co_return|co_yield|compl|concept|const|consteval|constexpr|constinit|const_cast|continue|decltype|default|delete|do|double|dynamic_cast|else|enum|explicit|export|extern|false|final|float|for|friend|goto|if|import|inline|int|long|module|mutable|namespace|new|noexcept|not|not_eq|nullptr|operator|or|or_eq|override|private|protected|public|register|reinterpret_cast|requires|return|short|signed|sizeof|static|static_assert|static_cast|struct|switch|template|this|thread_local|throw|true|try|typedef|typeid|typename|union|unsigned|using|virtual|void|volatile|wchar_t|while|xor|xor_eq)\\b"),
            theme.getKeywordStyle()
        ),
        // Preprocessor directives
        SyntaxPattern(
            Regex("^\\s*#\\s*(include|define|undef|if|ifdef|ifndef|else|elif|endif|line|error|pragma|warning|import|using|module|export)\\b.*"),
            theme.getLanguageSpecificStyle("cpp_preprocessor")
        ),
        // Strings (all types)
        SyntaxPattern(
            Regex("(\"[^\"]*\"|'[^']*'|L\"[^\"]*\"|u8\"[^\"]*\"|u\"[^\"]*\"|U\"[^\"]*\")"),
            theme.getStringStyle()
        ),
        // Raw strings
        SyntaxPattern(
            Regex("R\"([^()\\\\\\s]{0,16})\\([\\s\\S]*?\\)\\1\""),
            theme.getStringStyle()
        ),
        // Numbers (all formats)
        SyntaxPattern(
            Regex("\\b([0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?|0x[0-9a-fA-F]+|0b[01]+|0[0-7]+)\\b"),
            theme.getNumbersStyle()
        ),
        // Single-line comments
        SyntaxPattern(Regex("//.*"), theme.getCommentStyle()),
        // Multi-line comments
        SyntaxPattern(Regex("/\\*[\\s\\S]*?\\*/"), theme.getCommentStyle()),
        // Class/struct definitions
        SyntaxPattern(
            Regex("\\b(class|struct|enum\\s+class|enum\\s+struct)\\s+(\\w+)"),
            theme.getLanguageSpecificStyle("cpp_class")
        ),
        // Function declarations
        SyntaxPattern(
            Regex("\\b(\\w+(?:<[^>]*>)?)\\s+(\\w+)\\s*\\("),
            theme.getLanguageSpecificStyle("cpp_function")
        ),
        // Lambda expressions
        SyntaxPattern(
            Regex("\\[([^]]*)\\]\\s*\\([^)]*\\)\\s*\\{"),
            theme.getLanguageSpecificStyle("cpp_lambda")
        ),
        // Template declarations
        SyntaxPattern(
            Regex("template\\s*<[^>]*>"),
            theme.getLanguageSpecificStyle("cpp_template")
        ),
        // Namespace
        SyntaxPattern(
            Regex("\\b(namespace|using\\s+namespace)\\s+(\\w+)"),
            theme.getLanguageSpecificStyle("cpp_namespace")
        ),
        // Type references (user-defined types)
        SyntaxPattern(
            Regex("\\b([A-Z][A-Za-z0-9_]*)\\b"),
            theme.getLanguageSpecificStyle("cpp_type")
        ),
        // Constants (UPPER_CASE naming convention)
        SyntaxPattern(
            Regex("\\b[A-Z][A-Z0-9_]+\\b"),
            theme.getLanguageSpecificStyle("cpp_constant")
        ),
        // Operators
        SyntaxPattern(
            Regex("[+\\-*/=%&|^~<>!?]+|::|->|\\.\\*|->\\*"),
            theme.getLanguageSpecificStyle("cpp_operator")
        ),
        // Brackets, parentheses, and braces
        SyntaxPattern(
            Regex("[\\[\\]]"),
            theme.getLanguageSpecificStyle("cpp_bracket")
        ),
        SyntaxPattern(
            Regex("[()]"),
            theme.getLanguageSpecificStyle("cpp_parenthesis")
        ),
        SyntaxPattern(
            Regex("[{}]"),
            theme.getLanguageSpecificStyle("cpp_brace")
        ),
        // Semicolons
        SyntaxPattern(
            Regex(";"),
            theme.getLanguageSpecificStyle("cpp_semicolon")
        ),
        // Function parameters
        SyntaxPattern(
            Regex("\\b(\\w+)\\s+(\\w+)\\s*(?=\\,|\\))"),
            theme.getLanguageSpecificStyle("cpp_parameter")
        ),
        // Local variables
        SyntaxPattern(
            Regex("\\b(\\w+)\\s+(\\w+)\\s*="),
            theme.getLanguageSpecificStyle("cpp_local_variable")
        ),
        // Pointer and reference declarations
        SyntaxPattern(
            Regex("\\b(\\w+(?:<[^>]*>)?)\\s*([&*]+)\\s*(\\w+)"),
            theme.getLanguageSpecificStyle("cpp_pointer_reference")
        ),
        // Attributes (C++11+)
        SyntaxPattern(
            Regex("\\[\\[.*?\\]\\]"),
            theme.getLanguageSpecificStyle("cpp_attribute")
        ),
        // Concepts (C++20)
        SyntaxPattern(
            Regex("\\bconcept\\s+(\\w+)"),
            theme.getLanguageSpecificStyle("cpp_concept")
        ),
        // Modules (C++20)
        SyntaxPattern(
            Regex("\\b(module|import|export\\s+module|export\\s+import)\\b"),
            theme.getLanguageSpecificStyle("cpp_module")
        )
    )
}
