package ru.tehdmitry.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.gcode.G0_G1_Move;
import ru.tehdmitry.gcode.GCode;
import ru.tehdmitry.gcode.M109_SetExtruderTemperatureAndWait;
import ru.tehdmitry.pattern.RetractionTower;
import ru.tehdmitry.pattern.RetractionTowerCallbacks;

import java.util.List;

public class RetractionDistanceVsTemperatureTest extends CommonSingleTest {
    private final Logger log = LogManager.getLogger(RetractionDistanceVsTemperatureTest.class);
    private final RetractionDistanceVsTemperatureConfiguration testConfiguration;

    public RetractionDistanceVsTemperatureTest(PrinterConfiguration printSettings, CalibrationConfiguration calibrationConfiguration, RetractionDistanceVsTemperatureConfiguration testConfiguration) {
        super(printSettings, calibrationConfiguration);
        this.testConfiguration = testConfiguration;
    }

    public void process() {

        double extruderTemperatureStart = testConfiguration.getExtruderTemperatureStart();
        double extruderTemperatureInc = testConfiguration.getExtruderTemperatureInc();

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
                return new M109_SetExtruderTemperatureAndWait(layerTestParameterValue(currentTest));
            }

            @Override
            public double layerTestParameterValue(int currentTest) {
                return extruderTemperatureStart + extruderTemperatureInc * currentTest;
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
                retractionMove.setExtrusionLength(branchParameterValue(branch) * -1);
                return null;
            }

            @Override
            public GCode onBranchEnd(int currentTest, int branch, G0_G1_Move deretractionMove) {
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
