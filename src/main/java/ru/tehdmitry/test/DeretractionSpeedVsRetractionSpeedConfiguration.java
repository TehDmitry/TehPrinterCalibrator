package ru.tehdmitry.test;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeretractionSpeedVsRetractionSpeedConfiguration extends CommonSingleConfiguration {
    double deretractionSpeedStart = 10;
    double deretractionSpeedInc = 5;

    double retractionSpeedStart = 10;
    double retractionSpeedInc = 5;
}
