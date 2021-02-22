package ru.tehdmitry.gcode;

public class M104_SetExtruderTemperature extends GCodeCommon {
    private final Double S;

    public M104_SetExtruderTemperature(Double s) {
        super("M104");
        S = s;
    }

    @Override
    public String getText() {
        ParameterBuilder parameterBuilder = new ParameterBuilder(codePrefix);
        parameterBuilder.addParameter("S", S);
        return parameterBuilder.build();
    }
}
