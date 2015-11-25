package edu.usc.cs.db.hw5.model;

import edu.usc.cs.db.hw5.services.DbService;

import java.util.Iterator;

/**
 * This model stores information related to Ponds
 */
public class Pond extends GeoModel  {
    public static Iterator<Pond> getAll(){
        return DbService.getInstance().getAll(Pond.class);
    }
}
