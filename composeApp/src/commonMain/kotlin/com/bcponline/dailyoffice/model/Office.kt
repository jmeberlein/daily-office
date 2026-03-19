package com.bcponline.dailyoffice.model

class Office(
    var name: String,
    var rank: Rank,
    var season: Season,
    var psalter: String,
    var firstReading: String,
    var secondReading: String,
    var collect: String,
    var color: LiturgicalColor = LiturgicalColor.NONE
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
                """Almighty and everlasting God, by whose Spirit the whole
body of your faithful people is governed and sanctified:
Receive our supplications and prayers, which we offer before
you for all members of your holy Church, that in their vocation
and ministry they may truly and devoutly serve you; through
our Lord and Savior Jesus Christ, who lives and reigns with
you, in the unity of the Holy Spirit, one God, now and for ever.
Amen."""
            )

        val PENTECOST_WEEKDAY =
            Office(
                "The {{day_of_week}} after Pentecost",
                Rank.NAMED_FERIA,
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
            """Almighty and gracious Father, we give you thanks for the
fruits of the earth in their season and for the labors of those
who harvest them. Make us, we pray, faithful stewards of
your great bounty, for the provision of our necessities and
the relief of all who are in need, to the glory of your Name;
through Jesus Christ our Lord, who lives and reigns with
you and the Holy Spirit, one God, now and for ever. Amen."""
        )

        val THANKSGIVING_EVENING = Office(
            "Thanksgiving",
            Rank.OPTIONAL,
            Season.PENTECOST,
            "145",
            "Joel 2:21-27",
            "1 Thess. 5:12-24",
            """Almighty and gracious Father, we give you thanks for the
fruits of the earth in their season and for the labors of those
who harvest them. Make us, we pray, faithful stewards of
your great bounty, for the provision of our necessities and
the relief of all who are in need, to the glory of your Name;
through Jesus Christ our Lord, who lives and reigns with
you and the Holy Spirit, one God, now and for ever. Amen.""",
            LiturgicalColor.WHITE
        )

        val LABOR_DAY = Office(
            "Labor Day",
            Rank.OPTIONAL,
            Season.PENTECOST,
            "",
            "",
            "",
            """Almighty God, you have so linked our lives one with another
that all we do affects, for good or ill, all other lives: So guide
us in the work we do, that we may do it not for self alone, but
for the common good; and, as we seek a proper return for
our own labor, make us mindful of the rightful aspirations of
other workers, and arouse our concern for those who are out
of work; through Jesus Christ our Lord, who lives and reigns
with you and the Holy Spirit, one God, for ever and ever.
Amen."""
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
        if (this.color == LiturgicalColor.NONE || (other.color != LiturgicalColor.NONE && other.rank > this.rank)) {
            this.color = other.color
        }
        if (other.rank > this.rank) {
            this.rank = other.rank
        }
    }
}