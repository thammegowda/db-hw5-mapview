package edu.usc.cs.db.hw5.view;

import edu.usc.cs.db.hw5.model.Lion;
import edu.usc.cs.db.hw5.model.Pond;
import edu.usc.cs.db.hw5.model.Region;
import edu.usc.cs.db.hw5.services.DbService;
import edu.usc.cs.db.hw5.util.GeometryHelper;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import oracle.spatial.geometry.JGeometry;

import java.util.Iterator;

public class MapView extends Application {


    private int sceneWidth = 800;
    private int sceneHeight= 600;
    private double lineWidth = 1.5;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("USC CSCI 585 HW5 Solution Map View");

        Group root = new Group();
        Canvas canvas = new Canvas(sceneWidth, sceneHeight);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        drawShapes(gc);

        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void drawGeometry(JGeometry geom, GraphicsContext gc, Paint fillColor,
                              Paint lineColor, double lineWidth){
        switch (geom.getType()) {

            case JGeometry.GTYPE_POINT:
                System.out.println("Draw Point");
                double[] pt = geom.getPoint();
                gc.setFill(fillColor);
                gc.fillOval(pt[0] - lineWidth, pt[1] - lineWidth, lineWidth, lineWidth);
                break;
            case JGeometry.GTYPE_POLYGON:
                int nPoints = geom.getNumPoints();
                double[] ordinates = geom.getOrdinatesArray();
                //assuming 2d graphics
                assert nPoints * 2 == ordinates.length;
                System.out.println("points " + nPoints);
                double[] xPts = new double[nPoints];
                double[] yPts = new double[nPoints];
                for (int i = 0, ptIndex = 0; i < ordinates.length - 1; i += 2, ptIndex++) {
                    xPts[ptIndex] = ordinates[i];
                    yPts[ptIndex] = ordinates[i + 1];
                }

                if (geom.isCircle()) {
                    System.out.println("Draw Circle");
                    double[] circlePts = GeometryHelper.centerOfCircle(xPts[0], yPts[0],
                            xPts[1], yPts[1], xPts[2], yPts[2]);
                    double centerX = circlePts[0];
                    double centerY = circlePts[1];
                    double radius = circlePts[2];
                    if (fillColor != null) {
                        gc.setFill(fillColor);
                        gc.fillOval(centerX - radius, centerY - radius, radius, radius);
                    }
                    if (lineColor != null) {
                        gc.setStroke(lineColor);
                        gc.setLineWidth(lineWidth);
                        gc.strokeOval(centerX - radius, centerY - radius, radius, radius);
                    }
                } else {
                    System.out.println("Draw Polygon");
                    if (fillColor != null) {
                        gc.setFill(fillColor);
                        gc.fillPolygon(xPts, yPts, nPoints);
                    }
                    if (lineColor != null) {
                        gc.setStroke(lineColor);
                        gc.setLineWidth(lineWidth);
                        gc.strokePolygon(xPts, yPts, nPoints);
                    }
                }
                break;
            default:
                throw new IllegalStateException("Not implemented yet : " + geom.getType());
        }
    }

    private void drawShapes(GraphicsContext gc) {

        gc.setFill(Color.RED);
        gc.strokeRect(0.0, 0.0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        ((Iterable<Region>)(Region::getAll)).forEach(r ->
                drawGeometry(r.getGeom(), gc, Color.WHITE, Color.BLACK, lineWidth));
        ((Iterable<Pond>)(Pond::getAll)).forEach(p ->
                drawGeometry(p.getGeom(), gc, Color.BLUE, Color.BLACK, lineWidth));

        ((Iterable<Lion>)(Lion::getAll)).forEach(l ->
                drawGeometry(l.getGeom(), gc, Color.GREEN, null, 5));

    }


    public static void main(String[] args) {
        launch(args);
    }
}