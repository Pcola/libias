package sk.atos.fri.ws.ges.service.model;

import java.util.List;

public class AnalyseRequest {
    private byte[] img;
    private List<Double> landmarks;
    private List<Double> bbox;
    private String ref_id;

    public AnalyseRequest() {
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
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

    public String getRef_id() {
        return ref_id;
    }

    public void setRef_id(String ref_id) {
        this.ref_id = ref_id;
    }
}