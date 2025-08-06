package sk.atos.fri.dao;

import java.util.Properties;
import javax.sql.DataSource;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Class for creating data source based on property file variables
 */
@Configuration
@EnableTransactionManagement
public class PersistenceJPAConfig {

  @Autowired
  private Environment env;

  @Bean
  @Primary
  public LocalContainerEntityManagerFactoryBean libiasEntityManagerFactory() throws Exception {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setPersistenceUnitName("libias-pu");
    em.setDataSource(libiasDataSource());
    em.setPackagesToScan(new String[]{"sk.atos.fri.dao.model.libias"});

    JpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);
    em.setJpaProperties(additionalProperties());

    return em;
  }

  @Bean
  @Qualifier("libias")
  @Primary
  public DataSource libiasDataSource() throws Exception {
    Properties pool = new Properties();
    pool.put("driverClassName", "oracle.jdbc.OracleDriver");
    pool.put("url", env.getProperty("jdbc.datasource.url"));
    pool.put("username", env.getProperty("jdbc.datasource.username"));
    pool.put("password", env.getProperty("jdbc.datasource.password"));
    pool.put("initialSize", env.getProperty("jdbc.datasource.pool.initialSize"));
    pool.put("minIdle", env.getProperty("jdbc.datasource.pool.minIdle"));
    pool.put("maxIdle", env.getProperty("jdbc.datasource.pool.maxIdle"));
    pool.put("maxActive", env.getProperty("jdbc.datasource.pool.maxActive"));
    pool.put("maxWait", env.getProperty("jdbc.datasource.pool.maxWait"));
    pool.put("testOnBorrow", env.getProperty("jdbc.datasource.pool.testOnBorrow"));
    pool.put("validationInterval", env.getProperty("jdbc.datasource.pool.validationInterval"));
    pool.put("validationQuery", env.getProperty("jdbc.datasource.pool.validationQuery"));
    return new DataSourceFactory().createDataSource(pool);
  }

  @Bean(name = "libiasTransactionManager")
  @Primary
  public PlatformTransactionManager libiasTransactionManager() throws Exception {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(libiasEntityManagerFactory().getObject());
    return transactionManager;
  }

  @Bean
  public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
    return new PersistenceExceptionTranslationPostProcessor();
  }

  public Properties additionalProperties() {
    Properties properties = new Properties();
    properties.setProperty("eclipselink.weaving", "false");
    properties.setProperty("eclipselink.cache.shared.default", "false");
    properties.setProperty("eclipselink.id-validation", "NULL");
    properties.setProperty("eclipselink.target-database", "Oracle");
    return properties;
  }

}
