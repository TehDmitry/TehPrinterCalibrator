package ru.tehdmitry.gcode;

public class M109_SetExtruderTemperatureAndWait extends GCodeCommon {
    private final Double S;

    public M109_SetExtruderTemperatureAndWait(Double s) {
        super("M109");
        S = s;
    }

    @Override
    public String getText() {
        ParameterBuilder parameterBuilder = new ParameterBuilder(codePrefix);
        parameterBuilder.addParameter("S", S);
        return parameterBuilder.build();
    }
}
