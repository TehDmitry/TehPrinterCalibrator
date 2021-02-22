package ru.tehdmitry.test.updated;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.gcode.*;
import ru.tehdmitry.pattern.Raft;
import ru.tehdmitry.pattern.TextGenerator;

import java.awt.geom.Point2D;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




@Getter
public abstract class CommonSquareTest extends CommonTestImpl {

    private final Logger log = LogManager.getLogger(CommonSquareTest.class);

    protected int sideTestCount = 4;

    @Setter
    protected int layerTestCount = 1;
    @Setter
    protected int layersPerTestCount = 10;

    @Setter
    protected double patternSizeX = 20;
    @Setter
    protected double patternSizeY = 20;
    @Setter
    protected double raftOffsetMm = 2.5;

    // protected final double startPositionZ;
    protected Point2D.Double startPosition;

    protected int currentLayer = 1;

    protected CommonSquareTest() {
    }


    public void setSideTestCount(int sideTestCount) {
        this.sideTestCount = (sideTestCount / 4) * 4;
    }


    public String getTestName() {
//        return "cube";
        return this.getClass().getSimpleName().replace("Test", "");
    }

    public String getDescription() {
        return "simple_" + getTestName();
    }

    @Override
    public void process() {
        this.startPosition = new Point2D.Double(
                printerConfiguration.getDimensionX() / 2 - patternSizeX / 2 + Math.random() * calibrationConfiguration.getRandomizeStartPosition(),
                printerConfiguration.getDimensionY() / 2 - patternSizeY / 2 + Math.random() * calibrationConfiguration.getRandomizeStartPosition()
        );

        previousMove = startPosition;

        log.info(layersPerTestCount);

        generateHeader();
        generateInitialization();

        nextLayer();
        generateDescription();

        moveTo(startPosition, feedRateMove, true);

        for (int currentTest = 0; currentTest < layerTestCount; currentTest++) {
            gCodes.add(new CodeComment("Test " + currentTest));

            setExtruderTemperatureForLayerTest(currentTest);
            onTestChanged(currentTest);

            for (int currentTestLayer = 0; currentTestLayer < layersPerTestCount; currentTestLayer++) {
                gCodes.add(new CodeComment("Test " + currentTest + " Layer " + currentLayer));
                onTestLayerChanged(currentTest, currentTestLayer);


                int sideTestPerSide = sideTestCount / 4;

                double pathSizeX = patternSizeX - printerConfiguration.getExtrusionWidth();
                double pathSizeY = patternSizeY - printerConfiguration.getExtrusionWidth();

                double stepSizeX = pathSizeX / (sideTestPerSide);
                double stepSizeY = pathSizeY / (sideTestPerSide);

                moveTo(startPosition, feedRateMove, true);

                Point2D.Double toPosition;

                for (int sideTestNum = 0; sideTestNum < sideTestCount; sideTestNum++) {
                    makeTest(currentTest, currentTestLayer, sideTestNum, getTestPosition(sideTestNum));
                    moveToTestPosition(currentTest, currentTestLayer, sideTestNum, getTestPosition(sideTestNum + 1));
                }

                nextLayer();
                onLayerChanged(currentTest);
            }
        }

        generateFinalization();
        writeFile();
    }

    protected Point2D.Double getTestPosition(int sideTestNum) {
        Point2D.Double testPosition;
        int sideTestPerSide = sideTestCount / 4;

        double pathSizeX = patternSizeX - printerConfiguration.getExtrusionWidth();
        double pathSizeY = patternSizeY - printerConfiguration.getExtrusionWidth();

        double stepSizeX = pathSizeX / (sideTestPerSide);
        double stepSizeY = pathSizeY / (sideTestPerSide);

        int num = sideTestNum % sideTestPerSide;
        if (sideTestNum < sideTestPerSide) {
            //num = sideTestPerSide - sideTestNum;
            testPosition = new Point2D.Double(startPosition.getX() + stepSizeX * num, startPosition.getY());
        } else if (sideTestNum < sideTestPerSide * 2) {
            testPosition = new Point2D.Double(startPosition.getX() + pathSizeX, startPosition.getY() + stepSizeY * num);
        } else if (sideTestNum < sideTestPerSide * 3) {
            testPosition = new Point2D.Double(startPosition.getX() + pathSizeX - stepSizeX * num, startPosition.getY() + pathSizeY);
        } else if (sideTestNum < sideTestPerSide * 4) {
            testPosition = new Point2D.Double(startPosition.getX(), startPosition.getY() + pathSizeY - stepSizeY * num);
        } else {
            testPosition = new Point2D.Double(startPosition.getX(), startPosition.getY());
        }

        return testPosition;
    }

    protected void moveToTestPosition(int currentTest, int currentTestLayer, int sideTestNum, Point2D.Double position) {
        gCodes.add(new G0_G1_Move(false).setX(position.getX()).setY(position.getY()).setF(feedRatePrint).setExtrusionLength(extruder.getNextEValue(previousMove.distance(position))));
        previousMove = position;
    }

    protected void makeTest(int currentTest, int currentTestLayer, int sideTestNum, Point2D.Double position) {
        //do nothing for simple test
        if (currentTestLayer > 0) {

        }
    }



    protected void setExtruderTemperatureForLayerTest(int currentTest) {
        retraction(feedRateRetraction, printerConfiguration.getRetractionDistance());
        gCodes.add(new M109_SetExtruderTemperatureAndWait(printerConfiguration.getExtruderTemperature()));
        deretraction(feedRateRetraction, printerConfiguration.getRetractionDistance());
    }

    protected void nextLayer() {
        DecimalFormat dfRound = new DecimalFormat("0.##");

        G0_G1_Move move = new G0_G1_Move(false).setF(feedRateMove).setZ(currentLayer * printerConfiguration.getLayerHeight());

        //for g code viewer
        gCodes.add(new CodeComment("LAYER_CHANGE"));
        gCodes.add(new CodeComment("Z:" + dfRound.format(move.getZ())));
        gCodes.add(new CodeComment("HEIGHT:" + dfRound.format(printerConfiguration.getLayerHeight())));

        gCodes.add(move);
        currentLayer++;
    }

    protected void onLayerChanged(int currentTest) {
    }

    protected void onTestChanged(int currentTest) {
    }

    protected void onTestLayerChanged(int currentTest, int currentTestLayer) {
    }

    public List<String> getTopDescription() {
        List<String> result = new ArrayList<>();
        result.add("TopDescription");
        return result;
    }

    public List<String> getBottomDescription() {
        List<String> result = new ArrayList<>();
        result.add("BottomDescription");
        return result;
    }

    protected void generateDescription() {
        TextGenerator textGenerator = new TextGenerator(extruder, printerConfiguration);

        if (patternSizeX > 40) {
            int line = 1;
            for (String s : getTopDescription()) {
                gCodes.addAll(textGenerator.fromText(startPosition.getX() + 5, startPosition.getY() + patternSizeY - 5 * line - 5, 0.7, s));
                line++;
            }

            line = 1;
            List<String> lines = getBottomDescription();
            Collections.reverse(lines);
            for (String s : lines) {
                gCodes.addAll(textGenerator.fromText(startPosition.getX() + 5, startPosition.getY() + 3 + 5 * line, 0.7, s));
                line++;
            }

            gCodes.addAll(textGenerator.fromText(startPosition.getX() + 25, startPosition.getY() + 25, 0.9, "TPC 1.0"));
            gCodes.addAll(textGenerator.fromText(startPosition.getX() + 25, startPosition.getY() + 20, 0.9, String.format("run %.5s", calibrationConfiguration.getRunCount())));
        } else {
            gCodes.addAll(textGenerator.fromText(startPosition.getX() + 2, startPosition.getY() + 10, 0.9, "TPC 1.0"));
            gCodes.addAll(textGenerator.fromText(startPosition.getX() + 2, startPosition.getY() + 5, 0.9, String.format("run %.5s", calibrationConfiguration.getRunCount())));
        }
    }


    protected void generateInitialization() {

        gCodes.add(new CodeComment("Start Gcode"));
        gCodes.add(new M107_FanOff());

        gCodes.add(new M140_SetBedTemperature(printerConfiguration.getBedTemperature()));
        //gCodes.add(new M105_GetExtruderTemperature());
        gCodes.add(new M190_WaitForBedTemperature(printerConfiguration.getBedTemperature()));

        gCodes.add(new M104_SetExtruderTemperature(printerConfiguration.getExtruderTemperature()));
        //gCodes.add(new M105_GetExtruderTemperature());
        gCodes.add(new M109_SetExtruderTemperatureAndWait(printerConfiguration.getExtruderTemperature()));

        gCodes.add(new M82_SetExtruderToAbsoluteMode());
        gCodes.add(new G90_SetToAbsolutePositioning());
        gCodes.add(new G28_MoveToHome());

        gCodes.add(new G92_SetPosition().setE(0.0));
        gCodes.add(new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(extruder.getNextEValue(printerConfiguration.getRetractionDistance())));
        //gCodes.add(new G92_SetPosition().setE(0.0));

        gCodes.add(new CodeComment(""));
        gCodes.add(new CodeComment(""));

        // Start Movement
        gCodes.add(new CodeComment("Start Movement"));
        gCodes.add(new CodeComment(""));

        gCodes.add(new G0_G1_Move(false).setZ(2.0));
        gCodes.add(new G0_G1_Move(false).setF(printerConfiguration.getTravelSpeed() * 60).setX(startPosition.getX()).setY(startPosition.getY()).setZ(printerConfiguration.getLayerHeight()));

        if(printerConfiguration.getLinearAdvanceFactor() > 0) {
            gCodes.add(new M900_LinearAdvanceFactor().setK(printerConfiguration.getLinearAdvanceFactor()));
        }

        gCodes.add(new CodeComment(""));


//
//        if (printerConfiguration.getFirmware().equals("smoothie")) {
//            gCodes.add(new M204_smoothie_SetAcceleration().setE(printerConfiguration.getExtruderAcceleration()));
//        } else if (printerConfiguration.getFirmware().equals("klipper")) {
//            log.warn("klipper has no g-code for global extruder acceleration. do not forger to update config after tests");
//        } else {
//            throw new RuntimeException(printerConfiguration.getFirmware() + " Firmware is not implemented yet");
//        }

        // Overextruding Raft
        extruder.setMultiplierExtrusion(printerConfiguration.getExtrusionMultiplier() * 1.25);
        Raft raft = new Raft(extruder);
        gCodes.addAll(raft.generate(patternSizeX + raftOffsetMm * 2, patternSizeY + raftOffsetMm * 2, 1, startPosition.getX() - raftOffsetMm, startPosition.getY() - raftOffsetMm, printerConfiguration.getLayerHeight(), feedRatePrint, feedRateMove));
        extruder.setMultiplierExtrusion(printerConfiguration.getExtrusionMultiplier());
        currentLayer = 3;

        gCodes.add(new M106_FanOn(printerConfiguration.getFanSpeed() * 255 / 100));
    }

    protected void generateFinalization() {
        // Raise 5mm
        // gCodes.add(new G0_G1_Move(false).setZ(5.0));
        nextLayer();
        nextLayer();
        nextLayer();

        // Absolute Position
        gCodes.add(new G90_SetToAbsolutePositioning());

        //# Home X Y
        gCodes.add(new G28_MoveToHome().setX(true).setY(true));

        // Turn off Steppers
        gCodes.add(new M84_StopIdleHold());

        // Turn off Fan
        gCodes.add(new M107_FanOff());

        // Turn off Extruder
        gCodes.add(new M104_SetExtruderTemperature(0.0));

        // Turn off Bed
        gCodes.add(new M140_SetBedTemperature(0.0));
    }




}
