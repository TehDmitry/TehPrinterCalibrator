package ru.tehdmitry.pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.gcode.*;

import java.util.ArrayList;
import java.util.List;

public abstract class TestPatternImpl implements TestPattern {

    private final Logger log = LogManager.getLogger(TestPatternImpl.class);

    protected final PrinterConfiguration printerConfiguration;
    protected final CalibrationConfiguration calibrationConfiguration;

    protected final double patternSizeX;
    protected final double patternSizeY;

    protected final double feedRatePrint;
    protected final double feedRateMove;
    protected final Extruder extruder;

    protected List<GCode> gCodes = new ArrayList<>();
    protected List<GCode> headerGCodes = new ArrayList<>();

    protected double raftOffsetMm = 5.0;

    protected double startPositionX;
    protected double startPositionY;
    protected double startPositionZ;

    protected TestPatternImpl(PrinterConfiguration printerConfiguration, CalibrationConfiguration calibrationConfiguration, double patternSizeX, double patternSizeY) {
        this.printerConfiguration = printerConfiguration;
        this.calibrationConfiguration = calibrationConfiguration;
        this.patternSizeX = patternSizeX;
        this.patternSizeY = patternSizeY;

        feedRatePrint = printerConfiguration.getPrintSpeed() * 60; // mm/min
        feedRateMove = printerConfiguration.getTravelSpeed() * 60; // mm/min

        extruder = new Extruder(printerConfiguration.getExtrusionWidth(), printerConfiguration.getLayerHeight(), printerConfiguration.getFilamentDiameter(), printerConfiguration.getExtrusionMultiplier());
    }

    protected void testStart() {
        headerGCodes.add(new CodeComment(getDescription()));


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

        startPositionX = printerConfiguration.getDimensionX() / 2 - patternSizeX / 2;
        startPositionY = printerConfiguration.getDimensionY() / 2 - patternSizeY / 2;
        startPositionZ = printerConfiguration.getLayerHeight();

        // Start Movement
        gCodes.add(new CodeComment("Start Movement"));
        gCodes.add(new CodeComment(""));

        gCodes.add(new G0_G1_Move(false).setZ(2.0));
        gCodes.add(new G0_G1_Move(false).setF(printerConfiguration.getTravelSpeed() * 60).setX(startPositionX).setY(startPositionY).setZ(startPositionZ));

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

        Raft raft = new Raft(extruder);
        gCodes.addAll(raft.generate(patternSizeX + raftOffsetMm * 2, patternSizeY + raftOffsetMm * 2, 1, startPositionX - raftOffsetMm, startPositionY - raftOffsetMm, printerConfiguration.getLayerHeight(), feedRatePrint, feedRateMove));

        extruder.setMultiplierExtrusion(printerConfiguration.getExtrusionMultiplier());

    }


    public String getTest() {
        testStart();
        process();

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
