package edu.usc.cs.db.hw5.services;

import edu.usc.cs.db.hw5.C;
import edu.usc.cs.db.hw5.model.GeoModel;
import edu.usc.cs.db.hw5.model.Lion;
import edu.usc.cs.db.hw5.util.ModelIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.Iterator;
import java.util.Properties;

/**
 *
 * Singleton Service for interacting with database
 */
public class DbService {

    private static final Logger LOG = LoggerFactory.getLogger(DbService.class);

    private final Connection conn;

    private interface holder {
        DbService INSTANCE = new DbService();
    }

    public static DbService getInstance() {
        return holder.INSTANCE;
    }

    public DbService(){
        try {
            try (InputStream stream = getClass().getClassLoader()
                    .getResourceAsStream("db.props") ) {
                Properties props = new Properties();
                props.load(stream);
                String driverName = props.getProperty("db.driver");
                String url = props.getProperty("db.url");
                String username = props.getProperty("db.username");
                String password = props.getProperty("db.password");
                this.conn = openConnection(driverName, url, username, password);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    public Connection openConnection(String driverName, String url,
                                     String username, String password)
            throws Exception {

        LOG.info("Opening connection driver:{}; \n url:{}," +
                "\n username:{}, password:****", driverName, url, username);

        //load the driver
        Class.forName(driverName);
        return DriverManager.getConnection(url, username, password);
    }

    public void testConnection() throws SQLException {
        Statement statement = conn.createStatement();
        ResultSet set = statement.executeQuery("SELECT * from LION");

        int cols = set.getMetaData().getColumnCount();
        int count = 0;
        while(set.next()) {
            for (int i = 1; i <= cols; i++) {
                System.out.printf(count++ + "\t" + set.getObject(i) + "\t");
            }
            System.out.println();
        }
    }

    public <T extends GeoModel> Iterator<T> getAll(Class<T> clazz) {
        try {
            Statement statement = conn.createStatement();
            statement.setFetchSize(10);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + C.table.index.get(clazz));
            return new ModelIterator<>(OrmService.get(clazz), resultSet);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
