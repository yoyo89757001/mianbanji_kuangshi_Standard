package megvii.testfacepass.pa.beans;



import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Arrays;



/**
 * Created by Administrator on 2018/5/31.
 */
@Entity
public class Subject  {

    public Subject() {

        id = "";
    }


    @PrimaryKey
    @NonNull
    private String id;
    @ColumnInfo private String name;// 姓名
    @ColumnInfo private String companyId; // 公司ID
    @ColumnInfo private String companyName; // 公司名称
    @ColumnInfo private String workNumber; // 工号
    @ColumnInfo private String sex; // 性别
    @ColumnInfo private String phone;// 手机号
    @ColumnInfo private String peopleType;// 人员类型
    @ColumnInfo private String email;// 电子邮箱
    @ColumnInfo private String position; // 职位
    @ColumnInfo private int employeeStatus; // 是否在职
    @ColumnInfo  private int isOpen; // 是否开门  1是关，0是开
    @ColumnInfo private String remark;// 备注
    @ColumnInfo  private String photo;// 照片
    @ColumnInfo  private String storeId;// 门店ID
    @ColumnInfo  private String storeName;// 门店名称
    @ColumnInfo  private long entryTime; // 入职时间
    @ColumnInfo  private String birthday; // 生日
    @ColumnInfo private String teZhengMa;
    @ColumnInfo private String departmentName;
    @ColumnInfo private int daka;
    @ColumnInfo private String shijian;
    @ColumnInfo private String displayPhoto;
    @ColumnInfo private byte[] txBytes;
    @ColumnInfo  private int w;
    @ColumnInfo  private int h;
    @ColumnInfo private String idcardNum;
    @ColumnInfo private String  faceIds1;
    @ColumnInfo private String  faceIds2;
    @ColumnInfo  private String  faceIds3;
    @ColumnInfo  private String zpPath;


    public String getZpPath() {
        return zpPath;
    }

    public void setZpPath(String zpPath) {
        this.zpPath = zpPath;
    }

    public String getFaceIds1() {
        return faceIds1;
    }

    public void setFaceIds1(String faceIds1) {
        this.faceIds1 = faceIds1;
    }

    public String getFaceIds2() {
        return faceIds2;
    }

    public void setFaceIds2(String faceIds2) {
        this.faceIds2 = faceIds2;
    }

    public String getFaceIds3() {
        return faceIds3;
    }

    public void setFaceIds3(String faceIds3) {
        this.faceIds3 = faceIds3;
    }

    public String getIdcardNum() {
        return idcardNum;
    }

    public void setIdcardNum(String idcardNum) {
        this.idcardNum = idcardNum;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public byte[] getTxBytes() {
        return txBytes;
    }

    public void setTxBytes(byte[] txBytes) {
        this.txBytes = txBytes;
    }

    public String getDisplayPhoto() {
        return displayPhoto;
    }

    public void setDisplayPhoto(String displayPhoto) {
        this.displayPhoto = displayPhoto;
    }

    public String getShijian() {
        return shijian;
    }

    public void setShijian(String shijian) {
        this.shijian = shijian;
    }

    public int getDaka() {
        return daka;
    }

    public void setDaka(int daka) {
        this.daka = daka;
    }




    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getTeZhengMa() {
        return teZhengMa;
    }

    public void setTeZhengMa(String teZhengMa) {
        this.teZhengMa = teZhengMa;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPeopleType() {
        return peopleType;
    }

    public void setPeopleType(String peopleType) {
        this.peopleType = peopleType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getEmployeeStatus() {
        return employeeStatus;
    }

    public void setEmployeeStatus(int employeeStatus) {
        this.employeeStatus = employeeStatus;
    }

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public long getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(long entryTime) {
        this.entryTime = entryTime;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }


    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", companyId='" + companyId + '\'' +
                ", companyName='" + companyName + '\'' +
                ", workNumber='" + workNumber + '\'' +
                ", sex='" + sex + '\'' +
                ", phone='" + phone + '\'' +
                ", peopleType='" + peopleType + '\'' +
                ", email='" + email + '\'' +
                ", position='" + position + '\'' +
                ", employeeStatus=" + employeeStatus +
                ", isOpen=" + isOpen +
                ", remark='" + remark + '\'' +
                ", photo='" + photo + '\'' +
                ", storeId='" + storeId + '\'' +
                ", storeName='" + storeName + '\'' +
                ", entryTime='" + entryTime + '\'' +
                ", birthday='" + birthday + '\'' +
                ", teZhengMa='" + teZhengMa + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", daka=" + daka +
                ", shijian='" + shijian + '\'' +
                ", displayPhoto='" + displayPhoto + '\'' +
                ", txBytes=" + Arrays.toString(txBytes) +
                ", w=" + w +
                ", h=" + h +
                ", view="  +
                '}';
    }
}
