package ru.tehdmitry.test;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetractionAccelerationVsTemperatureConfiguration extends CommonSingleConfiguration {
    double retractionAccelerationStart = 10;
    double retractionAccelerationInc = 10;

    double extruderTemperatureStart = 230;
    double extruderTemperatureInc = 2;
}
