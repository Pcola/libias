package sk.atos.fri.dao.libias.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sk.atos.fri.common.Constants;
import sk.atos.fri.dao.libias.model.DbIdentProbes;
import sk.atos.fri.dao.libias.model.DbIdentResults;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.pdf.DbIdentReport;
import sk.atos.fri.ws.maris.model.PersonResponse;
import sk.atos.fri.ws.maris.service.MarisWSClient;

@Repository
public class DbIdentService {

  private static final String FILE_DIR = "C:/Temp/";
  private static final String FILE_EXT = ".pdf";

  @Autowired
  private Logger LOG;

  @PersistenceContext(unitName = "libias-pu")
  private EntityManager entityManager;

  @Autowired
  private MarisWSClient marisClient;

  @Autowired
  private DbIdentReport dbIdentReport;

  @Transactional
  public void enrichDbIdentResults(HttpServletRequest httpServletRequest) {
    String username = httpServletRequest.getUserPrincipal().getName();
    List<DbIdentProbes> probes = entityManager.createNamedQuery("DbIdentProbes.findAll").getResultList();

    for (DbIdentProbes probe : probes) {
      LOG.debug(username, "Probe " + probe.getProbeid());
      for (DbIdentResults result : probe.getResults()) {
        LOG.debug(username, "Result " + result.getGalleryid());
        PersonResponse response = marisClient.getPerson(Long.parseLong(result.getGalleryid()));
        if (response != null) {
          result.setApplicantOid(response.getApplicantOid());
          result.setPkz(response.getPkz());
          result.setFileNumber(response.getFileNumber());
          result.setAzrNumber(response.getAzrNumber());
          result.setDNumber(response.getdNumber());
          result.setENumber(response.geteNumber());
          result.setEuroDacNumber(response.getEuroDacNumber());
          result.setLastName(response.getLastName());
          result.setFirstName(response.getFirstName());
          result.setBirthDate(response.getBirthDate());
          result.setBirthCountry(response.getBirthCountry());
          result.setBirthPlace(response.getBirthPlace());
          result.setOriginCountry(response.getOriginCountry());
          result.setApplicantDate(response.getApplicantDate());
          result.setApplicantType(response.getApplicantType());
          result.setWorkplace(response.getWorkplace());
          result.setGender(response.getGender());
          result.setNationality(response.getNationality());
          result.setDateModified(response.getDateModified());
          result.setPersData(ToStringBuilder.reflectionToString(response, ToStringStyle.JSON_STYLE));
          entityManager.merge(result);
        }
      }
    }

    entityManager.flush();
  }

  public void exportDbIdentResults(HttpServletRequest httpServletRequest) {
    String username = httpServletRequest.getUserPrincipal().getName();
    List<DbIdentProbes> probes = entityManager.createNamedQuery("DbIdentProbes.findAll").getResultList();

    String personName = null;
    List<DbIdentProbes> personProbes = new ArrayList<DbIdentProbes>();

    for (DbIdentProbes probe : probes) {
      if (personName == null) {
        int idx = probe.getProbeid().lastIndexOf('_');
        personName = idx > 0 ? probe.getProbeid().substring(0, idx) : probe.getProbeid();
        personProbes.add(probe);
      } else {
        int idx = probe.getProbeid().lastIndexOf('_');
        String otherPersonName = idx > 0 ? probe.getProbeid().substring(0, idx) : probe.getProbeid();
        if (!otherPersonName.equalsIgnoreCase(personName)) {
          generateAndWriteExport(username, personName, personProbes);
          personName = otherPersonName;
          personProbes.clear();
        }
        personProbes.add(probe);
      }
    }

    if (personName != null) {
      generateAndWriteExport(username, personName, personProbes);
    }
  }

  private void generateAndWriteExport(String username, String personName, List<DbIdentProbes> personProbes) {
    LOG.debug(username, "Generating report for probe " + personName);
    try {
      byte[] reportBytes = dbIdentReport.createReport(personProbes, username, Constants.LANG_DE);
      FileUtils.writeByteArrayToFile(new File(FILE_DIR + personName + FILE_EXT), reportBytes);
    } catch (Exception e) {
      LOG.error(username, Error.CREATE_REPORT, e);
    }
  }

}
