package megvii.testfacepass.pa.beans;

public class KaoQingPage {
    private int size;
    private int page;
    private String time;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    @Override
    public String toString() {
        return "KaoQingPage{" +
                "size=" + size +
                ", page=" + page +
                ", time='" + time + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
