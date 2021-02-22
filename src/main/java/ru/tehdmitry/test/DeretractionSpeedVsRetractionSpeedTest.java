package ru.tehdmitry.test;

import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.gcode.G0_G1_Move;
import ru.tehdmitry.gcode.GCode;
import ru.tehdmitry.gcode.M203_SetMaximumFeedrate;
import ru.tehdmitry.pattern.RetractionTower;
import ru.tehdmitry.pattern.RetractionTowerCallbacks;

import java.util.ArrayList;
import java.util.List;

public class DeretractionSpeedVsRetractionSpeedTest extends CommonSingleTest {

    private final DeretractionSpeedVsRetractionSpeedConfiguration testConfiguration;

    public DeretractionSpeedVsRetractionSpeedTest(PrinterConfiguration printerConfiguration, CalibrationConfiguration calibrationConfiguration, DeretractionSpeedVsRetractionSpeedConfiguration testConfiguration) {
        super(printerConfiguration, calibrationConfiguration);
        this.testConfiguration = testConfiguration;
    }

    public void process() {

        RetractionTower retractionTower = new RetractionTower(printerConfiguration, calibrationConfiguration, testConfiguration.getLayersPerTest(), testConfiguration.getNumTests());
        retractionTower.setTowerCallbacks(new RetractionTowerCallbacks() {

            @Override
            public List<GCode> getInitializationGCode() {
                List<GCode> initialization = new ArrayList<>();
                initialization.add(new M203_SetMaximumFeedrate().setE(layerTestParameterValue(testConfiguration.getNumTests())));
                return initialization;
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
            public String getBranchParameterName() {
                return "Deretraction Speed";
            }

            @Override
            public double branchParameterValue(int branch) {
                return testConfiguration.getDeretractionSpeedStart() + testConfiguration.getDeretractionSpeedInc() * branch;
            }

            @Override
            public GCode onBranchStart(int currentTest, int branch, G0_G1_Move retractionMove) {
                retractionMove.setF(layerTestParameterValue(currentTest) * 60);
                return null;
            }

            @Override
            public GCode onBranchEnd(int currentTest, int branch, G0_G1_Move deretractionMove) {
                deretractionMove.setF(branchParameterValue(branch) * 60);
                return null;
            }
        });

        writeFile(retractionTower.build(), retractionTower.getDescription());
    }
}
