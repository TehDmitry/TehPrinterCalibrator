package ru.tehdmitry.test;

import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.gcode.GCode;
import ru.tehdmitry.gcode.M204_smoothie_SetAcceleration;
import ru.tehdmitry.gcode.M205_smoothie_SetJunctionDeviation;
import ru.tehdmitry.pattern.SimpleCubePattern;
import ru.tehdmitry.pattern.SimpleCubePatternCallbacks;

import java.util.ArrayList;
import java.util.List;

public class JunctionDeviationTest extends CommonSingleTest {

    private final JunctionDeviationTestConfiguration testConfiguration;

    public JunctionDeviationTest(PrinterConfiguration printerConfiguration, CalibrationConfiguration calibrationConfiguration, JunctionDeviationTestConfiguration testConfiguration) {
        super(printerConfiguration, calibrationConfiguration);
        this.testConfiguration = testConfiguration;
    }

    @Override
    public void process() {


        SimpleCubePattern cubeTest = new SimpleCubePattern(printerConfiguration, calibrationConfiguration, testConfiguration.getCubeSizeMm(), testConfiguration.getLayersPerTest(), testConfiguration.getNumTests());
        cubeTest.setCubeTestCallbacks(new SimpleCubePatternCallbacks() {

            @Override
            public List<GCode> getInitializationGCode() {
                List<GCode> initialization = new ArrayList<>();
                initialization.add(new M204_smoothie_SetAcceleration().setX(printerConfiguration.getMoveAcceleration()).setY(printerConfiguration.getMoveAcceleration()));

                return initialization;
            }

            @Override
            public String getLayerParameterName() {
                return "Junction Deviation";
            }

            @Override
            public double layerTestParameterValue(int currentTest) {
                return testConfiguration.getJunctionDeviationStart() + testConfiguration.getJunctionDeviationInc() * currentTest;
            }

            @Override
            public GCode layerTestParameter(int currentTest) {
                return new M205_smoothie_SetJunctionDeviation().setS(testConfiguration.getJunctionDeviationMinimumPlannerSpeed()).setX(layerTestParameterValue(currentTest));
            }
        });

        writeFile(cubeTest.getTest(),  cubeTest.getDescription());

    }
}
