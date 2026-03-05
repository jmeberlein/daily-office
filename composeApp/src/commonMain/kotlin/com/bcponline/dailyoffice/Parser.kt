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
import com.charleskorn.kaml.yamlMap
import com.charleskorn.kaml.yamlScalar
import kotlinx.serialization.decodeFromString

object FileRegistry {
    private val cache = mutableMapOf<String, List<Any?>>()
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
                    val parsed: List<Any?> = Yaml.default.decodeFromString<List<Any?>>(bytes.decodeToString())
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
        loadFiles(today, tomorrow)
    }

    fun getFileFromDate(date: LocalDate): String {
        val epiphany = LocalDate(date.year, 1, 6)
        val easter = computus(date.year)
        val quinquagesima = easter.minus(7, DateTimeUnit.WEEK)
        val pentecost = easter.plus(7, DateTimeUnit.WEEK)
        val trinity = pentecost.plus(1, DateTimeUnit.WEEK)
        val christmas = LocalDate(date.year, 12, 25)
        val firstAdvent = christmas.minus(christmas.dayOfWeek.isoDayNumber + 21, DateTimeUnit.DAY)

        return if (date < epiphany)
            "christmas"
        else if (date < quinquagesima)
            "epiphany"
        else if (date < easter)
            "lent"
        else if (date <= pentecost || date == trinity)
            "easter"
        else if (date < firstAdvent)
            "advent"
        else
            "christmas"
    }
}
