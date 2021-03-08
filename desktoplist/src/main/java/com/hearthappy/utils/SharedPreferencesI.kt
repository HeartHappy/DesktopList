package com.hearthappy.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Created Date 2021/1/20.
 * @author ChenRui
 * ClassDescription:SP工具类
 */

inline fun Context.editApplySaveByName(spFileName: String, block: (edit: SharedPreferences.Editor) -> Unit) {
    val sp = this.getSharedPreferences(spFileName, Context.MODE_PRIVATE)
    val edit = sp.edit()
    block(edit)
    edit.apply()
}

inline fun Context.editCommitSaveByName(spFileName: String, block: (edit: SharedPreferences.Editor) -> Unit): Boolean {
    val sp = this.getSharedPreferences(spFileName, Context.MODE_PRIVATE)
    val edit = sp.edit()
    block(edit)
    return edit.commit()
}


inline fun Context.getSharedPreferences(spFileName: String, block: (sp: SharedPreferences) -> Unit) {
    block(this.getSharedPreferences(spFileName, Context.MODE_PRIVATE))
}

inline fun SharedPreferences.editApplySaveByName(block: (edit: SharedPreferences.Editor) -> Unit) {
    val edit = edit()
    block(edit)
    edit.apply()
}

infix fun SharedPreferences.toInt(key: String): Int {
    return this.getInt(key, 0)
}

infix fun SharedPreferences.toString(key: String): String {
    this.getString(key, "")?.let {
        return it
    } ?: return ""
}

infix fun SharedPreferences.toLong(key: String): Long {
    return this.getLong(key, 0L)
}

infix fun SharedPreferences.toFloat(key: String): Float {
    return this.getFloat(key, 0f)
}

infix fun SharedPreferences.toBoolean(key: String): Boolean {
    return this.getBoolean(key, false)
}

const val SP_FILENAME = "DesktopDefinitionTable"
const val KEY_PORTRAIT_SPAN_COUNT = "PORTRAIT"
const val KEY_LANDSCAPE_SPAN_COUNT = "LANDSCAPE"
const val KEY_PORTRAIT_SINGLE_SHOW_COUNT="KEY_PORTRAIT_SINGLE_SHOW_COUNT"
const val KEY_LANDSCAPE_SINGLE_SHOW_COUNT="KEY_LANDSCAPE_SINGLE_SHOW_COUNT"
const val KEY_SCREEN_WIDTH="KEY_SCREEN_WIDTH"
const val KEY_SCREEN_HEIGHT="KEY_SCREEN_HEIGHT"
