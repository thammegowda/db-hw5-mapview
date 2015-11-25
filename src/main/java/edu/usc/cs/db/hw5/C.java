package edu.usc.cs.db.hw5;

import edu.usc.cs.db.hw5.model.*;

import java.util.HashMap;
import java.util.Map;

/**
 *  A (C)onstant or (C)onfig block for all the config constants
 */
public interface C {

    interface column {
        String ID = "id";
        String GEOM = "geom";
    }

    interface table {
        String LION = "lion";
        String REGION = "region";
        String POND = "pond";
        String AMBULANCE = "ambulance";
        Map<Class<?extends GeoModel>, String> index = new HashMap<Class<?extends GeoModel>, String>(){{
            put(Lion.class, LION);
            put(Region.class, REGION);
            put(Pond.class, POND);
            put(Ambulance.class, AMBULANCE);
        }};
    }
}
