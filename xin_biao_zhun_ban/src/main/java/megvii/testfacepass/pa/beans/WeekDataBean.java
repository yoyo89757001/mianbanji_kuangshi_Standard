package megvii.testfacepass.pa.beans;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class WeekDataBean {//工作日

    @Id
    private Long id;
    private String data;
    private long time;

    public WeekDataBean(String data, long time) {
        this.data = data;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public WeekDataBean() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
