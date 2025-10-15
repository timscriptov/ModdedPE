package com.mcal.editor.lang.provider

import com.mcal.editor.lang.SyntaxHighlightingTheme
import com.mcal.editor.lang.SyntaxPattern
import com.mcal.editor.lang.cpp.getCppSyntaxPatterns
import com.mcal.editor.lang.cpp.light.CppLightTheme
import com.mcal.editor.lang.html.getHtmlSyntaxPatterns
import com.mcal.editor.lang.html.light.HtmlLightTheme
import com.mcal.editor.lang.java.getJavaSyntaxPatterns
import com.mcal.editor.lang.java.light.JavaLightTheme
import com.mcal.editor.lang.javascript.getJavaScriptSyntaxPatterns
import com.mcal.editor.lang.javascript.light.JavaScriptLightTheme
import com.mcal.editor.lang.json.getJsonSyntaxPatterns
import com.mcal.editor.lang.json.light.JsonLightTheme
import com.mcal.editor.lang.kotlin.getKotlinSyntaxPatterns
import com.mcal.editor.lang.kotlin.light.KotlinLightTheme
import com.mcal.editor.lang.python.getPythonSyntaxPatterns
import com.mcal.editor.lang.python.light.PythonLightTheme
import com.mcal.editor.lang.ruby.getRubySyntaxPatterns
import com.mcal.editor.lang.ruby.light.RubyDefaultTheme
import com.mcal.editor.lang.xml.getXmlSyntaxPatterns
import com.mcal.editor.lang.xml.light.XmlLightTheme

object SyntaxProvider {
    fun getSyntaxPatterns(language: Language, theme: SyntaxHighlightingTheme): List<SyntaxPattern> {
        return when (language) {
            Language.KOTLIN -> getKotlinSyntaxPatterns(theme)
            Language.PYTHON -> getPythonSyntaxPatterns(theme)
            Language.RUBY -> getRubySyntaxPatterns(theme)
            Language.JAVASCRIPT -> getJavaScriptSyntaxPatterns(theme)
            Language.JAVA -> getJavaSyntaxPatterns(theme)
            Language.CPP -> getCppSyntaxPatterns(theme)
            Language.XML -> getXmlSyntaxPatterns(theme)
            Language.HTML -> getHtmlSyntaxPatterns(theme)
            Language.JSON -> getJsonSyntaxPatterns(theme)
        }
    }

    fun getDefaultTheme(language: Language): SyntaxHighlightingTheme {
        return when (language) {
            Language.KOTLIN -> KotlinLightTheme()
            Language.PYTHON -> PythonLightTheme()
            Language.RUBY -> RubyDefaultTheme()
            Language.JAVASCRIPT -> JavaScriptLightTheme()
            Language.JAVA -> JavaLightTheme()
            Language.CPP -> CppLightTheme()
            Language.XML -> XmlLightTheme()
            Language.HTML -> HtmlLightTheme()
            Language.JSON -> JsonLightTheme()
        }
    }
}

enum class Language {
    KOTLIN,
    PYTHON,
    RUBY,
    JAVASCRIPT,
    JAVA,
    CPP,
    XML,
    HTML,
    JSON
}
