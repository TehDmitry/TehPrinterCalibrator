package ru.tehdmitry.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

// Naming should be
// XYNameVsZNameTest
// BranchNameVsLayerNameTest

public abstract class CommonSingleTest implements SingleTest {
    private final Logger log = LogManager.getLogger(CommonSingleTest.class);

    protected final PrinterConfiguration printerConfiguration;
    protected final CalibrationConfiguration calibrationConfiguration;

    protected CommonSingleTest(PrinterConfiguration printerConfiguration, CalibrationConfiguration calibrationConfiguration) {
        this.printerConfiguration = printerConfiguration;
        this.calibrationConfiguration = calibrationConfiguration;
    }

    public void writeFile(String data, String filename) {
        DecimalFormat df = new DecimalFormat("0000_");
        filename = df.format(calibrationConfiguration.getRunCount()) + filename.replace(" ", "_");

        log.trace("writing file: {} size:{}", filename, data.length());

        //noinspection ResultOfMethodCallIgnored
        new File("./tests/").mkdirs();
        filename = "./tests/" + filename + ".gcode";

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            writer.write(data);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
