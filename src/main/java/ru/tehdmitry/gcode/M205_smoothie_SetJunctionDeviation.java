package ru.tehdmitry.gcode;

import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(chain = true)
public class M205_smoothie_SetJunctionDeviation extends GCodeCommon {
    private Double X;
    private Double Z;
    private Double S;

    public M205_smoothie_SetJunctionDeviation() {
        super("M205");
    }

    @Override
    public String getText() {
        ParameterBuilder parameterBuilder = new ParameterBuilder(codePrefix);
        parameterBuilder.addParameter("X", X);
        parameterBuilder.addParameter("Z", Z);
        parameterBuilder.addParameter("S", S);
        return parameterBuilder.build();
    }
}
