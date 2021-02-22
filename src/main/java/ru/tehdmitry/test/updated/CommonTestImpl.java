package ru.tehdmitry.test.updated;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.gcode.CodeComment;
import ru.tehdmitry.gcode.Extruder;
import ru.tehdmitry.gcode.G0_G1_Move;
import ru.tehdmitry.gcode.GCode;

import java.awt.geom.Point2D;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE
)

@Getter
public abstract class CommonTestImpl implements CommonTest {

    private final Logger log = LogManager.getLogger(CommonTestImpl.class);

    @Setter
    protected PrinterConfiguration printerConfiguration;
    @Setter
    protected CalibrationConfiguration calibrationConfiguration;
    protected List<GCode> gCodes = new ArrayList<>();
    protected double feedRatePrint;
    protected double feedRateMove;
    protected double feedRateRetraction;
    protected Extruder extruder;
    Point2D.Double previousMove;

    public void setPrinterConfiguration(PrinterConfiguration printerConfiguration) {
        this.printerConfiguration = printerConfiguration;
        extruder = new Extruder(printerConfiguration.getExtrusionWidth(), printerConfiguration.getLayerHeight(), printerConfiguration.getFilamentDiameter(), printerConfiguration.getExtrusionMultiplier());

        feedRatePrint = printerConfiguration.getPrintSpeed() * 60; // mm/min
        feedRateMove = printerConfiguration.getTravelSpeed() * 60; // mm/min
        feedRateRetraction = printerConfiguration.getRetractionSpeed() * 60; // mm/min
    }

    @Override
    public String getDescription() {
        return this.getClass().getSimpleName().replace("Test", "");
    }

    public void writeFile() {
        String filename = getDescription();

        StringBuilder gCodeText = new StringBuilder();
        gCodes.forEach(gCode -> {
            gCodeText.append(gCode.getText());
        });
        gCodeText.append("\n");

        DecimalFormat df = new DecimalFormat("0000_");
        filename = df.format(calibrationConfiguration.getRunCount()) + filename.replace(" ", "_");

        log.trace("writing file: {}", filename);

        //noinspection ResultOfMethodCallIgnored
        new File("./tests/").mkdirs();
        filename = "./tests/" + filename + ".gcode";

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            writer.write(gCodeText.toString());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    //todo: move gcode and moveTo/extrudeTo/retraction/etc to Printer Object


    protected void moveTo(Point2D.Double position, double feedRate, boolean withRetraction) {
        if (withRetraction) {
            retraction(feedRateRetraction, printerConfiguration.getRetractionDistance());
        }
        gCodes.add(new G0_G1_Move(true).setX(position.getX()).setY(position.getY()).setF(feedRate));
        if (withRetraction) {
            deretraction(feedRateRetraction, printerConfiguration.getRetractionDistance());
        }
        previousMove = position;
    }

    protected void extrudeTo(Point2D.Double position, double feedRate) {
        gCodes.add(new G0_G1_Move(false).setX(position.getX()).setY(position.getY()).setF(feedRate).setExtrusionLength(extruder.getNextEValue(previousMove.distance(position))));
        previousMove = position;
    }

    protected void retraction(double feedRate, double retractionDistance) {
        extruder.increaseE(-1.0 * retractionDistance);
        gCodes.add(new G0_G1_Move(false).setF(feedRate).setExtrusionLength(extruder.currentE()));

    }

    protected void deretraction(double feedRate, double retractionDistance) {
        extruder.increaseE(retractionDistance);
        gCodes.add(new G0_G1_Move(false).setF(feedRate).setExtrusionLength(extruder.currentE()));

    }

    protected void generateHeader() {
        gCodes.add(new CodeComment(getDescription()));
        gCodes.add(new CodeComment(""));
        gCodes.add(new CodeComment("printerConfiguration: "));
        for (String s : printerConfiguration.dumpConfig().split("\n")) {
            gCodes.add(new CodeComment(s));
        }

        gCodes.add(new CodeComment(""));
        gCodes.add(new CodeComment("calibrationConfiguration: "));
        for (String s : calibrationConfiguration.dumpConfig().split("\n")) {
            gCodes.add(new CodeComment(s));
        }

        gCodes.add(new CodeComment(""));
    }
}
