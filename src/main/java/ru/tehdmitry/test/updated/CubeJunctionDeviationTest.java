package ru.tehdmitry.test.updated;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tehdmitry.gcode.M204_smoothie_SetAcceleration;
import ru.tehdmitry.gcode.M205_marlin_SetAdvancedSettings;

import java.awt.geom.Point2D;

public class CubeJunctionDeviationTest extends CommonSquareTestWithOneParameter {

//    @JsonProperty
//    double retractionDistanceStart = 0.5;
//    @JsonProperty
//    double retractionDistanceInc = -1; // do not exceed nozzle length; -1 for auto

    @JsonProperty
    private double junctionDeviationStart = 0.02;
    @JsonProperty
    private double junctionDeviationInc = 0.02;

    @Override
    public void created() {
        super.created();
        setPatternSizeX(30);
        setPatternSizeY(30);
    }

    @Override
    public String getLayerParameterName() {
        return "Junction Deviation";
    }

    @Override
    public double layerTestParameterValue(int currentTest) {
        return junctionDeviationStart + junctionDeviationInc * currentTest;
    }

    @Override
    protected void onTestChanged(int currentTest) {
        // gCodes.add(new M204_smoothie_SetAcceleration().setX(layerTestParameterValue(currentTest)).setY(layerTestParameterValue(currentTest)));
        gCodes.add(new M205_marlin_SetAdvancedSettings().setJ(layerTestParameterValue(currentTest)));
    }

    @Override
    protected void onTestLayerChanged(int currentTest, int currentTestLayer) {
        if (currentTestLayer == 0) {
            extruder.setMultiplierExtrusion(printerConfiguration.getExtrusionMultiplier() * 1.6);
        } else if (currentTestLayer == 1) {
            extruder.setMultiplierExtrusion(printerConfiguration.getExtrusionMultiplier());
        }
    }

    @Override
    protected void moveToTestPosition(int currentTest, int currentTestLayer, int sideTestNum, Point2D.Double position) {
        extrudeTo(new Point2D.Double(position.getX(), position.getY()), currentTestLayer == 0 ? getFeedRatePrint() / 1.5 : getFeedRatePrint());
    }
}
