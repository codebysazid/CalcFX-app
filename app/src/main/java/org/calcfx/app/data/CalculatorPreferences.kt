package org.calcfx.app.data

import android.content.Context
import android.content.SharedPreferences
import org.calcfx.app.engine.AngleUnit
import org.calcfx.app.ui.viewmodel.HistoryItem
import org.json.JSONArray
import org.json.JSONObject

class CalculatorPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("calcfx_prefs", Context.MODE_PRIVATE)

    fun saveHistory(history: List<HistoryItem>) {
        val array = JSONArray()
        for (item in history.take(50)) {
            val obj = JSONObject().apply {
                put("expression", item.expression)
                put("result", item.result)
                put("timestamp", item.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString("history_json", array.toString()).apply()
    }

    fun loadHistory(): List<HistoryItem> {
        val json = prefs.getString("history_json", null) ?: return emptyList()
        val list = mutableListOf<HistoryItem>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    HistoryItem(
                        expression = obj.getString("expression"),
                        result = obj.getString("result"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveAngleUnit(unit: AngleUnit) {
        prefs.edit().putString("angle_unit", unit.name).apply()
    }

    fun loadAngleUnit(): AngleUnit {
        val name = prefs.getString("angle_unit", AngleUnit.DEGREE.name)
        return try {
            AngleUnit.valueOf(name ?: AngleUnit.DEGREE.name)
        } catch (_: Exception) {
            AngleUnit.DEGREE
        }
    }

    fun saveMemory(value: Double) {
        prefs.edit().putLong("memory_store", java.lang.Double.doubleToRawLongBits(value)).apply()
    }

    fun loadMemory(): Double {
        return java.lang.Double.longBitsToDouble(prefs.getLong("memory_store", 0L))
    }

    fun saveAccentTheme(themeId: String) {
        prefs.edit().putString("accent_theme", themeId).apply()
    }

    fun loadAccentTheme(): String {
        return prefs.getString("accent_theme", "dynamic") ?: "dynamic"
    }
}
