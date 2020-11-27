package megvii.testfacepass.pa.beans;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;




@Database(entities = {Subject.class,HuiFuBean.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase databaseCreator;

    public static AppDatabase getInstance(Context context) {
        if (databaseCreator == null) {
            synchronized (AppDatabase.class) {
                if (databaseCreator == null) {
                    databaseCreator = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                            "user_perfom_2020.db").build();
                }
            }
        }
        return databaseCreator;
    }

    public static void destroyDatabase() {
        databaseCreator = null;
    }

   public abstract SubjectDao getSubjectDao();
   public abstract HuiFuBeanDao getHuiFuBeanDao();
}
