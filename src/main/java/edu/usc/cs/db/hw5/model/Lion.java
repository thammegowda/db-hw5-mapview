package edu.usc.cs.db.hw5.model;

import edu.usc.cs.db.hw5.services.DbService;

import java.util.Iterator;

/**
 * Created by tg on 11/24/15.
 */
public class Lion extends GeoModel {

    public static Iterator<Lion> getAll() {
        return DbService.getInstance().getAll(Lion.class);
    }
}
