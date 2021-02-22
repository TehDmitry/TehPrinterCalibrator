package ru.tehdmitry.test;

import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.gcode.GCode;
import ru.tehdmitry.gcode.M204_smoothie_SetAcceleration;
import ru.tehdmitry.pattern.SimpleCubePattern;
import ru.tehdmitry.pattern.SimpleCubePatternCallbacks;

import java.util.ArrayList;
import java.util.List;

public class AccelerationTest extends CommonSingleTest {

    private final AccelerationTestConfiguration testConfiguration;

    public AccelerationTest(PrinterConfiguration printerConfiguration, CalibrationConfiguration calibrationConfiguration, AccelerationTestConfiguration testConfiguration) {
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
                return initialization;
            }

            @Override
            public String getLayerParameterName() {
                return "Acceleration XY";
            }

            @Override
            public double layerTestParameterValue(int currentTest) {
                return testConfiguration.getAccelerationStart() + testConfiguration.getAccelerationInc() * currentTest;
            }

            @Override
            public GCode layerTestParameter(int currentTest) {
                return new M204_smoothie_SetAcceleration().setX(layerTestParameterValue(currentTest)).setY(layerTestParameterValue(currentTest));
            }
        });

        writeFile(cubeTest.getTest(),  cubeTest.getDescription());

    }
}
