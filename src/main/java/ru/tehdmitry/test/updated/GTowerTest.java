package ru.tehdmitry.test.updated;

import lombok.Setter;

import java.awt.geom.Point2D;

public abstract class GTowerTest extends CommonSquareTestWithTwoParameters {

    @Setter
    protected double gTowerWidth = 5;

    protected double delimeterLayers = 2;

    @Override
    protected void makeTest(int currentTest, int currentTestLayer, int sideTestNum, Point2D.Double position) {
        //double widthWithoutOverlap = printerConfiguration.getExtrusionWidth() * (1 - printerConfiguration.getPerimetersOverlapPc() / 100);
        double positionDifference = gTowerWidth / 2;

        if (currentTestLayer < 2) {
            positionDifference *= 1.05;
        }

        moveTo(new Point2D.Double(position.getX() - positionDifference, position.getY() - positionDifference), getFeedRateMove(), true);
        extrudeTo(new Point2D.Double(position.getX() + positionDifference, position.getY() - positionDifference), getFeedRatePrint());
        extrudeTo(new Point2D.Double(position.getX() + positionDifference, position.getY() + positionDifference), getFeedRatePrint());
        extrudeTo(new Point2D.Double(position.getX() - positionDifference, position.getY() + positionDifference), getFeedRatePrint());
        extrudeTo(new Point2D.Double(position.getX() - positionDifference, position.getY() - positionDifference), getFeedRatePrint());

        extrudeTo(new Point2D.Double(position.getX(), position.getY()), getFeedRatePrint());
    }

    @Override
    protected void onTestLayerChanged(int currentTest, int currentTestLayer) {
        if (currentTestLayer < delimeterLayers) {
            extruder.setMultiplierExtrusion(printerConfiguration.getExtrusionMultiplier() * 1.5);
        } else if (currentTestLayer == delimeterLayers) {
            extruder.setMultiplierExtrusion(printerConfiguration.getExtrusionMultiplier());
        }
    }
}
