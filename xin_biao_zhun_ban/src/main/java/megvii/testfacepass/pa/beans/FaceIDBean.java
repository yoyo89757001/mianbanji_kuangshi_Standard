package megvii.testfacepass.pa.beans;


import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class FaceIDBean {


    @Id
    private Long id;
    @Index
    private String subjectId;
    @Index
    private String faceBitmapId;
    @Index
    private String teZhengMa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getFaceBitmapId() {
        return faceBitmapId;
    }

    public void setFaceBitmapId(String faceBitmapId) {
        this.faceBitmapId = faceBitmapId;
    }

    public String getTeZhengMa() {
        return teZhengMa;
    }

    public void setTeZhengMa(String teZhengMa) {
        this.teZhengMa = teZhengMa;
    }
}
