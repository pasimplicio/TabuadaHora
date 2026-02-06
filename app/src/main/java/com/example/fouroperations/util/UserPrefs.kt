package com.example.fouroperations.util

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class UserProfile(
    val id: String,
    val name: String,
    val bestScores: Map<String, Int>
)

object UserPrefs {
    private const val PREFS_NAME = "tabuada_hora_prefs"
    private const val KEY_USERS = "users"
    private const val KEY_ACTIVE_USER = "active_user"
    private const val KEY_MUSIC_MUTED = "music_muted"

    fun getUsers(context: Context): List<UserProfile> {
        val raw = prefs(context).getString(KEY_USERS, "[]").orEmpty()
        val array = JSONArray(raw)
        val users = mutableListOf<UserProfile>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            users.add(
                UserProfile(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    bestScores = parseScores(obj.optJSONObject("bestScores"))
                )
            )
        }
        return users
    }

    fun saveUsers(context: Context, users: List<UserProfile>) {
        val array = JSONArray()
        users.forEach { user ->
            val obj = JSONObject()
            obj.put("id", user.id)
            obj.put("name", user.name)
            obj.put("bestScores", scoresToJson(user.bestScores))
            array.put(obj)
        }
        prefs(context).edit().putString(KEY_USERS, array.toString()).apply()
    }

    fun addUser(context: Context, name: String): UserProfile {
        val newUser = UserProfile(
            id = UUID.randomUUID().toString(),
            name = name.trim().uppercase(),
            bestScores = emptyMap()
        )
        val users = getUsers(context).toMutableList()
        users.add(newUser)
        saveUsers(context, users)
        setActiveUserId(context, newUser.id)
        return newUser
    }

    fun getActiveUserId(context: Context): String {
        return prefs(context).getString(KEY_ACTIVE_USER, "").orEmpty()
    }

    fun setActiveUserId(context: Context, userId: String) {
        prefs(context).edit().putString(KEY_ACTIVE_USER, userId).apply()
    }

    fun isMusicMuted(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_MUSIC_MUTED, false)
    }

    fun setMusicMuted(context: Context, muted: Boolean) {
        prefs(context).edit().putBoolean(KEY_MUSIC_MUTED, muted).apply()
    }

    fun updateBestScore(
        context: Context,
        userId: String,
        operationId: String,
        score: Int
    ): List<UserProfile> {
        val users = getUsers(context).map { user ->
            if (user.id == userId) {
                val currentBest = user.bestScores[operationId] ?: 0
                if (score > currentBest) {
                    user.copy(bestScores = user.bestScores + (operationId to score))
                } else {
                    user
                }
            } else {
                user
            }
        }
        saveUsers(context, users)
        return users
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun parseScores(obj: JSONObject?): Map<String, Int> {
        if (obj == null) return emptyMap()
        val result = mutableMapOf<String, Int>()
        obj.keys().forEach { key ->
            result[key] = obj.getInt(key)
        }
        return result
    }

    private fun scoresToJson(scores: Map<String, Int>): JSONObject {
        val obj = JSONObject()
        scores.forEach { (key, value) -> obj.put(key, value) }
        return obj
    }
}
