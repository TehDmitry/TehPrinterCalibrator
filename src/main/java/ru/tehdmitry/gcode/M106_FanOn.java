package ru.tehdmitry.gcode;

public class M106_FanOn extends GCodeCommon {

    // Pnnn Fan number (optional, defaults to 0)2
    private final Double S; //nnn Fan speed (0 to 255; RepRapFirmware also accepts 0.0 to 1.0))
    // Extra Parameters
    // Innn Invert signal, or disable fan1 3
    // Fnnn Set fan PWM frequency, in Hz1 3
    // Lnnn Set minimum fan speed (0 to 255 or 0.0 to 1.0)1 3
    // Xnnn Set maximum fan speed (0 to 255 or 0.0 to 1.0)1 3
    // Bnnn Blip time - fan will be run at full PWM for this number of seconds when started from standstill1
    // Hnn:nn:nn... Select heaters monitored when in thermostatic mode1 3
    // Rnnn Restore fan speed to the value it has when the print was paused1
    // Tnnn Set thermostatic mode trigger temperature
    // Cnnn Set custom name (RRF > 2.01 only)1

    public M106_FanOn(Double s) {
        super("M106");
        S = s;
    }

    @Override
    public String getText() {
        ParameterBuilder parameterBuilder = new ParameterBuilder(codePrefix);
        parameterBuilder.addParameter("S", S);
        return parameterBuilder.build();
    }
}
