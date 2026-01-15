package sk.atos.fri.ws.ges.service.model;

import java.util.List;

public class EnrollRequest extends BaseIndexAuth {
    private Long dam_kbt_id;
    private Long kbq_id;
    private Long id;
    private String partition;
    private byte[] img;
    private Boolean overwrite_existing_doc;
    private List<Double> landmarks;
    private List<Double> bbox;
    private List<Double> eye_position;
    private Boolean do_not_enroll;

    public EnrollRequest() {
    }

    public Long getDam_kbt_id() {
        return dam_kbt_id;
    }

    public void setDam_kbt_id(Long dam_kbt_id) {
        this.dam_kbt_id = dam_kbt_id;
    }

    public Long getKbq_id() {
        return kbq_id;
    }

    public void setKbq_id(Long kbq_id) {
        this.kbq_id = kbq_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public Boolean getOverwrite_existing_doc() {
        return overwrite_existing_doc;
    }

    public void setOverwrite_existing_doc(Boolean overwrite_existing_doc) {
        this.overwrite_existing_doc = overwrite_existing_doc;
    }

    public List<Double> getLandmarks() {
        return landmarks;
    }

    public void setLandmarks(List<Double> landmarks) {
        this.landmarks = landmarks;
    }

    public List<Double> getBbox() {
        return bbox;
    }

    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    public List<Double> getEye_position() {
        return eye_position;
    }

    public void setEye_position(List<Double> eye_position) {
        this.eye_position = eye_position;
    }

    public Boolean getDo_not_enroll() {
        return do_not_enroll;
    }

    public void setDo_not_enroll(Boolean do_not_enroll) {
        this.do_not_enroll = do_not_enroll;
    }
}