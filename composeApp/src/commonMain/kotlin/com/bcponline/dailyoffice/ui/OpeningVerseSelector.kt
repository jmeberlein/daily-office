package com.bcponline.dailyoffice.ui

import com.bcponline.dailyoffice.model.Office
import com.bcponline.dailyoffice.model.Rank
import com.bcponline.dailyoffice.model.Season
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

object OpeningVerseSelector {
    fun getVerse(office: Office, date: LocalDate): String? {
        return when {
            office.season == Season.LENT && office.rank == Rank.HOLY_WEEK -> 
                "All we like sheep have gone astray; we have turned every one to his own way; and the Lord has laid on him the iniquity of us all.    Isaiah 53:6"
            
            office.season == Season.ADVENT -> {
                if (date.month == Month.DECEMBER && date.dayOfMonth >= 17)
                    "The glory of the Lord shall be revealed, and all flesh shall see it together.    Isaiah 40:5"
                else
                    "In the wilderness prepare the way of the Lord, make straight in the desert a highway for our God.    Isaiah 40:3"
            }
            
            office.season == Season.CHRISTMAS -> 
                "Behold, I bring you good news of a great joy, which will come to all the people; for unto you is born this day in the city of David, a Savior, who is Christ the Lord.    Luke 2:10, 11"
            
            office.season == Season.EPIPHANY -> 
                "I will give you as a light to the nations, that my salvation may reach to the end of the earth.    Isaiah 49:6b"
            
            office.season == Season.LENT -> 
                "If we say we have no sin, we deceive ourselves, and the truth is not in us; but if we confess our sins, God who is faithful and just, will forgive our sins and cleanse us from all unrighteousness.    I John 1:8, 9"
            
            office.season == Season.EASTER -> 
                "Thanks be to God, who gives us the victory through our Lord Jesus Christ.    1 Corinthians 15:57"
            
            office.season == Season.ASCENSION -> 
                "Christ has entered, not into a sanctuary made with hands, a copy of the true one, but into heaven itself, now to appear in the presence of God on our behalf.    Hebrews 9:24"
            
            office.season == Season.PENTECOST -> 
                "Grace to you and peace from God our Father and from the Lord Jesus Christ.    Philippians 1:2"
            
            else -> null
        }
    }
}
