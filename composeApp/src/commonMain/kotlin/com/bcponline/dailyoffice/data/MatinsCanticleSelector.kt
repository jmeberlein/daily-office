package com.bcponline.dailyoffice.data

import com.bcponline.dailyoffice.model.Office
import com.bcponline.dailyoffice.model.Rank
import com.bcponline.dailyoffice.model.Season
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.StringResource

/**
 * Returns the 1st canticle options for Morning Prayer.
 * A single-element list means no choice; two elements means offer a tab.
 * The Benedictus (2nd canticle) is always fixed and not returned here.
 */
object MatinsCanticleSelector {
    fun firstCanticleOptions(office: Office, date: LocalDate): List<Pair<String, StringResource>> {
        if (office.rank == Rank.FEAST || office.rank == Rank.PRINCIPAL) return listOf("Te Deum" to CanticleRepository.TE_DEUM)

        return when (date.dayOfWeek) {
            DayOfWeek.SUNDAY -> when (office.season) {
                Season.ADVENT    -> listOf("3rd Song of Isaiah" to CanticleRepository.THIRD_ISAIAH,
                                           "Song of the Wilderness" to CanticleRepository.WILDERNESS)
                Season.CHRISTMAS -> listOf("Song of Hannah" to CanticleRepository.HANNAH,
                                           "Te Deum" to CanticleRepository.TE_DEUM)
                Season.LENT      -> listOf("Song of Hosea" to CanticleRepository.HOSEA,
                                           "Kyrie Pantokrator" to CanticleRepository.KYRIE_PANTOKRATOR)
                Season.EASTER    -> listOf("Song of Moses" to CanticleRepository.MOSES)
                else             -> listOf("Te Deum" to CanticleRepository.TE_DEUM)
            }
            DayOfWeek.MONDAY    -> listOf("Song of Wisdom" to CanticleRepository.WISDOM,
                                          "1st Song of Isaiah" to CanticleRepository.FIRST_ISAIAH)
            DayOfWeek.TUESDAY   -> listOf("Song of Pilgrimage" to CanticleRepository.PILGRIMAGE,
                                          "Song of Praise" to CanticleRepository.PRAISE)
            DayOfWeek.WEDNESDAY -> when (office.season) {
                Season.LENT     -> listOf("Song of Ezekiel" to CanticleRepository.EZEKIEL,
                                          "Kyrie Pantokrator" to CanticleRepository.KYRIE_PANTOKRATOR)
                else            -> listOf("Song of Ezekiel" to CanticleRepository.EZEKIEL,
                                          "3rd Song of Isaiah" to CanticleRepository.THIRD_ISAIAH)
            }
            DayOfWeek.THURSDAY  -> listOf("Song of Judith" to CanticleRepository.JUDITH,
                                          "Song of Moses" to CanticleRepository.MOSES)
            DayOfWeek.FRIDAY    -> listOf("Kyrie Pantokrator" to CanticleRepository.KYRIE_PANTOKRATOR,
                                          "2nd Song of Isaiah" to CanticleRepository.SECOND_ISAIAH)
            DayOfWeek.SATURDAY  -> listOf("Song of Praise" to CanticleRepository.PRAISE)
            else                -> listOf("Te Deum" to CanticleRepository.TE_DEUM)
        }
    }

    fun invitatoryResource(office: Office): StringResource = when (office.season) {
        Season.EASTER -> CanticleRepository.PASCHA_NOSTRUM
        Season.LENT   -> CanticleRepository.PSALM_95_LENT
        else          -> CanticleRepository.PSALM_95
    }
}
