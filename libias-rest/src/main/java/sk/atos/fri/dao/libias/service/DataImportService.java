package sk.atos.fri.dao.libias.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.dao.libias.model.Image;
import sk.atos.fri.log.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Repository used for importing data
 */
@Repository
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class DataImportService {

    @Autowired
    @Qualifier("libias")
    private DataSource dataSource;

    @Autowired
    private Logger LOG;

    @PersistenceContext(unitName = "libias-pu")
    private EntityManager entityManager;

    public void importMaris2Libias() throws SQLException {
        CallableStatement callableStatement = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            callableStatement = connection.prepareCall("{call IMPORT_MARIS_TO_LIBIAS()}");
            callableStatement.execute();
        } finally {
            JdbcUtils.closeStatement(callableStatement);
            JdbcUtils.closeConnection(connection);
        }
    }

    public void importLibias2Cognitec(String jobId) throws SQLException {
        CallableStatement callableStatement = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            callableStatement = connection.prepareCall("{call IMPORT_LIBIAS_TO_COGNITEC(?)}");
            callableStatement.setString(1, jobId);
            callableStatement.execute();
        } finally {
            JdbcUtils.closeStatement(callableStatement);
            JdbcUtils.closeConnection(connection);
        }
    }

    public void importCrossIdentificationResult2Libias(String jobId) throws SQLException {
        CallableStatement callableStatement = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            callableStatement = connection.prepareCall("{call IMPORT_COGNITEC_TO_LIBIAS(?)}");
            callableStatement.setString(1, jobId);
            callableStatement.execute();
        } finally {
            JdbcUtils.closeStatement(callableStatement);
            JdbcUtils.closeConnection(connection);
        }
    }

    public void filterIncidentBeforeDataFetch() throws SQLException {
        CallableStatement callableStatement = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            callableStatement = connection.prepareCall("{call FILTER_BEFORE_DATA_FETCH()}");
            callableStatement.execute();
        } finally {
            JdbcUtils.closeStatement(callableStatement);
            JdbcUtils.closeConnection(connection);
        }
    }

    public void filterIncidentAfterDataFetch() throws SQLException {
        CallableStatement callableStatement = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            callableStatement = connection.prepareCall("{call FILTER_AFTER_DATA_FETCH()}");
            callableStatement.execute();
        } finally {
            JdbcUtils.closeStatement(callableStatement);
            JdbcUtils.closeConnection(connection);
        }
    }

    public void resetAfterDataFetchFilter() throws SQLException {
        CallableStatement callableStatement = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            callableStatement = connection.prepareCall("{call RESET_FILTER_AFTER_DATA_FETCH()}");
            callableStatement.execute();
        } finally {
            JdbcUtils.closeStatement(callableStatement);
            JdbcUtils.closeConnection(connection);
        }
    }

    public void importDeleted2Libias(Date pDate) throws SQLException {
        CallableStatement callableStatement = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            callableStatement = connection.prepareCall("{call IMPORT_DELETED_TO_LIBIAS(?)}");
            callableStatement.setDate(1, new java.sql.Date(pDate.getTime()));
            callableStatement.execute();
        } finally {
            JdbcUtils.closeStatement(callableStatement);
            JdbcUtils.closeConnection(connection);
        }
    }

    public void importDeleted2Cognitec(Date pDate) throws SQLException {
        CallableStatement callableStatement = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            callableStatement = connection.prepareCall("{call IMPORT_DELETED_TO_COGNITEC(?)}");
            callableStatement.setDate(1, new java.sql.Date(pDate.getTime()));
            callableStatement.execute();
        } finally {
            JdbcUtils.closeStatement(callableStatement);
            JdbcUtils.closeConnection(connection);
        }
    }

    public List<Long> findCasesToDelete(Date pDate) {
        Query query = entityManager.createQuery("SELECT i.oid FROM Image i WHERE i.dateDeleted = :pDate");
        query.setParameter("pDate", pDate);
        return (List<Long>) query.getResultList();
    }

    public void updateDeletedPersons(Date pDate, Long pkz, IIncidentService incidentService) {
        if (pkz == null) return;

        int updated = incidentService.markPersonsDeletedByPkz(pDate, pkz);
        LOG.debug("Updated  " + updated + "  incidents with deleted PKZ " + pkz);
    }

    public void updateDeletedFiles(Date pDate, String akz, IIncidentService incidentService) {
        if (akz == null) return;

        int updated = incidentService.markFilesDeletedByAkz(pDate, akz);
        LOG.debug("Updated  " + updated + "  incidents with deleted AKZ " + akz);
    }

    @SuppressWarnings("unchecked")
    public List<Long> findCasesToDeleteForGes(Date pDate) {
        Query query = entityManager.createQuery(
                "SELECT i.oid FROM Image i " +
                        "WHERE i.dateDeleted = :pDate " +
                        "AND i.dateDeletedGes IS NULL"
        );
        query.setParameter("pDate", pDate);
        return (List<Long>) query.getResultList();
    }

    @Transactional
    public void markGesDeleted(Collection<Long> caseIds, Date deletedAt) {
        if (caseIds == null || caseIds.isEmpty()) {
            return;
        }

        Query q = entityManager.createQuery(
                "UPDATE Image i " +
                        "SET i.dateDeletedGes = :deletedAt " +
                        "WHERE i.oid IN :ids"
        );
        q.setParameter("deletedAt", deletedAt);
        q.setParameter("ids", caseIds);
        q.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public List<Image> findImagesToEnrollForGes(Date pDate) {
        Query q = entityManager.createQuery(
                "SELECT i FROM Image i " +
                        "WHERE i.dateCreated = :pDate " +
                        "AND i.dateCreatedGes IS NULL"
        );
        q.setParameter("pDate", pDate);
        return (List<Image>) q.getResultList();
    }

    @Transactional
    public void markGesDateCreated(Collection<Long> ids, Date createdAt) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        Query q = entityManager.createQuery(
                "UPDATE Image i " +
                        "SET i.dateCreatedGes = :createdAt " +
                        "WHERE i.oid IN :ids"
        );
        q.setParameter("createdAt", createdAt);
        q.setParameter("ids", ids);
        q.executeUpdate();
    }

	/*
	public void updateLockedFiles(Date pDate, String akz, IncidentService incidentService) {
		if (akz == null) {
			return;
		}

		Query query = entityManager.createQuery("SELECT i FROM Incident i " +
				"WHERE i.filter = 0 AND (i.aFileNumber = :akz OR i.bFileNumber = :akz)");
		query.setParameter("akz", akz);
		List<Incident> incidentsToUpdate = query.getResultList();
		LOG.debug("Updating " + incidentsToUpdate.size() + " incidents with locked AKZ " + akz);

		incidentsToUpdate.forEach(incident -> {
			if (akz.equals(incident.getaFileNumber())) {
				incident.setaAkteLocked(pDate);
			}
			if (akz.equals(incident.getbFileNumber())) {
				incident.setbAkteLocked(pDate);
			}
			incidentService.updateIncident(incident);
		});
	}
	*/

}
