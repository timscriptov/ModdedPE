package com.mcal.editor.lang.kotlin

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.SyntaxPattern

fun getKotlinSyntaxPatterns(theme: SyntaxHighlightingTheme): List<SyntaxPattern> {
    return listOf(
        // Keywords (including modern Kotlin features)
        SyntaxPattern(
            Regex("\\b(abstract|actual|annotation|as|as\\?|break|by|catch|class|companion|const|constructor|continue|crossinline|data|delegate|do|dynamic|else|enum|expect|external|false|field|file|final|finally|for|fun|get|if|import|in|infix|init|inline|inner|interface|internal|is|it|lateinit|noinline|null|object|open|operator|out|override|package|param|private|property|protected|public|receiver|reified|return|sealed|set|super|suspend|tailrec|this|throw|true|try|typealias|typeof|val|var|vararg|when|where|while|yield)\\b"),
            theme.getKeywordStyle()
        ),
        // Strings (all types)
        SyntaxPattern(
            Regex("(\"\"\"[\\s\\S]*?\"\"\"|\"[^\"]*\")"),
            theme.getStringStyle()
        ),
        // Character literals
        SyntaxPattern(
            Regex("'[^']*'"),
            theme.getStringStyle()
        ),
        // Numbers (all formats)
        SyntaxPattern(
            Regex("\\b([0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?|0x[0-9a-fA-F]+|0b[01]+|[0-9]+_[0-9_]+[LfF]?)\\b"),
            theme.getNumbersStyle()
        ),
        // Single-line comments
        SyntaxPattern(Regex("//.*"), theme.getCommentStyle()),
        // Multi-line comments
        SyntaxPattern(Regex("/\\*[\\s\\S]*?\\*/"), theme.getCommentStyle()),
        // KDoc comments
        SyntaxPattern(
            Regex("/\\*\\*[\\s\\S]*?\\*/"),
            theme.getLanguageSpecificStyle("kotlin_kdoc")
        ),
        // Class definitions
        SyntaxPattern(
            Regex("\\b(class|interface|object|data\\s+class|sealed\\s+class|enum\\s+class)\\s+(\\w+)"),
            theme.getLanguageSpecificStyle("kotlin_class")
        ),
        // Function declarations
        SyntaxPattern(
            Regex("\\b(fun|suspend\\s+fun)\\s+([A-Za-z_][A-Za-z0-9_]*)"),
            theme.getLanguageSpecificStyle("kotlin_function")
        ),
        // Properties and variables
        SyntaxPattern(
            Regex("\\b(val|var)\\s+(\\w+)\\s*:"),
            theme.getLanguageSpecificStyle("kotlin_property")
        ),
        // Annotations
        SyntaxPattern(
            Regex("@[\\w.]+(\\([^)]*\\))?"),
            theme.getLanguageSpecificStyle("kotlin_annotation")
        ),
        // Type references
        SyntaxPattern(
            Regex(":\\s*([A-Z][A-Za-z0-9_]*)"),
            theme.getLanguageSpecificStyle("kotlin_type")
        ),
        // Generic types
        SyntaxPattern(
            Regex("<[^>]*>"),
            theme.getLanguageSpecificStyle("kotlin_generic")
        ),
        // Constants
        SyntaxPattern(
            Regex("\\b[A-Z][A-Z0-9_]+\\b"),
            theme.getLanguageSpecificStyle("kotlin_constant")
        ),
        // Operators
        SyntaxPattern(
            Regex("[+\\-*/=%&|^~<>!?]+|!!|\\.\\.|\\.\\?|::|->"),
            theme.getLanguageSpecificStyle("kotlin_operator")
        ),
        // Brackets, parentheses, and braces
        SyntaxPattern(
            Regex("[\\[\\]]"),
            theme.getLanguageSpecificStyle("kotlin_bracket")
        ),
        SyntaxPattern(
            Regex("[()]"),
            theme.getLanguageSpecificStyle("kotlin_parenthesis")
        ),
        SyntaxPattern(
            Regex("[{}]"),
            theme.getLanguageSpecificStyle("kotlin_brace")
        ),
        // Semicolons
        SyntaxPattern(
            Regex(";"),
            theme.getLanguageSpecificStyle("kotlin_semicolon")
        ),
        // Function parameters
        SyntaxPattern(
            Regex("\\b(\\w+)\\s*:"),
            theme.getLanguageSpecificStyle("kotlin_parameter")
        ),
        // Local variables
        SyntaxPattern(
            Regex("\\b(val|var)\\s+(\\w+)\\s*="),
            theme.getLanguageSpecificStyle("kotlin_local_variable")
        ),
        // Extension functions
        SyntaxPattern(
            Regex("fun\\s+([A-Z][A-Za-z0-9_]*\\.)?[a-z][A-Za-z0-9_]*\\s*\\("),
            theme.getLanguageSpecificStyle("kotlin_extension_function")
        ),
        // Lambda expressions
        SyntaxPattern(
            Regex("\\{[^}]*\\}"),
            theme.getLanguageSpecificStyle("kotlin_lambda")
        ),
        // String templates
        SyntaxPattern(
            Regex("\\$[\\w{]+"),
            theme.getLanguageSpecificStyle("kotlin_string_template")
        ),
        // Control flow keywords
        SyntaxPattern(
            Regex("\\b(if|else|when|for|while|do|break|continue|return)\\b"),
            theme.getLanguageSpecificStyle("kotlin_control_flow")
        ),
        // Modifiers
        SyntaxPattern(
            Regex("\\b(public|private|protected|internal|open|abstract|final|sealed|const|lateinit|vararg|suspend|tailrec|operator|infix|inline|noinline|crossinline|reified|expect|actual)\\b"),
            theme.getLanguageSpecificStyle("kotlin_modifier")
        ),
        // Built-in types
        SyntaxPattern(
            Regex("\\b(Any|Unit|Nothing|String|Char|Boolean|Byte|Short|Int|Long|Float|Double|UByte|UShort|UInt|ULong|Array|List|Set|Map|MutableList|MutableSet|MutableMap|Sequence|Flow|CoroutineScope)\\b"),
            theme.getLanguageSpecificStyle("kotlin_builtin_type")
        )
    )
}
