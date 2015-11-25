package edu.usc.cs.db.hw5.services;

import edu.usc.cs.db.hw5.C;
import edu.usc.cs.db.hw5.model.*;
import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static edu.usc.cs.db.hw5.C.column.GEOM;

/**
 * This service maps Objects to relations and vice versa
 */
public class OrmService<T extends GeoModel> {


    public static Map<Class<? extends GeoModel>, OrmService<? extends GeoModel>> REGISTRY = new HashMap<>();
    static {
        Arrays.asList(Lion.class, Ambulance.class, Region.class, Pond.class)
                .forEach(c -> REGISTRY.put(c, new OrmService<>(c)));
    }

    private final Class<T> clazz;

    public static <T extends GeoModel> OrmService<T> get(Class<T> tClass) {
        if(!REGISTRY.containsKey(tClass)) {
            throw new IllegalArgumentException(tClass + " not mapped by ORM");
        }
        return (OrmService<T>) REGISTRY.get(tClass);
    }

    public static <T extends GeoModel> T mapRow(ResultSet result, Class<T> cls) throws SQLException {
        return get(cls).mapRow(result);
    }

    public OrmService(Class<T> clazz) {
        this.clazz = clazz;
    }

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
