package megvii.testfacepass.pa.beans;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HuiFuBeanDao {

    @Query("SELECT * FROM huifubean")
    List<HuiFuBean> getAllSubject();

    @Query("SELECT * FROM huifubean WHERE id = :id3 LIMIT 1")
    HuiFuBean getSubjectById(Long id3);

    @Insert(onConflict = OnConflictStrategy.REPLACE) //相同的主键对象替换
    void insertAll(HuiFuBean users);//vararg 多个参数,也就是多个user

    @Delete
    void delete(HuiFuBean subject);


}
