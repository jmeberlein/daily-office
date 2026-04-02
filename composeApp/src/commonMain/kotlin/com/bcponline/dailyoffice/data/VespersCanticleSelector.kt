package com.bcponline.dailyoffice.data

import com.bcponline.dailyoffice.model.Office
import com.bcponline.dailyoffice.model.Rank
import com.bcponline.dailyoffice.model.Season
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource

/**
 * Vespers canticle selection result.
 *
 * [Independent]: 1st and 2nd canticle tabs are chosen independently.
 * [Linked]: Advent Sunday — a single tab index drives both slots simultaneously.
 * [MagnificatOnly]: Only the Magnificat is shown (one reading, or showFirstCanticle=false).
 */
sealed interface VespersCanticles {
    /** Each list is (name, resource); single-element = no tab shown. */
    data class Independent(
        val first: List<Pair<String, StringResource>>,
        val second: List<Pair<String, StringResource>>
    ) : VespersCanticles

    /** Tab 0 = (Song of the Spirit, Magnificat), Tab 1 = (Magnificat, Nunc Dimittis) */
    data object Linked : VespersCanticles

    data object MagnificatOnly : VespersCanticles
}

object VespersCanticleSelector {

    fun select(office: Office, date: LocalDate, showFirstCanticle: Boolean): VespersCanticles {
        val hasTwoReadings = office.firstReading.isNotBlank()

        if (!hasTwoReadings || !showFirstCanticle)
            return VespersCanticles.MagnificatOnly

        // Two readings + two canticles
        if (office.rank == Rank.FEAST || office.rank == Rank.PRINCIPAL)
            return VespersCanticles.Independent(
                first  = listOf("Magnificat" to CanticleRepository.MAGNIFICAT),
                second = listOf("Nunc Dimittis" to CanticleRepository.NUNC_DIMITTIS)
            )

        if (date.dayOfWeek == DayOfWeek.SUNDAY) {
            if (office.season == Season.ADVENT)
                return VespersCanticles.Linked
            return VespersCanticles.Independent(
                first  = listOf("Magnificat" to CanticleRepository.MAGNIFICAT),
                second = listOf("Nunc Dimittis" to CanticleRepository.NUNC_DIMITTIS)
            )
        }

        val firstOptions = weekdayFirst(date)
        return VespersCanticles.Independent(
            first  = firstOptions,
            second = listOf("Magnificat" to CanticleRepository.MAGNIFICAT)
        )
    }

    private fun weekdayFirst(date: LocalDate): List<Pair<String, StringResource>> = when (date.dayOfWeek) {
        DayOfWeek.MONDAY    -> listOf("Song of the Redeemed" to CanticleRepository.REDEEMED,
                                      "Song of Moses" to CanticleRepository.MOSES)
        DayOfWeek.TUESDAY   -> listOf("Song to the Lamb" to CanticleRepository.LAMB,
                                      "2nd Song of Isaiah" to CanticleRepository.SECOND_ISAIAH)
        DayOfWeek.WEDNESDAY -> listOf("Song of Our Adoption" to CanticleRepository.ADOPTION,
                                      "Song of Praise" to CanticleRepository.PRAISE)
        DayOfWeek.THURSDAY  -> listOf("Song of Faith" to CanticleRepository.FAITH,
                                      "3rd Song of Isaiah" to CanticleRepository.THIRD_ISAIAH)
        DayOfWeek.FRIDAY    -> listOf("Song of Christ's Humility" to CanticleRepository.HUMILITY,
                                      "Song of Praise" to CanticleRepository.PRAISE)
        DayOfWeek.SATURDAY  -> listOf("Song of the Heavenly City" to CanticleRepository.HEAVENLY_CITY,
                                      "1st Song of Isaiah" to CanticleRepository.FIRST_ISAIAH)
        else                -> listOf("Magnificat" to CanticleRepository.MAGNIFICAT)
    }
}
