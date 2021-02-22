package ru.tehdmitry.test;

import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.gcode.*;
import ru.tehdmitry.pattern.RetractionTower;
import ru.tehdmitry.pattern.RetractionTowerCallbacks;

import java.util.ArrayList;
import java.util.List;

public class RetractionAccelerationVsRetractionSpeedTest extends CommonSingleTest {

    private final RetractionAccelerationVsRetractionSpeedConfiguration testConfiguration;


    public RetractionAccelerationVsRetractionSpeedTest(PrinterConfiguration printerConfiguration, CalibrationConfiguration calibrationConfiguration, RetractionAccelerationVsRetractionSpeedConfiguration testConfiguration) {
        super(printerConfiguration, calibrationConfiguration);
        this.testConfiguration = testConfiguration;

    }

    public void process() {

        RetractionTower retractionTower = new RetractionTower(printerConfiguration, calibrationConfiguration, testConfiguration.getLayersPerTest(), testConfiguration.getNumTests());
        retractionTower.setTowerCallbacks(new RetractionTowerCallbacks() {

            @Override
            public List<GCode> getInitializationGCode() {
                List<GCode> initialization = new ArrayList<>();
                //initialization.add(new M204_smoothie_SetExtruderAcceleration(20000.0));
                initialization.add(new M203_SetMaximumFeedrate().setE(branchParameterValue(16)));
                return initialization;
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
            public String getLayerParameterName() {
                return "Retraction Speed";
            }

            @Override
            public double layerTestParameterValue(int currentTest) {
                return testConfiguration.getRetractionSpeedStart() + testConfiguration.getRetractionSpeedInc() * currentTest;
            }

            @Override
            public GCode layerTestParameter(int currentTest) {
                return null;
            }

            @Override
            public GCode onBranchStart(int currentTest, int branch, G0_G1_Move retractionMove) {
                //retractionMove.setF(branchParameterValue(branch) * 60);

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
                deretractionMove.setF(layerTestParameterValue(currentTest) * 60);

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
