package megvii.testfacepass.pa.beans;

import java.util.List;

public class DataSaveBean {

    private List<String> isArrange;
    private String radio;
    private String startTime1;
    private String endTime1;
    private String startTime2;
    private String endTime2;
    private int minute1_1;
    private int minute1_2;
    private int minute1_3;
    private int minute1_4;
    private int minute1_5;
    private int minute2_1;
    private int minute2_2;
    private String xinqi;

    public int getMinute1_4() {
        return minute1_4;
    }

    public void setMinute1_4(int minute1_4) {
        this.minute1_4 = minute1_4;
    }

    public int getMinute1_5() {
        return minute1_5;
    }

    public void setMinute1_5(int minute1_5) {
        this.minute1_5 = minute1_5;
    }



    public List<String> getIsArrange() {
        return isArrange;
    }

    public void setIsArrange(List<String> isArrange) {
        this.isArrange = isArrange;
    }

    public String getRadio() {
        return radio;
    }

    public void setRadio(String radio) {
        this.radio = radio;
    }

    public String getStartTime1() {
        return startTime1;
    }

    public void setStartTime1(String startTime1) {
        this.startTime1 = startTime1;
    }

    public String getEndTime1() {
        return endTime1;
    }

    public void setEndTime1(String endTime1) {
        this.endTime1 = endTime1;
    }

    public String getStartTime2() {
        return startTime2;
    }

    public void setStartTime2(String startTime2) {
        this.startTime2 = startTime2;
    }

    public String getEndTime2() {
        return endTime2;
    }

    public void setEndTime2(String endTime2) {
        this.endTime2 = endTime2;
    }

    public int getMinute1_1() {
        return minute1_1;
    }

    public void setMinute1_1(int minute1_1) {
        this.minute1_1 = minute1_1;
    }

    public int getMinute1_2() {
        return minute1_2;
    }

    public void setMinute1_2(int minute1_2) {
        this.minute1_2 = minute1_2;
    }

    public int getMinute1_3() {
        return minute1_3;
    }

    public void setMinute1_3(int minute1_3) {
        this.minute1_3 = minute1_3;
    }

    public int getMinute2_1() {
        return minute2_1;
    }

    public void setMinute2_1(int minute2_1) {
        this.minute2_1 = minute2_1;
    }

    public int getMinute2_2() {
        return minute2_2;
    }

    public void setMinute2_2(int minute2_2) {
        this.minute2_2 = minute2_2;
    }

    public String getXinqi() {
        return xinqi;
    }

    public void setXinqi(String xinqi) {
        this.xinqi = xinqi;
    }

    @Override
    public String toString() {
        return "DataSaveBean{" +
                "isArrange=" + isArrange +
                ", radio='" + radio + '\'' +
                ", startTime1='" + startTime1 + '\'' +
                ", endTime1='" + endTime1 + '\'' +
                ", startTime2='" + startTime2 + '\'' +
                ", endTime2='" + endTime2 + '\'' +
                ", minute1_1=" + minute1_1 +
                ", minute1_2=" + minute1_2 +
                ", minute1_3=" + minute1_3 +
                ", minute1_4=" + minute1_4 +
                ", minute1_5=" + minute1_5 +
                ", minute2_1=" + minute2_1 +
                ", minute2_2=" + minute2_2 +
                ", xinqi='" + xinqi + '\'' +
                '}';
    }
}
