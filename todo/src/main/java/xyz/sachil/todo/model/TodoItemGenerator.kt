package xyz.sachil.todo.model

import xyz.sachil.todo.bean.TodoIcon
import xyz.sachil.todo.bean.TodoItem

fun generateRandomTodoItem(): TodoItem {
    val message = listOf(
        "Learn compose",
        "Learn state",
        "Build dynamic UIs",
        "Learn Unidirectional Data Flow",
        "Integrate LiveData",
        "Integrate ViewModel",
        "Remember to savedState!",
        "Build stateless composables",
        "Use state from stateless composables"
    ).random()
    return TodoItem(message, TodoIcon.values().random())
}