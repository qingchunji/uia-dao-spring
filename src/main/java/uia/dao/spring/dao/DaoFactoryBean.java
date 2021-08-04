package uia.dao.spring.dao;

import org.springframework.beans.factory.FactoryBean;
import uia.dao.spring.DaoFactory;
import uia.dao.spring.TableDao;
import uia.dao.spring.transaction.SpringConnectionManagement;

import javax.sql.DataSource;

public class DaoFactoryBean<T extends TableDao<?>> implements FactoryBean<T> {

    private Class<T> daoClass;

    private DataSource dataSource;

    private DaoFactory daoFactory;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDaoFactory(DaoFactory daoFactory) {
        if (daoFactory == null) {
            this.daoFactory = new DaoFactory(false);
        }else {
            this.daoFactory = daoFactory;
        }
    }

    public DaoFactoryBean(Class<T> daoClass) {
        this.daoClass = daoClass;
    }
    @Override
    public T getObject() throws Exception {
        return daoFactory.proxyTableDao(daoClass, new SpringConnectionManagement(dataSource));
    }


    @Override
    public Class<?> getObjectType() {
        return this.daoClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
