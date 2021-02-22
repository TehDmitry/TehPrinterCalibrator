package ru.tehdmitry.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.gcode.G0_G1_Move;
import ru.tehdmitry.gcode.GCode;
import ru.tehdmitry.gcode.M203_SetMaximumFeedrate;
import ru.tehdmitry.pattern.RetractionTower;
import ru.tehdmitry.pattern.RetractionTowerCallbacks;

import java.util.ArrayList;
import java.util.List;

public class RetractionDistanceVsRetractionSpeedTest extends CommonSingleTest {
    private final Logger log = LogManager.getLogger(RetractionDistanceVsRetractionSpeedTest.class);

    private final RetractionDistanceVsRetractionSpeedConfiguration testConfiguration;


    public RetractionDistanceVsRetractionSpeedTest(PrinterConfiguration printerConfiguration, CalibrationConfiguration calibrationConfiguration, RetractionDistanceVsRetractionSpeedConfiguration testConfiguration) {
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
                return "Retraction Distance";
            }

            @Override
            public double branchParameterValue(int branch) {
                double retractionDistanceInc =  testConfiguration.getRetractionDistanceInc();
                if(retractionDistanceInc < 0) {
                    retractionDistanceInc = Math.floor(((printerConfiguration.getNozzleLength() - testConfiguration.getRetractionDistanceStart()) / 16) * 10.0) / 10.0;
                }

                return testConfiguration.getRetractionDistanceStart() + retractionDistanceInc * branch;
            }

            @Override
            public GCode onBranchStart(int currentTest, int branch, G0_G1_Move retractionMove) {
                retractionMove.setF(layerTestParameterValue(currentTest) * 60);
                retractionMove.setExtrusionLength(branchParameterValue(branch) * -1);
                return null;
            }

            @Override
            public GCode onBranchEnd(int currentTest, int branch, G0_G1_Move deretractionMove) {
                deretractionMove.setF(layerTestParameterValue(currentTest) * 60);
                deretractionMove.setExtrusionLength(branchParameterValue(branch));
                return null;
            }
        });

        if(retractionTower.getTowerCallbacks().branchParameterValue(16) > printerConfiguration.getNozzleLength()) {
            log.warn("{} {} exceeds nozzle length! {}", retractionTower.getTowerCallbacks().getBranchParameterName() , retractionTower.getTowerCallbacks().branchParameterValue(16) , printerConfiguration.getNozzleLength());
        }

        writeFile(retractionTower.build(), retractionTower.getDescription());
    }
}
