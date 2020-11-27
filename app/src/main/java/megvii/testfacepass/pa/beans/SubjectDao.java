package megvii.testfacepass.pa.beans;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SubjectDao {

    @Query("SELECT * FROM subject")
    List<Subject> getAllSubject();

    @Query("SELECT * FROM subject WHERE id = :id3 LIMIT 1")
    Subject getSubjectById(String id3);

    @Query("SELECT * FROM subject WHERE teZhengMa = :te LIMIT 1")
    Subject getSubjectByTZM(String te);

    @Insert(onConflict = OnConflictStrategy.REPLACE) //相同的主键对象替换
    void insertAll(Subject users);//vararg 多个参数,也就是多个user

    @Delete
    void delete(Subject subject);


}
