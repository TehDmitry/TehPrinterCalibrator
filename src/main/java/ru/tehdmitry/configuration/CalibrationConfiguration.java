package ru.tehdmitry.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Getter
@Setter
public class CalibrationConfiguration {
    @JsonIgnore
    private final Logger log = LogManager.getLogger(CalibrationConfiguration.class);

    private int runCount = 0;
    private double branchLength = 10;
    private double randomizeStartPosition = 2;

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
