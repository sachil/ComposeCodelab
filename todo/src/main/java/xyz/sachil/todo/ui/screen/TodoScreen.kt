package xyz.sachil.todo.ui.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import xyz.sachil.todo.bean.TodoIcon
import xyz.sachil.todo.bean.TodoItem
import kotlin.random.Random

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun TodoScreen(
    todoItems: List<TodoItem>,
    currentlyEditTodoItem: TodoItem?,
    //添加TodoItem时的事件触发
    addTodoItem: (TodoItem?) -> Unit,
    //删除TodoItem时的事件触发
    removeTodoItem: (TodoItem) -> Unit,
    //修改TodoItem时的事件触发
    changeTodoItem: (TodoItem) -> Unit,
    //点击TodoItem准备开始编辑时的事件触发
    startEditTodoItem: (TodoItem) -> Unit,
    //停止编辑TodoItem时的事件触发
    stopEditTodoItem: () -> Unit
) {

    Surface(color = MaterialTheme.colors.surface) {
        Column {
            //是否存在正在编辑的TodoItem
            val isTodoItemEditing = currentlyEditTodoItem != null

            TodoItemInputBackground(elevate = isTodoItemEditing.not()) {
                //如果存在正在编辑的TodoItem，则显示"Editing item"的title，否则显示TodoItemInputEntry
                if (isTodoItemEditing) {
                    Text(
                        text = "Editing item",
                        style = MaterialTheme.typography.h6,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )

                } else {
                    TodoItemInputEntry(addTodoItem)
                }
            }
            //显示TodoItem列表
            TodoItemList(
                todoItems = todoItems,
                currentlyEditTodoItem = currentlyEditTodoItem,
                startEditTodoItem = startEditTodoItem,
                stopEditTodoItem = stopEditTodoItem,
                removeTodoItem = removeTodoItem,
                //将TodoItem填满列表剩余的空间
                modifier = Modifier.weight(1F),
                changeTodoItem = changeTodoItem
            )
            //显示添加随机TodoItem的button
            RandomTodoItemButton(onclick = { addTodoItem(null) })
        }
    }


}


@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun TodoItemList(
    todoItems: List<TodoItem>,
    currentlyEditTodoItem: TodoItem?,
    startEditTodoItem: (TodoItem) -> Unit,
    stopEditTodoItem: () -> Unit,
    changeTodoItem: (TodoItem) -> Unit,
    removeTodoItem: (TodoItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier) {
        items(todoItems) { todoItem ->
            //如果该TodoItem正在被编辑，则显示TodoItemInput，否则显示TodoItemRow
            if (currentlyEditTodoItem?.id == todoItem.id) {
                TodoItemInput(
                    text = todoItem.task,
                    onTextChanged = { changeTodoItem(todoItem.copy(task = it)) },
                    selectedTodoIcon = todoItem.todoIcon,
                    onTodoIconSelected = { changeTodoItem(todoItem.copy(todoIcon = it)) },
                    submit = { stopEditTodoItem() },

                    buttonSlot = {
                        Row() {
                            IconButton(onClick = { stopEditTodoItem() }) {
                                Icon(
                                    imageVector = Icons.Default.Save,
                                    contentDescription = "Save",
                                    tint = MaterialTheme.colors.secondary
                                )
                            }

                            IconButton(onClick = { removeTodoItem(todoItem) }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colors.error
                                )
                            }
                        }
                    })
            } else {
                TodoItemRow(item = todoItem, startEditTodoItem = startEditTodoItem)
            }

        }
    }
}

@Composable
fun TodoItemRow(
    item: TodoItem,
    startEditTodoItem: (TodoItem) -> Unit,
    modifier: Modifier = Modifier,
    alphaTint: Float = remember { randomTint() }
) {
    Column {
        Row(
            modifier = modifier
                .height(48.dp)
                .fillMaxWidth()
                .clickable { startEditTodoItem(item) }
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = item.task, style = MaterialTheme.typography.body1)
            IconButton(onClick = { startEditTodoItem(item) }) {
                Icon(
                    imageVector = item.todoIcon.icon,
                    contentDescription = item.todoIcon.description,
                    tint = LocalContentColor.current.copy(alpha = alphaTint)
                )
            }
        }
        Divider()
    }
}


@Composable
fun RandomTodoItemButton(modifier: Modifier = Modifier, onclick: () -> Unit) {
    Button(
        onClick = onclick,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(48.dp)
    ) {
        Text(text = "Add random item", style = MaterialTheme.typography.body1)
    }
}


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun TodoItemInputEntry(addTodoItem: (TodoItem?) -> Unit) {
    //保存TextField中的内容，否则当compose重组时，内容会丢失
    val (text, setText) = remember { mutableStateOf("") }
    //保存selectedTodoIcon，否则当compose重组时，selectedTodoIcon会丢失
    val (selectedTodoIcon, setSelectedTodoIcon) = remember { mutableStateOf(TodoIcon.Default) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val submit = {
        addTodoItem(TodoItem(text, selectedTodoIcon))
        keyboardController?.hide()
        setText("")
    }

    TodoItemInput(
        text,
        setText,
        selectedTodoIcon,
        setSelectedTodoIcon,
        submit,
        //AddButton
        buttonSlot = {
            Button(onClick = submit, shape = CircleShape, enabled = text.isNotBlank()) {
                Text(text = "Add")
            }
        }
    )
}


/**
 * 将TodoItemInput定义为Stateless(无状态的)，并使用slots(view的占位符)可以实现更好的复用
 */
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun TodoItemInput(
    text: String,
    onTextChanged: (String) -> Unit,
    selectedTodoIcon: TodoIcon,
    onTodoIconSelected: (TodoIcon) -> Unit,
    submit: () -> Unit,
    //使用slots(view占位符)提高灵活性
    buttonSlot: @Composable () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TodoInputTextField(
                text = text,
                onTextChanged = onTextChanged,
                onInputCompleted = submit,
                modifier = Modifier.weight(1.0F)
            )
            Box(modifier = Modifier.padding(end = 16.dp)) {
                buttonSlot()
            }
        }

        AnimatedTodoIconRow(
            selectedTodoIcon = selectedTodoIcon,
            onTodoIconSelected = { onTodoIconSelected(it) },
            visible = text.isNotBlank()
        )
    }
}


@ExperimentalComposeUiApi
@Composable
fun TodoInputTextField(
    text: String,
    onTextChanged: (String) -> Unit,
    onInputCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    //获取keyboardController
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        value = text,
        maxLines = 2,
        onValueChange = { onTextChanged(it) },
        modifier = modifier,
        colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            onInputCompleted()
            keyboardController?.hide()
        })
    )
}

/**
 * 用于为TodoItemInputEntry添加尺寸变化时的动画
 */
@Composable
fun TodoItemInputBackground(
    elevate: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val animateElevation by animateDpAsState(
        targetValue = if (elevate) 1.dp else 1.5.dp,
        animationSpec = TweenSpec(durationMillis = 300)
    )
    Surface(
        elevation = animateElevation,
        shape = RectangleShape,
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.05F)
    ) {
        Row(
            content = content,
            modifier = modifier.animateContentSize(animationSpec = TweenSpec(durationMillis = 300))
        )
    }
}

@Composable
fun SelectableIconButton(
    todoIcon: TodoIcon,
    isSelected: Boolean,
    //当选中TodoIcon时的事件触发
    onTodoIconSelected: (TodoIcon) -> Unit,
    modifier: Modifier = Modifier
) {
    val tintColor = if (isSelected) {
        MaterialTheme.colors.primary
    } else {
        MaterialTheme.colors.onSurface.copy(0.6F)
    }
    IconButton(
        //触发TodoIcon的选中事件
        onClick = { if (isSelected.not()) onTodoIconSelected(todoIcon) },
        modifier = modifier
    ) {
        Column {
            Icon(
                imageVector = todoIcon.icon,
                contentDescription = todoIcon.description,
                tint = tintColor
            )
            //当TodoIcon选中时，在其下方显示下划线，否则仅显示一定高度的空白
            if (isSelected) {
                Divider(
                    modifier = Modifier.width(todoIcon.icon.defaultWidth),
                    thickness = 2.dp,
                    color = MaterialTheme.colors.primary
                )
            } else {
                Spacer(
                    modifier = Modifier
                        .height(2.dp)
                        .width(todoIcon.icon.defaultWidth)
                )
            }
        }
    }
}

@Composable
fun TodoIconRow(
    modifier: Modifier = Modifier,
    selectedTodoIcon: TodoIcon,
    onTodoIconSelected: (TodoIcon) -> Unit
) {
    Row(modifier = modifier) {
        TodoIcon.values().forEach {
            val isSelected = selectedTodoIcon == it
            SelectableIconButton(
                todoIcon = it,
                isSelected = isSelected,
                onTodoIconSelected = onTodoIconSelected
            )
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun AnimatedTodoIconRow(
    modifier: Modifier = Modifier,
    selectedTodoIcon: TodoIcon,
    onTodoIconSelected: (TodoIcon) -> Unit,
    visible: Boolean
) {
    //定义FadeIn和FadeOut两种动画，并保存动画状态，以防止在动画过程中遇到重组时导致动画重启
    val enter = remember {
        fadeIn(
            animationSpec = TweenSpec(
                durationMillis = 300,
                easing = FastOutLinearInEasing
            )
        )
    }
    val exit = remember {
        fadeOut(
            animationSpec = TweenSpec(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(modifier = modifier) {
        //开启显示/隐藏动画
        AnimatedVisibility(visible = visible, enter = enter, exit = exit) {
            TodoIconRow(
                selectedTodoIcon = selectedTodoIcon,
                onTodoIconSelected = onTodoIconSelected
            )
        }
    }
}

private fun randomTint(): Float = Random.nextFloat().coerceIn(0.3F, 0.9F)

@Preview
@Composable
fun PreviewTodoScreen() {
    //TodoScreen()
}