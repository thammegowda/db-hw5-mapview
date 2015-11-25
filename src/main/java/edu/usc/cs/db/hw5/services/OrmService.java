package edu.usc.cs.db.hw5.services;

import edu.usc.cs.db.hw5.C;
import edu.usc.cs.db.hw5.model.*;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static edu.usc.cs.db.hw5.C.column.GEOM;

/**
 * This service maps Objects to relations and vice versa
 */
public class OrmService<T extends GeoModel> {


    /**
     * Simple map based registry for mapping all the domain models to tables
     */
    public static Map<Class<? extends GeoModel>, OrmService<? extends GeoModel>> REGISTRY = new HashMap<>();
    static {
        //An ORM service is created for each Model class
        Arrays.asList(Lion.class, Ambulance.class, Region.class, Pond.class)
                .forEach(c -> REGISTRY.put(c, new OrmService<>(c)));
    }

    private final Class<T> clazz;

    /**
     * gets an ORM service for model class
     * @param tClass model class
     * @param <T> target type
     * @return ORM service instance
     */
    public static <T extends GeoModel> OrmService<T> get(Class<T> tClass) {
        if(!REGISTRY.containsKey(tClass)) {
            throw new IllegalArgumentException(tClass + " not mapped by ORM");
        }
        return (OrmService<T>) REGISTRY.get(tClass);
    }

    /**
     * Maps the current row in result set to target model
     * @param result result cursor
     * @param cls target class
     * @param <T> target type
     * @return an instance of target class
     * @throws SQLException
     */
    public static <T extends GeoModel> T mapRow(ResultSet result, Class<T> cls) throws SQLException {
        return get(cls).mapRow(result);
    }

    /**
     * Creates an instance of ORM service for given class
     * @param clazz model class which needs ORM
     */
    public OrmService(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * maps the current row in result set to Model object
     * @param result result set cursor
     * @return an instance of domain class created by mapping the curent row in cursor
     * @throws SQLException
     */
    public T mapRow(ResultSet result) throws SQLException {
        try {
            long t = System.currentTimeMillis();
            T model = clazz.newInstance();
            model.setId(result.getString(C.column.ID));
            System.out.println("Time taken to get ID :" + (System.currentTimeMillis() - t));
            JGeometry geom = JGeometry.loadJS((STRUCT) result.getObject(GEOM));
            t = System.currentTimeMillis();
            model.setGeom(geom);
            System.out.println("Time taken to map :"+ (System.currentTimeMillis() - t));
            return model;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
