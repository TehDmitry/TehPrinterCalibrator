package ru.tehdmitry.test;

import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.gcode.*;
import ru.tehdmitry.pattern.RetractionTower;
import ru.tehdmitry.pattern.RetractionTowerCallbacks;

import java.util.List;

public class RetractionAccelerationVsTemperatureTest extends CommonSingleTest {

    private final RetractionAccelerationVsTemperatureConfiguration testConfiguration;

//    private final double startAcceleration;
//    private final double incAcceleration;

    public RetractionAccelerationVsTemperatureTest(PrinterConfiguration printSettings, CalibrationConfiguration calibrationConfiguration, RetractionAccelerationVsTemperatureConfiguration testConfiguration) {
        super(printSettings, calibrationConfiguration);
        this.testConfiguration = testConfiguration;
    }

    public void process() {

        RetractionTower retractionTower = new RetractionTower(printerConfiguration, calibrationConfiguration, testConfiguration.getLayersPerTest(), testConfiguration.getNumTests());

        retractionTower.setTowerCallbacks(new RetractionTowerCallbacks() {

            @Override
            public String getLayerParameterName() {
                return "Extruder Temp";
            }

            @Override
            public List<GCode> getInitializationGCode() {
                return null;
            }

            @Override
            public GCode layerTestParameter(int currentTest) {
                return new M104_SetExtruderTemperature(layerTestParameterValue(currentTest));
            }

            @Override
            public double layerTestParameterValue(int currentTest) {
                return testConfiguration.getExtruderTemperatureStart() + testConfiguration.getExtruderTemperatureInc() * currentTest;
            }

            @Override
            public String getBranchParameterName() {
                return "Retraction Acceleration";
            }

            @Override
            public double branchParameterValue(int branch) {
                return testConfiguration.getRetractionAccelerationStart() + testConfiguration.getRetractionAccelerationInc() * branch;
            }

            @Override
            public GCode onBranchStart(int currentTest, int branch, G0_G1_Move retractionMove) {
                if(printerConfiguration.getFirmware().equals("smoothie")) {
                    return new M204_smoothie_SetAcceleration().setE(branchParameterValue(branch));
                }
                else if(printerConfiguration.getFirmware().equals("klipper")) {
                    return new M204_klipper_SetAcceleration(branchParameterValue(branch));
                }
                else {
                    throw new RuntimeException("Firmware is not supported");
                }
            }

            @Override
            public GCode onBranchEnd(int currentTest, int branch, G0_G1_Move deretractionMove) {
                if(printerConfiguration.getFirmware().equals("smoothie")) {
                    return new M204_smoothie_SetAcceleration().setE(testConfiguration.getRetractionAccelerationStart());
                }
                else if(printerConfiguration.getFirmware().equals("klipper")) {
                    return new M204_klipper_SetAcceleration(printerConfiguration.getMoveAcceleration());
                }
                else {
                    throw new RuntimeException("Firmware is not supported");
                }
            }
        });

        writeFile(retractionTower.build(), retractionTower.getDescription());
    }
}
