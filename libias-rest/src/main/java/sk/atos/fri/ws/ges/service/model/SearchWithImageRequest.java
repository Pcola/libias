package sk.atos.fri.ws.ges.service.model;

import java.util.List;

public class SearchWithImageRequest extends SearchBaseRequest {
    private byte[] img;
    private List<Double> landmarks;
    private List<Double> bbox;
    private List<Double> eye_position;

    public SearchWithImageRequest() {
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

    public List<Double> getEye_position() {
        return eye_position;
    }

    public void setEye_position(List<Double> eye_position) {
        this.eye_position = eye_position;
    }
}