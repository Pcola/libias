package sk.atos.fri.dao.libias.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.*;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import sk.atos.fri.dao.libias.model.Incident;
import sk.atos.fri.dao.libias.service.IncidentService;
import sk.atos.fri.log.Logger;

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

	public void updateDeletedPersons(Date pDate, Long pkz, IncidentService incidentService) {
		if (pkz == null) {
			return;
		}

		Query query = entityManager.createQuery("SELECT i FROM Incident i " +
				"WHERE i.filter = 0 AND (i.aPkz = :pkz OR i.bPkz = :pkz)");
		query.setParameter("pkz", pkz);
		List<Incident> incidentsToUpdate = query.getResultList();
		LOG.debug("Updating " + incidentsToUpdate.size() + " incidents with deleted PKZ " + pkz);

		incidentsToUpdate.forEach(incident -> {
			if (pkz.equals(incident.getaPkz())) {
				incident.setaPersonDeleted(pDate);
			}
			if (pkz.equals(incident.getbPkz())) {
				incident.setbPersonDeleted(pDate);
			}
			incidentService.updateIncident(incident);
		});
	}

	public void updateDeletedFiles(Date pDate, String akz, IncidentService incidentService) {
		if (akz == null) {
			return;
		}

		Query query = entityManager.createQuery("SELECT i FROM Incident i " +
				"WHERE i.filter = 0 AND (i.aFileNumber = :akz OR i.bFileNumber = :akz)");
		query.setParameter("akz", akz);
		List<Incident> incidentsToUpdate = query.getResultList();
		LOG.debug("Updating " + incidentsToUpdate.size() + " incidents with deleted AKZ " + akz);

		incidentsToUpdate.forEach(incident -> {
			if (akz.equals(incident.getaFileNumber())) {
				incident.setaAkteDeleted(pDate);
			}
			if (akz.equals(incident.getbFileNumber())) {
				incident.setbAkteDeleted(pDate);
			}
			incidentService.updateIncident(incident);
		});
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
