package com.wsw.actlearn.config;

import com.google.common.collect.Lists;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.wsw.actlearn.entity.properties.DataSourceProperties;

import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.impl.db.DbSqlSessionFactory;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import lombok.extern.log4j.Log4j2;

/**
 * @author wsw
 */
@Log4j2
@Configuration
public class ActivityConfig extends AbstractProcessEngineAutoConfiguration {

    @Autowired
    private ActivitiEventListener globalEventListener;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private DataSourceProperties mysqlDataSourceProps;

    /**
     * 注入数据源和事务管理器
     * @param springAsyncExecutor 异步执行器
     * @param dbSqlSessionFactory 会话工厂
     * @return SpringProcessEngineConfiguration 流程引擎配置bean
     * @throws IOException
     */
    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(SpringAsyncExecutor springAsyncExecutor,
                                                                             DbSqlSessionFactory dbSqlSessionFactory) throws IOException {
        SpringProcessEngineConfiguration configuration
            = this.baseSpringProcessEngineConfiguration(dataSource(), transactionManager, springAsyncExecutor);
        configuration.setDbSqlSessionFactory(dbSqlSessionFactory);
        configuration.setActivityFontName("宋体");
        configuration.setLabelFontName("宋体");
        configuration.setAnnotationFontName("宋体");
        configuration.setEventListeners(new ArrayList<ActivitiEventListener>());
        configuration.getEventListeners().add(globalEventListener);
        return configuration;
    }


    /**
     * 自定义dataSource配置
     */
    @Bean(name = "dataSource")
    @Primary
    public DruidDataSource dataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl(mysqlDataSourceProps.getUrl());
        druidDataSource.setUsername(mysqlDataSourceProps.getUserName());
        druidDataSource.setPassword(mysqlDataSourceProps.getPassword());
        druidDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        druidDataSource.setInitialSize(1);
        druidDataSource.setMinIdle(1);
        druidDataSource.setMaxActive(mysqlDataSourceProps.getMaxActive());
        druidDataSource.setMaxWait(60000);
        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);
        druidDataSource.setValidationQuery("SELECT 1");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        try {
            druidDataSource.setFilters("stat,log4j");
            druidDataSource.setProxyFilters(Lists.newArrayList(wallFilter()));
        } catch (SQLException e) {
            log.error("druid configuration initialization filter", e);
        }
        Properties properties = new Properties();
        properties.setProperty("druid.stat.mergeSql", "true");
        properties.setProperty("druid.stat.slowSqlMillis", "2000");
        druidDataSource.setConnectProperties(properties);
        return druidDataSource;
    }

    @Bean("wallFilter")
    @Primary
    public WallFilter wallFilter() {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setConfig(wallConfig());
        return wallFilter;
    }

    @Bean("wallConfig")
    @Primary
    public WallConfig wallConfig() {
        WallConfig config = new WallConfig();
        config.setMultiStatementAllow(true);
        config.setNoneBaseStatementAllow(true);
        return config;
    }

    @Primary
    @Bean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }


}
