package sk.atos.fri.ws.ges.service.model;

import sk.atos.fri.dao.libias.model.Image;

public class EnrollResultForImage {
    private final Image image;
    private final EnrollResponse enrollResponse;

    public EnrollResultForImage(Image image, EnrollResponse enrollResponse) {
        this.image = image;
        this.enrollResponse = enrollResponse;
    }

    public Image getImage() {
        return image;
    }

    public EnrollResponse getEnrollResponse() {
        return enrollResponse;
    }
}