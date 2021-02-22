package ru.tehdmitry.gcode;

import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(chain = true)
public class M205_marlin_SetAdvancedSettings extends GCodeCommon {
    private Double B;
    private Double E;
    private Double J;
    private Double S;
    private Double T;
    private Double X;
    private Double Y;
    private Double Z;

    public M205_marlin_SetAdvancedSettings() {
        super("M205");
    }

    @Override
    public String getText() {
        ParameterBuilder parameterBuilder = new ParameterBuilder(codePrefix);
        parameterBuilder.addParameter("B", B);
        parameterBuilder.addParameter("E", E);
        parameterBuilder.addParameter("J", J);
        parameterBuilder.addParameter("S", S);
        parameterBuilder.addParameter("T", T);
        parameterBuilder.addParameter("X", X);
        parameterBuilder.addParameter("Y", Y);
        parameterBuilder.addParameter("Z", Z);
        return parameterBuilder.build();
    }
}
