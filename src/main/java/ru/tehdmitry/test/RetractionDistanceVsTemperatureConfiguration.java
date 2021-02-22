package ru.tehdmitry.test;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetractionDistanceVsTemperatureConfiguration extends CommonSingleConfiguration {
    double retractionDistanceStart = 2.0;
    double retractionDistanceInc = -1; // do not exceed nozzle length; -1 for auto

    double extruderTemperatureStart = 230;
    double extruderTemperatureInc = 2;
}
