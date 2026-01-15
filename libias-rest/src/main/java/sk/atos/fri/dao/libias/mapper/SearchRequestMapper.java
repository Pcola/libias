package sk.atos.fri.dao.libias.mapper;

import sk.atos.fri.dao.libias.model.SearchRequestCandidateEntity;
import sk.atos.fri.dao.libias.model.SearchRequestEntity;
import sk.atos.fri.dao.libias.model.SearchRequestListItemDto;

import java.util.List;

public class SearchRequestMapper {

    public static SearchRequestListItemDto toListItemDto(SearchRequestEntity entity) {
        if (entity == null) {
            return null;
        }
        SearchRequestListItemDto dto = new SearchRequestListItemDto();
        dto.setRequestId(entity.getRequestId());
        dto.setDateCreated(entity.getDateCreated());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setDateModified(entity.getDateModified());
        dto.setModifiedBy(entity.getModifiedBy());
        dto.setDienststelleId(entity.getDienststelleId());
        dto.setMaxCandidates(entity.getMaxCandidates());
        dto.setMinScore(entity.getMinScore());
        dto.setMinAge(entity.getMinAge());
        dto.setGeschlecht(entity.getGeschlecht());
        dto.setStaatsangehoerigkeit(entity.getStaatsangehoerigkeit());
        dto.setHighestScore(calculateHighestScore(entity.getCandidates()));
        return dto;
    }

    private static Double calculateHighestScore(List<SearchRequestCandidateEntity> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        return candidates.stream()
                .map(SearchRequestCandidateEntity::getScore)
                .filter(score -> score != null)
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
    }
}
