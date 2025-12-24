package com.example.animesearchapp.core.util

fun sanitizeDescription(raw: String?): String {
    if (raw.isNullOrBlank()) return ""

    val sb = StringBuilder(raw.length)
    var depth = 0

    for (ch in raw) {
        when (ch) {
            '[', '［' -> {
                depth++
                // не добавляем скобку
            }
            ']', '］' -> {
                if (depth > 0) depth--
                // не добавляем скобку
            }
            else -> {
                if (depth == 0) sb.append(ch)
            }
        }
    }

    return sb.toString()
        .replace("\r\n", "\n")
        .replace(Regex("""[ \t]+\n"""), "\n")
        .replace(Regex("""\n{3,}"""), "\n\n")
        .trim()
}
