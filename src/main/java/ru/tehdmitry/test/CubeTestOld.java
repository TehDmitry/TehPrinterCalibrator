package ru.tehdmitry.test;

import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.pattern.RetractionTestCube;

public class CubeTestOld extends CommonSingleTest {
    public CubeTestOld(PrinterConfiguration printerConfiguration, CalibrationConfiguration calibrationConfiguration) {
        super(printerConfiguration, calibrationConfiguration);
    }

    @Override
    public void process() {
        RetractionTestCube retractionTestCube = new RetractionTestCube(printerConfiguration, calibrationConfiguration, 1, 40);
        writeFile(retractionTestCube.build(), retractionTestCube.getDescription());
    }
}
