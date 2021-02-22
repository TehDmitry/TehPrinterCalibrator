package ru.tehdmitry.gcode;

import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(chain = true)
public class M204_smoothie_SetAcceleration extends GCodeCommon {
    private Double X;
    private Double Y;
    private Double Z;
    private Double E;

    public M204_smoothie_SetAcceleration() {
        super("M204");
    }

    @Override
    public String getText() {
        ParameterBuilder parameterBuilder = new ParameterBuilder(codePrefix);
        parameterBuilder.addParameter("X", X);
        parameterBuilder.addParameter("Y", Y);
        parameterBuilder.addParameter("Z", Z);
        parameterBuilder.addParameter("E", E);
        return parameterBuilder.build();
    }
}
