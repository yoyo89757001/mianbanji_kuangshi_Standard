package com.example.ceshi_room_shujuku;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = { Student.class }, version = 1,exportSchema = false)
public abstract class StudentDatabase extends RoomDatabase {

    private static final String DB_NAME = "UserDatabase.db";
    private static volatile StudentDatabase instance;

    static synchronized StudentDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static StudentDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                StudentDatabase.class,
                DB_NAME).build();
    }

    public abstract StudentDao getStudentDao();

}
