package xyz.sachil.staggeredgrid.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Grain
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import xyz.sachil.staggeredgrid.R
import xyz.sachil.staggeredgrid.bean.Topic
import xyz.sachil.staggeredgrid.widget.Orientation
import xyz.sachil.staggeredgrid.widget.StaggeredGridLayout

@Composable
fun StaggeredGridScreen(
    modifier: Modifier = Modifier,
    orientation: Orientation = Orientation.HORIZONTAL
) {
    val topics = listOf(
        Topic("Architecture", 58),
        Topic("Arts & Crafts", 121),
        Topic("Business", 78),
        Topic("Culinary", 118),
        Topic("Design", 423),
        Topic("Fashion", 92),
        Topic("Film", 165),
        Topic("Gaming", 164),
        Topic("Illustration", 326),
        Topic("Lifestyle", 305),
        Topic("Music", 212),
        Topic("Painting", 172),
        Topic("Photography", 321),
        Topic("Technology", 118),
        Topic("Math", 902),
        Topic("News", 1650),
        Topic("Basketball", 14),
        Topic("Football", 36),
        Topic("Drawing", 35),
        Topic("Broadcast", 452),
        Topic("Wine", 1722),
        Topic("Cooking", 521),
        Topic("Shopping", 18)
    )

    Column {
        Spacer(modifier = Modifier.height(116.dp))
        Text(
            modifier = Modifier.padding(16.dp),
            text = "Choose topics that interest you",
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(64.dp))
        StaggeredGridLayout(
            modifier = modifier
                //添加横向滑动功能
                .horizontalScroll(rememberScrollState())
                .padding(4.dp),
            orientation = orientation,
            spanCount = 4
        ) {
            topics.forEach {
                TopicItem(topic = it)
            }
        }
    }
}

@Composable
fun TopicItem(topic: Topic, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .height(72.dp)
            .padding(4.dp),
        color = MaterialTheme.colors.surface
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.mipmap.avator),
                contentDescription = topic.name
            )
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = topic.name,
                    style = MaterialTheme.typography.body1,
                    fontWeight = FontWeight.Bold,
                )

                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Grain, contentDescription = topic.name,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "${topic.count}", style = MaterialTheme.typography.caption)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewStaggeredGirdScreen() {
    StaggeredGridScreen()
}