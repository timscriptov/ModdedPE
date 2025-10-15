package com.mcal.editor.lang.javascript

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.SyntaxPattern

fun getJavaScriptSyntaxPatterns(theme: SyntaxHighlightingTheme): List<SyntaxPattern> {
    return listOf(
        // Keywords (ES6+ and modern JavaScript)
        SyntaxPattern(
            Regex("\\b(abstract|arguments|async|await|break|case|catch|class|const|continue|debugger|default|delete|do|else|enum|eval|export|extends|false|finally|for|function|get|if|implements|import|in|instanceof|interface|let|new|null|of|package|private|protected|public|return|set|static|super|switch|this|throw|true|try|typeof|var|void|while|with|yield|undefined|NaN|Infinity)\\b"),
            theme.getKeywordStyle()
        ),
        // Strings (all types)
        SyntaxPattern(
            Regex("(\"\"\".*?\"\"\"|\"[^\"]*\"|'[^']*')", RegexOption.DOT_MATCHES_ALL),
            theme.getStringStyle()
        ),
        // Template literals
        SyntaxPattern(
            Regex("`[^`]*`", RegexOption.DOT_MATCHES_ALL),
            theme.getStringStyle()
        ),
        // Numbers (all formats)
        SyntaxPattern(
            Regex("\\b([0-9]+(\\.[0-9]+)?([eE][+-]?[0-9]+)?|0x[0-9a-fA-F]+|0b[01]+|0o[0-7]+)\\b"),
            theme.getNumbersStyle()
        ),
        // Single-line comments
        SyntaxPattern(Regex("//.*"), theme.getCommentStyle()),
        // Multi-line comments
        SyntaxPattern(
            Regex("/\\*[\\s\\S]*?\\*/", RegexOption.DOT_MATCHES_ALL),
            theme.getCommentStyle()
        ),
        // JSDoc comments
        SyntaxPattern(
            Regex("/\\*\\*[\\s\\S]*?\\*/", RegexOption.DOT_MATCHES_ALL),
            theme.getLanguageSpecificStyle("javascript_jsdoc")
        ),
        // Class definitions
        SyntaxPattern(
            Regex("\\b(class|interface)\\s+(\\w+)"),
            theme.getLanguageSpecificStyle("javascript_class")
        ),
        // Function declarations
        SyntaxPattern(
            Regex("\\b(function|async\\s+function)\\s+(\\w+)\\s*\\("),
            theme.getLanguageSpecificStyle("javascript_function")
        ),
        // Arrow functions
        SyntaxPattern(
            Regex("(\\w+)\\s*=>"),
            theme.getLanguageSpecificStyle("javascript_arrow_function")
        ),
        // Method definitions
        SyntaxPattern(
            Regex("\\b(\\w+)\\s*\\([^)]*\\)\\s*\\{"),
            theme.getLanguageSpecificStyle("javascript_method")
        ),
        // Variables (let, const, var)
        SyntaxPattern(
            Regex("\\b(let|const|var)\\s+(\\w+)\\s*[=;]"),
            theme.getLanguageSpecificStyle("javascript_variable")
        ),
        // Constants (UPPER_CASE naming convention)
        SyntaxPattern(
            Regex("\\b[A-Z_][A-Z0-9_]+\\b"),
            theme.getLanguageSpecificStyle("javascript_constant")
        ),
        // Operators
        SyntaxPattern(
            Regex("[+\\-*/=%&|^~<>!?]+|===|!==|==|!=|<=|>=|&&|\\|\\||\\?\\?|\\.\\.\\."),
            theme.getLanguageSpecificStyle("javascript_operator")
        ),
        // Brackets, parentheses, and braces
        SyntaxPattern(
            Regex("[\\[\\]]"),
            theme.getLanguageSpecificStyle("javascript_bracket")
        ),
        SyntaxPattern(
            Regex("[()]"),
            theme.getLanguageSpecificStyle("javascript_parenthesis")
        ),
        SyntaxPattern(
            Regex("[{}]"),
            theme.getLanguageSpecificStyle("javascript_brace")
        ),
        // Semicolons
        SyntaxPattern(
            Regex(";"),
            theme.getLanguageSpecificStyle("javascript_semicolon")
        ),
        // Function parameters
        SyntaxPattern(
            Regex("\\b(\\w+)\\s*(?=,|\\))"),
            theme.getLanguageSpecificStyle("javascript_parameter")
        ),
        // Object properties
        SyntaxPattern(
            Regex("\\.(\\w+)"),
            theme.getLanguageSpecificStyle("javascript_property")
        ),
        // Computed properties
        SyntaxPattern(
            Regex("\\[(\\w+)\\]"),
            theme.getLanguageSpecificStyle("javascript_computed_property")
        ),
        // Template literal placeholders
        SyntaxPattern(
            Regex("\\$\\{[^}]*\\}"),
            theme.getLanguageSpecificStyle("javascript_template_placeholder")
        ),
        // Decorators (TypeScript/experimental)
        SyntaxPattern(
            Regex("@\\w+"),
            theme.getLanguageSpecificStyle("javascript_decorator")
        ),
        // Type annotations (TypeScript)
        SyntaxPattern(
            Regex(":\\s*\\w+"),
            theme.getLanguageSpecificStyle("javascript_type")
        ),
        // Generics (TypeScript)
        SyntaxPattern(
            Regex("<[^>]*>"),
            theme.getLanguageSpecificStyle("javascript_generic")
        ),
        // Modules (import/export)
        SyntaxPattern(
            Regex("\\b(import|export)\\s+.*?from"),
            theme.getLanguageSpecificStyle("javascript_module")
        ),
        // Promises and async/await
        SyntaxPattern(
            Regex("\\b(Promise|async|await)\\b"),
            theme.getLanguageSpecificStyle("javascript_promise")
        ),
        // Built-in objects and APIs
        SyntaxPattern(
            Regex("\\b(console|document|window|navigator|JSON|Math|Date|Array|Object|String|Number|Boolean|Symbol|Map|Set|WeakMap|WeakSet|Promise|Proxy|Reflect|Intl|ArrayBuffer|DataView|URL|URLSearchParams|FormData|Headers|Request|Response|fetch|localStorage|sessionStorage)\\b"),
            theme.getLanguageSpecificStyle("javascript_builtin")
        )
    )
}
