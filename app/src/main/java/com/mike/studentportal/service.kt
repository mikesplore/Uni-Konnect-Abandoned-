package com.mike.studentportal
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay

class ChatFetchWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private var previousChatSize = 0
    private val context = appContext

    override suspend fun doWork(): Result {
        var chats: List<Chat> = emptyList()
        var errorMessage: String? = null

        fun fetchChats() {
            try {
                MyDatabase.fetchChats { fetchedChats ->
                    chats = fetchedChats
                }
            } catch (e: Exception) {
                errorMessage = e.message
                // Handle error as needed, e.g., logging or user feedback.
            }
        }

        while (true) {
            delay(10) // delay 1 minute
            fetchChats()
            if (chats.size != previousChatSize) {
                previousChatSize = chats.size
                showNotification(context, title = "New Chat", message = "Check out new notification!")
            }
            return Result.retry() // Indicate that the work should be retried later
        }
    }
}