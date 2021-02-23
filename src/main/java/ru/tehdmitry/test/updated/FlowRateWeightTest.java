package ru.tehdmitry.test.updated;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tehdmitry.configuration.CalibrationConfiguration;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.gcode.*;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class FlowRateWeightTest extends CommonTestImpl {

    private final Logger log = LogManager.getLogger(FlowRateWeightTest.class);

    protected Point2D.Double startPosition;

    protected double slowMoveSpeed = 10.0;

    @JsonProperty
    private double temperatureStart = 230;
    @JsonProperty
    private double temperatureInc = 10;

    @JsonProperty
    protected double volumetricFlowRateStart = 5; //mm³/s
    @JsonProperty
    protected double volumetricFlowRateInc = 5;
    @JsonProperty
    protected double extrusionLength = 250.0;

    @Override
    public void created() {

    }

    @Override
    public void process() {

        startPosition = new Point2D.Double(
                printerConfiguration.getDimensionX() / 2,
                printerConfiguration.getDimensionY() / 2
        );


        gCodes.add(new CodeComment("Start Gcode"));
        gCodes.add(new M107_FanOff());

        gCodes.add(new M104_SetExtruderTemperature(temperatureStart));
        gCodes.add(new M109_SetExtruderTemperatureAndWait(temperatureStart));

        gCodes.add(new M82_SetExtruderToAbsoluteMode());
        gCodes.add(new G90_SetToAbsolutePositioning());
        gCodes.add(new G28_MoveToHome());

        gCodes.add(new G92_SetPosition().setE(0.0));

        gCodes.add(new G0_G1_Move(false).setZ(50.0));


        double offsetFromCenterX = printerConfiguration.getDimensionX() / 4;
        double offsetFromCenterY = printerConfiguration.getDimensionY() / 4;


        for (int i = 0; i < 6; i++) {
            moveTo(startPosition, feedRateMove, false);
            setTestTemperature(i);

            gCodes.add(new G0_G1_Move(false).setF(feedRatePrint).setExtrusionLength(extruder.increaseE(extrusionLength / 10)));

            // slow extrude for pause
            gCodes.add(new G0_G1_Move(false).setF(0.5).setExtrusionLength(extruder.increaseE(0.1)));


            switch (i) {
                case 0:
                    moveTo(new Point2D.Double(startPosition.getX() - offsetFromCenterX, startPosition.getY() - offsetFromCenterY), slowMoveSpeed * 60, false);
                    break;
                case 1:
                    moveTo(new Point2D.Double(startPosition.getX(), startPosition.getY() - offsetFromCenterY), slowMoveSpeed * 60, false);
                    break;
                case 2:
                    moveTo(new Point2D.Double(startPosition.getX() + offsetFromCenterX, startPosition.getY() - offsetFromCenterY), slowMoveSpeed * 60, false);
                    break;
                case 3:
                    moveTo(new Point2D.Double(startPosition.getX() - offsetFromCenterX, startPosition.getY() + offsetFromCenterY), slowMoveSpeed * 60, false);
                    break;
                case 4:
                    moveTo(new Point2D.Double(startPosition.getX(), startPosition.getY() + offsetFromCenterY), slowMoveSpeed * 60, false);
                    break;
                case 5:
                    moveTo(new Point2D.Double(startPosition.getX() + offsetFromCenterX, startPosition.getY() + offsetFromCenterY), slowMoveSpeed * 60, false);
                    break;
            }

            extrudeForTest(i);
        }

        //# Home X Y
        gCodes.add(new G28_MoveToHome().setX(true).setY(true));
        // Turn off Steppers
        gCodes.add(new M84_StopIdleHold());
        // Turn off Fan
        gCodes.add(new M107_FanOff());
        // Turn off Extruder
        gCodes.add(new M104_SetExtruderTemperature(0.0));

        writeFile();
    }

    @Override
    public String getDescription() {
        DecimalFormat dfRound = new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.US));

        return "Temperature [" + dfRound.format(getTemperature(0)) + "..." + dfRound.format(getTemperature(5)) + "] vs. " +
                "VolumetricFlow [" + dfRound.format(getVolumetricFlowRate(0)) + "..." + dfRound.format(getVolumetricFlowRate(5)) + "]"
                + "_FlowRateWeight";
    }

    protected double getTemperature(int testNumber) {
        return temperatureStart + ((testNumber / 3) * temperatureInc);
    }

    protected void setTestTemperature(int testNumber) {
        double temperature = getTemperature(testNumber);
        gCodes.add(new M104_SetExtruderTemperature(temperature));
        gCodes.add(new M109_SetExtruderTemperatureAndWait(temperature));
    }

    protected double getVolumetricFlowRate(int testNumber) {
        return (volumetricFlowRateStart + volumetricFlowRateInc * (testNumber % 3));
    }

    protected void extrudeForTest(int testNumber) {
        double volumetricFlowRate = getVolumetricFlowRate(testNumber);
        double feedRate = volumetricFlowRate / (Math.PI * Math.pow((printerConfiguration.getFilamentDiameter() / 2), 2));

        gCodes.add(new G0_G1_Move(false).setF(feedRate * 60).setExtrusionLength(extruder.increaseE(extrusionLength / 4)));
        gCodes.add(new G0_G1_Move(false).setF(feedRate * 60).setExtrusionLength(extruder.increaseE(extrusionLength / 4)));
        gCodes.add(new G0_G1_Move(false).setF(feedRate * 60).setExtrusionLength(extruder.increaseE(extrusionLength / 4)));
        gCodes.add(new G0_G1_Move(false).setF(feedRate * 60).setExtrusionLength(extruder.increaseE(extrusionLength / 4)));
    }
}
