package com.bcponline.dailyoffice.model

import com.bcponline.dailyoffice.FileRegistry
import com.bcponline.dailyoffice.computus
import com.bcponline.dailyoffice.toOrdinal
import kotlinx.datetime.*

class Office(
    var name: String,
    var rank: Rank,
    var season: Season,
    var psalter: String,
    var firstReading: String,
    var secondReading: String,
    var collect: String
) {
    companion object {
        val SUMMER_EMBER =
            Office(
                "Summer Ember Day",
                Rank.OPTIONAL,
                Season.PENTECOST,
                "",
                "",
                "",
                "Almighty and everlasting God, by whose Spirit the whole body of your faithful people is governed and sanctified: Receive our supplications and prayers, which we offer before you for all members of your holy Church, that in their vocation and ministry they may truly and devoutly serve you; through our Lord and Savior Jesus Christ, who lives and reigns with you, in the unity of the Holy Spirit, one God, now and forever. Amen."
            )

        val PENTECOST_WEEKDAY =
            Office(
                "The {{day_of_week}} after Pentecost",
                Rank.OPTIONAL,
                Season.PENTECOST,
                "",
                "",
                "",
                ""
            )

        val THANKSGIVING_MORNING = Office(
            "Thanksgiving",
            Rank.OPTIONAL,
            Season.PENTECOST,
            "147",
            "Deut. 26:1-11",
            "John 6:26-35",
            "Almighty and gracious Father, we give you thanks for the fruits of the earth in their season and for the labors of those who harvest them. Make us, we pray, faithful stewards of your great bounty, for the provision of our necessities and the relief of all who are in need, to the glory of your Name; through Jesus Christ our Lord, who lives and reigns with you and the Holy Spirit, one God, now and for ever. Amen."
        )

        val THANKSGIVING_EVENING = Office(
            "Thanksgiving",
            Rank.OPTIONAL,
            Season.PENTECOST,
            "145",
            "Joel 2:21-27",
            "1 Thess. 5:12-24",
            "Almighty and gracious Father, we give you thanks for the fruits of the earth in their season and for the labors of those who harvest them. Make us, we pray, faithful stewards of your great bounty, for the provision of our necessities and the relief of all who are in need, to the glory of your Name; through Jesus Christ our Lord, who lives and reigns with you and the Holy Spirit, one God, now and for ever. Amen."
        )

        val LABOR_DAY = Office(
            "Labor Day",
            Rank.OPTIONAL,
            Season.PENTECOST,
            "",
            "",
            "",
            "Almighty God, you have so linked our lives one with another that all we do affects, for good or ill, all other lives: So guide us in the work we do, that we may do it not for self alone, but for the common good; and, as we seek a proper return for our own labor, make us mindful of the rightful aspirations of other workers, and arouse our concern for those who are out of work; through Jesus Christ our Lord, who lives and reigns with you and the Holy Spirit, one God, for ever and ever. Amen."
        )
    }

    fun merge(other: Office) {
        if (this.name.isBlank() || (other.name.isNotBlank() && other.rank > this.rank)) {
            this.name = other.name
        }
        if (this.season == Season.NONE || (other.season != Season.NONE && other.rank > this.rank)) {
            this.season = other.season
        }
        if (this.psalter.isBlank() || (other.psalter.isNotBlank() && other.rank > this.rank)) {
            this.psalter = other.psalter
        }
        if (this.firstReading.isBlank() ||
            (other.firstReading.isNotBlank() && other.rank > this.rank)
        ) {
            this.firstReading = other.firstReading
        }
        if (this.secondReading.isBlank() ||
            (other.secondReading.isNotBlank() && other.rank > this.rank)
        ) {
            this.secondReading = other.secondReading
        }
        if (this.collect.isBlank() || (other.collect.isNotBlank() && other.rank > this.rank)) {
            this.collect = other.collect
        }
        if (other.rank > this.rank) {
            this.rank = other.rank
        }
    }
}

class LiturgicalDay(var morning: Office, var evening: Office, var vigil: Office? = null) {
    companion object {
        fun of(today: LocalDate): LiturgicalDay {
            val weeklyProper = FileRegistry.getWeeklyProper(today)!!

            // Check for saint or other daily proper
            val dailyProper = FileRegistry.getDailyProper(today)
            if (dailyProper != null) {
                weeklyProper.morning.merge(dailyProper.morning)
                weeklyProper.evening.merge(dailyProper.evening)
            }

            // Check tomorrow for a vigil
            val tomorrow = today.plus(1, DateTimeUnit.DAY)
            val tomorrowWeeklyProper = FileRegistry.getWeeklyProper(tomorrow)
            val tomorrowDailyProper = FileRegistry.getDailyProper(tomorrow)
            if (tomorrowWeeklyProper?.vigil != null) {
                weeklyProper.evening.merge(tomorrowWeeklyProper.vigil!!)
            }
            if (tomorrowDailyProper?.vigil != null) {
                weeklyProper.evening.merge(tomorrowDailyProper.vigil!!)
            }

            // If today is Monday and yesterday was a feast, use it
            if (today.dayOfWeek == DayOfWeek.MONDAY) {
                val yesterday = today.minus(1, DateTimeUnit.DAY)
                val yesterdayDailyProper = FileRegistry.getDailyProper(yesterday)
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
                    val tmp = FileRegistry.getDailyProper(LocalDate(today.year, 3, 19))!!
                    weeklyProper.morning.merge(tmp.morning)
                    weeklyProper.evening.merge(tmp.evening)
                } else if (today.dayOfYear - easter.dayOfYear == 9) {
                    val tmp = FileRegistry.getDailyProper(LocalDate(today.year, 3, 25))!!
                    weeklyProper.morning.merge(tmp.morning)
                    weeklyProper.evening.merge(tmp.evening)
                }
            } else if (easter.month == Month.MARCH || (easter.month == Month.APRIL && easter.day == 1)) {
                if (today.dayOfYear - easter.dayOfYear == 8) {
                    val tmp = FileRegistry.getDailyProper(LocalDate(today.year, 3, 25))!!
                    weeklyProper.morning.merge(tmp.morning)
                    weeklyProper.evening.merge(tmp.evening)
                }
            } else if (easter.day >= 18) {
                if (today.dayOfYear - easter.dayOfYear == 8) {
                    val tmp = FileRegistry.getDailyProper(LocalDate(today.year, 4, 25))!!
                    weeklyProper.morning.merge(tmp.morning)
                    weeklyProper.evening.merge(tmp.evening)
                }
            }

            // Check for extra bumped Stephen/John
            if (today.month == Month.DECEMBER && today.day == 29) {
                if (today.dayOfWeek == DayOfWeek.WEDNESDAY) {
                    val tmp = FileRegistry.getDailyProper(LocalDate(today.year, 12, 26))!!
                    weeklyProper.morning.merge(tmp.morning)
                    weeklyProper.evening.merge(tmp.evening)
                } else if (today.dayOfWeek == DayOfWeek.TUESDAY) {
                    val tmp = FileRegistry.getDailyProper(LocalDate(today.year, 12, 27))!!
                    weeklyProper.morning.merge(tmp.morning)
                    weeklyProper.evening.merge(tmp.evening)
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
}

enum class Season {
    NONE,
    ADVENT,
    CHRISTMAS,
    EPIPHANY,
    LENT,
    EASTER,
    PENTECOST
}

enum class Rank {
    NONE,
    FERIA,
    OPTIONAL,
    FEAST,
    SUNDAY,
    PRINCIPAL
}
