package edu.usc.cs.db.hw5.model;

import edu.usc.cs.db.hw5.services.DbService;

import java.util.Iterator;

/**
 * This model stores information related to Regions
 * @author Thamme Gowda N
 */
public class Region extends GeoModel {
    public static Iterator<Region> getAll() {
        return DbService.getInstance().getAll(Region.class);
    }
}
