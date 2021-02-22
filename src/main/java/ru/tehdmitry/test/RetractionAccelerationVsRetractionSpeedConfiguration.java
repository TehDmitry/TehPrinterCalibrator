package ru.tehdmitry.test;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetractionAccelerationVsRetractionSpeedConfiguration extends CommonSingleConfiguration {
    double retractionAccelerationStart = 100;
    double retractionAccelerationInc = 200;

    double retractionSpeedStart = 5;
    double retractionSpeedInc = 5;

}
