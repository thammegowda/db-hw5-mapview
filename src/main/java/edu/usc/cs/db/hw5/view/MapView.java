package edu.usc.cs.db.hw5.view;

import edu.usc.cs.db.hw5.model.Lion;
import edu.usc.cs.db.hw5.model.Pond;
import edu.usc.cs.db.hw5.model.Region;
import edu.usc.cs.db.hw5.services.DbService;
import edu.usc.cs.db.hw5.util.GeometryHelper;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import oracle.spatial.geometry.JGeometry;

/**
 * This is the main View of this application.
 * This view contains {@link Canvas} based interface that maps Oracle's {@link JGeometry} to view.
 * This class also has other UI elements to satisfy the requirements Stated in 'USC Fall 2015 CSCI585 HW5'
 * homework requirements.
 *
 * @author Thamme Gowda.
 */
public class MapView extends Application {

    private GraphicsContext gc;
    private int sceneWidth = 800;
    private int sceneHeight= 600;
    private int pointSize = 4;
    private double lineWidth = 1.5;

    private CheckBox checkBox;

    private boolean special;
    /**
     * This handler is responsible for triggering UI updates based on mouse clicks
     */
    private EventHandler<? super MouseEvent> mouseClickHandler = e -> {
        if (MapView.this.checkBox.isSelected()) {
            System.out.println("Mouse Clicked XY : " + e.getX() + ", " + e.getY());
            drawCanvas(e.getX(), e.getY());
            special = true;
        } else if (special){
            //previously special view was rendered, now resetting it
            drawCanvas(-1, -1);
            special = false;
        }
        System.out.println("Checkbox not selected");
    };


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("USC CSCI 585 HW5 Solution Map View");
        BorderPane root = new BorderPane();
        Canvas canvas = new Canvas(sceneWidth, sceneHeight);
        canvas.setOnMouseClicked(mouseClickHandler);
        root.setCenter(canvas);
        root.setTop(getInputLayout());
        this.gc = canvas.getGraphicsContext2D();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        drawCanvas(-1, -1);
    }

    /**
     * Creates an input layout having checkbox
     * @return check box with input layout
     */
    public HBox getInputLayout(){
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #336699;");
        this.checkBox = new CheckBox("Show Lions and Ponds in same region");
        hbox.getChildren().addAll(checkBox);

        return hbox;
    }

    /**
     * Draws Geometry element in canvas
     * @param geom the geometry which is to be drawn
     * @param gc the context
     * @param fillColor fill color for geometry, if not required pass null
     * @param lineColor border/margin color of geometry, if not required pass null
     * @param lineWidth the width of border/margin line
     */
    private void drawGeometry(JGeometry geom, GraphicsContext gc, Paint fillColor,
                              Paint lineColor, double lineWidth){
        switch (geom.getType()) {

            case JGeometry.GTYPE_POINT:
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

    /**
     * Draws canvas and applies special operation on view based on coordinates of mouse click.
     * If the mouse click coordinates are negative, then no special operation is performed.
     * @param x the x coordinate of mouse click point
     * @param y the y coordinate of mouse click point
     */
    private void drawCanvas(double x, double y) {

        //clear
        gc.clearRect(0.0, 0.0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        //boundary
        gc.setStroke(Color.RED);
        gc.strokeRect(0.0, 0.0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        try {
            ///for testing
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ((Iterable<Region>) (Region::getAll)).forEach(r ->
                drawGeometry(r.getGeom(), gc, Color.WHITE, Color.BLACK, lineWidth));
        ((Iterable<Pond>)(Pond::getAll)).forEach(p ->
                drawGeometry(p.getGeom(), gc, Color.BLUE, Color.BLACK, lineWidth));

        ((Iterable<Lion>)(Lion::getAll)).forEach(l ->
                drawGeometry(l.getGeom(), gc, Color.GREEN, null, pointSize));

        if (x >= 0 && y >= 0) {
            // draw the selected region
            ///get the region

            Region region = DbService.getInstance().getRegionHavingPoint(x, y);
            if (region != null) {
                System.out.println("Region Clicked : " + region.getId());
                Iterable<Pond> ponds = () -> DbService.getInstance().getPondsInRegion(region.getId());
                ponds.forEach(p -> drawGeometry(p.getGeom(), gc, Color.RED, Color.BLACK, lineWidth));
                Iterable<Lion> lions = () -> DbService.getInstance().getLionsInRegion(region.getId());
                lions.forEach(l -> drawGeometry(l.getGeom(), gc, Color.RED, null, pointSize));
            }
        }

    }

    /**
     * Entry point to the application
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}