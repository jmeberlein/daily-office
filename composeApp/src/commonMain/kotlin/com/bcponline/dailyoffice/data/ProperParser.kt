package com.bcponline.dailyoffice.data

import com.bcponline.dailyoffice.computus
import com.bcponline.dailyoffice.toOrdinal
import com.bcponline.dailyoffice.model.*
import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.yamlList
import com.charleskorn.kaml.yamlMap
import com.charleskorn.kaml.yamlScalar
import kotlinx.datetime.*

object ProperParser {
    private val FORMAT = LocalDate.Format { monthNumber(); day() }

    suspend fun loadFilesForDate(date: LocalDate) {
        val today = getFileFromDate(date)
        val tomorrow = getFileFromDate(date.plus(1, DateTimeUnit.DAY))
        FileRegistry.loadFiles(today.first, tomorrow.first)
    }

    fun getWeeklyProper(today: LocalDate, forceTwoReadings: Boolean): LiturgicalDay? {
        val (file, weekNumber) = getFileFromDate(today)
        val propers = FileRegistry.getFile(file)?.yamlList ?: return null
        val week = propers[weekNumber].yamlMap
        return loadPropersFromWeek(today, week, forceTwoReadings)
    }

    fun getDailyProper(today: LocalDate, forceTwoReadings: Boolean): LiturgicalDay? {
        val key = today.format(FORMAT)
        val dailyPropers = FileRegistry.getFile("daily_propers")?.yamlMap
        if (dailyPropers == null) {
            return null
        }
        val day = dailyPropers.get<YamlMap>(key) ?: return null
        return loadPropersFromDay(today, day, forceTwoReadings)
    }

    fun getOptionalFeast(today: LocalDate, forceTwoReadings: Boolean, useExtraFeasts: Boolean): LiturgicalDay? {
        val key = today.format(FORMAT)
        val optionalFeasts = FileRegistry.getFile("optional_feasts")?.yamlMap
        if (optionalFeasts == null) {
            return null
        }
        var day = optionalFeasts.get<YamlMap>(key)
        if (day != null) {
            return loadPropersFromDay(today, day, forceTwoReadings)
        }

        val extraFeasts = FileRegistry.getFile("extra_feasts")?.yamlMap
        if (extraFeasts == null || !useExtraFeasts) {
            return null
        }
        day = extraFeasts.get<YamlMap>(key) ?: return null
        return loadPropersFromDay(today, day, forceTwoReadings)
    }

    private fun loadPropersFromDay(today: LocalDate, day: YamlMap, forceTwoReadings: Boolean): LiturgicalDay {
        val christmas = LocalDate(today.year, 12, 25)
        val firstAdvent = christmas.minus(christmas.dayOfWeek.isoDayNumber + 21, DateTimeUnit.DAY)
        val name = day.get<YamlScalar>("name")?.content ?: ""
        val rank = Rank.valueOf(day.get<YamlScalar>("rank")?.content ?: "FERIA")
        val color = day.get<YamlScalar>("color")?.content?.let { LiturgicalColor.valueOf(it) } ?: LiturgicalColor.NONE
        val morningPsalter = day.get<YamlMap>("morning")?.get<YamlScalar>("psalter")?.content ?: ""
        val eveningPsalter = day.get<YamlMap>("evening")?.get<YamlScalar>("psalter")?.content ?: ""
        val morningReadings = day.get<YamlMap>("morning")?.get<YamlList>("readings")?.items?.map { it.yamlScalar.content!! } ?:
            if ((today.year % 2 == 0) xor (today >= firstAdvent))
                listOf(
                    day.get<YamlList>("year_2_readings")?.get(0)?.yamlScalar?.content!!,
                    day.get<YamlList>("year_2_readings")?.get(2)?.yamlScalar?.content!!
                )
            else
                listOf(
                    day.get<YamlList>("year_1_readings")?.get(0)?.yamlScalar?.content!!,
                    day.get<YamlList>("year_1_readings")?.get(1)?.yamlScalar?.content!!
                )
        val eveningReadings = day.get<YamlMap>("evening")?.get<YamlList>("readings")?.items?.map { it.yamlScalar.content!! } ?:
            if ((today.year % 2 == 0) xor (today >= firstAdvent))
                listOf(
                    if (forceTwoReadings) day.get<YamlList>("year_1_readings")?.get(0)?.yamlScalar?.content!! else "",
                    day.get<YamlList>("year_2_readings")?.get(1)?.yamlScalar?.content!!
                )
            else
                listOf(
                    if (forceTwoReadings) day.get<YamlList>("year_2_readings")?.get(0)?.yamlScalar?.content!! else "",
                    day.get<YamlList>("year_1_readings")?.get(2)?.yamlScalar?.content!!
                )
        val morningCollect = day.get<YamlMap>("morning")?.get<YamlScalar>("collect")?.content ?: day.get<YamlScalar>("collect")?.content ?: ""
        val eveningCollect = day.get<YamlMap>("evening")?.get<YamlScalar>("collect")?.content ?: day.get<YamlScalar>("collect")?.content ?: ""

        if (day.get<YamlMap>("vigil") != null) {
            val vigilPsalter = day.get<YamlMap>("vigil")?.get<YamlScalar>("psalter")?.content ?: ""
            val vigilReadings = day.get<YamlMap>("vigil")?.get<YamlList>("readings")?.items?.map { it.yamlScalar.content } ?: listOf("", "")
            val vigilCollect = day.get<YamlMap>("vigil")?.get<YamlScalar>("collect")?.content ?: day.get<YamlScalar>("collect")?.content ?: ""

            return LiturgicalDay(
                Office(
                    name,
                    rank,
                    Season.NONE,
                    morningPsalter,
                    morningReadings[0],
                    morningReadings[1],
                    morningCollect,
                    color
                ),
                Office(
                    name,
                    rank,
                    Season.NONE,
                    eveningPsalter,
                    eveningReadings[0],
                    eveningReadings[1],
                    eveningCollect,
                    color
                ),
                Office(
                    name,
                    rank,
                    Season.NONE,
                    vigilPsalter,
                    vigilReadings[0],
                    vigilReadings[1],
                    vigilCollect,
                    color
                )
            )
        } else {
            return LiturgicalDay(
                Office(
                    name,
                    rank,
                    Season.NONE,
                    morningPsalter,
                    morningReadings[0],
                    morningReadings[1],
                    morningCollect,
                    color
                ),
                Office(
                    name,
                    rank,
                    Season.NONE,
                    eveningPsalter,
                    eveningReadings[0],
                    eveningReadings[1],
                    eveningCollect,
                    color
                )
            )
        }
    }

    private fun loadPropersFromWeek(today: LocalDate, week: YamlMap, forceTwoReadings: Boolean): LiturgicalDay {
        val day = week.get<YamlMap>(today.dayOfWeek.toString())!!
        val christmas = LocalDate(today.year, 12, 25)
        val firstAdvent = christmas.minus(christmas.dayOfWeek.isoDayNumber + 21, DateTimeUnit.DAY)
        val name = day.get<YamlScalar>("name")?.content ?: week.get<YamlScalar>("name")?.content ?: ""
        val rank = Rank.valueOf(day.get<YamlScalar>("rank")?.content ?: week.get<YamlScalar>("rank")?.content ?: if (today.dayOfWeek == DayOfWeek.SUNDAY) "SUNDAY" else "FERIA")
        val season = Season.valueOf(day.get<YamlScalar>("season")?.content ?: week.get<YamlScalar>("season")?.content ?: "NONE")
        val color = day.get<YamlScalar>("color")?.content?.let { LiturgicalColor.valueOf(it) } ?: when (season) {
            Season.CHRISTMAS, Season.EASTER -> LiturgicalColor.WHITE
            Season.LENT -> LiturgicalColor.PURPLE
            Season.ADVENT -> LiturgicalColor.BLUE
            Season.EPIPHANY, Season.PENTECOST -> LiturgicalColor.GREEN
            Season.NONE -> LiturgicalColor.NONE
        }
        val morningPsalter = day.get<YamlMap>("morning")?.get<YamlScalar>("psalter")?.content ?: ""
        val eveningPsalter = day.get<YamlMap>("evening")?.get<YamlScalar>("psalter")?.content ?: ""
        val morningReadings = day.get<YamlMap>("morning")?.get<YamlList>("readings")?.items?.map { it.yamlScalar.content!! } ?:
        if ((today.year % 2 == 0) xor (today >= firstAdvent))
            listOf(
                day.get<YamlList>("year_2_readings")?.get(0)?.yamlScalar?.content!!,
                day.get<YamlList>("year_2_readings")?.get(2)?.yamlScalar?.content!!
            )
        else
            listOf(
                day.get<YamlList>("year_1_readings")?.get(0)?.yamlScalar?.content!!,
                day.get<YamlList>("year_1_readings")?.get(1)?.yamlScalar?.content!!
            )
        val eveningReadings = day.get<YamlMap>("evening")?.get<YamlList>("readings")?.items?.map { it.yamlScalar.content!! } ?:
        if ((today.year % 2 == 0) xor (today >= firstAdvent))
            listOf(
                if (forceTwoReadings) day.get<YamlList>("year_1_readings")?.get(0)?.yamlScalar?.content!! else "",
                day.get<YamlList>("year_2_readings")?.get(1)?.yamlScalar?.content!!
            )
        else
            listOf(
                if (forceTwoReadings) day.get<YamlList>("year_2_readings")?.get(0)?.yamlScalar?.content!! else "",
                day.get<YamlList>("year_1_readings")?.get(2)?.yamlScalar?.content!!
            )
        val morningCollect = day.get<YamlMap>("morning")?.get<YamlScalar>("collect")?.content ?: day.get<YamlScalar>("collect")?.content ?: week.get<YamlScalar>("collect")?.content ?: ""
        val eveningCollect = day.get<YamlMap>("evening")?.get<YamlScalar>("collect")?.content ?: day.get<YamlScalar>("collect")?.content ?: week.get<YamlScalar>("collect")?.content ?: ""

        if (day.get<YamlMap>("vigil") != null) {
            val vigilPsalter = day.get<YamlMap>("vigil")?.get<YamlScalar>("psalter")?.content ?: ""
            val vigilReadings = day.get<YamlMap>("vigil")?.get<YamlList>("readings")?.items?.map { it.yamlScalar.content } ?: listOf("", "")
            val vigilCollect = day.get<YamlMap>("vigil")?.get<YamlScalar>("collect")?.content ?: day.get<YamlScalar>("collect")?.content ?: week.get<YamlScalar>("collect")?.content ?: ""

            return LiturgicalDay(
                Office(
                    name,
                    rank,
                    season,
                    morningPsalter,
                    morningReadings[0],
                    morningReadings[1],
                    morningCollect,
                    color
                ),
                Office(
                    name,
                    rank,
                    season,
                    eveningPsalter,
                    eveningReadings[0],
                    eveningReadings[1],
                    eveningCollect,
                    color
                ),
                Office(
                    name,
                    rank,
                    season,
                    vigilPsalter,
                    vigilReadings[0],
                    vigilReadings[1],
                    vigilCollect,
                    color
                )
            )
        } else {
            return LiturgicalDay(
                Office(
                    name,
                    rank,
                    season,
                    morningPsalter,
                    morningReadings[0],
                    morningReadings[1],
                    morningCollect,
                    color
                ),
                Office(
                    name,
                    rank,
                    season,
                    eveningPsalter,
                    eveningReadings[0],
                    eveningReadings[1],
                    eveningCollect,
                    color
                )
            )
        }
    }

    private fun getFileFromDate(date: LocalDate): Pair<String, Int> {
        val sunday = date.minus(date.dayOfWeek.isoDayNumber % 7, DateTimeUnit.DAY)
        val epiphany = LocalDate(date.year, 1, 6)
        val epiphanyZero = epiphany.minus(epiphany.dayOfWeek.isoDayNumber % 7, DateTimeUnit.DAY)
        val easter = computus(date.year)
        val quinquagesima = easter.minus(7, DateTimeUnit.WEEK)
        val pentecost = easter.plus(7, DateTimeUnit.WEEK)
        val trinity = pentecost.plus(1, DateTimeUnit.WEEK)
        val christmas = LocalDate(date.year, 12, 25)
        val christmasZero = christmas.minus(christmas.dayOfWeek.isoDayNumber % 7, DateTimeUnit.DAY)
        val firstAdvent = christmas.minus(christmas.dayOfWeek.isoDayNumber + 21, DateTimeUnit.DAY)

        return if (date < epiphany) {
            val prevChristmas = LocalDate(date.year - 1, 12, 25)
            val prevChristmasZero =
                prevChristmas.minus(prevChristmas.dayOfWeek.isoDayNumber % 7, DateTimeUnit.DAY)
            val index = (sunday.day - prevChristmasZero.day + (if (sunday.month == Month.DECEMBER) 0 else 31)) / 7
            Pair("christmas", index)
        } else if (date < quinquagesima) {
            val index = (sunday.dayOfYear - epiphanyZero.dayOfYear) / 7
            Pair("epiphany", index)
        } else if (date < easter) {
            val index = 7 - (easter.dayOfYear - sunday.dayOfYear) / 7
            Pair("lent", index)
        } else if (date <= pentecost || date == trinity) {
            val index = (sunday.dayOfYear - easter.dayOfYear) / 7
            Pair("easter", index)
        } else if (date < firstAdvent) {
            val index = 29 - (firstAdvent.dayOfYear - sunday.dayOfYear) / 7
            Pair("pentecost", index)
        } else if (date < christmas) {
            val index = (sunday.dayOfYear - firstAdvent.dayOfYear) / 7
            Pair("advent", index)
        } else {
            val index = (sunday.dayOfYear - christmasZero.dayOfYear) / 7
            Pair("christmas", index)
        }
    }
}
