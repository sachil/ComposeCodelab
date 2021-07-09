package xyz.sachil.todo.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import xyz.sachil.todo.bean.TodoItem
import xyz.sachil.todo.model.generateRandomTodoItem

class TodoViewModel : ViewModel() {
    //用于记录当前正在编辑的TodoItem的位置
    private var currentEditPosition by mutableStateOf(-1)
    //这里使用一个可以记录state(状态)的MutableList来保存Todo列表
    val todoItems = mutableStateListOf<TodoItem>()

    //用于记录当前正在编辑的TodoItem，若没有编辑的item则为null
    val currentEditTodoItem: TodoItem?
        get() = todoItems.getOrNull(currentEditPosition)

    init {
        repeat(8) {
            addTodoItem(null)
        }
    }

    fun addTodoItem(todoItem: TodoItem?) {
        if (todoItem == null) {
            todoItems.add(generateRandomTodoItem())
        } else {
            todoItems.add(todoItem)
        }
    }

    fun removeTodoItem(todoItem: TodoItem) {
        todoItems.remove(todoItem)
        completeEdit()
    }

    fun selectTodoItemToEdit(todoItem: TodoItem) {
        currentEditPosition = todoItems.indexOf(todoItem)
    }

    fun completeEdit() {
        currentEditPosition = -1
    }

    fun changeTodoItem(todoItem: TodoItem) {
        val currentEditItem = requireNotNull(currentEditTodoItem)
        require(currentEditItem.id == todoItem.id) {
            "The apply to changed todo item must be same with the edit toto item."
        }
        todoItems[currentEditPosition] = todoItem
    }

}