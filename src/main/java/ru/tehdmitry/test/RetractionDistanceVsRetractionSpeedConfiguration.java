package ru.tehdmitry.test;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RetractionDistanceVsRetractionSpeedConfiguration extends CommonSingleConfiguration {
    double retractionDistanceStart = 0.5;
    double retractionDistanceInc = -1; // do not exceed nozzle length; -1 for auto

    double retractionSpeedStart = 10;
    double retractionSpeedInc = 5;
}
