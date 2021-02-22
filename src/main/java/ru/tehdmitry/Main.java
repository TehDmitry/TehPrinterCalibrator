package ru.tehdmitry;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.tehdmitry.configuration.Configuration;
import ru.tehdmitry.test.updated.CommonTest;

import java.io.File;
import java.util.Arrays;
import java.util.List;


public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Configuration configuration = null;

        boolean dryRun = false;
        File f = new File("configuration.json");
        if(!f.exists() || f.isDirectory()) {
            configuration = new Configuration();
            log.info("Default configuration created under configuration.json file. Please fill it with known printer settings and run the calibration again.");

            dryRun = true;
        }
        else {
            try {
                ObjectMapper mapper = new ObjectMapper();
                configuration = mapper.readValue(new File("configuration.json"), Configuration.class);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }

        if (configuration == null) {
            configuration = new Configuration();
        }

        configuration.getCalibration().setRunCount(configuration.getCalibration().getRunCount() + 1);

        try {
            //update file for new fields
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File("configuration.json"), configuration);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }


        List<CommonTest> testList = Arrays.asList(
//                configuration.getCubeTest(),
                configuration.getCubeJunctionDeviationTest(),
                configuration.getRetractOnLongDistanceTest(),
                configuration.getTemperatureTowerTest(),
                configuration.getRetractOnLongDistanceTemperatureTest(),
                configuration.getFlowRateWeightTest()
        );

        for (CommonTest commonTest : testList) {
            commonTest.setPrinterConfiguration(configuration.getPrinter());
            commonTest.setCalibrationConfiguration(configuration.getCalibration());
            commonTest.created();
        }


        for (CommonTest commonTest : testList) {
            commonTest.process();

        }

        if(!dryRun) {




//
//            new RetractionDistanceVsTemperatureTest(configuration.getPrinter(), configuration.getCalibration(), configuration.getRetractionDistanceVsTemperature()).process();
//            new RetractionAccelerationVsTemperatureTest(configuration.getPrinter(), configuration.getCalibration(), configuration.getRetractionAccelerationVsTemperature()).process();
//            new RetractionAccelerationVsRetractionSpeedTest(configuration.getPrinter(), configuration.getCalibration(), configuration.getRetractionAccelerationVsRetractionSpeed()).process();
//            new RetractionDistanceVsRetractionSpeedTest(configuration.getPrinter(), configuration.getCalibration(), configuration.getRetractionDistanceVsRetractionSpeed()).process();
//            new DeretractionSpeedVsRetractionSpeedTest(configuration.getPrinter(), configuration.getCalibration(), configuration.getDeretractionSpeedVsRetractionSpeed()).process();
//            new CubeTestOld(configuration.getPrinter(), configuration.getCalibration()).process();
//
//            new AccelerationTest(configuration.getPrinter(), configuration.getCalibration(), configuration.getAccelerationTest()).process();
//            new JunctionDeviationTest(configuration.getPrinter(), configuration.getCalibration(), configuration.getJunctionDeviation()).process();

        }
    }
}
