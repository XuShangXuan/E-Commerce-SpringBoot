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
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement; 

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
	entityManagerFactoryRef = "oracleEntityManagerFactory",//名稱自訂，但要與49行同名
	transactionManagerRef = "oracleTransactionManager", //名稱自訂，但要與62行同名
	basePackages = { "com.SideProject.ECommerce.dao" }
)
public class OracleDataSourceConfig { 

   @Autowired
   @Qualifier("oracle")
   private DataSource dataSource; 

   @Autowired
   private HibernateProperties hibernateProperties; 

   @Autowired
   private JpaProperties jpaProperties; 

   //啟用使用CriteriaQuery
   @Primary
   @Bean(name = "oracleEntityManager")
   public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
      return entityManagerFactory(builder).getObject().createEntityManager();
   } 
   
   //用來建立EntityManager和TransactionManager
   @Primary
   @Bean(name = "oracleEntityManagerFactory") //可用來自控制Transaction，但不建議
   public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
      return builder.dataSource(dataSource) //選擇連接的DB
    		  .properties(getVendorProperties()) //啟用.yml中共用的SQL設定 Ex:show-sql,format_sql，要關閉就不寫這行
    		  .packages("com.SideProject.ECommerce.entity").build(); //oracle的Table的DataModel路徑
   } 

   //取得.yml中共用的SQL設定 Ex:show-sql,format_sql
   private Map<String, Object> getVendorProperties() {
      return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
   }

   @Primary
   @Bean(name = "oracleTransactionManager")
   public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
      return new JpaTransactionManager(entityManagerFactory(builder).getObject());
   }

}
