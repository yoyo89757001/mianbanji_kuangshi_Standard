package megvii.testfacepass.pa.utils;

import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.beans.AppDatabase;
import megvii.testfacepass.pa.beans.HuiFuBeanDao;
import megvii.testfacepass.pa.beans.SubjectDao;

public class DBUtils {

    public static SubjectDao getSubjectDao() {
        return AppDatabase.getInstance(MyApplication.context).getSubjectDao();
    }
    public static HuiFuBeanDao getHuiFuBeanDao() {
        return AppDatabase.getInstance(MyApplication.context).getHuiFuBeanDao();
    }
}
