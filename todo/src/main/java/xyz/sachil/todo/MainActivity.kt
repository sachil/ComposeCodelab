package xyz.sachil.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.tooling.preview.Preview
import xyz.sachil.todo.bean.TodoItem
import xyz.sachil.todo.model.generateRandomTodoItem
import xyz.sachil.todo.ui.theme.ComposeCodelabTheme
import xyz.sachil.todo.ui.screen.TodoScreen
import xyz.sachil.todo.vm.TodoViewModel

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<TodoViewModel>()

    @ExperimentalComposeUiApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCodelabTheme {
                TodoScreen(
                    todoItems = viewModel.todoItems,
                    currentlyEditTodoItem = viewModel.currentEditTodoItem,
                    addTodoItem = viewModel::addTodoItem,
                    removeTodoItem = viewModel::removeTodoItem,
                    changeTodoItem = viewModel::changeTodoItem,
                    startEditTodoItem = viewModel::selectTodoItemToEdit,
                    stopEditTodoItem = viewModel::completeEdit
                )
            }
        }
    }
}


@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun PreviewTodoScreen() {
    ComposeCodelabTheme {
        val todoItems = mutableListOf<TodoItem>()
        repeat(8) {
            todoItems.add(generateRandomTodoItem())
        }
        TodoScreen(
            todoItems = todoItems,
            currentlyEditTodoItem = null,
            addTodoItem = { /*TODO*/ },
            removeTodoItem = { /*TODO*/ },
            changeTodoItem = { /*TODO*/ },
            startEditTodoItem = { /*TODO*/ },
            stopEditTodoItem = {/*TODO*/ })
    }
}