package ru.tehdmitry.test;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccelerationTestConfiguration extends CommonSingleConfiguration {
    private double cubeSizeMm = 50;
    private double accelerationStart = 400;
    private double accelerationInc = 400;
}
