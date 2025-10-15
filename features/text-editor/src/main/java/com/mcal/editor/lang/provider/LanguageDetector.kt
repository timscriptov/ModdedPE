package com.mcal.editor.lang.provider

import java.io.File

object LanguageDetector {
    fun detectLanguage(file: File): Language {
        return when (getFileExtension(file).lowercase()) {
            "kt", "kts" -> Language.KOTLIN
            "java", "jav" -> Language.JAVA
            "cpp", "cc", "cxx", "c++", "hpp", "h", "hh", "hxx", "h++" -> Language.CPP
            "c" -> Language.CPP // C language uses same syntax as C++ for highlighting
            "py", "pyw", "pyi" -> Language.PYTHON
            "rb", "rbw", "rake", "gemspec", "podspec" -> Language.RUBY
            "js", "mjs", "cjs", "jsx" -> Language.JAVASCRIPT
            "ts", "tsx" -> Language.JAVASCRIPT // TypeScript uses JavaScript highlighting
            "xml", "xhtml", "xsd", "xsl", "xslt", "rss", "atom" -> Language.XML
            "html", "htm", "shtml", "xhtml" -> Language.HTML
            "json", "jsonc", "json5" -> Language.JSON
            else -> detectLanguageByContent(file)
        }
    }

    private fun getFileExtension(file: File): String {
        val name = file.name
        val lastDotIndex = name.lastIndexOf('.')
        return if (lastDotIndex != -1 && lastDotIndex < name.length - 1) {
            name.substring(lastDotIndex + 1)
        } else {
            ""
        }
    }

    private fun detectLanguageByContent(file: File): Language {
        if (!file.exists() || !file.isFile) {
            return Language.XML // Default fallback
        }

        return try {
            val firstLine = file.bufferedReader().use { it.readLine() } ?: ""
            when {
                firstLine.startsWith("#!/usr/bin/env python") ||
                        firstLine.startsWith("#!/usr/bin/python") -> Language.PYTHON
                firstLine.startsWith("#!/usr/bin/env ruby") ||
                        firstLine.startsWith("#!/usr/bin/ruby") -> Language.RUBY
                firstLine.startsWith("#!/usr/bin/env node") ||
                        firstLine.startsWith("#!/usr/bin/node") -> Language.JAVASCRIPT
                firstLine.startsWith("<?xml") -> Language.XML
                firstLine.startsWith("<!DOCTYPE html") ||
                        firstLine.contains("<html") -> Language.HTML
                firstLine.trim().startsWith("{") ||
                        firstLine.trim().startsWith("[") -> Language.JSON
                firstLine.startsWith("package ") ||
                        firstLine.contains("class ") -> Language.JAVA
                firstLine.startsWith("fun ") ||
                        firstLine.contains("package ") -> Language.KOTLIN
                firstLine.contains("#include") ||
                        firstLine.contains("using namespace") -> Language.CPP
                else -> Language.XML // Default fallback
            }
        } catch (e: Exception) {
            Language.XML // Default fallback
        }
    }

    fun getSupportedExtensions(): List<String> {
        return listOf(
            "kt", "kts",           // Kotlin
            "java", "jav",         // Java
            "cpp", "cc", "cxx", "c++", "hpp", "h", "hh", "hxx", "h++", "c", // C/C++
            "py", "pyw", "pyi",    // Python
            "rb", "rbw", "rake", "gemspec", "podspec", // Ruby
            "js", "mjs", "cjs", "jsx", "ts", "tsx", // JavaScript/TypeScript
            "xml", "xhtml", "xsd", "xsl", "xslt", "rss", "atom", // XML
            "html", "htm", "shtml", "xhtml", // HTML
            "json", "jsonc", "json5" // JSON
        )
    }

    fun isFileSupported(file: File): Boolean {
        val extension = getFileExtension(file).lowercase()
        return getSupportedExtensions().contains(extension)
    }
}
