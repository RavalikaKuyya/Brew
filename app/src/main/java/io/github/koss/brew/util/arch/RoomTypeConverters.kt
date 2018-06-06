package io.github.koss.brew.util.arch

import android.arch.persistence.room.TypeConverter
import android.net.Uri
import com.google.firebase.Timestamp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomTypeConverters {

    /**
     * URI type converters
     */
    @TypeConverter
    fun uriFromString(uriString: String?): Uri? =
            if (uriString == null ) null else Uri.parse(uriString)

    @TypeConverter
    fun uriToString(uri: Uri?): String? = uri?.toString()

    /**
     * List of String type converters
     */
    @TypeConverter
    fun listToString(list: List<String>): String = Gson().toJson(list)

    @TypeConverter
    fun stringToList(string: String): List<String> =
            Gson().fromJson(string, object: TypeToken<List<String>>(){}.type)


    @TypeConverter
    fun timestampFromSeconds(seconds: Long): Timestamp = Timestamp(seconds, 0)

    @TypeConverter
    fun secondsFromTimestamp(timestamp: Timestamp): Long = timestamp.seconds

}