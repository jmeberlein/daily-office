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

object ProperFetcher {
    fun getProperForDate(today: LocalDate, forceTwoReadings: Boolean, useOptionalFeasts: Boolean, useExtraFeasts: Boolean): LiturgicalDay {
        val weeklyProper = ProperParser.getWeeklyProper(today, forceTwoReadings)!!

        // Check for saint or other daily proper
        val dailyProper = ProperParser.getDailyProper(today, forceTwoReadings)
        if (dailyProper != null) {
            weeklyProper.morning.merge(dailyProper.morning)
            weeklyProper.evening.merge(dailyProper.evening)
        }

        // Check tomorrow for a vigil
        val tomorrow = today.plus(1, DateTimeUnit.DAY)
        val tomorrowWeeklyProper = ProperParser.getWeeklyProper(tomorrow, forceTwoReadings)
        val tomorrowDailyProper = ProperParser.getDailyProper(tomorrow, forceTwoReadings)
        if (tomorrowWeeklyProper?.vigil != null) {
            weeklyProper.evening.merge(tomorrowWeeklyProper.vigil!!)
        }
        if (tomorrowDailyProper?.vigil != null) {
            weeklyProper.evening.merge(tomorrowDailyProper.vigil!!)
        }

        // If today is Monday and yesterday was a feast, use it
        if (today.dayOfWeek == DayOfWeek.MONDAY) {
            val yesterday = today.minus(1, DateTimeUnit.DAY)
            val yesterdayDailyProper = ProperParser.getDailyProper(yesterday, forceTwoReadings)
            if (yesterdayDailyProper?.morning?.rank == Rank.FEAST) {
                weeklyProper.morning.merge(yesterdayDailyProper.morning)
                weeklyProper.evening.merge(yesterdayDailyProper.evening)
            }
        }

        // Check if it's the week of Pentecost
        val easter = computus(today.year)
        val pentecost = easter.plus(7, DateTimeUnit.WEEK)
        if (today > pentecost && today < pentecost.plus(7, DateTimeUnit.DAY)) {
            val tmp =
                if (today.dayOfWeek == DayOfWeek.WEDNESDAY || today.dayOfWeek == DayOfWeek.FRIDAY || today.dayOfWeek == DayOfWeek.SATURDAY)
                    Office.SUMMER_EMBER
                else
                    Office.PENTECOST_WEEKDAY
            weeklyProper.morning.merge(tmp)
            weeklyProper.evening.merge(tmp)
        }

        // Check for moved Joseph/Annunciation/Mark
        if (easter.month == Month.MARCH && easter.day <= 26) {
            if (today.dayOfYear - easter.dayOfYear == 8) {
                val tmp = ProperParser.getDailyProper(LocalDate(today.year, 3, 19), forceTwoReadings)!!
                weeklyProper.morning.merge(tmp.morning)
                weeklyProper.evening.merge(tmp.evening)
            } else if (today.dayOfYear - easter.dayOfYear == 9) {
                val tmp = ProperParser.getDailyProper(LocalDate(today.year, 3, 25), forceTwoReadings)!!
                weeklyProper.morning.merge(tmp.morning)
                weeklyProper.evening.merge(tmp.evening)
            }
        } else if (easter.month == Month.MARCH || (easter.month == Month.APRIL && easter.day == 1)) {
            if (today.dayOfYear - easter.dayOfYear == 8) {
                val tmp = ProperParser.getDailyProper(LocalDate(today.year, 3, 25), forceTwoReadings)!!
                weeklyProper.morning.merge(tmp.morning)
                weeklyProper.evening.merge(tmp.evening)
            }
        } else if (easter.day >= 18) {
            if (today.dayOfYear - easter.dayOfYear == 8) {
                val tmp = ProperParser.getDailyProper(LocalDate(today.year, 4, 25), forceTwoReadings)!!
                weeklyProper.morning.merge(tmp.morning)
                weeklyProper.evening.merge(tmp.evening)
            }
        }

        // Check for extra bumped Stephen/John
        if (today.month == Month.DECEMBER && today.day == 29) {
            if (today.dayOfWeek == DayOfWeek.WEDNESDAY) {
                val tmp = ProperParser.getDailyProper(LocalDate(today.year, 12, 26), forceTwoReadings)!!
                weeklyProper.morning.merge(tmp.morning)
                weeklyProper.evening.merge(tmp.evening)
            } else if (today.dayOfWeek == DayOfWeek.TUESDAY) {
                val tmp = ProperParser.getDailyProper(LocalDate(today.year, 12, 27), forceTwoReadings)!!
                weeklyProper.morning.merge(tmp.morning)
                weeklyProper.evening.merge(tmp.evening)
            }
        }

        // Check for optional feast. This has to come after Pentecost week for consistency,
        // because most of the Ember days are hardcoded into the propers, but these would block
        // the summer Ember days from being applied.
        if (useOptionalFeasts) {
            val optionalFeast = ProperParser.getOptionalFeast(today, forceTwoReadings, useExtraFeasts)
            if (optionalFeast != null) {
                weeklyProper.morning.merge(optionalFeast.morning)
                weeklyProper.evening.merge(optionalFeast.evening)
            }
        }

        // Civil holidays
        if (today.month == Month.SEPTEMBER && today.day <= 7 && today.dayOfWeek == DayOfWeek.MONDAY) {
            weeklyProper.morning.merge(Office.LABOR_DAY)
            weeklyProper.evening.merge(Office.LABOR_DAY)
        }

        if (today.month == Month.NOVEMBER && today.day > 21 && today.day <= 28 && today.dayOfWeek == DayOfWeek.THURSDAY) {
            weeklyProper.morning.merge(Office.THANKSGIVING_MORNING)
            weeklyProper.evening.merge(Office.THANKSGIVING_EVENING)
        }


        // Format name
        val sunday = today.minus(today.dayOfWeek.isoDayNumber % 7, DateTimeUnit.DAY)
        val ordinal = toOrdinal(1 + (sunday.dayOfYear - pentecost.dayOfYear) / 7)
        weeklyProper.morning.name = weeklyProper.morning.name.replace("{{pentecost_ordinal}}", ordinal)
        weeklyProper.evening.name = weeklyProper.evening.name.replace("{{pentecost_ordinal}}", ordinal)
        var dayOfWeek = today.dayOfWeek.toString()
        dayOfWeek = dayOfWeek[0] + dayOfWeek.substring(1).lowercase()
        weeklyProper.morning.name = weeklyProper.morning.name.replace("{{day_of_week}}", dayOfWeek)
        weeklyProper.evening.name = weeklyProper.evening.name.replace("{{day_of_week}}", dayOfWeek)

        return weeklyProper
    }
}