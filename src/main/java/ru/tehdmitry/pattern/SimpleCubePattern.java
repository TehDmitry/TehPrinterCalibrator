package ru.tehdmitry.pattern;

import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.gcode.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SimpleCubePattern extends TestPatternImpl {

    private final Logger log = LogManager.getLogger(SimpleCubePattern.class);

    private final PrinterConfiguration printerConfiguration;
    private final CalibrationConfiguration calibrationConfiguration;

    private final double cubeSizeMm;
    private final int numTests;
    private final int layersPerTest;

    @Setter
    private SimpleCubePatternCallbacks cubeTestCallbacks;

    DecimalFormat dfRound = new DecimalFormat("0.##");

    public SimpleCubePattern(PrinterConfiguration printerConfiguration, CalibrationConfiguration calibrationConfiguration, double cubeSizeMm, int layersPerTest, int numTests) {
        super(printerConfiguration, calibrationConfiguration, cubeSizeMm, cubeSizeMm);
        this.printerConfiguration = printerConfiguration;
        this.calibrationConfiguration = calibrationConfiguration;
        this.cubeSizeMm = cubeSizeMm;
        this.numTests = numTests;
        this.layersPerTest = layersPerTest;

        gCodes = new ArrayList<>();
        headerGCodes = new ArrayList<>();
    }

    @Override
    public void process() {
        gCodes.add(new M106_FanOn(printerConfiguration.getFanSpeed() * 255 / 100));

        TextGenerator textGenerator = new TextGenerator(extruder, printerConfiguration);
        gCodes.add(new CodeComment("description"));
        extruder.setMultiplierExtrusion(printerConfiguration.getExtrusionMultiplier());

        gCodes.add(new G0_G1_Move(true).setZ(printerConfiguration.getLayerHeight() * 3));


        gCodes.addAll(textGenerator.fromText(startPositionX + 3, startPositionY + 45, 0.7, String.format("%.20s", cubeTestCallbacks.getLayerParameterName())));
        gCodes.addAll(textGenerator.fromText(startPositionX + 3, startPositionY + 36, 0.9, String.format("%.20s", dfRound.format(cubeTestCallbacks.layerTestParameterValue(numTests - 1)))));
        gCodes.addAll(textGenerator.fromText(startPositionX + 3, startPositionY + 29, 0.9, String.format("%.20s", dfRound.format(cubeTestCallbacks.layerTestParameterValue(0)))));

        gCodes.addAll(textGenerator.fromText(startPositionX + 25, startPositionY + 25, 0.9, "TPC 1.0"));
        gCodes.addAll(textGenerator.fromText(startPositionX + 25, startPositionY + 20, 0.9, String.format("run %.5s", calibrationConfiguration.getRunCount())));

        // Bring back to Calibration Starting Position
        gCodes.add(new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(extruder.getNextEValue(printerConfiguration.getRetractionDistance() * -1)));
        gCodes.add(new G0_G1_Move(true).setX(startPositionX).setY(startPositionY));
        gCodes.add(new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(extruder.getNextEValue(printerConfiguration.getRetractionDistance())));

        // Relative Movements
        gCodes.add(new M83_SetExtruderToRelativeMode());
        gCodes.add(new G91_SetToRelativePositioning());

        //extruder.resetE();
        List<GCode> initialize = cubeTestCallbacks.getInitializationGCode();
        if (initialize != null) {
            gCodes.addAll(initialize);
        }


        int layer = 3;

        for (int curTest = 0; curTest < numTests; curTest++) {
            gCodes.add(new CodeComment("Test " + curTest));

            gCodes.add(new G0_G1_Move(false).setF(feedRateMove).setExtrusionLength(printerConfiguration.getRetractionDistance() * -1));

            GCode layerTestGcode = cubeTestCallbacks.layerTestParameter(curTest);
            if (layerTestGcode != null) {
                gCodes.add(layerTestGcode);
            }
            if (!(layerTestGcode instanceof M104_SetExtruderTemperature) && !(layerTestGcode instanceof M109_SetExtruderTemperatureAndWait)) {
                gCodes.add(new M109_SetExtruderTemperatureAndWait(printerConfiguration.getExtruderTemperature()));
            }

            gCodes.add(new G0_G1_Move(false).setF(feedRateMove).setExtrusionLength(printerConfiguration.getRetractionDistance()));


            for (int testLayer = 0; testLayer < layersPerTest; testLayer++) {
                gCodes.add(new CodeComment("Test " + curTest + " Layer " + layer));


                if (testLayer == 0) {
                    extruder.setMultiplierExtrusion(printerConfiguration.getExtrusionMultiplier() * 1.5);

                    double shift = 0.05;

                    gCodes.add(new G0_G1_Move(false).setY(-1 * shift).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(shift)));

                    gCodes.add(new G0_G1_Move(false).setX(cubeSizeMm + shift).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(cubeSizeMm)));
                    gCodes.add(new G0_G1_Move(false).setY(cubeSizeMm + shift * 2).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(cubeSizeMm)));
                    gCodes.add(new G0_G1_Move(false).setX(-1 * cubeSizeMm - shift * 2).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(cubeSizeMm)));
                    gCodes.add(new G0_G1_Move(false).setY(-1 * cubeSizeMm - shift).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(cubeSizeMm)));

                    gCodes.add(new G0_G1_Move(false).setX(shift).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(shift)));
                }
                else {
                    extruder.setMultiplierExtrusion(printerConfiguration.getExtrusionMultiplier());
                    gCodes.add(new G0_G1_Move(false).setX(cubeSizeMm).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(cubeSizeMm)));
                    gCodes.add(new G0_G1_Move(false).setY(cubeSizeMm).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(cubeSizeMm)));
                    gCodes.add(new G0_G1_Move(false).setX(-1 * cubeSizeMm).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(cubeSizeMm)));
                    gCodes.add(new G0_G1_Move(false).setY(-1 * cubeSizeMm).setF(feedRatePrint).setExtrusionLength(extruder.getEValue(cubeSizeMm)));
                }




                gCodes.add(new G0_G1_Move(false).setZ(printerConfiguration.getLayerHeight()));
                layer++;
            }
        }

        headerGCodes.add(new CodeComment(""));
        headerGCodes.add(new CodeComment("Variables by Height"));
        headerGCodes.add(new CodeComment(""));

        headerGCodes.add(new CodeComment("Height         " + cubeTestCallbacks.getLayerParameterName()));
        headerGCodes.add(new CodeComment(""));

        for (int curTest = numTests - 1; curTest >= 0; curTest--) {
            headerGCodes.add(new CodeComment("" + layersPerTest + " layers      " + cubeTestCallbacks.layerTestParameterValue(curTest)));
        }


    }

    @Override
    public String getDescription() {
        return cubeTestCallbacks.getLayerParameterName() + " [" + dfRound.format(cubeTestCallbacks.layerTestParameterValue(0)) + "..." + dfRound.format(cubeTestCallbacks.layerTestParameterValue(numTests - 1)) + "]";
    }
}
