package ru.tehdmitry.test;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JunctionDeviationTestConfiguration extends CommonSingleConfiguration {
    private double cubeSizeMm = 50;
    private double junctionDeviationStart = 0.05;
    private double junctionDeviationInc = 0.05;
    private double junctionDeviationMinimumPlannerSpeed = 30;
}
