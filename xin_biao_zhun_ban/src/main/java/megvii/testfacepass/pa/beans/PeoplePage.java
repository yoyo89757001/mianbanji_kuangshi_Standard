package megvii.testfacepass.pa.beans;

public class PeoplePage {
    private int size;
    private int page;
    private int peopleType;

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

    public int getPeopleType() {
        return peopleType;
    }

    public void setPeopleType(int peopleType) {
        this.peopleType = peopleType;
    }

    @Override
    public String toString() {
        return "PeoplePage{" +
                "size=" + size +
                ", page=" + page +
                ", peopleType=" + peopleType +
                '}';
    }
}
