package com.example.notemoon.notes.domain.util

private val HTML_TAG_REGEX = Regex("<[^>]*>")
private val WHITESPACE_REGEX = Regex("\\s+")

/**
 * Strips HTML tags and decodes a few common entities, returning readable plain
 * text. Note content is stored as HTML (rich text), so this is used for search
 * matching and for the plain-text preview shown on note cards.
 */
fun String.stripHtml(): String {
    return HTML_TAG_REGEX.replace(this, " ")
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .let { WHITESPACE_REGEX.replace(it, " ") }
        .trim()
}
