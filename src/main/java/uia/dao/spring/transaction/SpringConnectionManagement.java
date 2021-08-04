package uia.dao.spring.transaction;

import org.springframework.jdbc.datasource.DataSourceUtils;
import uia.dao.spring.ConnectionManagement;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.springframework.util.Assert.notNull;

/**
 * @author ks015774
 * @since 2021-08-03 13:59:00
 */
public class SpringConnectionManagement implements ConnectionManagement {

    private final DataSource dataSource;

    public SpringConnectionManagement(DataSource dataSource) {
        notNull(dataSource, "No DataSource specified");
        this.dataSource = dataSource;
    }

    public Connection openConnection() throws SQLException {
        return DataSourceUtils.getConnection(this.dataSource);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void close(Connection connection) throws SQLException {
        DataSourceUtils.releaseConnection(connection, this.dataSource);
    }


}

