package com.mike.studentportal

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

open class User(
    val id: String = MyDatabase.generateIndexNumber(),
    val name: String = "",
    val email: String = "",
    )

data class Timetable(
    val id: String = MyDatabase.generateTimetableID(),
    val startTime: String = "",
    val endTime: String = "",
    val unitName: String = "",
    val venue: String = "",
    val lecturer: String = "",
    val dayId: String = ""
)

data class Course(
    val courseCode: String = "",
    val courseName: String = "",
    var lastDate: String = ""
)
data class Subjects(
    val id: String = MyDatabase.generateSubjectsID(),
    val name: String = "",
)

data class Student(
    val id: String = MyDatabase.generateIndexNumber(), val firstName: String
)

data class AttendanceRecord(
    val studentId: String, val dayOfWeek: String, val isPresent: Boolean, val lesson: String
)

data class Feedback(
    val id: String = MyDatabase.generateFeedbackID(),
    val message: String = "",
    val sender: String = "",
    val admissionNumber: String = ""
)

data class Assignment(
    val id: String = MyDatabase.generateAssignmentID(),
    val name: String = "",
    val description: String = "",
    val dueDate: String = "",
    val subjectId: String = ""
)

data class Day(
    val id: String = MyDatabase.generateDayID(), val name: String = ""
)

enum class Section {
    NOTES, PAST_PAPERS, RESOURCES
}

data class Announcement(
    val id: String = MyDatabase.generateAnnouncementID(),
    val date: String = "",
    val title: String = "",
    val description: String = "",
    val author: String = ""
)

data class Event(
    val id: String = MyDatabase.generateAnnouncementID(),
    val date: String = "",
    val title: String = "",
    val description: String = "",
    val author: String = "",
    val venue: String = ""
)

data class Fcm(
    val id: String = MyDatabase.generateFcmID(), val token: String = ""
)

data class GridItem(
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "",
    val link: String = "",
    var fileType: String = "image"
)



object MyDatabase {
    val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()
    val logout = auth.signOut()



    //initialize the Unique id of the items
    private var userID = 0
    private var announcementID = 0
    private var timetableID = 0
    private var assignmentID = 0
    private var subjectsID = 0
    private var dayID = 0
    private var attendanceID = 0
    private var FcmID = 0
    private var feedbackID = 0
    private var EventToken = 0
    private var calendar: Calendar = Calendar.getInstance()
    private var year = calendar.get(Calendar.YEAR)
    private var gridID = 0

    // index number
    fun generateIndexNumber(): String {
        val currentID = userID
        userID++
        return "CP$currentID$year"
    }

    fun generateGridID(): String{
        val currentID = gridID
        gridID++
        return "G$currentID$year"
    }

    fun generateEventID(): String {
        val currentID = EventToken
        EventToken++
        return "ET$currentID$year"
    }

    fun generateFcmID(): String {
        val currentID = FcmID
        FcmID++
        return "FC$currentID$year"
    }

    fun generateFeedbackID(): String {
        val currentID = feedbackID
        feedbackID++
        return "FD$currentID$year"
    }

    fun generateAttendanceID(): String {
        val currentID = attendanceID
        attendanceID++
        return "AT$currentID$year"
    }

    fun generateAnnouncementID(): String {
        val currentID = announcementID
        announcementID++
        return "AN$currentID$year"
    }

    fun generateTimetableID(): String {
        val currentID = timetableID
        timetableID++
        return "TT$currentID$year"
    }

    fun generateAssignmentID(): String {
        val currentID = assignmentID
        assignmentID++
        return "AS$currentID$year"
    }

    fun generateSubjectsID(): String {
        val currentID = subjectsID
        subjectsID++
        return "SB$currentID"
    }

    fun generateDayID(): String {
        val currentID = dayID
        dayID++
        return "DY$currentID$year"
    }


    fun writeUsers(user: User) {
        database.child("Users").child(user.id).setValue(user)
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
        }
        )
    }

    fun fetchUserDataByEmail(email: String, callback: (User?) -> Unit) {
         database.child("Users").orderByChild("email").equalTo(email)
             .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val userEmail = userSnapshot.child("email").getValue(String::class.java)
                    if (userEmail == email) {
                        val userId = userSnapshot.child("id").getValue(String::class.java) ?: ""
                        val userName = userSnapshot.child("name").getValue(String::class.java) ?: ""
                        callback(User(id = userId, name = userName, email = userEmail))
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



    fun updateUserNameById(userId: String, newName: String, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        val userRef = database.child("Users").child(userId)

        userRef.child("name").setValue(newName).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true)
            } else {
                callback(false)
            }
        }
    }



    fun updatePassword(newPassword: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.updatePassword(newPassword)
            ?.addOnSuccessListener { onSuccess() }
            ?.addOnFailureListener { exception -> onFailure(exception) }
    }


    fun readItems(courseId: String, section: Section, onItemsRead: (List<GridItem>) -> Unit) {
        database.child("Courses").child(courseId).child(section.name).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(GridItem::class.java) }
                onItemsRead(items)
            }

            override fun onCancelled(error: DatabaseError) {
                onItemsRead(emptyList())
            }
        })
    }


    fun writeFeedback(feedback: Feedback, onSuccess: () -> Unit, onFailure: (Exception?) -> Unit) {
        val feedbackRef: DatabaseReference = database.child("Feedback").push()
        feedbackRef.setValue(feedback)
            .addOnSuccessListener {
                onSuccess() // Callback on successful write
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // Callback on failure with exception
            }
    }

    //send the token to the database
    fun writeFcmToken(token: Fcm) {
        database.child("FCM").child(token.id).setValue(token)
    }


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

    fun fetchCourses(onCoursesFetched: (List<Course>) -> Unit) {
        database.child("Courses").addListenerForSingleValueEvent(object : ValueEventListener {
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

    fun writeSubject(subject: Subjects, onComplete: (Boolean) -> Unit) {
        database.child("Subjects").child(subject.id).setValue(subject)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getSubjects(onSubjectsFetched: (List<Subjects>?) -> Unit) {
        database.child("Subjects").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val subjects = snapshot.children.mapNotNull { it.getValue(Subjects::class.java) }
                onSubjectsFetched(subjects)
            }

            override fun onCancelled(error: DatabaseError) {
                onSubjectsFetched(null)
            }
        })
    }


    fun saveRating(userName: String, rating: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val userRatingRef = database.child("Ratings").child(userName)

        userRatingRef.setValue(rating)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun getRating(userName: String, onRatingFetched: (Int) -> Unit, onFailure: (DatabaseError) -> Unit) {
        val userRatingRef = database.child("Ratings").child(userName)
        userRatingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val rating = snapshot.getValue(Int::class.java) ?: 0
                onRatingFetched(rating)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error)
            }
        })
    }



    fun getDays(onSubjectsFetched: (List<Day>?) -> Unit) {
        database.child("Days").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val days = snapshot.children.mapNotNull { it.getValue(Day::class.java) }
                onSubjectsFetched(days)
            }

            override fun onCancelled(error: DatabaseError) {
                onSubjectsFetched(null)
            }
        })
    }

    fun writeAssignment(assignment: Assignment, onComplete: (Boolean) -> Unit) {
        database.child("Assignments").child(assignment.id).setValue(assignment)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getAssignments(subjectId: String, onAssignmentsFetched: (List<Assignment>?) -> Unit) {
        database.child("Assignments").orderByChild("subjectId").equalTo(subjectId)
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

    fun deleteAssignment(assignmentId: String, onComplete: (Boolean) -> Unit) {
        database.child("Assignments").child(assignmentId).removeValue()
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun editAssignment(assignment: Assignment, onComplete: (Boolean) -> Unit) {
        database.child("Assignments").child(assignment.id).setValue(assignment)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }


    fun writeAnnouncement(announcement: Announcement) {
        database.child("Announcements").child(announcement.id).setValue(announcement)
    }

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
        }
      )
    }


    fun getEvents(onUsersFetched: (List<Event>?) -> Unit) {
        database.child("Events").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val event =
                    snapshot.children.mapNotNull { it.getValue(Event::class.java) }
                onUsersFetched(event)
            }

            override fun onCancelled(error: DatabaseError) {
                onUsersFetched(null)
            }
        })
    }


    fun loadSubjectsAndAssignments(callback: (List<Subjects>?) -> Unit) {
        database.child("Subjects").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val subjectsList = task.result.children.mapNotNull { dataSnapshot ->
                    val subject = dataSnapshot.getValue(Subjects::class.java)
                    if (subject?.name.isNullOrEmpty()) {
                        Log.e("DataFetch", "Subject with missing name: $dataSnapshot")
                        null
                    } else {
                        subject
                    }
                }
                callback(subjectsList)
            } else {
                Log.e("DataFetch", "Error fetching subjects: ${task.exception?.message}")
                callback(null)
            }
        }
    }

    fun loadStudents(onStudentsLoaded: (List<Student>?) -> Unit) {
        database.child("Students").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val students = snapshot.children.mapNotNull {
                    val id = it.child("id").getValue(String::class.java)
                    val firstName = it.child("firstName").getValue(String::class.java)
                    if (id != null && firstName != null) {
                        Student(id, firstName)
                    } else null
                }
                onStudentsLoaded(students)
            }

            override fun onCancelled(error: DatabaseError) {
                onStudentsLoaded(null)
            }
        }
      )
    }

    fun loadAttendanceRecords(onAttendanceRecordsLoaded: (List<AttendanceRecord>?) -> Unit) {
        database.child("attendanceRecords")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val attendanceRecords = snapshot.children.mapNotNull {
                        val studentId = it.child("studentId").getValue(String::class.java)
                        val dayOfWeek = it.child("dayOfWeek").getValue(String::class.java)
                        val isPresent = it.child("isPresent").getValue(Boolean::class.java)
                        val lesson = it.child("lesson").getValue(String::class.java)
                        if (studentId != null && dayOfWeek != null && isPresent != null && lesson != null) {
                            AttendanceRecord(studentId, dayOfWeek, isPresent, lesson)
                        } else null
                    }
                    onAttendanceRecordsLoaded(attendanceRecords)
                }

                override fun onCancelled(error: DatabaseError) {
                    onAttendanceRecordsLoaded(null)
                }
            }
        )
    }

    //fetch the day id using the day name
    fun getDayIdByName(dayName: String, onDayIdFetched: (String?) -> Unit) {
        database.child("Days").orderByChild("name").equalTo(dayName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dayId = snapshot.children.firstOrNull()?.key
                    onDayIdFetched(dayId)
                }

                override fun onCancelled(error: DatabaseError) {
                    onDayIdFetched(null)
                }
            }
        )
    }


}
