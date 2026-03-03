package com.bcponline.dailyoffice.offices

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bcponline.dailyoffice.MORNING_SUFFRAGES
import com.bcponline.dailyoffice.components.OptionPanel
import com.bcponline.dailyoffice.model.LiturgicalDay
import com.bcponline.dailyoffice.model.Season
import kotlinx.datetime.LocalDate

@Composable
fun MatinsFull(liturgicalDay: LiturgicalDay, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = liturgicalDay.morning.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Invitatory",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = """V. Lord, open my lips.
R. And my mouth shall declare your praise.
Glory to the Father, and to the Son, and to the Holy Spirit:
as it was in the beginning, is now, and will be for ever. Amen.
            """.trimIndent(),
            style = MaterialTheme.typography.bodyLarge
        )

        if (liturgicalDay.morning.season != Season.EASTER) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Psalm 95",
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic
            )
            Text(
                text = """Come, let us sing to the Lord; *
    let us shout for joy to the Rock of our salvation.
Let us come before his presence with thanksgiving *
    and raise a loud shout to him with psalms.

For the Lord is a great God, *
    and a great King above all gods.
In his hand are the caverns of the earth, *
    and the heights of the hills are his also.
The sea is his, for he made it, *
    and his hands have molded the dry land.

Come, let us bow down, and bend the knee, *
    and kneel before the Lord our Maker.
For he is our God,
and we are the people of his pasture and the sheep of his hand. *
    Oh, that today you would hearken to his voice!
    
Harden not your hearts,
as your forebears did in the wilderness, *
    at Meribah, and on that day at Massah,
    when they tempted me.
They put me to the test, *
    though they had seen my works.
     
Forty years long I detested that generation and said, *
    "This people are wayward in their hearts;
    they do not know my ways."
So I swore in my wrath, *
    "They shall not enter into my rest."""".trimIndent(),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pascha Nostrum (1 Cor 5:7-8; Rom 6:9-11; 1 Cor 15:20-22",
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic
            )
            Text(
                text = """Alleluia.
Christ our Passover has been sacrificed for us; *
    therefore let us keep the feast,
Not with the old leaven, the leaven of malice and evil, *
    but with the unleavened bread of sincerity and truth. Alleluia.

Christ being raised from the dead will never die again; *
    death no longer has dominion over him.
The death that he died, he died to sin, once for all; *
    but the life he lives, he lives to God.
So also consider yourselves dead to sin, *
    and alive to God in Jesus Christ our Lord. Alleluia.

Christ has been raised from the dead, *
    the first fruits of those who have fallen asleep.
For since by a man came death, *
    by a man has come also the resurrection of the dead.
For as in Adam all die, *
    so also in Christ shall all be made alive. Alleluia."""".trimIndent(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Psalms Appointed",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Psalms Appointed: ${liturgicalDay.morning.psalter}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = """At the end of the psalms is sung or said:
Glory to the Father, and to the Son, and to the Holy Spirit: *
    as it was in the beginning, is now, and will be for ever. Amen. 
            """.trimIndent(),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "The Readings",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "First Reading: ${liturgicalDay.morning.firstReading}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Second Reading: ${liturgicalDay.morning.secondReading}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Benedictus (Luke 1:68-79)",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = """Blessed be the Lord, the God of Israel; *
    he has come to his people and set them free.
He has raised up for us a mighty savior, *
    born of the house of his servant David.
Through his holy prophets he promised of old,
that he would save us from our enemies, *
    from the hands of all who hate us.
He promised to show mercy to our fathers *
    and to remember his holy covenant.
This was the oath he swore to our father Abraham, *
    to set us free from the hands of our enemies,
Free to worship him without fear, *
    holy and righteous in his sight
    all the days of our life. 
You, my child, shall be called the prophet of the Most High, *
    for you will go before the Lord to prepare his way,
To give his people knowledge of salvation *
    by the forgiveness of their sins.
In the tender compassion of our God *
    the dawn from on high shall break upon us,
To shine on those who dwell in darkness and the
                             shadow of death, *
    and to guide our feet into the way of peace.
    
Glory to the Father, and to the Son, and to the Holy Spirit: *
    as it was in the beginning, is now, and will be for ever. Amen. """.trimIndent(),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "The Prayers",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Our Father",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = """Our Father, who art in heaven,
    hallowed be thy Name,
    thy kingdom come,
    thy will be done,
        on earth as it is in heaven.
Give us this day our daily bread.
And forgive us our trespasses,
    as we forgive those
        who trespass against us.
And lead us not into temptation,
    but deliver us from evil.
For thine is the kingdom,
    and the power, and the glory,
    for ever and ever. Amen.""".trimIndent(),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "The Suffrages",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        OptionPanel(options = MORNING_SUFFRAGES)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "The Collect",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = liturgicalDay.morning.collect,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "A Prayer for Mission",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = "O God, you have made of one blood all the peoples of the earth, and sent your blessed Son to preach peace to those who are far off and to those who are near: Grant that people everywhere may seek after you and find you; bring the nations into your fold; pour out your Spirit upon all flesh; and hasten the coming of your kingdom; through Jesus Christ our Lord. Amen. ",
            style = MaterialTheme.typography.bodyLarge
        )
        if (liturgicalDay.morning.season == Season.EASTER) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Let us bless the Lord. Alleluia, alleluia.\n" +
                        "Thanks be to God. Alleluia, alleluia.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Let us bless the Lord.\n" +
                        "Thanks be to God.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
