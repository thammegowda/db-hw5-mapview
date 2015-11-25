package edu.usc.cs.db.hw5.services;

import edu.usc.cs.db.hw5.C;
import edu.usc.cs.db.hw5.model.GeoModel;
import edu.usc.cs.db.hw5.model.Lion;
import edu.usc.cs.db.hw5.model.Pond;
import edu.usc.cs.db.hw5.model.Region;
import edu.usc.cs.db.hw5.util.ModelIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.*;
import java.util.Iterator;
import java.util.Properties;

/**
 * Singleton Service for interacting with database.
 * @author Thamme Gowda N
 */
public class DbService {

    private static final Logger LOG = LoggerFactory.getLogger(DbService.class);

    private final Connection conn;

    public static final String TEST_QRY = "SELECT * from LION where ROWNUM <= 1";
    private static final String REGION_QUERY = "SELECT * FROM region r " +
            " WHERE SDO_CONTAINS(r.geom, SDO_GEOMETRY(2001, NULL," +
            " MDSYS.SDO_POINT_TYPE(?, ?, NULL), NULL, NULL)) = 'TRUE'";

    private static final String PONDS_IN_REGION_QRY =
            "SELECT * FROM pond p WHERE SDO_INSIDE(p.geom, (SELECT geom FROM region WHERE id = ?)) = 'TRUE'";

    private static final String LIONS_IN_REGION_QRY =
            "SELECT * FROM lion l WHERE SDO_INSIDE(l.geom, (SELECT geom FROM region WHERE id = ?)) = 'TRUE'";


    /**
     * finds a region that covers the given point
     * @param x x coordinate of pint
     * @param y y coordinate of point
     * @return an instance of region if found, else null
     */
    public Region getRegionHavingPoint(double x, double y) {
        try {
            PreparedStatement statement = conn.prepareStatement(REGION_QUERY);
            statement.setDouble(1, x);
            statement.setDouble(2, y);
            ResultSet set = statement.executeQuery();
            return getFirst(set, Region.class, true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets all ponds in a given region
     * @param regionId id of region
     * @return stream of all ponds in a region
     */
    public Iterator<Pond> getPondsInRegion(String regionId){
        try {
            PreparedStatement statement = conn.prepareStatement(PONDS_IN_REGION_QRY);
            statement.setString(1, regionId);
            ResultSet set = statement.executeQuery();
            return new ModelIterator<>(OrmService.get(Pond.class), set);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * gets all lions in a given region
     * @param regionId region id
     * @return stream of lions which are in given region
     */
    public Iterator<Lion> getLionsInRegion(String regionId){
        try {
            PreparedStatement statement = conn.prepareStatement(LIONS_IN_REGION_QRY);
            statement.setString(1, regionId);
            ResultSet set = statement.executeQuery();
            return new ModelIterator<>(OrmService.get(Lion.class), set);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lazy initialization hack on class loading
     */
    private interface holder {
        DbService INSTANCE = new DbService();
    }

    /**
     * Gets single ton instance
     * @return single ton instance
     */
    public static DbService getInstance() {
        return holder.INSTANCE;
    }

    /**
     * Creates a default DB service which reads configs from setting file
     */
    private DbService(){
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
                // Looks like Oracle Spatial DB has cold start issue. Keeping it ready by doing a test
                this.testConnection();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new IllegalStateException(e);
        }
    }

    /**
     * Opens jdbc connection
     * @param driverName JDBC driver class name
     * @param url url to database
     * @param username username for authentication
     * @param password password for authentication
     * @return JDBC connection
     * @throws Exception when an error occurs
     */
    public Connection openConnection(String driverName, String url,
                                     String username, String password)
            throws Exception {

        LOG.info("Opening connection driver:{}; \n url:{}," +
                "\n username:{}, password:****", driverName, url, username);

        //load the driver
        Class.forName(driverName);
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Gets first record from result set and maps to object
     * @param result db result cursor
     * @param tClass the target class
     * @param closeAfter should the cursor be closed after mapping?
     * @param <T> target type
     * @return object or null based on success or failure
     * @throws SQLException
     */
    public<T extends GeoModel> T getFirst(ResultSet result, Class<T> tClass,
                                          boolean closeAfter) throws SQLException {
        try {
            return result.next() ? OrmService.get(tClass).mapRow(result) : null;
        } finally {
            if (closeAfter) {
                result.close();
            }
        }
    }

    /**
     * Test connectivity
     * @throws SQLException on error
     */
    public void testConnection() throws SQLException {
        long t = System.currentTimeMillis();
        Statement statement = conn.createStatement();
        ResultSet set = statement.executeQuery(TEST_QRY);
        int cols = set.getMetaData().getColumnCount();
        while(set.next()) {
            for (int i = 1; i <= cols; i++) {
                set.getObject(i);
            }
        }
        set.close();
        statement.close();
        System.out.println("Time taken for test Query :" + (System.currentTimeMillis() - t) + "ms");

    }

    /**
     * Gets all records in a table
     * @param clazz class name
     * @param <T> target type
     * @return stream of objects mapped from relations
     */
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
