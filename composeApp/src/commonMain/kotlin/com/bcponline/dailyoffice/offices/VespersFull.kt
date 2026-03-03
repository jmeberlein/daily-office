package com.bcponline.dailyoffice.offices

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bcponline.dailyoffice.EVENING_SUFFRAGES
import com.bcponline.dailyoffice.MORNING_SUFFRAGES
import com.bcponline.dailyoffice.components.OptionPanel
import com.bcponline.dailyoffice.model.LiturgicalDay
import com.bcponline.dailyoffice.model.Season
import kotlinx.datetime.LocalDate

@Composable
fun VespersFull(liturgicalDay: LiturgicalDay, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = liturgicalDay.evening.name,
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

        Text(
            text = "Phos Hilaron",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = """O gracious light,
pure brightness of the everliving Father in heaven,
O Jesus Christ, holy and blessed!

Now as we come to the setting of the sun,
and our eyes behold the vesper light,
we sing your praises, O God: Father, Son, and Holy Spirit.

You are worthy at all times to be praised by happy voices,
O Son of God, O Giver of Life,
and to be glorified through all the worlds.""".trimIndent(),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Psalms Appointed",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Psalms Appointed: ${liturgicalDay.evening.psalter}",
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
            text = "First Reading: ${liturgicalDay.evening.firstReading}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Second Reading: ${liturgicalDay.evening.secondReading}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Magnificat (Luke 1:46-55)",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = """My soul proclaims the greatness of the Lord,
my spirit rejoices in God my Savior; *
    for he has looked with favor on his lowly servant.
From this day all generations will call me blessed: *
    the Almighty has done great things for me,
    and holy is his Name.
He has mercy on those who fear him *
    in every generation.
He has shown the strength of his arm, *
    he has scattered the proud in their conceit.
He has cast down the mighty from their thrones, *
    and has lifted up the lowly.
He has filled the hungry with good things, *
    and the rich he has sent away empty.
He has come to the help of his servant Israel, *
    for he has remembered his promise of mercy,
The promise he made to our fathers, *
    to Abraham and his children for ever. """.trimIndent(),
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
        OptionPanel(options = EVENING_SUFFRAGES)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "The Collect",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = liturgicalDay.evening.collect,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "A Prayer for Mission",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = "Keep watch, dear Lord, with those who work, or watch, or weep this night, and give your angels charge over those who sleep. Tend the sick, Lord Christ; give rest to the weary, bless the dying, soothe the suffering, pity the afflicted, shield the joyous; and all for your love's sake. Amen.",
            style = MaterialTheme.typography.bodyLarge
        )
        if (liturgicalDay.evening.season == Season.EASTER) {
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
