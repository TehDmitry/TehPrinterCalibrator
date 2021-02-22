package ru.tehdmitry.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import ru.tehdmitry.test.updated.*;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Configuration {

    private PrinterConfiguration printer = new PrinterConfiguration();
    private CalibrationConfiguration calibration = new CalibrationConfiguration();

//    private RetractionAccelerationVsRetractionSpeedConfiguration retractionAccelerationVsRetractionSpeed = new RetractionAccelerationVsRetractionSpeedConfiguration();
//    private RetractionAccelerationVsTemperatureConfiguration retractionAccelerationVsTemperature = new RetractionAccelerationVsTemperatureConfiguration();
//    private RetractionDistanceVsTemperatureConfiguration retractionDistanceVsTemperature = new RetractionDistanceVsTemperatureConfiguration();
//    private RetractionDistanceVsRetractionSpeedConfiguration retractionDistanceVsRetractionSpeed = new RetractionDistanceVsRetractionSpeedConfiguration();
//    private DeretractionSpeedVsRetractionSpeedConfiguration deretractionSpeedVsRetractionSpeed = new DeretractionSpeedVsRetractionSpeedConfiguration();
//
//    private AccelerationTestConfiguration accelerationTest = new AccelerationTestConfiguration();
//    private JunctionDeviationTestConfiguration junctionDeviation = new JunctionDeviationTestConfiguration();




    private CubeTest cubeTest = new CubeTest();
    private RetractOnLongDistanceTest retractOnLongDistanceTest = new RetractOnLongDistanceTest();
    private CubeJunctionDeviationTest cubeJunctionDeviationTest = new CubeJunctionDeviationTest();
    private TemperatureTowerTest temperatureTowerTest = new TemperatureTowerTest();
    private RetractOnLongDistanceTemperatureTest retractOnLongDistanceTemperatureTest = new RetractOnLongDistanceTemperatureTest();
    private FlowRateWeightTest flowRateWeightTest = new FlowRateWeightTest();



}
