package edu.usc.cs.db.hw5.model;

import edu.usc.cs.db.hw5.services.DbService;

import java.util.Iterator;

/**
 * This model stores information related to Lions
 * @author Thamme Gowda N
 */
public class Lion extends GeoModel {

    public static Iterator<Lion> getAll() {
        return DbService.getInstance().getAll(Lion.class);
    }
}
