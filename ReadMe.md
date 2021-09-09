# uia dao spring
Spring integration for uia.dao

## Installation and Getting Started
1.使用spring boot initializr初始化工程(不需要勾选任何依赖)

2.加入依赖
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
  <!-- 自行选用连接池 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.24</version>
</dependency>
  <!-- 自行引用jdbc驱动 -->
<dependency>
    <groupId>com.sap.cloud.db.jdbc</groupId>
    <artifactId>ngdbc</artifactId>
    <version>2.3.48</version>
</dependency>
<dependency>
    <groupId>org.uia.solution</groupId>
    <artifactId>uia-dao</artifactId>
    <version>0.3.2-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.uia.solution</groupId>
    <artifactId>uia-dao-spring</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
3.注入dataSource和daoFactory
```
    @Bean
    @Primary
    public DataSource dataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername("*******");
        druidDataSource.setPassword("*******");
        druidDataSource.setUrl("jdbc:XXXXXXXX");
        // druidDataSource.setFilters("stat,wall");
        druidDataSource.setFilters("stat");
        druidDataSource.setInitialSize(1);
        druidDataSource.setMinIdle(1);
        druidDataSource.setMaxActive(1);
        druidDataSource.setMaxWait(60000);
        druidDataSource.setUseGlobalDataSourceStat(true);
        druidDataSource.setDriverClassName("com.sap.db.jdbc.Driver");
        druidDataSource.setLogAbandoned(true);
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(6000000);
        druidDataSource.setTimeBetweenEvictionRunsMillis(3600000);
        druidDataSource.setMinEvictableIdleTimeMillis(120000);
        druidDataSource.setValidationQuery("SELECT * FROM DUMMY");
        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(true);
        return druidDataSource;
    }

    @Bean
    public DaoFactory daoFactory(){
        return new DaoFactory(true);
    }
```

4.增加DaoScan注解
```
@DaoScan(value="com.example.demo.dao")
```
5.在com.example.demo.dao下编写tableDao即可
