package xyz.sachil.animation.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.sachil.animation.ui.theme.*
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


enum class TabPage {
    Home, Work
}

private val topics = listOf(
    "2 new packages arrived",
    "DIY project recommendation",
    "Festival next month",
    "New flower seeds available"
)

private val tasks = listOf(
    "Buy milk",
    "Choose curtain",
    "Plant rosemary",
    "Finish the essay",
    "Receive new packages",
    "Take a photo"
)

private const val TOPIC_INFO = "Lorem ipsum dolor sit amet, consectetur adipiscing elit," +
        " sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad " +
        "minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea " +
        "commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse" +
        " cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident," +
        " sunt in culpa qui officia deserunt mollit anim id est laborum."

@Composable
fun HomeScreen() {
    //???????????????????????????tab
    var selectedTabPage by remember { mutableStateOf(TabPage.Home) }
    //????????????edit message??????????????????
    var editMessageShown by remember { mutableStateOf(false) }
    //??????????????????????????????
    var loadingWeather by remember { mutableStateOf(false) }
    //???????????????????????????topic
    var expandedTopic by remember { mutableStateOf<String?>(null) }
    //????????????LazyColumn????????????
    val lazyListState = rememberLazyListState()
    val backgroundColor = if (selectedTabPage == TabPage.Home) Purple200 else Green300

    val allTasks = remember { tasks.toMutableStateList() }

    val coroutineScope = rememberCoroutineScope()

    suspend fun showEditMessage() {
        if (!editMessageShown) {
            editMessageShown = true
            delay(3000L)
            editMessageShown = false
        }
    }

    suspend fun loadWeather() {
        if (!loadingWeather) {
            loadingWeather = true
            delay(3000L)
            loadingWeather = false
        }
    }
    //?????????composition??????????????????weather,
    // LaunchedEffect??????????????????compose?????????
    LaunchedEffect(Unit) {
        loadWeather()
    }
    Scaffold(
        topBar = {
            HomeTopBar(
                selectedTabPage = selectedTabPage,
                backgroundColor = backgroundColor,
                onPageSelected = {
                    selectedTabPage = it
                }
            )
        },
        backgroundColor = backgroundColor,
        floatingActionButton = {
            HomeFloatActionButton(
                onclick = {
                    coroutineScope.launch {
                        showEditMessage()
                    }
                },
                expanded = lazyListState.isScrollingUp()
            )
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp),
            state = lazyListState
        ) {
            item { Header(title = "Weather") }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                if (loadingWeather) {
                    WeatherLoadingRow()
                } else {
                    WeatherRow(onRefresh = {
                        coroutineScope.launch {
                            loadWeather()
                        }
                    })
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
            item { Header(title = "Topics") }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            items(topics) { topic ->
                TopicRow(
                    topic = topic,
                    expanded = expandedTopic == topic,
                    onclick = { expandedTopic = if (expandedTopic == topic) null else topic })
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
            item { Header(title = "Tasks") }
            item { Spacer(modifier = Modifier.height(16.dp)) }

            if (allTasks.isEmpty()) {
                item {
                    Surface(color = MaterialTheme.colors.surface, elevation = 4.dp) {
                        TextButton(
                            onClick = { allTasks.clear(); allTasks.addAll(tasks) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "ADD TASKS", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            } else {
                items(allTasks.count()) { index ->
                    val task = allTasks.getOrNull(index)
                    if (task != null) {
                        key(task) {
                            TaskRow(task = task, onRemove = { allTasks.remove(task) })
                        }
                    }
                }
            }
        }

        EditMessage(shown = editMessageShown)

    }
}

@Composable
private fun HomeTopBar(
    selectedTabPage: TabPage,
    backgroundColor: Color,
    onPageSelected: (TabPage) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabPage.ordinal,
        backgroundColor = backgroundColor,
        indicator = { tabPositions ->
            HomeTabIndicator(
                tabPositions = tabPositions,
                tabPage = selectedTabPage
            )
        }) {
        HomeTab(
            icon = Icons.Default.Home,
            title = "Home",
            clickable = { onPageSelected(TabPage.Home) })
        HomeTab(
            icon = Icons.Default.AccountBox,
            title = "Work",
            clickable = { onPageSelected(TabPage.Work) })
    }
}

@Composable
private fun HomeTabIndicator(tabPositions: List<TabPosition>, tabPage: TabPage) {
    //???Indicator???????????????updateTransition????????????Transition?????????????????????????????????????????????targetState
    //???????????????????????????????????????????????????????????????
    val transition = updateTransition(targetState = tabPage, label = "HomeTabIndicator")
    //animate*?????????????????????State???????????????????????????by???????????????
    val color by transition.animateColor(label = "color") {
        if (it == TabPage.Home) Purple700 else Green800
    }
    //???tab page???Home???Work??????indicator??????????????????????????????????????????????????????????????????????????????????????????
    val indicatorLeft by transition.animateDp(label = "indicatorLeft",
        transitionSpec = {
            if (TabPage.Home isTransitioningTo TabPage.Work) {
                spring(stiffness = Spring.StiffnessLow)
            } else {
                spring(stiffness = Spring.StiffnessMedium)
            }
        }) {
        tabPositions[it.ordinal].left
    }
    //???tab page???Home???Work??????indicator?????????????????????????????????????????????????????????????????????????????????
    val indicatorRight by transition.animateDp(label = "indicatorRight",
        transitionSpec = {
            if (TabPage.Home isTransitioningTo TabPage.Work) {
                spring(stiffness = Spring.StiffnessMedium)
            } else {
                spring(stiffness = Spring.StiffnessLow)
            }
        }) {
        tabPositions[it.ordinal].right
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(align = Alignment.BottomStart)
            .offset(x = indicatorLeft)
            .width(indicatorRight - indicatorLeft)
            .padding(4.dp)
            .fillMaxSize()
            .border(border = BorderStroke(4.dp, color), shape = RoundedCornerShape(4.dp))
    )

}

@Composable
private fun HomeTab(
    icon: ImageVector,
    title: String,
    clickable: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = clickable)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = icon, contentDescription = title)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.button)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun HomeFloatActionButton(onclick: () -> Unit, expanded: Boolean) {
    FloatingActionButton(onClick = onclick) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
            AnimatedVisibility(visible = expanded) {
                Text(text = "EDIT", modifier = Modifier.padding(start = 8.dp, top = 3.dp))
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun EditMessage(shown: Boolean) {
    //?????????????????????????????????
    AnimatedVisibility(
        visible = shown,
        enter = slideInVertically(
            //???-fullHeight???0
            initialOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
        ),
        exit = slideOutVertically(
            //???0???-fullHeight
            targetOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(durationMillis = 200, easing = FastOutLinearInEasing)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.secondary,
            elevation = 4.dp
        ) {
            Text(text = "Edit feature is not supported", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun Header(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.h5,
        modifier = modifier.semantics { heading() }
    )
}

@Composable
fun WeatherRow(onRefresh: () -> Unit) {
    Surface(color = MaterialTheme.colors.surface, elevation = 2.dp) {
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = 64.dp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Amber600)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "18 ???", fontSize = 24.sp)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onRefresh) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
            }

        }

    }
}

@Composable
fun WeatherLoadingRow() {
    //?????????????????????????????????rememberInfiniteTransition????????????InfiniteTransition?????????????????????????????????????????????
    //???????????????????????????
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = InfiniteRepeatableSpec(animation = keyframes {
            durationMillis = 1000
            0.7f at 500
        }, repeatMode = RepeatMode.Reverse)
    )

    val backgroundColor = Color.LightGray.copy(alpha = alpha)

    Surface(color = MaterialTheme.colors.surface, elevation = 2.dp) {
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = 64.dp)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .padding(end = 16.dp)
                    .background(backgroundColor)
            )
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TopicRow(topic: String, expanded: Boolean, onclick: () -> Unit) {
    TopicRowSpacer(shown = expanded)
    Surface(
        color = MaterialTheme.colors.surface,
        elevation = 4.dp,
        onClick = onclick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            //??????????????????????????????????????????????????????view???Modifier?????????view????????????????????????????????????????????????
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            Row {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Info")
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = topic, style = MaterialTheme.typography.body1)
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = TOPIC_INFO, textAlign = TextAlign.Justify)
            }
        }
    }
    TopicRowSpacer(shown = expanded)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun TopicRowSpacer(shown: Boolean) {
    AnimatedVisibility(visible = shown) {
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun TaskRow(task: String, onRemove: () -> Unit) {
    Surface(
        color = MaterialTheme.colors.surface,
        modifier = Modifier
            .fillMaxWidth()
            .swipeToRemove(onRemove = onRemove)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Done, contentDescription = "Done")
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = task, style = MaterialTheme.typography.body1)
        }
    }
}

/**
 * ??????LazyColumn??????????????????
 */
@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

/**
 * ??????????????????????????????????????????????????????
 */
private fun Modifier.swipeToRemove(onRemove: () -> Unit): Modifier = composed {
    //offsetX?????????Animatable????????????????????????????????????????????????
    val offsetX = remember { Animatable(0f) }
    pointerInput(Unit) {
        //????????????fling?????????????????????
        val decay = splineBasedDecay<Float>(this)
        coroutineScope {
            while (true) {
                //??????action down??????
                val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                //??????????????????????????????????????????????????????
                offsetX.stop()
                //???????????????????????????
                val velocityTracker = VelocityTracker()
                //????????????drag??????
                awaitPointerEventScope {
                    horizontalDrag(pointerId) { change ->
                        //??????drag????????????
                        val horizontalDragOffset = offsetX.value + change.positionChange().x
                        //?????????????????????offsetX??????
                        launch {
                            offsetX.snapTo(horizontalDragOffset)
                        }
                        //??????drag?????????
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        //?????????????????????
                        change.consumePositionChange()
                    }
                }
                //drag????????????????????????????????????fling??????
                val velocity = velocityTracker.calculateVelocity().x
                //?????????fling???????????????
                val targetOffsetX = decay.calculateTargetValue(offsetX.value, velocity)

                //??????????????????????????????????????????????????????????????????
                offsetX.updateBounds(
                    lowerBound = -size.width.toFloat(),
                    upperBound = size.width.toFloat()
                )
                //???????????????????????????
                launch {
                    if (targetOffsetX.absoluteValue <= size.width) {
                        //???fling???????????????????????????????????????
                        offsetX.animateTo(targetValue = 0f, initialVelocity = velocity)
                    } else {
                        //???fling????????????????????????????????????
                        offsetX.animateDecay(velocity, decay)
                        onRemove()
                    }
                }
            }
        }

    }.offset {
        //???offsetX?????????????????????
        IntOffset(offsetX.value.roundToInt(), 0)
    }
}

@Preview
@Composable
fun PreviewHomeTabBar() {
    HomeTopBar(selectedTabPage = TabPage.Home,
        backgroundColor = Purple200,
        onPageSelected = {})
}

@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}
