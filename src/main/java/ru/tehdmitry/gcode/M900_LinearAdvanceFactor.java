package ru.tehdmitry.gcode;

import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(chain = true)
public class M900_LinearAdvanceFactor extends GCodeCommon {
    private Double K;
    private Double L;
    private Integer S;
    private Integer T;

    public M900_LinearAdvanceFactor() {
        super("M900");
    }

    @Override
    public String getText() {
        ParameterBuilder parameterBuilder = new ParameterBuilder(codePrefix);
        parameterBuilder.addParameter("K", K);
        parameterBuilder.addParameter("L", L);
        parameterBuilder.addParameter("S", S);
        parameterBuilder.addParameter("T", T);
        return parameterBuilder.build();
    }
}
