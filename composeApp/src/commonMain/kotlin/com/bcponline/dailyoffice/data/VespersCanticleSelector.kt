package com.bcponline.dailyoffice.data

import com.bcponline.dailyoffice.model.Office
import com.bcponline.dailyoffice.model.Rank
import com.bcponline.dailyoffice.model.Season
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource

data class Canticle(val name: String, val reference: String, val resource: StringResource)

/**
 * Vespers canticle selection result.
 *
 * [Independent]: 1st and 2nd canticle lists are chosen independently.
 * [Linked]: Advent Sunday — a single tab index drives both slots simultaneously.
 *           pairs[i] = (first canticle, second canticle) for tab i.
 * [MagnificatOnly]: Only the Magnificat is shown (one reading, or showFirstCanticle=false).
 */
sealed interface VespersCanticles {
    data class Independent(
        val first: List<Canticle>,
        val second: List<Canticle>
    ) : VespersCanticles

    /** pairs[i] = (first canticle, second canticle) for tab i */
    data class Linked(val pairs: List<Pair<Canticle, Canticle>>) : VespersCanticles

    data object MagnificatOnly : VespersCanticles
}

object VespersCanticleSelector {

    fun select(office: Office, date: LocalDate, showFirstCanticle: Boolean): VespersCanticles {
        val hasTwoReadings = office.firstReading.isNotBlank()

        if (!hasTwoReadings || !showFirstCanticle)
            return VespersCanticles.MagnificatOnly

        if (office.rank == Rank.FEAST || office.rank == Rank.PRINCIPAL)
            return VespersCanticles.Independent(
                first  = listOf(MAGNIFICAT),
                second = listOf(NUNC_DIMITTIS)
            )

        if (date.dayOfWeek == DayOfWeek.SUNDAY) {
            if (office.season == Season.ADVENT)
                return VespersCanticles.Linked(listOf(
                    SPIRIT to MAGNIFICAT,
                    MAGNIFICAT to NUNC_DIMITTIS
                ))
            return VespersCanticles.Independent(
                first  = listOf(MAGNIFICAT),
                second = listOf(NUNC_DIMITTIS)
            )
        }

        return VespersCanticles.Independent(
            first  = weekdayFirst(date),
            second = listOf(MAGNIFICAT)
        )
    }

    private fun weekdayFirst(date: LocalDate): List<Canticle> = when (date.dayOfWeek) {
        DayOfWeek.MONDAY    -> listOf(REDEEMED, MOSES)
        DayOfWeek.TUESDAY   -> listOf(LAMB, SECOND_ISAIAH)
        DayOfWeek.WEDNESDAY -> listOf(ADOPTION, PRAISE, CREATION)
        DayOfWeek.THURSDAY  -> listOf(FAITH, THIRD_ISAIAH)
        DayOfWeek.FRIDAY    -> listOf(HUMILITY, PRAISE, CREATION)
        DayOfWeek.SATURDAY  -> listOf(HEAVENLY_CITY, FIRST_ISAIAH)
        else                -> listOf(MAGNIFICAT)
    }

    // Canticle definitions with references
    val PHOS_HILARON          = Canticle("Phos Hilaron",            "Traditional",                     CanticleRepository.PHOS_HILARON)
    val PHOS_HILARON_METRICAL = Canticle("Phos Hilaron (Metrical)", "Traditional",                     CanticleRepository.PHOS_HILARON_METRICAL)
    val MAGNIFICAT    = Canticle("Magnificat",                "Luke 1:46-55",                    CanticleRepository.MAGNIFICAT)
    val NUNC_DIMITTIS = Canticle("Nunc Dimittis",             "Luke 2:29-32",                    CanticleRepository.NUNC_DIMITTIS)
    val SPIRIT        = Canticle("Song of the Spirit",      "Revelation 22:12-17",             CanticleRepository.SPIRIT)
    val REDEEMED      = Canticle("Song of the Redeemed",    "Revelation 15:3-4",               CanticleRepository.REDEEMED)
    val MOSES         = Canticle("Song of Moses",           "Exodus 15:1-6,11-13,17-18",       CanticleRepository.MOSES)
    val LAMB          = Canticle("Song to the Lamb",        "Revelation 4:11, 5:9-10,13",      CanticleRepository.LAMB)
    val SECOND_ISAIAH = Canticle("Second Song of Isaiah",   "Isaiah 55:6-11",                  CanticleRepository.SECOND_ISAIAH)
    val ADOPTION      = Canticle("Song of Our Adoption",    "Ephesians 1:3-10",                CanticleRepository.ADOPTION)
    val PRAISE        = Canticle("Song of Praise",          "Three Children 29-34",            CanticleRepository.PRAISE)
    val CREATION      = Canticle("Song of Creation",        "Three Children 35-65",            CanticleRepository.CREATION)
    val FAITH         = Canticle("Song of Faith",           "1 Peter 1:3-4,18-21",             CanticleRepository.FAITH)
    val THIRD_ISAIAH  = Canticle("Third Song of Isaiah",    "Isaiah 60:1-3,11a,14c,18-19",     CanticleRepository.THIRD_ISAIAH)
    val HUMILITY      = Canticle("Song of Christ's Humility","Philippians 2:6-11",             CanticleRepository.HUMILITY)
    val HEAVENLY_CITY = Canticle("Song of the Heavenly City","Revelation 21:22-26, 22:1-4",    CanticleRepository.HEAVENLY_CITY)
    val FIRST_ISAIAH  = Canticle("First Song of Isaiah",    "Isaiah 12:2-6",                   CanticleRepository.FIRST_ISAIAH)
}
