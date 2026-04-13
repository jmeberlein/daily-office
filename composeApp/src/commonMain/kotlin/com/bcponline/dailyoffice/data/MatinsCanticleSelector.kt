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
    fun firstCanticleOptions(office: Office, date: LocalDate): List<Canticle> {
        if (office.rank == Rank.FEAST || office.rank == Rank.PRINCIPAL) return listOf(TE_DEUM)

        return when (date.dayOfWeek) {
            DayOfWeek.SUNDAY -> when (office.season) {
                Season.ADVENT    -> listOf(THIRD_ISAIAH, WILDERNESS)
                Season.CHRISTMAS -> listOf(HANNAH, TE_DEUM)
                Season.LENT      -> listOf(HOSEA, KYRIE_PANTOKRATOR)
                Season.EASTER    -> listOf(MOSES)
                else             -> listOf(TE_DEUM)
            }
            DayOfWeek.MONDAY    -> listOf(WISDOM, FIRST_ISAIAH)
            DayOfWeek.TUESDAY   -> listOf(PILGRIMAGE, PRAISE, CREATION)
            DayOfWeek.WEDNESDAY -> when (office.season) {
                Season.LENT     -> listOf(EZEKIEL, KYRIE_PANTOKRATOR)
                else            -> listOf(EZEKIEL, THIRD_ISAIAH)
            }
            DayOfWeek.THURSDAY  -> listOf(JUDITH, MOSES)
            DayOfWeek.FRIDAY    -> listOf(KYRIE_PANTOKRATOR, SECOND_ISAIAH)
            DayOfWeek.SATURDAY  -> listOf(PRAISE, CREATION)
            else                -> listOf(TE_DEUM)
        }
    }

    fun invitatoryCanticle(office: Office): Canticle = when (office.season) {
        Season.EASTER -> PASCHA_NOSTRUM
        Season.LENT   -> PSALM_95_LENT
        else          -> PSALM_95
    }

    fun invitatoryResource(office: Office): StringResource = invitatoryCanticle(office).resource

    val BENEDICTUS      = Canticle("Benedictus",             "Luke 1:68-79",                    CanticleRepository.BENEDICTUS)
    val TE_DEUM         = Canticle("Te Deum",                "Traditional",                     CanticleRepository.TE_DEUM)
    val PSALM_95        = Canticle("Psalm 95",               "",                                CanticleRepository.PSALM_95)
    val PSALM_95_LENT   = Canticle("Psalm 95",               "",                                CanticleRepository.PSALM_95_LENT)
    val PASCHA_NOSTRUM  = Canticle("Pascha Nostrum",         "",                                CanticleRepository.PASCHA_NOSTRUM)
    val THIRD_ISAIAH    = Canticle("Third Song of Isaiah",   "Isaiah 60:1-3,11a,14c,18-19",     CanticleRepository.THIRD_ISAIAH)
    val WILDERNESS      = Canticle("Song of the Wilderness", "Isaiah 35:1-7,10",                CanticleRepository.WILDERNESS)
    val HANNAH          = Canticle("Song of Hannah",         "1 Samuel 2:1-8",                  CanticleRepository.HANNAH)
    val HOSEA           = Canticle("Song of Hosea",          "Hosea 6:1-3",                     CanticleRepository.HOSEA)
    val KYRIE_PANTOKRATOR = Canticle("Song of Penitence",    "Prayer of Manasseh 1-2,4,6-7,11-15", CanticleRepository.KYRIE_PANTOKRATOR)
    val MOSES           = Canticle("Song of Moses",          "Exodus 15:1-6,11-13,17-18",       CanticleRepository.MOSES)
    val WISDOM          = Canticle("Song of Wisdom",         "Wisdom 10:15-19,20b-21",          CanticleRepository.WISDOM)
    val FIRST_ISAIAH    = Canticle("First Song of Isaiah",   "Isaiah 12:2-6",                   CanticleRepository.FIRST_ISAIAH)
    val PILGRIMAGE      = Canticle("Song of Pilgrimage",     "Ecclesiasticus 51:13-16,20b-22",  CanticleRepository.PILGRIMAGE)
    val PRAISE          = Canticle("Song of Praise",         "Three Children 29-34",            CanticleRepository.PRAISE)
    val CREATION        = Canticle("Song of Creation",       "Three Children 35-65",            CanticleRepository.CREATION)
    val EZEKIEL         = Canticle("Song of Ezekiel",        "Ezekiel 36:24-28",                CanticleRepository.EZEKIEL)
    val SECOND_ISAIAH   = Canticle("Second Song of Isaiah",  "Isaiah 55:6-11",                  CanticleRepository.SECOND_ISAIAH)
    val JUDITH          = Canticle("Song of Judith",         "Judith 16:13-16",                 CanticleRepository.JUDITH)
}
