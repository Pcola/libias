package sk.atos.fri.rest.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncidentCountResponse {

  private Long statusId;
  private Long count;
  private Map<Long, Long> countPrioMap = new HashMap<>();

  public IncidentCountResponse(Long statusId, List<Long> prioList) {
    this.statusId = statusId;
    this.count = 0L;
    this.countPrioMap = new HashMap<>();

    for (Long prio : prioList) {
      countPrioMap.put(prio, 0L);
    }
  }

  public IncidentCountResponse() {
  }

  public Long getStatusId() {
    return statusId;
  }

  public void setStatusId(Long statusId) {
    this.statusId = statusId;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }

  public Map<Long, Long> getCountPrioMap() {
    return countPrioMap;
  }

  public void setCountPrioMap(Map<Long, Long> countPrioMap) {
    this.countPrioMap = countPrioMap;
  }

}
