package com.SideProject.ECommerce.config;

import java.util.Map;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement; 

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
	entityManagerFactoryRef = "mySqlEntityManagerFactory",
	transactionManagerRef = "mySqlTransactionManager", 
	basePackages = { "com.SideProject.ECommerce.manager.dao" }
)
public class MySqlDataSourceConfig { 

   @Autowired
   @Qualifier("mysql")
   private DataSource dataSource; 

   @Autowired
   private HibernateProperties hibernateProperties; 

   @Autowired
   private JpaProperties jpaProperties; 

   @Bean(name = "mySqlEntityManager")
   public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
      return entityManagerFactory(builder).getObject().createEntityManager();
   } 

   @Bean(name = "mySqlEntityManagerFactory")
   public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
      return builder.dataSource(dataSource)
    		  .properties(getVendorProperties())
    		  .packages("com.SideProject.ECommerce.manager.entity").build();
   } 

   private Map<String, Object> getVendorProperties() {
      return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
   } 

   @Bean(name = "mySqlTransactionManager")
   public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
      return new JpaTransactionManager(entityManagerFactory(builder).getObject());
   }

}
