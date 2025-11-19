package com.wei.music.database.typeconverter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wei.music.bean.CreatorDTO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlaylistConverters {

    private static final Gson gson = new Gson();

    // 用于 CreatorDTO
    @TypeConverter
    public static CreatorDTO fromCreatorString(String value) {
        if (value == null) {
            return null;
        }
        Type type = new TypeToken<CreatorDTO>() {}.getType();
        return gson.fromJson(value, type);
    }

    @TypeConverter
    public static String creatorToString(CreatorDTO creator) {
        if (creator == null) {
            return null;
        }
        return gson.toJson(creator);
    }

    // 如果你以后还有其他 Object/List<?> 字段，也可以在这里加（比如 tracks、tags 等）
    // 示例：List<String> tags
    @TypeConverter
    public static List<String> fromTagsString(String value) {
        if (value == null) return new ArrayList<>();
        Type listType = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(value, listType);
    }

    @TypeConverter
    public static String tagsToString(List<String> tags) {
        if (tags == null) return null;
        return gson.toJson(tags);
    }
}