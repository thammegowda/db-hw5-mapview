package edu.usc.cs.db.hw5.model;

import oracle.spatial.geometry.JGeometry;

/**
 * Base Class for all the models with geometry and ID
 */
public class GeoModel {

    private String id;
    private JGeometry geom;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JGeometry getGeom() {
        return geom;
    }

    public void setGeom(JGeometry geom) {
        this.geom = geom;
    }
}
