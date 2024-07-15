package com.mike.unikonnect.classes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AddAlert
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

data class User(
    var id: String = "", // Use a mutable 'var'
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val gender: String = "",
    val profileImageLink: String = ""
)

data class Timetable(
    val id: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val unitName: String = "",
    val venue: String = "",
    val lecturer: String = "",
    val dayId: String = "",

    )

data class UserPreferences(
    val studentID: String = "",
    val id: String = "",
    val profileImageLink: String = "",
    val biometrics: String = "disabled",
    val darkMode: String = "disabled",
    val notifications: String = "disabled"

)

data class Chat(
    var id: String = "",
    var message: String = "",
    var senderName: String = "",
    var senderID: String = "",
    var time: String = "",
    var date: String = "",
    var profileImageLink: String = ""

)

data class Message(
    var id: String = "",
    var message: String = "",
    var senderName: String = "",
    var senderID: String = "",
    var time: String = "",
    var date: String = "",
    var recipientID: String = "",
    var profileImageLink: String = ""

)


data class AttendanceState(
    val courseID: String = "",
    val courseName: String = "",
    var state: Boolean = false
)

data class Update(
    val id: String = "", val version: String = ""
)

data class Course(
    val courseCode: String = "",
    val courseName: String = "",
    var visits: Int = 0,
)

data class Feedback(
    val id: String = "",
    val rating: Int = 0,
    val message: String = "",
    val sender: String = "",
    val admissionNumber: String = ""
)

data class AccountDeletion(
    val id: String = "",
    val admissionNumber: String = "",
    val email: String = ""
)

data class Assignment(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val dueDate: String = "",
    val courseCode: String = ""
)

data class Day(
    val id: String = "",
    val name: String = ""
)

enum class Section { NOTES, PAST_PAPERS, RESOURCES }
data class Announcement(
    val id: String = "",
    val date: String = "",
    val title: String = "",
    val description: String = "",
    val author: String = ""
)


data class Attendance(
    val id: String = "",
    val date: String = "",
    val status: String = "",
    val studentId: String = ""
)

data class Fcm(val id: String = "", val token: String = "")


data class GridItem(
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "",
    val link: String = "",
    var fileType: String = "image"
)

data class MyCode(
    val id: String = UUID.randomUUID().toString(),
    var code: Int = 0
)

data class ScreenTime(
    val id: String = "",
    val screenName: String = "",
    val time: Long = 0
)

data class Screens(
    val screenId: String = "",
    val screenName: String = "",

)

sealed class Screen(
    val selectedIcon: ImageVector, val unselectedIcon: ImageVector, val name: String
) {
    data object Home : Screen(
        Icons.Filled.Home, Icons.Outlined.Home, "Home"
    )

    data object Timetable :
        Screen(Icons.Filled.CalendarToday, Icons.Outlined.CalendarToday, "Timetable")

    data object Assignments : Screen(
        Icons.AutoMirrored.Filled.Assignment, Icons.AutoMirrored.Outlined.Assignment, "Assignments"
    )

    data object Announcements : Screen(
        Icons.Filled.AddAlert, Icons.Outlined.AddAlert, "Announcements"
    )

    data object Attendance : Screen(
        Icons.Filled.Book, Icons.Outlined.Book, "Attendance"
    )

}