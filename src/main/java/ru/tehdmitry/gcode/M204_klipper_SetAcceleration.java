package ru.tehdmitry.gcode;

public class M204_klipper_SetAcceleration extends GCodeCommon {
    private final Double S;

    public M204_klipper_SetAcceleration(Double s) {
        super("M204");
        S = s;
    }

    @Override
    public String getText() {
        ParameterBuilder parameterBuilder = new ParameterBuilder(codePrefix);
        parameterBuilder.addParameter("S", S);
        return parameterBuilder.build();
    }
}
