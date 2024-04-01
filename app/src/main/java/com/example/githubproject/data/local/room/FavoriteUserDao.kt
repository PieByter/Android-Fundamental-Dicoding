package com.example.githubproject.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.githubproject.data.local.entity.FavoriteUser

@Dao
interface FavoriteUserDao {
    @Query("SELECT * FROM FavoriteUser WHERE username = :username")
    fun getFavoriteUserByUsername(username: String): LiveData<FavoriteUser?>

    @Query("SELECT * FROM FavoriteUser ORDER BY username ASC")
    fun getAllUsers(): LiveData<List<FavoriteUser>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(favoriteUser: FavoriteUser)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(favoriteUsers: List<FavoriteUser>)

    @Delete
    fun delete(favoriteUser: FavoriteUser)

    @Query("DELETE FROM FavoriteUser WHERE username = :username")
    suspend fun deleteByUsername(username: String)

    @Query("SELECT * FROM FavoriteUser ORDER BY username ASC")
    fun getAllFavoriteUsers(): LiveData<List<FavoriteUser>>

    @Query("SELECT * FROM FavoriteUser WHERE isFavorite = 1 ORDER BY username ASC")
    fun getFavoriteUsers(): LiveData<List<FavoriteUser>>

}


