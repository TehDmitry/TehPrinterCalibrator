package ru.tehdmitry.pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.gcode.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RetractionTestCube implements TestPattern {
    private final Logger log = LogManager.getLogger(RetractionTestCube.class);

    private final PrinterConfiguration printerConfiguration;
    private final CalibrationConfiguration calibrationConfiguration;

    private final int branchPerSideCount;
    private final int layersCount;
    //
//    private final double layersPerTest;
//    private final int numTests;
    DecimalFormat dfRound = new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.US));


    private final double wallPieceLengthMm = 10;
    private final double raftOffsetMm = 5;

    //@Setter
    //private RetractionTowerCallbacks towerCallbacks;

    public RetractionTestCube(PrinterConfiguration printerConfiguration, CalibrationConfiguration calibrationConfiguration, int branchPerSideCount, int layersCount) {
        this.printerConfiguration = printerConfiguration;
        this.calibrationConfiguration = calibrationConfiguration;
        this.branchPerSideCount = branchPerSideCount;
        this.layersCount = layersCount;

//        this.layersPerTest = layersPerTest;
//        this.numTests = numTests;
    }

    @Override
    public String getDescription() {
        return "RetractionTestCube";

//        return towerCallbacks.getBranchParameterName() + " [" + dfRound.format(towerCallbacks.branchParameterValue(0)) + "..." + dfRound.format(towerCallbacks.branchParameterValue(15)) + "] vs. " +
//                towerCallbacks.getLayerParameterName() + " [" + dfRound.format(towerCallbacks.layerTestParameterValue(0)) + "..." + dfRound.format(towerCallbacks.layerTestParameterValue(numTests - 1)) + "]";
    }

    @Override
    public void process() {

    }

    public String build() {

        double feedRatePrint = printerConfiguration.getPrintSpeed() * 60; // mm/min
        double feedRateMove = printerConfiguration.getTravelSpeed() * 60; // mm/min

        Extruder extruder = new Extruder(printerConfiguration.getExtrusionWidth(), printerConfiguration.getLayerHeight(), printerConfiguration.getFilamentDiameter(), printerConfiguration.getExtrusionMultiplier());

        List<GCode> gCodes = new ArrayList<>();
        List<GCode> headerGCodes = new ArrayList<>();

        DecimalFormat df = new DecimalFormat("0.00");

        headerGCodes.add(new CodeComment("RetractionTestCube Test"));
        headerGCodes.add(new CodeComment(getDescription()));

        //ToDo show this
//        headerGCodes.add(new CodeComment(""));
//        headerGCodes.add(new CodeComment(towerCallbacks.getBranchParameterName() + " from the top looking down"));
//        headerGCodes.add(new CodeComment(""));
//        headerGCodes.add(new CodeComment("       " + df.format(towerCallbacks.branchParameterValue(11)) + "    " + df.format(towerCallbacks.branchParameterValue(10)) + "    " + df.format(towerCallbacks.branchParameterValue(9)) + "    " + df.format(towerCallbacks.branchParameterValue(8))));
//        headerGCodes.add(new CodeComment("		|		|		|		|"));
//        headerGCodes.add(new CodeComment(""));
//        headerGCodes.add(new CodeComment("" + df.format(towerCallbacks.branchParameterValue(12)) + "-                               -" + df.format(towerCallbacks.branchParameterValue(7))));
//        headerGCodes.add(new CodeComment(""));
//        headerGCodes.add(new CodeComment(""));
//        headerGCodes.add(new CodeComment("" + df.format(towerCallbacks.branchParameterValue(13)) + "-                               -" + df.format(towerCallbacks.branchParameterValue(6))));
//        headerGCodes.add(new CodeComment(""));
//        headerGCodes.add(new CodeComment(""));
//        headerGCodes.add(new CodeComment("" + df.format(towerCallbacks.branchParameterValue(14)) + "-                               -" + df.format(towerCallbacks.branchParameterValue(5))));
//        headerGCodes.add(new CodeComment(""));
//        headerGCodes.add(new CodeComment(""));
//        headerGCodes.add(new CodeComment("" + df.format(towerCallbacks.branchParameterValue(15)) + "-                               -" + df.format(towerCallbacks.branchParameterValue(4))));
//        headerGCodes.add(new CodeComment(""));
//        headerGCodes.add(new CodeComment("		|		|		|		|"));
//        headerGCodes.add(new CodeComment("       " + df.format(towerCallbacks.branchParameterValue(0)) + "    " + df.format(towerCallbacks.branchParameterValue(1)) + "    " + df.format(towerCallbacks.branchParameterValue(2)) + "    " + df.format(towerCallbacks.branchParameterValue(3)) + ""));
//        headerGCodes.add(new CodeComment(""));

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

        double xpos = printerConfiguration.getDimensionX() / 2 - (branchPerSideCount + 1) * wallPieceLengthMm / 2;
        double ypos = printerConfiguration.getDimensionY() / 2 - (branchPerSideCount + 1) * wallPieceLengthMm / 2;
        double zpos = printerConfiguration.getLayerHeight();

        // Start Movement
        gCodes.add(new CodeComment("Start Movement"));
        gCodes.add(new CodeComment(""));

        gCodes.add(new G0_G1_Move(false).setZ(2.0));
        gCodes.add(new G0_G1_Move(false).setF(printerConfiguration.getTravelSpeed() * 60).setX(xpos).setY(ypos).setZ(zpos));

        gCodes.add(new CodeComment(""));

        // Overextruding Raft
        extruder.setMultiplierExtrusion(printerConfiguration.getExtrusionMultiplier() * 1.25);

        if (printerConfiguration.getFirmware().equals("smoothie")) {
            gCodes.add(new M204_smoothie_SetAcceleration().setE(printerConfiguration.getExtruderAcceleration()));
        } else if (printerConfiguration.getFirmware().equals("klipper")) {
            log.warn("klipper has no g-code for global extruder acceleration. do not forger to update config after tests");
        } else {
            throw new RuntimeException(printerConfiguration.getFirmware() + " Firmware is not implemented yet");
        }

        double raftSize = raftOffsetMm * 2 + (branchPerSideCount + 1) * wallPieceLengthMm;
        Raft raft = new Raft(extruder);
        gCodes.addAll(raft.generate(raftSize, raftSize, 1, xpos - raftOffsetMm, ypos - raftOffsetMm, printerConfiguration.getLayerHeight(), feedRatePrint, feedRateMove));

        //gCodes.add(new G0_G1_Move(true).setX(xpos + 10).setY(ypos + 10).setZ(printerConfiguration.getLayerHeight() * 3));
        gCodes.add(new M106_FanOn(printerConfiguration.getFanSpeed() * 255 / 100));


        TextGenerator textGenerator = new TextGenerator(extruder, printerConfiguration);
        gCodes.add(new CodeComment("description"));
        extruder.setMultiplierExtrusion(printerConfiguration.getExtrusionMultiplier());

        gCodes.add(new G0_G1_Move(true).setZ(printerConfiguration.getLayerHeight() * 3));

//
//        gCodes.addAll(textGenerator.fromText(xpos + 8, ypos + 48, 0.8, String.format("%.20s", towerCallbacks.getLayerParameterName())));
//        gCodes.addAll(textGenerator.fromText(xpos + 8, ypos + 41, 1, String.format("%.20s", dfRound.format(towerCallbacks.layerTestParameterValue(numTests - 1)))));
//        gCodes.addAll(textGenerator.fromText(xpos + 8, ypos + 34, 1, String.format("%.20s", dfRound.format(towerCallbacks.layerTestParameterValue(0)))));
//
        gCodes.addAll(textGenerator.fromText(xpos + 1, ypos + 12, 0.8, "TPC 1.0"));
        gCodes.addAll(textGenerator.fromText(xpos + 1, ypos + 4, 0.8, String.format("run %.5s", calibrationConfiguration.getRunCount())));
//
//        gCodes.addAll(textGenerator.fromText(xpos + 8, ypos + 17, 0.8, String.format("%.20s", towerCallbacks.getBranchParameterName())));
//        gCodes.addAll(textGenerator.fromText(xpos + 8, ypos + 10, 1, String.format("%.20s", dfRound.format(towerCallbacks.branchParameterValue(0)) + " to " + dfRound.format(towerCallbacks.branchParameterValue(15)))));

        // Bring back to Calibration Starting Position
        gCodes.add(new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(extruder.getNextEValue(printerConfiguration.getRetractionDistance() * -1)));
        gCodes.add(new G0_G1_Move(true).setX(xpos).setY(ypos));
        gCodes.add(new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(extruder.getNextEValue(printerConfiguration.getRetractionDistance())));

        // Relative Movements
        gCodes.add(new M83_SetExtruderToRelativeMode());
        gCodes.add(new G91_SetToRelativePositioning());

        //extruder.resetE();

        int layer = 3;
        for (int testLayer = 0; testLayer < layersCount; testLayer++) {
            gCodes.add(new CodeComment("Layer " + layer));

            gCodes.add(new G0_G1_Move(false).setX(wallPieceLengthMm).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(wallPieceLengthMm)));
            for (int i = 0; i < branchPerSideCount; i++) {
                gCodes.addAll(goTestMoveAndWall(1, feedRatePrint, feedRateMove, extruder));
            }

            gCodes.add(new G0_G1_Move(false).setY(wallPieceLengthMm).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(wallPieceLengthMm)));
            for (int i = 0; i < branchPerSideCount; i++) {
                gCodes.addAll(goTestMoveAndWall(2, feedRatePrint, feedRateMove, extruder));
            }

            gCodes.add(new G0_G1_Move(false).setX(-1 * wallPieceLengthMm).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(wallPieceLengthMm)));
            for (int i = 0; i < branchPerSideCount; i++) {
                gCodes.addAll(goTestMoveAndWall(3, feedRatePrint, feedRateMove, extruder));
            }

            gCodes.add(new G0_G1_Move(false).setY(-1 * wallPieceLengthMm).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(wallPieceLengthMm)));
            for (int i = 0; i < branchPerSideCount; i++) {
                gCodes.addAll(goTestMoveAndWall(4, feedRatePrint, feedRateMove, extruder));
            }


            gCodes.add(new G0_G1_Move(false).setZ(printerConfiguration.getLayerHeight()));
            layer++;
        }


//        for (int curTest = 0; curTest < numTests; curTest++) {
//
//            gCodes.add(new CodeComment("Test " + curTest));
//
//            //gCodes.add(new M106_FanOn((double) Math.round((fs + fsi * curTest) * 255 / 100)));
//
//            gCodes.add(new G0_G1_Move(false).setF(feedRateMove).setExtrusionLength(printerConfiguration.getRetractionDistance() * -1));
//
//            GCode layerTestGcode = towerCallbacks.layerTestParameter(curTest);
//            if (layerTestGcode != null) {
//                gCodes.add(layerTestGcode);
//            }
//            if (!(layerTestGcode instanceof M104_SetExtruderTemperature) && !(layerTestGcode instanceof M109_SetExtruderTemperatureAndWait)) {
//                gCodes.add(new M109_SetExtruderTemperatureAndWait(printerConfiguration.getExtruderTemperature()));
//            }
//
//            gCodes.add(new G0_G1_Move(false).setF(feedRateMove).setExtrusionLength(printerConfiguration.getRetractionDistance()));
//
//
//            int blankLevels = 2;
//
//            for (int testLayer = 0; testLayer < (layersPerTest + blankLevels); testLayer++) {
//                gCodes.add(new CodeComment("Layer " + layer));
//
//                //Begin
//                if (testLayer < blankLevels) {
//                    //Layer Marker Bottom Left
//                    gCodes.add(new G0_G1_Move(false).setX(-2.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(2)));
//                    gCodes.add(new G0_G1_Move(false).setY(-2.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(2)));
//                    gCodes.add(new G0_G1_Move(false).setX(2.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(2)));
//                    gCodes.add(new G0_G1_Move(false).setY(2.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(2)));
//
//                    gCodes.add(new G0_G1_Move(false).setX(50.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(50)));
//
//                    //Layer Marker Bottom Right
//                    gCodes.add(new G0_G1_Move(false).setX(1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
//                    gCodes.add(new G0_G1_Move(false).setY(-1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
//                    gCodes.add(new G0_G1_Move(false).setX(-1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
//                    gCodes.add(new G0_G1_Move(false).setY(1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
//
//                    gCodes.add(new G0_G1_Move(false).setY(50.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(50)));
//
//                    //Layer Marker Top Right
//                    gCodes.add(new G0_G1_Move(false).setX(1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
//                    gCodes.add(new G0_G1_Move(false).setY(1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
//                    gCodes.add(new G0_G1_Move(false).setX(-1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
//                    gCodes.add(new G0_G1_Move(false).setY(-1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
//
//                    gCodes.add(new G0_G1_Move(false).setX(-50.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(50)));
//
//                    //Layer Marker Top Left
//                    gCodes.add(new G0_G1_Move(false).setX(-1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
//                    gCodes.add(new G0_G1_Move(false).setY(1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
//                    gCodes.add(new G0_G1_Move(false).setX(1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
//                    gCodes.add(new G0_G1_Move(false).setY(-1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
//
//                    gCodes.add(new G0_G1_Move(false).setY(-50.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(50)));
//                } else {
//
//                    // Bottom
//                    for (int branch = 0; branch < 16; branch++) {
//
//                        if (branch == 0) {
//                            gCodes.add(new G0_G1_Move(false).setX(10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10)));
//                        } else if (branch == 4) {
//                            gCodes.add(new G0_G1_Move(false).setY(10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10)));
//                        } else if (branch == 8) {
//                            gCodes.add(new G0_G1_Move(false).setX(-10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10)));
//                        } else if (branch == 12) {
//                            gCodes.add(new G0_G1_Move(false).setY(-10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10)));
//                        }
//
//                        G0_G1_Move retractionMove = new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(printerConfiguration.getRetractionDistance() * -1);
//                        GCode branchStartGcode = towerCallbacks.onBranchStart(curTest, branch, retractionMove);
//                        if (branchStartGcode != null) {
//                            gCodes.add(branchStartGcode);
//                        }
//                        gCodes.add(retractionMove);
//
//                        GCodeCommon stepCode;
//                        if (branch < 4) {
//                            gCodes.add(new G0_G1_Move(true).setY(-1 * calibrationConfiguration.getBranchLength()).setF(feedRateMove));
//                            gCodes.add(new G0_G1_Move(true).setY(calibrationConfiguration.getBranchLength()).setF(feedRateMove));
//                            stepCode = new G0_G1_Move(false).setX(10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10));
//                        } else if (branch < 8) {
//                            gCodes.add(new G0_G1_Move(true).setX(calibrationConfiguration.getBranchLength()).setF(feedRateMove));
//                            gCodes.add(new G0_G1_Move(true).setX(-1 * calibrationConfiguration.getBranchLength()).setF(feedRateMove));
//                            stepCode = new G0_G1_Move(false).setY(10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10));
//                        } else if (branch < 12) {
//                            gCodes.add(new G0_G1_Move(true).setY(calibrationConfiguration.getBranchLength()).setF(feedRateMove));
//                            gCodes.add(new G0_G1_Move(true).setY(-1 * calibrationConfiguration.getBranchLength()).setF(feedRateMove));
//                            stepCode = new G0_G1_Move(false).setX(-10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10));
//                        } else {
//                            gCodes.add(new G0_G1_Move(true).setX(-1 * calibrationConfiguration.getBranchLength()).setF(feedRateMove));
//                            gCodes.add(new G0_G1_Move(true).setX(calibrationConfiguration.getBranchLength()).setF(feedRateMove));
//                            stepCode = new G0_G1_Move(false).setY(-10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10));
//                        }
//
//
//                        G0_G1_Move deretractionMove = new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(printerConfiguration.getRetractionDistance());
//                        GCode branchEndGcode = towerCallbacks.onBranchEnd(curTest, branch, deretractionMove);
//                        if (branchEndGcode != null) {
//                            gCodes.add(branchEndGcode);
//                        }
//                        gCodes.add(deretractionMove);
//
//                        gCodes.add(stepCode);
//                    }
//                }
//
//                gCodes.add(new G0_G1_Move(false).setZ(printerConfiguration.getLayerHeight()));
//                layer++;
//            }
//        }

        //clean nozzle
        gCodes.add(new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(extruder.getEValue(printerConfiguration.getRetractionDistance() * -1)));
        gCodes.add(new G0_G1_Move(false).setF(feedRatePrint).setX((branchPerSideCount + 1) * wallPieceLengthMm));
        gCodes.add(new G0_G1_Move(false).setF(feedRatePrint).setY((branchPerSideCount + 1) * wallPieceLengthMm));
        gCodes.add(new G0_G1_Move(true).setF(feedRateMove).setY(10.0));

        // Raise 5mm
        gCodes.add(new G0_G1_Move(false).setZ(5.0));

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


//        headerGCodes.add(new CodeComment(""));
//        headerGCodes.add(new CodeComment("Variables by Height"));
//        headerGCodes.add(new CodeComment(""));
//
//
//        headerGCodes.add(new CodeComment("Height         " + towerCallbacks.getLayerParameterName()));
//        headerGCodes.add(new CodeComment(""));
//
//        for (int curTest = numTests - 1; curTest >= 0; curTest--) {
//            headerGCodes.add(new CodeComment("" + layersPerTest + " layers      " + towerCallbacks.layerTestParameterValue(curTest)));
//        }

        headerGCodes.add(new CodeComment(""));
        headerGCodes.add(new CodeComment("printerConfiguration: "));
        for (String s : printerConfiguration.dumpConfig().split("\n")) {
            headerGCodes.add(new CodeComment(s));
        }

        headerGCodes.add(new CodeComment(""));
        headerGCodes.add(new CodeComment("calibrationConfiguration: "));
        for (String s : calibrationConfiguration.dumpConfig().split("\n")) {
            headerGCodes.add(new CodeComment(s));
        }

        headerGCodes.add(new CodeComment(""));

        StringBuilder gCodeText = new StringBuilder();
        headerGCodes.forEach(gCode -> {
            gCodeText.append(gCode.getText());
        });

        gCodes.forEach(gCode -> {
            gCodeText.append(gCode.getText());
        });
        gCodeText.append("\n");


        return gCodeText.toString();
    }

    private List<GCode> goTestMoveAndWall(int direction, double feedRatePrint, double feedRateMove, Extruder extruder) {
        List<GCode> gCodes = new ArrayList<>();

        G0_G1_Move retractionMove = new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(printerConfiguration.getRetractionDistance() * -1);
        gCodes.add(retractionMove);

        GCodeCommon stepCode;
        switch (direction) {
            case 1:
                gCodes.add(new G0_G1_Move(true).setY(-1 * calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                gCodes.add(new G0_G1_Move(true).setY(calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                stepCode = new G0_G1_Move(false).setX(wallPieceLengthMm).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(wallPieceLengthMm));
                break;
            case 2:
                gCodes.add(new G0_G1_Move(true).setX(calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                gCodes.add(new G0_G1_Move(true).setX(-1 * calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                stepCode = new G0_G1_Move(false).setY(wallPieceLengthMm).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(wallPieceLengthMm));
                break;
            case 3:
                gCodes.add(new G0_G1_Move(true).setY(calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                gCodes.add(new G0_G1_Move(true).setY(-1 * calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                stepCode = new G0_G1_Move(false).setX(-1 * wallPieceLengthMm).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(wallPieceLengthMm));
                break;
            case 4:
                gCodes.add(new G0_G1_Move(true).setX(-1 * calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                gCodes.add(new G0_G1_Move(true).setX(calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                stepCode = new G0_G1_Move(false).setY(-1 * wallPieceLengthMm).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(wallPieceLengthMm));
                break;
            default:
                throw new RuntimeException("unknown step");
        }

        G0_G1_Move deretractionMove = new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(printerConfiguration.getRetractionDistance());
        gCodes.add(deretractionMove);

        gCodes.add(stepCode);

        return gCodes;
    }

}
