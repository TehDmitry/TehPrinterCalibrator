package ru.tehdmitry.pattern;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.gcode.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class RetractionTower implements TestPattern {
    private final Logger log = LogManager.getLogger(RetractionTower.class);

    private final PrinterConfiguration printerConfiguration;
    private final CalibrationConfiguration calibrationConfiguration;

    private final double layersPerTest;
    private final int numTests;

    DecimalFormat dfRound = new DecimalFormat("0.##");

    private double raftSize = 60;

    @Setter
    @Getter
    private RetractionTowerCallbacks towerCallbacks;

    public RetractionTower(PrinterConfiguration printerConfiguration, CalibrationConfiguration calibrationConfiguration, double layersPerTest, int numTests) {
        this.printerConfiguration = printerConfiguration;
        this.calibrationConfiguration = calibrationConfiguration;
        this.layersPerTest = layersPerTest;
        this.numTests = numTests;
    }

    @Override
    public String getDescription() {
        return towerCallbacks.getBranchParameterName() + " [" + dfRound.format(towerCallbacks.branchParameterValue(0)) + "..." + dfRound.format(towerCallbacks.branchParameterValue(15)) + "] vs. " +
                towerCallbacks.getLayerParameterName() + " [" + dfRound.format(towerCallbacks.layerTestParameterValue(0)) + "..." + dfRound.format(towerCallbacks.layerTestParameterValue(numTests - 1)) + "]";
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

        headerGCodes.add(new CodeComment("RetractionTower Test"));
        headerGCodes.add(new CodeComment(getDescription()));

        headerGCodes.add(new CodeComment(""));
        headerGCodes.add(new CodeComment(towerCallbacks.getBranchParameterName() + " from the top looking down"));
        headerGCodes.add(new CodeComment(""));
        headerGCodes.add(new CodeComment("       " + df.format(towerCallbacks.branchParameterValue(11)) + "    " + df.format(towerCallbacks.branchParameterValue(10)) + "    " + df.format(towerCallbacks.branchParameterValue(9)) + "    " + df.format(towerCallbacks.branchParameterValue(8))));
        headerGCodes.add(new CodeComment("		|		|		|		|"));
        headerGCodes.add(new CodeComment(""));
        headerGCodes.add(new CodeComment("" + df.format(towerCallbacks.branchParameterValue(12)) + "-                               -" + df.format(towerCallbacks.branchParameterValue(7))));
        headerGCodes.add(new CodeComment(""));
        headerGCodes.add(new CodeComment(""));
        headerGCodes.add(new CodeComment("" + df.format(towerCallbacks.branchParameterValue(13)) + "-                               -" + df.format(towerCallbacks.branchParameterValue(6))));
        headerGCodes.add(new CodeComment(""));
        headerGCodes.add(new CodeComment(""));
        headerGCodes.add(new CodeComment("" + df.format(towerCallbacks.branchParameterValue(14)) + "-                               -" + df.format(towerCallbacks.branchParameterValue(5))));
        headerGCodes.add(new CodeComment(""));
        headerGCodes.add(new CodeComment(""));
        headerGCodes.add(new CodeComment("" + df.format(towerCallbacks.branchParameterValue(15)) + "-                               -" + df.format(towerCallbacks.branchParameterValue(4))));
        headerGCodes.add(new CodeComment(""));
        headerGCodes.add(new CodeComment("		|		|		|		|"));
        headerGCodes.add(new CodeComment("       " + df.format(towerCallbacks.branchParameterValue(0)) + "    " + df.format(towerCallbacks.branchParameterValue(1)) + "    " + df.format(towerCallbacks.branchParameterValue(2)) + "    " + df.format(towerCallbacks.branchParameterValue(3)) + ""));
        headerGCodes.add(new CodeComment(""));

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

        double xpos = printerConfiguration.getDimensionX() / 2 - 30;
        double ypos = printerConfiguration.getDimensionY() / 2 - 30;
        double zpos = printerConfiguration.getLayerHeight();

        xpos += Math.random() * calibrationConfiguration.getRandomizeStartPosition();
        ypos += Math.random() * calibrationConfiguration.getRandomizeStartPosition();

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

        gCodes.add(new CodeComment("Layer 1"));

        // Vertical
        for (int i = 0; i < 30; i++) {
            double curX = xpos + (i * 2);
            gCodes.add(new G0_G1_Move(false).setX(curX).setY(ypos + raftSize).setF(feedRatePrint * 0.5).setExtrusionLength(extruder.getNextEValue(raftSize)));
            gCodes.add(new G0_G1_Move(true).setX(curX + 1).setY(ypos + raftSize).setF(feedRateMove));
            gCodes.add(new G0_G1_Move(false).setX(curX + 1).setY(ypos).setF(feedRatePrint * 0.5).setExtrusionLength(extruder.getNextEValue(raftSize)));
            gCodes.add(new G0_G1_Move(true).setX(curX + 2).setY(ypos).setF(feedRateMove));
        }

        // Bring back to raft origin

        gCodes.add(new G0_G1_Move(true).setZ(printerConfiguration.getLayerHeight() * 3));
        gCodes.add(new G0_G1_Move(true).setX(xpos).setY(ypos).setZ(printerConfiguration.getLayerHeight() * 2).setF(feedRateMove));

        gCodes.add(new CodeComment("Layer 2"));

        // Horizontal
        for (int i = 0; i < 30; i++) {
            double curY = ypos + (i * 2);
            gCodes.add(new G0_G1_Move(false).setX(xpos + raftSize).setY(curY).setF(feedRatePrint * 0.65).setExtrusionLength(extruder.getNextEValue(raftSize)));
            gCodes.add(new G0_G1_Move(true).setX(xpos + raftSize).setY(curY + 1).setF(feedRateMove));
            gCodes.add(new G0_G1_Move(false).setX(xpos).setY(curY + 1).setF(feedRatePrint * 0.65).setExtrusionLength(extruder.getNextEValue(raftSize)));
            gCodes.add(new G0_G1_Move(true).setX(xpos).setY(curY + 2).setF(feedRateMove));
        }

        //gCodes.add(new G0_G1_Move(true).setX(xpos + 10).setY(ypos + 10).setZ(printerConfiguration.getLayerHeight() * 3));
        gCodes.add(new M106_FanOn(printerConfiguration.getFanSpeed() * 255 / 100));


        TextGenerator textGenerator = new TextGenerator(extruder, printerConfiguration);
        gCodes.add(new CodeComment("description"));
        extruder.setMultiplierExtrusion(printerConfiguration.getExtrusionMultiplier());

        gCodes.add(new G0_G1_Move(true).setZ(printerConfiguration.getLayerHeight() * 3));

        gCodes.addAll(textGenerator.fromText(xpos + 8, ypos + 48, 0.7, String.format("%.20s", towerCallbacks.getLayerParameterName())));
        gCodes.addAll(textGenerator.fromText(xpos + 8, ypos + 41, 0.9, String.format("%.20s", dfRound.format(towerCallbacks.layerTestParameterValue(numTests - 1)))));
        gCodes.addAll(textGenerator.fromText(xpos + 8, ypos + 34, 0.9, String.format("%.20s", dfRound.format(towerCallbacks.layerTestParameterValue(0)))));

        gCodes.addAll(textGenerator.fromText(xpos + 30, ypos + 30, 0.9, "TPC 1.0"));
        gCodes.addAll(textGenerator.fromText(xpos + 30, ypos + 25, 0.9, String.format("run %.5s", calibrationConfiguration.getRunCount())));

        gCodes.addAll(textGenerator.fromText(xpos + 8, ypos + 17, 0.7, String.format("%.20s", towerCallbacks.getBranchParameterName())));
        gCodes.addAll(textGenerator.fromText(xpos + 8, ypos + 10, 0.9,
                String.format("%.20s", dfRound.format(towerCallbacks.branchParameterValue(0)) + " to " + dfRound.format(towerCallbacks.branchParameterValue(15)) + " inc " + dfRound.format(towerCallbacks.branchParameterValue(1) - towerCallbacks.branchParameterValue(0)))
        ));

        // Bring back to Calibration Starting Position
        gCodes.add(new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(extruder.getNextEValue(printerConfiguration.getRetractionDistance() * -1)));
        gCodes.add(new G0_G1_Move(true).setX(xpos + 5).setY(ypos + 5));
        gCodes.add(new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(extruder.getNextEValue(printerConfiguration.getRetractionDistance())));

        // Relative Movements
        gCodes.add(new M83_SetExtruderToRelativeMode());
        gCodes.add(new G91_SetToRelativePositioning());


        //extruder.resetE();
        List<GCode> initialize = towerCallbacks.getInitializationGCode();
        if (initialize != null) {
            gCodes.addAll(initialize);
        }


        int layer = 3;

        for (int curTest = 0; curTest < numTests; curTest++) {

            gCodes.add(new CodeComment("Test " + curTest));

            //gCodes.add(new M106_FanOn((double) Math.round((fs + fsi * curTest) * 255 / 100)));

            gCodes.add(new G0_G1_Move(false).setF(feedRateMove).setExtrusionLength(printerConfiguration.getRetractionDistance() * -1));

            GCode layerTestGcode = towerCallbacks.layerTestParameter(curTest);
            if (layerTestGcode != null) {
                gCodes.add(layerTestGcode);
            }
            if (!(layerTestGcode instanceof M104_SetExtruderTemperature) && !(layerTestGcode instanceof M109_SetExtruderTemperatureAndWait)) {
                gCodes.add(new M109_SetExtruderTemperatureAndWait(printerConfiguration.getExtruderTemperature()));
            }

            gCodes.add(new G0_G1_Move(false).setF(feedRateMove).setExtrusionLength(printerConfiguration.getRetractionDistance()));


            int blankLevels = 2;

            for (int testLayer = 0; testLayer < (layersPerTest + blankLevels); testLayer++) {
                gCodes.add(new CodeComment("Test " + curTest + " Layer " + layer));

                //Begin
                if (testLayer < blankLevels) {
                    gCodes.add(new CodeComment("blank level"));

                    //Layer Marker Bottom Left
                    gCodes.add(new G0_G1_Move(false).setX(-2.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(2)));
                    gCodes.add(new G0_G1_Move(false).setY(-2.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(2)));
                    gCodes.add(new G0_G1_Move(false).setX(2.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(2)));
                    gCodes.add(new G0_G1_Move(false).setY(2.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(2)));

                    gCodes.add(new G0_G1_Move(false).setX(50.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(50)));

                    //Layer Marker Bottom Right
                    gCodes.add(new G0_G1_Move(false).setX(1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
                    gCodes.add(new G0_G1_Move(false).setY(-1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
                    gCodes.add(new G0_G1_Move(false).setX(-1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
                    gCodes.add(new G0_G1_Move(false).setY(1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));

                    gCodes.add(new G0_G1_Move(false).setY(50.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(50)));

                    //Layer Marker Top Right
                    gCodes.add(new G0_G1_Move(false).setX(1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
                    gCodes.add(new G0_G1_Move(false).setY(1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
                    gCodes.add(new G0_G1_Move(false).setX(-1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
                    gCodes.add(new G0_G1_Move(false).setY(-1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));

                    gCodes.add(new G0_G1_Move(false).setX(-50.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(50)));

                    //Layer Marker Top Left
                    gCodes.add(new G0_G1_Move(false).setX(-1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
                    gCodes.add(new G0_G1_Move(false).setY(1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
                    gCodes.add(new G0_G1_Move(false).setX(1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));
                    gCodes.add(new G0_G1_Move(false).setY(-1.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(1)));

                    gCodes.add(new G0_G1_Move(false).setY(-50.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(50)));
                } else {

                    // Bottom
                    for (int branch = 0; branch < 16; branch++) {
                        gCodes.add(new CodeComment("Test " + curTest + " Layer " + layer + " branch " + branch));

                        if (branch == 0) {
                            gCodes.add(new G0_G1_Move(false).setX(10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10)));
                        } else if (branch == 4) {
                            gCodes.add(new G0_G1_Move(false).setY(10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10)));
                        } else if (branch == 8) {
                            gCodes.add(new G0_G1_Move(false).setX(-10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10)));
                        } else if (branch == 12) {
                            gCodes.add(new G0_G1_Move(false).setY(-10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10)));
                        }

                        G0_G1_Move retractionMove = new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(printerConfiguration.getRetractionDistance() * -1);
                        GCode branchStartGcode = towerCallbacks.onBranchStart(curTest, branch, retractionMove);
                        if (branchStartGcode != null) {
                            gCodes.add(branchStartGcode);
                        }
                        gCodes.add(retractionMove);

                        GCodeCommon stepCode;
                        if (branch < 4) {
                            gCodes.add(new G0_G1_Move(true).setY(-1 * calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                            gCodes.add(new G0_G1_Move(true).setY(calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                            stepCode = new G0_G1_Move(false).setX(10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10));
                        } else if (branch < 8) {
                            gCodes.add(new G0_G1_Move(true).setX(calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                            gCodes.add(new G0_G1_Move(true).setX(-1 * calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                            stepCode = new G0_G1_Move(false).setY(10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10));
                        } else if (branch < 12) {
                            gCodes.add(new G0_G1_Move(true).setY(calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                            gCodes.add(new G0_G1_Move(true).setY(-1 * calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                            stepCode = new G0_G1_Move(false).setX(-10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10));
                        } else {
                            gCodes.add(new G0_G1_Move(true).setX(-1 * calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                            gCodes.add(new G0_G1_Move(true).setX(calibrationConfiguration.getBranchLength()).setF(feedRateMove));
                            stepCode = new G0_G1_Move(false).setY(-10.0).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(10));
                        }


                        G0_G1_Move deretractionMove = new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(printerConfiguration.getRetractionDistance());
                        GCode branchEndGcode = towerCallbacks.onBranchEnd(curTest, branch, deretractionMove);
                        if (branchEndGcode != null) {
                            gCodes.add(branchEndGcode);
                        }
                        gCodes.add(deretractionMove);

                        gCodes.add(stepCode);
                    }
                }

                gCodes.add(new G0_G1_Move(false).setZ(printerConfiguration.getLayerHeight()));
                layer++;
            }
        }

        //clean nozzle
        gCodes.add(new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(extruder.getEValue(printerConfiguration.getRetractionDistance() * -1)));
        gCodes.add(new G0_G1_Move(false).setF(feedRatePrint).setX(50.0));
        gCodes.add(new G0_G1_Move(false).setF(feedRatePrint).setY(50.0));
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


        headerGCodes.add(new CodeComment(""));
        headerGCodes.add(new CodeComment("Variables by Height"));
        headerGCodes.add(new CodeComment(""));


        headerGCodes.add(new CodeComment("Height         " + towerCallbacks.getLayerParameterName()));
        headerGCodes.add(new CodeComment(""));

        for (int curTest = numTests - 1; curTest >= 0; curTest--) {
            headerGCodes.add(new CodeComment("" + layersPerTest + " layers      " + towerCallbacks.layerTestParameterValue(curTest)));
        }

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

}
