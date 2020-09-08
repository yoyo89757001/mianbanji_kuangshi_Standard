package megvii.testfacepass.pa.beans;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class AttendanceBean {//考勤表

    @Id
    private long id;
    private String name;
    private String sid;//員工id
    private String department;//部门
    private int normalNumber;//正常打卡
    private int lateNumber;//今天的迟到次数
    private int leaveEarlyNumber;//今天的早退次数
    private int absenteeismNumber;//今天的缺勤次数
    private int lateNumber2;//今天的迟到次数
    private int leaveEarlyNumber2;//今天的早退次数
    private int absenteeismNumber2;//今天的缺勤次数
    private int OvertimeTime;//今天的加班时间（小时）
    private String yearMonthDay;//年月日
    private String yearMonth;//年月
    private String photo;// 照片

    public int getLateNumber2() {
        return lateNumber2;
    }

    public void setLateNumber2(int lateNumber2) {
        this.lateNumber2 = lateNumber2;
    }

    public int getLeaveEarlyNumber2() {
        return leaveEarlyNumber2;
    }

    public void setLeaveEarlyNumber2(int leaveEarlyNumber2) {
        this.leaveEarlyNumber2 = leaveEarlyNumber2;
    }

    public int getAbsenteeismNumber2() {
        return absenteeismNumber2;
    }

    public void setAbsenteeismNumber2(int absenteeismNumber2) {
        this.absenteeismNumber2 = absenteeismNumber2;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getYearMonthDay() {
        return yearMonthDay;
    }

    public void setYearMonthDay(String yearMonthDay) {
        this.yearMonthDay = yearMonthDay;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getNormalNumber() {
        return normalNumber;
    }

    public void setNormalNumber(int normalNumber) {
        this.normalNumber = normalNumber;
    }

    public int getLateNumber() {
        return lateNumber;
    }

    public void setLateNumber(int lateNumber) {
        this.lateNumber = lateNumber;
    }

    public int getLeaveEarlyNumber() {
        return leaveEarlyNumber;
    }

    public void setLeaveEarlyNumber(int leaveEarlyNumber) {
        this.leaveEarlyNumber = leaveEarlyNumber;
    }

    public int getAbsenteeismNumber() {
        return absenteeismNumber;
    }

    public void setAbsenteeismNumber(int absenteeismNumber) {
        this.absenteeismNumber = absenteeismNumber;
    }

    public int getOvertimeTime() {
        return OvertimeTime;
    }

    public void setOvertimeTime(int overtimeTime) {
        OvertimeTime = overtimeTime;
    }

    @Override
    public String toString() {
        return "AttendanceBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sid='" + sid + '\'' +
                ", department='" + department + '\'' +
                ", normalNumber=" + normalNumber +
                ", lateNumber=" + lateNumber +
                ", leaveEarlyNumber=" + leaveEarlyNumber +
                ", absenteeismNumber=" + absenteeismNumber +
                ", lateNumber2=" + lateNumber2 +
                ", leaveEarlyNumber2=" + leaveEarlyNumber2 +
                ", absenteeismNumber2=" + absenteeismNumber2 +
                ", OvertimeTime=" + OvertimeTime +
                ", yearMonthDay='" + yearMonthDay + '\'' +
                ", yearMonth='" + yearMonth + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
