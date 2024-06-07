package com.mike.studentportal

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
data class Student(val registrationID: String, val name: String, var email: String, var password: String)
data class AttendanceRecord(val studentId: String, val date: String, val present: Boolean, val unit: String)
data class StudentAttendance(val student: Student, val totalPresent: Int, val totalAbsent: Int, val attendancePercentage: Int)
data class TimetableItem(val unit: String, val startTime: String, val duration: String, val lecturer: String, val venue: String, val day: String)
data class UnitData(var name: String, val assignments: MutableList<Assignment> = mutableListOf())
data class Assignment(val title: String, val description: String)


// Object to handle database operations
object MyDatabase {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun writeNewUser(userId: String, name: String, email: String) {
        val student = Student(registrationID = userId, name = name, email = email, password = "")
        database.child("Students").child(userId).setValue(student)
    }





}




