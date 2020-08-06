package com.example.ceshi_room_shujuku;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface StudentDao {

    @Query("SELECT * FROM student")
    List<Student> getAllUsers();

    @Query("SELECT * FROM student WHERE id=:id")
    Student getUser(String id);



    @Insert
    void insert(Student users);

    @Insert
    void insert(List<Student> users);

    @Update
    void update(Student users);

    @Update
    void update(List<Student> users);


    @Delete
    void delete(Student users);

    @Delete
    void delete(List<Student> users);

}
