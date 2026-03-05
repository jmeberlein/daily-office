package com.bcponline.dailyoffice

import kotlinx.datetime.*

fun Map<*, *>.dig(vararg keys: String): Any? {
    var current: Any? = this
    for (key in keys) {
        current = (current as? Map<*, *>)?.get(key)
    }
    return current
}

fun Map<*, *>.containsDeepPath(vararg keys: String): Boolean {
    var current: Any? = this
    for (key in keys) {
        if (current !is Map<*, *> || !current.containsKey(key)) {
            return false
        }
        current = current[key]
    }
    return true
}

@Suppress("UNCHECKED_CAST")
fun <T> Map<*, *>.getDeepOrDefault(default: T, vararg keys: String): T {
    var current: Any? = this
    for (key in keys) {
        current = (current as? Map<*, *>)?.get(key)
        if (current == null) return default
    }
    return (current as? T) ?: default
}

fun computus(year: Int): LocalDate {
    val a = year % 19
    val b = year / 100
    val c = year % 100
    val d = b / 4
    val e = b % 4
    val f = (b + 8) / 25
    val g = (b - f + 1) / 3
    val h = (19 * a + b - d - g + 15) % 30
    val i = c / 4
    val k = c % 4
    val l = (32 + 2 * e + 2 * i - h - k) % 7
    val m = (a + 11 * h + 22 * l) / 451
    val n = (h + l - 7 * m + 114) / 31
    val o = (h + l - 7 * m + 114) % 31
    return LocalDate(year, n, o + 1)
}

fun toOrdinal(n: Int): String {
    return when (n) {
        1 -> "First"
        2 -> "Second"
        3 -> "Third"
        4 -> "Fourth"
        5 -> "Fifth"
        6 -> "Sixth"
        7 -> "Seventh"
        8 -> "Eighth"
        9 -> "Ninth"
        10 -> "Tenth"
        11 -> "Eleventh"
        12 -> "Twelfth"
        13 -> "Thirteenth"
        14 -> "Fourteenth"
        15 -> "Fifteenth"
        16 -> "Sixteenth"
        17 -> "Seventeenth"
        18 -> "Eighteenth"
        19 -> "Nineteenth"
        20 -> "Twentieth"
        21 -> "Twenty-First"
        22 -> "Twenty-Second"
        23 -> "Twenty-Third"
        24 -> "Twenty-Fourth"
        25 -> "Twenty-Fifth"
        26 -> "Twenty-Sixth"
        27 -> "Twenty-Seventh"
        28 -> "Twenty-Eighth"
        29 -> "Twenty-Ninth"
        30 -> "Thirtieth"
        31 -> "Thirty-First"
        32 -> "Thirty-Second"
        33 -> "Thirty-Third"
        34 -> "Thirty-Fourth"
        else -> "N-th"
    }
}