package xyz.sachil.todo.bean

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.*

data class TodoItem(
    val task: String,
    val todoIcon: TodoIcon = TodoIcon.Default,
    val id: UUID = UUID.randomUUID()
)


enum class TodoIcon(val icon: ImageVector, val description: String) {
    Square(Icons.Default.CropSquare, "Square"),
    Done(Icons.Default.Done, "Done"),
    Event(Icons.Default.Event, "Event"),
    Privacy(Icons.Default.PrivacyTip, "Privacy"),
    Trash(Icons.Default.RestoreFromTrash, "restore");

    companion object {
        val Default = Square
    }

}