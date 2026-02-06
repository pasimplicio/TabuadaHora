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
    private const val KEY_ADS_REMOVED = "ads_removed"

    fun getUsers(context: Context): List<UserProfile> {
        val json = prefs(context).getString(KEY_USERS, "[]") ?: "[]"
        val arr = JSONArray(json)
        val list = mutableListOf<UserProfile>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val id = obj.getString("id")
            val name = obj.getString("name")
            val best = mutableMapOf<String, Int>()
            val bestObj = obj.optJSONObject("bestScores") ?: JSONObject()
            bestObj.keys().forEach { k -> best[k] = bestObj.getInt(k) }
            list.add(UserProfile(id = id, name = name, bestScores = best))
        }
        return list
    }

    fun addUser(context: Context, name: String): UserProfile {
        val users = getUsers(context).toMutableList()
        val id = UUID.randomUUID().toString()
        val profile = UserProfile(id = id, name = name, bestScores = emptyMap())
        users.add(profile)
        saveUsers(context, users)
        setActiveUserId(context, id)
        return profile
    }

    fun setActiveUserId(context: Context, userId: String) {
        prefs(context).edit().putString(KEY_ACTIVE_USER, userId).apply()
    }

    fun getActiveUserId(context: Context): String {
        return prefs(context).getString(KEY_ACTIVE_USER, "") ?: ""
    }

    fun isMusicMuted(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_MUSIC_MUTED, false)
    }

    fun setMusicMuted(context: Context, muted: Boolean) {
        prefs(context).edit().putBoolean(KEY_MUSIC_MUTED, muted).apply()
    }


    fun isAdsRemoved(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_ADS_REMOVED, false)
    }

    fun setAdsRemoved(context: Context, removed: Boolean) {
        prefs(context).edit().putBoolean(KEY_ADS_REMOVED, removed).apply()
    }


    fun updateBestScore(
        context: Context,
        userId: String,
        operationId: String,
        score: Int
    ): List<UserProfile> {
        val users = getUsers(context).toMutableList()
        val idx = users.indexOfFirst { it.id == userId }
        if (idx == -1) return users

        val current = users[idx]
        val best = current.bestScores.toMutableMap()
        val old = best[operationId] ?: 0
        if (score > old) {
            best[operationId] = score
            users[idx] = current.copy(bestScores = best)
            saveUsers(context, users)
        }
        return users
    }

    private fun saveUsers(context: Context, users: List<UserProfile>) {
        val arr = JSONArray()
        users.forEach { u ->
            val obj = JSONObject()
            obj.put("id", u.id)
            obj.put("name", u.name)
            val bestObj = JSONObject()
            u.bestScores.forEach { (k, v) -> bestObj.put(k, v) }
            obj.put("bestScores", bestObj)
            arr.put(obj)
        }
        prefs(context).edit().putString(KEY_USERS, arr.toString()).apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
