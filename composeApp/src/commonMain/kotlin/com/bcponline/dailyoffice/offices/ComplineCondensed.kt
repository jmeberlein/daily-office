package com.bcponline.dailyoffice.offices

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bcponline.dailyoffice.model.LiturgicalDay
import kotlinx.datetime.LocalDate

@Composable
fun ComplineCondensed(liturgicalDay: LiturgicalDay, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = liturgicalDay.evening.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "At the Close of Day",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Psalm 134",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = """Behold now, bless the LORD, all you servants of the LORD, *
    you that stand by night in the house of the LORD.
Lift up your hands in the holy place and bless the LORD; *
    the LORD who made heaven and earth bless you out of Zion.""".trimIndent(),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "A Reading (Jeremiah 14:9,22)",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = "Lord, you are in the midst of us and we are called by your Name: Do not forsake us, O Lord our God.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Nunc Dimittis (Luke 2:29-32)",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = """Lord, you now have set your servant free *
    to go in peace as you have promised;
For these eyes of mine have seen the Savior, *
    whom you have prepared for all the world to see;
A Light to enlighten the nations, *
    and the glory of your people Israel.""".trimIndent(),
            style = MaterialTheme.typography.bodyLarge
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
            text = "The Collect",
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic
        )
        Text(
            text = "Visit this place, O Lord, and drive far from it all snares of the " +
                    "enemy; let your holy angels dwell with us to preserve us in " +
                    "peace; and let your blessing be upon us always; through Jesus " +
                    "Christ our Lord. Amen.",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "The almighty and merciful Lord, Father, Son, and Holy Spirit, " +
                    "bless us and keep us. Amen.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
