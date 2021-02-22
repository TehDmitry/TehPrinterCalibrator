package ru.tehdmitry.test.updated;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tehdmitry.gcode.M109_SetExtruderTemperatureAndWait;
import ru.tehdmitry.gcode.M205_marlin_SetAdvancedSettings;

import java.awt.geom.Point2D;

public class TemperatureTowerTest extends CommonSquareTestWithOneParameter {

    @JsonProperty
    private double temperatureStart = 240;
    @JsonProperty
    private double temperatureInc = -5;

    @Override
    public String getLayerParameterName() {
        return "Temperature";
    }

    @Override
    public double layerTestParameterValue(int currentTest) {
        return temperatureStart + temperatureInc * currentTest;
    }

    @Override
    protected void setExtruderTemperatureForLayerTest(int currentTest) {
        retraction(feedRateRetraction, printerConfiguration.getRetractionDistance());
        gCodes.add(new M109_SetExtruderTemperatureAndWait(layerTestParameterValue(currentTest)));
        deretraction(feedRateRetraction, printerConfiguration.getRetractionDistance());
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
