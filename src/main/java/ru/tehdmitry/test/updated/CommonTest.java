package ru.tehdmitry.test.updated;

import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;

public interface CommonTest {

    void setPrinterConfiguration(PrinterConfiguration printerConfiguration);
    void setCalibrationConfiguration(CalibrationConfiguration calibrationConfiguration);

    void created();

    void process();

    String getDescription();

}
