package com.wei.music.database.typeconverter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wei.music.bean.AccountDTO;
import com.wei.music.bean.BindingsDTO;
import com.wei.music.bean.ExpertsDTO;
import com.wei.music.bean.ProfileDTO;

import java.util.Collections;
import java.util.List;

public class UserDtoConverters {

    private static final Gson gson = new Gson();

    @TypeConverter
    public static String accountDTO2Str(AccountDTO accountDTO) {
        if (accountDTO == null) {
            return null;
        }
        return gson.toJson(accountDTO);
    }

    @TypeConverter
    public static AccountDTO fromAccountDTOStr(String accountStr) {
        if (accountStr == null) {
            return null;
        }
        return gson.fromJson(accountStr, AccountDTO.class);
    }


    @TypeConverter
    public static String profileDTO2Str(ProfileDTO profileDTO) {
        if (profileDTO == null) {
            return null;
        }
        return gson.toJson(profileDTO);
    }

    @TypeConverter
    public static ProfileDTO fromProfileDTOStr(String profileDTOStr) {
        if (profileDTOStr == null) {
            return null;
        }
        return gson.fromJson(profileDTOStr, ProfileDTO.class);
    }


    @TypeConverter
    public static String bindingDto2Str(List<BindingsDTO> bindingsDTOList) {
        if (bindingsDTOList == null || bindingsDTOList.isEmpty()) {
            return null;
        }
        return gson.toJson(bindingsDTOList);
    }

    @TypeConverter
    public static List<BindingsDTO> str2BindingsList(String bindingStr) {
        if (bindingStr == null) {
            return Collections.emptyList();
        }
        return gson.fromJson(bindingStr, new TypeToken<List<BindingsDTO>>() {
        }.getType());
    }


    @TypeConverter
    public static String expertsDTO2Str(ExpertsDTO expertsDTO) {
        if (expertsDTO == null) {
            return null;
        }
        return gson.toJson(expertsDTO);
    }

    @TypeConverter
    public static ExpertsDTO str2ExpertsDTO(String expertsStr) {
        if (expertsStr == null) {
            return null;
        }
        return gson.fromJson(expertsStr, ExpertsDTO.class);
    }
}
