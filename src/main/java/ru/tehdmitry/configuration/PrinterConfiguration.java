package ru.tehdmitry.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Getter
@Setter
public class PrinterConfiguration {
    @JsonIgnore
    private final Logger log = LogManager.getLogger(PrinterConfiguration.class);

    private String firmware = "marlin";

    private double dimensionX = 200;
    private double dimensionY = 200;

    private double layerHeight = 0.2;
    private double nozzleDiameter = 0.4;
    private double nozzleLength = 8.0;
    private double filamentDiameter = 1.75;
    private double extrusionMultiplier = 1;
    private double extrusionWidth = nozzleDiameter * 1.2;
    private double perimetersOverlapPc = 25.0;

    private double travelSpeed = 120;
    private double printSpeed = 80;
    private double moveAcceleration = 2000;

    private double retractionDistance = 2;
    private double retractionSpeed = 25;
    private double extruderAcceleration = 1300;

    private double extruderTemperature = 230;
    private double bedTemperature = 60;
    private double fanSpeed = 100;

    private double LinearAdvanceFactor = 0.0;
    private double maxVolumetricFlowRate = 12.0;

    @JsonIgnore
    public String dumpConfig() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return "";
    }
}
