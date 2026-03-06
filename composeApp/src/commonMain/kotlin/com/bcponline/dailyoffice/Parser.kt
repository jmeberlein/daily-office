package com.bcponline.dailyoffice

import com.bcponline.dailyoffice.model.LiturgicalDay
import com.bcponline.dailyoffice.model.Office
import com.bcponline.dailyoffice.model.Rank
import com.bcponline.dailyoffice.model.Season
import com.bcponline.dailyoffice.resources.Res

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.*
import kotlinx.datetime.format.char
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.YamlMap
import com.charleskorn.kaml.YamlScalar
import com.charleskorn.kaml.parseToYamlNode
import com.charleskorn.kaml.yamlList
import com.charleskorn.kaml.yamlMap
import com.charleskorn.kaml.yamlScalar
import kotlinx.serialization.decodeFromString

object FileRegistry {
    private val cache = mutableMapOf<String, YamlList>()
    private var dailyPropers: YamlMap? = null
    private val mutex = Mutex()
    private val FORMAT = LocalDate.Format { monthNumber(); char('/'); day() }

    fun getDailyProper(today: LocalDate): LiturgicalDay? {
        val key = today.format(FORMAT)
        if (dailyPropers == null) {
            return null
        }
        val day = dailyPropers!!.get<YamlMap>(key) ?: return null

        val christmas = LocalDate(today.year, 12, 25)
        val firstAdvent = christmas.minus(christmas.dayOfWeek.isoDayNumber + 21, DateTimeUnit.DAY)
        val name = day.get<YamlScalar>("name")?.content ?: ""
        val rank = Rank.valueOf(day.get<YamlScalar>("rank")?.content ?: "FERIA")
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
        val eveningReadings = day.get<YamlMap>("morning")?.get<YamlList>("readings")?.items?.map { it.yamlScalar.content!! } ?:
            if ((today.year % 2 == 0) xor (today >= firstAdvent))
                listOf(
                    day.get<YamlList>("year_1_readings")?.get(0)?.yamlScalar?.content!!,
                    day.get<YamlList>("year_2_readings")?.get(1)?.yamlScalar?.content!!
                )
            else
                listOf(
                    day.get<YamlList>("year_2_readings")?.get(0)?.yamlScalar?.content!!,
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
                    morningCollect
                ),
                Office(
                    name,
                    rank,
                    Season.NONE,
                    eveningPsalter,
                    eveningReadings[0],
                    eveningReadings[1],
                    eveningCollect
                ),
                Office(
                    name,
                    rank,
                    Season.NONE,
                    vigilPsalter,
                    vigilReadings[0],
                    vigilReadings[1],
                    vigilCollect
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
                    morningCollect
                ),
                Office(
                    name,
                    rank,
                    Season.NONE,
                    eveningPsalter,
                    eveningReadings[0],
                    eveningReadings[1],
                    eveningCollect
                )
            )
        }
    }

    fun getWeeklyProper(today: LocalDate): LiturgicalDay? {
        val (file, weekNumber) = getFileFromDate(today)
        if (!cache.containsKey(file)) {
            return null
        }
        val week = cache[file]!![weekNumber].yamlMap
        val day = week.get<YamlMap>(today.dayOfWeek.toString())!!
        val christmas = LocalDate(today.year, 12, 25)
        val firstAdvent = christmas.minus(christmas.dayOfWeek.isoDayNumber + 21, DateTimeUnit.DAY)
        val name = day.get<YamlScalar>("name")?.content ?: week.get<YamlScalar>("name")?.content ?: ""
        val rank = Rank.valueOf(day.get<YamlScalar>("rank")?.content ?: week.get<YamlScalar>("rank")?.content ?: if (today.dayOfWeek == DayOfWeek.SUNDAY) "SUNDAY" else "FERIA")
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
        val eveningReadings = day.get<YamlMap>("morning")?.get<YamlList>("readings")?.items?.map { it.yamlScalar.content!! } ?:
        if ((today.year % 2 == 0) xor (today >= firstAdvent))
            listOf(
                day.get<YamlList>("year_1_readings")?.get(0)?.yamlScalar?.content!!,
                day.get<YamlList>("year_2_readings")?.get(1)?.yamlScalar?.content!!
            )
        else
            listOf(
                day.get<YamlList>("year_2_readings")?.get(0)?.yamlScalar?.content!!,
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
                    Season.NONE,
                    morningPsalter,
                    morningReadings[0],
                    morningReadings[1],
                    morningCollect
                ),
                Office(
                    name,
                    rank,
                    Season.NONE,
                    eveningPsalter,
                    eveningReadings[0],
                    eveningReadings[1],
                    eveningCollect
                ),
                Office(
                    name,
                    rank,
                    Season.NONE,
                    vigilPsalter,
                    vigilReadings[0],
                    vigilReadings[1],
                    vigilCollect
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
                    morningCollect
                ),
                Office(
                    name,
                    rank,
                    Season.NONE,
                    eveningPsalter,
                    eveningReadings[0],
                    eveningReadings[1],
                    eveningCollect
                )
            )
        }
    }

    suspend fun loadDailyPropers() = mutex.withLock {
        if (dailyPropers == null) {
            try {
                val bytes = Res.readBytes("files/saints.yml")
                dailyPropers = Yaml.default.parseToYamlNode(bytes.decodeToString()).yamlMap
            } catch (e: Exception) {
                println("Failed to load saints.yml: ${e.message}")
            }
        }
    }

    suspend fun loadFiles(vararg names: String) = mutex.withLock {
        names.forEach { name ->
            if (!cache.containsKey(name)) {
                try {
                    val bytes = Res.readBytes("files/$name.yml")
                    val parsed: YamlList = Yaml.default.parseToYamlNode(bytes.decodeToString()).yamlList
                    cache[name] = parsed
                } catch (e: Exception) {
                    println("Failed to load $name: ${e.message}")
                }
            }
        }
    }

    suspend fun loadFilesForDate(date: LocalDate) {
        val today = getFileFromDate(date)
        val tomorrow = getFileFromDate(date.plus(1, DateTimeUnit.DAY))
        loadFiles(today.first, tomorrow.first)
    }

    fun getFileFromDate(date: LocalDate): Pair<String, Int> {
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
            val index = (sunday.day - prevChristmasZero.day + 31) / 7
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
            Pair("christmas", index)
        } else {
            val index = (sunday.dayOfYear - christmasZero.dayOfYear) / 7
            Pair("christmas", index)
        }
    }
}
