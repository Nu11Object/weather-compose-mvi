package com.nullo.weathercompose.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nullo.weathercompose.data.local.model.CityDbModel

@Database(entities = [CityDbModel::class], version = 1, exportSchema = false)
abstract class FavouriteDatabase : RoomDatabase() {

    abstract fun getFavouriteCitiesDao(): FavouriteCitiesDao

    companion object {
        private const val DATABASE_NAME = "favourite_database.db"
        private var INSTANCE: FavouriteDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): FavouriteDatabase {
            INSTANCE?.let { return it }

            synchronized(LOCK) {
                INSTANCE?.let { return it }

                val database = Room.databaseBuilder(
                    context = context,
                    klass = FavouriteDatabase::class.java,
                    name = DATABASE_NAME,
                ).build()

                INSTANCE = database
                return database
            }
        }
    }
}
