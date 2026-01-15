package sk.atos.fri.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.fri.log.Logger;
import sk.atos.fri.ws.ges.service.model.SearchResponse;
import sk.atos.fri.ws.zimp.ZimpService;

import java.util.Map;

@RestController
@RequestMapping("/zimp")
public class ZimpController {

    private static final Logger LOGGER = new Logger();

    @Autowired
    private ZimpService zimpService;

    @PostMapping("/search")
    public SearchResponse search(@RequestBody Map<String, Object> request) {
        LOGGER.info("ZimpController: search requested");

        String imageBase64 = (String) request.get("image");
        if (imageBase64 == null || imageBase64.isEmpty()) {
            throw new IllegalArgumentException("Image is required");
        }

        String sanitizedImage = imageBase64.contains(",") ? imageBase64.split(",")[1] : imageBase64;

        String queryReason = (String) request.getOrDefault("queryReason", "keine Angabe");
        String correlationId = (String) request.get("correlationId");
        Integer maxHits = (Integer) request.get("maxHits");

        return zimpService.search(queryReason, sanitizedImage, correlationId, maxHits);
    }
}
