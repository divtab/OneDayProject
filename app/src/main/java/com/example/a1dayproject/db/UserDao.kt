package com.example.a1dayproject.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

interface UserDao {
    @Query("SELECT * FROM userinfo ORDER BY id DESC")
    fun getAllUserInfo(): List<UserEntity>?


    @Insert
    fun insertUser(user: UserEntity?)

    @Delete
    fun deleteUser(user: UserEntity?)

    @Update
    fun updateUser(user: UserEntity?)

}