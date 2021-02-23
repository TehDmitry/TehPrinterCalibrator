package ru.tehdmitry.test.updated;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tehdmitry.gcode.M109_SetExtruderTemperatureAndWait;

import java.awt.geom.Point2D;

public class RetractOnLongDistanceTemperatureTest extends GTowerTest {

    @JsonProperty
    protected double moveSpeed = 10;

    @JsonProperty
    protected double retractionDistanceStart = 2.0;

    @JsonProperty
    protected double retractionDistanceInc = 0.0; //0.0 for auto

    @JsonProperty
    private double temperatureStart = 230;
    @JsonProperty
    private double temperatureInc = 1;


    @JsonProperty
    protected int testCountMultiplier = 2;

    protected Point2D.Double currentTestPosition = null;

    @Override
    public void created() {
        super.created();
        //setSideTestCount(16);
        setSideTestCount(4 * testCountMultiplier);
        setPatternSizeX(50);
        setPatternSizeY(50);
        setRaftOffsetMm(5);
    }

    @Override
    protected void setExtruderTemperatureForLayerTest(int currentTest) {
        retraction(feedRateRetraction, printerConfiguration.getRetractionDistance());
        gCodes.add(new M109_SetExtruderTemperatureAndWait(layerTestParameterValue(currentTest)));
        deretraction(feedRateRetraction, printerConfiguration.getRetractionDistance());
    }


    @Override
    protected void moveToTestPosition(int currentTest, int currentTestLayer, int sideTestNum, Point2D.Double position) {
        if (currentTestPosition != null) {
            super.moveTo(currentTestPosition, getFeedRateMove(), false);
        }

        retraction(getFeedRateRetraction(), sideParameterValue(sideTestNum));

        //todo: first half is fast move
        super.moveTo(position, currentTestLayer < delimeterLayers ? getFeedRateMove() : (moveSpeed * 60), true);
        deretraction(getFeedRateRetraction(), sideParameterValue(sideTestNum));

        currentTestPosition = position;
    }

    @Override
    public String getLayerParameterName() {
        return "Temperature";
    }

    @Override
    public double layerTestParameterValue(int currentTest) {
        return temperatureStart + temperatureInc * currentTest;
    }

    @Override
    public String getSideParameterName() {
        return "Retraction Distance";
    }

    @Override
    public double sideParameterValue(int sideNumber) {

        double inc = retractionDistanceInc;
        if (Math.round(retractionDistanceInc) <= 0) {
            inc = Math.floor(((printerConfiguration.getNozzleLength() - retractionDistanceStart) / getSideTestCount()) * 10.0) / 10.0;
        }

        return retractionDistanceStart + inc * sideNumber;
    }


}
