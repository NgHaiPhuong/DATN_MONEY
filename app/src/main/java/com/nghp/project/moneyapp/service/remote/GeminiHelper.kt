package com.nghp.project.moneyapp.service.remote

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GeminiHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-2.5-flash")

    suspend fun generateText(prompt: String): String {
        return try {
            val response = withContext(Dispatchers.IO) {
                generativeModel.generateContent(prompt)
            }
            response.text ?: "No response from AI"
        } catch (e: Exception) {
            e.printStackTrace()
            "Error when calling  AI: ${e.localizedMessage}"
        }
    }
}
