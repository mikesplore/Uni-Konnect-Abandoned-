package com.mike.studentportal

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.util.UUID


data class User(
    var id: String = "", // Use a mutable 'var'
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val isAdmin: Boolean = false
)

data class Timetable(
    val id: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val unitName: String = "",
    val venue: String = "",
    val lecturer: String = "",
    val dayId: String = ""
)

data class UserPreferences(
    val lastScreen: String = "dashboard",
    val lastDate: String = ""

)

data class Chat(
    var id: String = "",
    var message: String = "",
    var senderName: String = "",
    var senderID: String = "",
    var time: String = "",
    var date: String = "",
    var isAdmin: Boolean = false

)

data class Message(
    var id: String = "",
    var message: String = "",
    var senderName: String = "",
    var senderID: String = "",
    var time: String = "",
    var date: String = "",
    var recipientID: String = ""

)


data class AttendanceState(
    val courseID: String = "",
    val courseName: String = "",
    val state: Boolean = false
)

data class Update(
    val id: String = "",
    val version: String = ""
)

data class Course(
    val courseCode: String = "",
    val courseName: String = "",
    var visits: Int = 0
)

data class Feedback(
    val id: String = "",
    val rating: Int = 0,
    val message: String = "",
    val sender: String = "",
    val admissionNumber: String = ""
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
    val name: String = "")
enum class Section { NOTES, PAST_PAPERS, RESOURCES }
data class Announcement(
    val id: String = "",
    val date: String = "",
    val title: String = "",
    val description: String = "",
    val author: String = ""
)

data class Event(
    val id: String = "",
    val date: String = "",
    val title: String = "",
    val description: String = "",
    val author: String = "",
    val venue: String = ""
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


object MyDatabase {
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var calendar: Calendar = Calendar.getInstance()
    private var year = calendar.get(Calendar.YEAR)


    // index number
    fun generateIndexNumber(onIndexNumberGenerated: (String) -> Unit) {
        updateAndGetCode { newCode ->
            val indexNumber = "CP$newCode$year"
            onIndexNumberGenerated(indexNumber) // Pass the generated index number to the callback
        }
    }

    fun generateChatID(onIndexNumberGenerated: (String) -> Unit) {
        updateAndGetCode { newCode ->
            val indexNumber = "CH$newCode$year"
            onIndexNumberGenerated(indexNumber) // Pass the generated index number to the callback
        }
    }

    fun generateFCMID(onIndexNumberGenerated: (String) -> Unit) {
        updateAndGetCode { newCode ->
            val indexNumber = "FC$newCode$year"
            onIndexNumberGenerated(indexNumber) // Pass the generated index number to the callback
        }
    }

    fun generateFeedbackID(onIndexNumberGenerated: (String) -> Unit) {
        updateAndGetCode { newCode ->
            val indexNumber = "FB$newCode$year"
            onIndexNumberGenerated(indexNumber) // Pass the generated index number to the callback
        }
    }

    private fun generateAttendanceID(onIndexNumberGenerated: (String) -> Unit) {
        updateAndGetCode { newCode ->
            val indexNumber = "AT$newCode$year"
            onIndexNumberGenerated(indexNumber) // Pass the generated index number to the callback
        }
    }


    //chats functions
    fun sendMessage(chat: Chat, onComplete: (Boolean) -> Unit) {
        database.child("Group Discussions").push().setValue(chat).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    fun fetchChats(onChatsFetched: (List<Chat>) -> Unit) {
        database.child("Group Discussions").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chats = snapshot.children.mapNotNull { it.getValue(Chat::class.java) }
                onChatsFetched(chats)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    private fun updateAndGetCode(onCodeUpdated: (Int) -> Unit) {
        val database = FirebaseDatabase.getInstance().getReference("Code")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val myCode = snapshot.getValue(MyCode::class.java)!!
                    myCode.code += 1
                    database.setValue(myCode).addOnSuccessListener {
                        onCodeUpdated(myCode.code) // Pass the incremented code to the callback
                    }
                } else {
                    val newCode = MyCode(code = 1)
                    database.setValue(newCode).addOnSuccessListener {
                        onCodeUpdated(newCode.code) // Pass the initial code to the callback
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error appropriately (e.g., log it or notify the user)
            }
        })
    }

    // Function to save Update to the "Update" node in the database
    fun saveUpdate(update: Update, onSuccess: () -> Unit, onFailure: (Exception?) -> Unit) {
        val updatesRef = database.child("Updates") // Reference to the "Updates" node
        updatesRef.setValue(update)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Function to retrieve Update from the "Update" node
    fun getUpdate(onResult: (Update?) -> Unit) {
        val updatesRef = database.child("Updates")
        updatesRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val update = snapshot.getValue(Update::class.java)
                onResult(update)
            } else {
                onResult(null) // Handle the case where no update data exists
            }
        }.addOnFailureListener { exception ->
            // Handle potential errors during data retrieval
            onResult(null)
        }
    }



    // Attendance functions
    fun signAttendance(studentID: String, courseCode: String, status: String, onResult: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        val attendanceRef = database.child("Attendances").child(courseCode).child(studentID).push()
        val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        generateAttendanceID { attendanceID ->
            val attendance = Attendance(id = attendanceID, date = today, status = status, studentId = studentID)

        attendanceRef.setValue(attendance)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }
    }


    fun fetchAttendances(
        studentID: String, courseCode: String, onAttendanceFetched: (List<Attendance>) -> Unit
    ) {
        val attendanceRef = database.child("Attendances/$courseCode/$studentID")
        attendanceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val attendances =
                    snapshot.children.mapNotNull { it.getValue(Attendance::class.java) }
                onAttendanceFetched(attendances)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, maybe pass an empty list or an error state to the callback
                onAttendanceFetched(emptyList())
            }
        })
    }

    fun sendUserToUserMessage(message: Message, path: String, onComplete: (Boolean) -> Unit) {
        database.child(path).push().setValue(message).addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
    }

    fun fetchUserToUserMessages(path: String, onMessagesFetched: (List<Message>) -> Unit) {
        database.child(path).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                onMessagesFetched(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


    fun fetchAttendanceState(courseCode: String, onStateFetched: (AttendanceState?) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("AttendanceStates").child(courseCode).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val attendanceState = snapshot.getValue(AttendanceState::class.java)
                onStateFetched(attendanceState) // Pass the fetched state or null if not found
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, maybe pass null to indicate failure
                onStateFetched(null)
            }
        })
    }

    fun writeCourse(course: Course, onSuccess: () -> Unit){
        database.child("NewCourses").child(course.courseCode).setValue(course)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                    exception ->
            }
    }

    // Courses functions
    fun fetchCourses(onCoursesFetched: (List<Course>) -> Unit) {
        database.child("NewCourses").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courseList = snapshot.children.mapNotNull { it.getValue(Course::class.java) }
                onCoursesFetched(courseList) // Call the callback with the fetched courses
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error, maybe pass an empty list or an error state to the callback
                onCoursesFetched(emptyList())
            }
        })
    }

    // User functions
    fun writeUsers(user:User, onComplete: (Boolean) -> Unit) {
        database.child("Users").child(user.id).setValue(user)
            .addOnSuccessListener {
                onComplete(true) // Success: invoke callback with 'true'
            }
            .addOnFailureListener {
                onComplete(false) // Failure: invoke callback with 'false'
            }
    }

    fun getUsers(onUsersFetched: (List<User>?) -> Unit) {
        database.child("Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull { it.getValue(User::class.java) }
                onUsersFetched(users)
            }

            override fun onCancelled(error: DatabaseError) {
                onUsersFetched(null)
            }
        })
    }

    fun fetchUserDataByEmail(email: String, callback: (User?) -> Unit) {
        database.child("Users").orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapshot in snapshot.children) {
                        val userEmail = userSnapshot.child("email").getValue(String::class.java)
                        if (userEmail == email) {
                            val userId = userSnapshot.child("id").getValue(String::class.java) ?: ""
                            val firstName = userSnapshot.child("firstName").getValue(String::class.java) ?: ""
                            val lastName = userSnapshot.child("lastName").getValue(String::class.java) ?: ""
                            val phoneNumber = userSnapshot.child("firstName").getValue(String::class.java) ?: ""
                            callback(User(id = userId, firstName = firstName, lastName = lastName,phoneNumber = phoneNumber, email = userEmail))
                            return
                        }
                    }
                    callback(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null) // Handle or log the error as needed
                }
            })
    }

    fun fetchUserDataByAdmissionNumber(admissionNumber: String, callback: (User?) -> Unit) {
        database.child("Users").orderByChild("id").equalTo(admissionNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (userSnapshot in snapshot.children) {
                        val userId = userSnapshot.child("id").getValue(String::class.java)
                        if (userId == admissionNumber) {
                            val userEmail = userSnapshot.child("Email").getValue(String::class.java) ?: ""
                            val firstName = userSnapshot.child("firstName").getValue(String::class.java) ?: ""
                            val lastName = userSnapshot.child("lastName").getValue(String::class.java) ?: ""
                            val phoneNumber = userSnapshot.child("phoneNumber").getValue(String::class.java) ?: ""
                            callback(User(id = userId, firstName = firstName, lastName  =lastName, phoneNumber = phoneNumber, email = userEmail))
                            return
                        }
                    }
                    callback(null)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null) // Handle or log the error as needed
                }
            }
         )
    }




    // Authentication functions
    fun updatePassword(newPassword: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.updatePassword(newPassword)?.addOnSuccessListener { onSuccess() }
            ?.addOnFailureListener { exception -> onFailure(exception) }
    }

    // Items functions
    fun readItems(courseId: String, section: Section, onItemsRead: (List<GridItem>) -> Unit) {
        database.child("Courses").child(courseId).child(section.name)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = snapshot.children.mapNotNull { it.getValue(GridItem::class.java) }
                    onItemsRead(items)
                }

                override fun onCancelled(error: DatabaseError) {
                    onItemsRead(emptyList())
                }
            })
    }

    // Feedback functions
    fun writeFeedback(feedback: Feedback, onSuccess: () -> Unit, onFailure: (Exception?) -> Unit) {
        val feedbackRef: DatabaseReference = database.child("Feedback").child(feedback.id)
        feedbackRef.setValue(feedback).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun fetchAverageRating(onAverageRatingFetched: (String) -> Unit) {
        val feedbackRef = database.child("Feedback")
        feedbackRef.get().addOnSuccessListener { snapshot ->
            var totalRating = 0.0
            var count = 0

            for (childSnapshot in snapshot.children) {
                val feedback = childSnapshot.getValue(Feedback::class.java)
                feedback?.rating?.let {
                    totalRating += it
                    count++
                }
            }

            val averageRating = if (count > 0) totalRating / count else 0.0
            val formattedAverage = String.format(Locale.US, "%.1f", averageRating)
            onAverageRatingFetched(formattedAverage)
        }.addOnFailureListener {
            onAverageRatingFetched(String.format(Locale.US, "%.1f", 0.0))
        }
    }

    // FCM functions
    fun writeFcmToken(token: Fcm) {
        database.child("FCM").child(token.id).setValue(token)
    }

    // Timetable functions
    fun getTimetable(dayId: String, onAssignmentsFetched: (List<Timetable>?) -> Unit) {
        database.child("Timetable").orderByChild("dayId").equalTo(dayId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val timetable =
                        snapshot.children.mapNotNull { it.getValue(Timetable::class.java) }
                    onAssignmentsFetched(timetable)
                }

                override fun onCancelled(error: DatabaseError) {
                    onAssignmentsFetched(null)
                }
            })
    }

    fun getCurrentDayTimetable(dayName: String, onTimetableFetched: (List<Timetable>?) -> Unit) {
        // Step 1: Fetch the dayId from the Day node using the dayName
        database.child("Days").orderByChild("name").equalTo(dayName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dayId = snapshot.children.firstOrNull()?.key

                    if (dayId != null) {
                        // Step 2: Use the fetched dayId to query the Timetable node
                        database.child("Timetable").orderByChild("dayId").equalTo(dayId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val timetable =
                                        snapshot.children.mapNotNull { it.getValue(Timetable::class.java) }
                                    onTimetableFetched(timetable)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    onTimetableFetched(null)
                                }
                            })
                    } else {
                        // Day not found
                        onTimetableFetched(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onTimetableFetched(null)
                }
            })
    }

    // Days functions
    fun getDays(onCoursesFetched: (List<Day>?) -> Unit) {
        database.child("Days").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val days = snapshot.children.mapNotNull { it.getValue(Day::class.java) }
                onCoursesFetched(days)
            }

            override fun onCancelled(error: DatabaseError) {
                onCoursesFetched(null)
            }
        })
    }

    // Assignment functions
    fun getAssignments(courseCode: String, onAssignmentsFetched: (List<Assignment>?) -> Unit) {
        database.child("Assignments").orderByChild("courseCode").equalTo(courseCode)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val assignments =
                        snapshot.children.mapNotNull { it.getValue(Assignment::class.java) }
                    onAssignmentsFetched(assignments)
                }

                override fun onCancelled(error: DatabaseError) {
                    onAssignmentsFetched(null)
                }
            })
    }

    // Announcements functions
    fun getAnnouncements(onUsersFetched: (List<Announcement>?) -> Unit) {
        database.child("Announcements").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val announcements =
                    snapshot.children.mapNotNull { it.getValue(Announcement::class.java) }
                onUsersFetched(announcements)
            }

            override fun onCancelled(error: DatabaseError) {
                onUsersFetched(null)
            }
        })
    }

}



