package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SchoolEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    @Query("SELECT * FROM schools ORDER BY name ASC")
    fun getAllSchools(): Flow<List<SchoolEntity>>

    @Query("SELECT * FROM schools WHERE id = :id LIMIT 1")
    suspend fun getSchoolById(id: String): SchoolEntity?

    @Query("SELECT * FROM schools WHERE eiin = :eiin LIMIT 1")
    suspend fun getSchoolByEiin(eiin: String): SchoolEntity?

    @Query("""
        SELECT * FROM schools 
        WHERE name LIKE '%' || :query || '%' 
           OR nameBn LIKE '%' || :query || '%' 
           OR eiin LIKE '%' || :query || '%' 
           OR district LIKE '%' || :query || '%'
        ORDER BY 
          CASE WHEN eiin = :query THEN 0 
               WHEN name LIKE :query || '%' THEN 1 
               ELSE 2 END,
          name ASC
    """)
    fun searchSchools(query: String): Flow<List<SchoolEntity>>

    @Query("SELECT DISTINCT division FROM schools ORDER BY division ASC")
    fun getAllDivisions(): Flow<List<String>>

    @Query("SELECT DISTINCT district FROM schools WHERE division = :division ORDER BY district ASC")
    fun getDistrictsByDivision(division: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchools(schools: List<SchoolEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchool(school: SchoolEntity)

    @Update
    suspend fun updateSchool(school: SchoolEntity)

    @Query("DELETE FROM schools WHERE id = :id")
    suspend fun deleteSchool(id: String)

    @Query("SELECT COUNT(*) FROM schools")
    suspend fun getSchoolCount(): Int
}
