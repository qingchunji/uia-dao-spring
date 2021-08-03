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
        Connection conn = DataSourceUtils.getConnection(this.dataSource);
        System.out.println(">>>>>>>>连接实例:" + conn);
        System.out.println(">>>>>>>>连接自动提交? " + conn.getAutoCommit());
        System.out.println(">>>>>>>>开启transaction " + DataSourceUtils.isConnectionTransactional(conn, this.dataSource));
        return conn;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void close(Connection connection) throws SQLException {
        System.out.println(">>>>>>>>关闭:" + connection);
        DataSourceUtils.releaseConnection(connection, this.dataSource);
    }


}

