package com.example.thcsedu.data.local.converter

import androidx.room.TypeConverter
import com.example.thcsedu.data.local.entity.LoaiDanhGia
import org.json.JSONArray

class Converters {

    @TypeConverter
    fun fromListFloat(list: List<Float>?): String {
        if (list == null) return "[]"
        val jsonArray = JSONArray()
        for (item in list) {
            jsonArray.put(item.toDouble())
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toListFloat(json: String?): List<Float> {
        if (json.isNullOrEmpty()) return emptyList()
        val list = mutableListOf<Float>()
        val jsonArray = JSONArray(json)
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.getDouble(i).toFloat())
        }
        return list
    }

    @TypeConverter
    fun fromLoaiDanhGia(loaiDanhGia: LoaiDanhGia?): String? {
        return loaiDanhGia?.name
    }

    @TypeConverter
    fun toLoaiDanhGia(name: String?): LoaiDanhGia? {
        return name?.let { LoaiDanhGia.valueOf(it) }
    }
}
