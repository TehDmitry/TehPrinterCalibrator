package ru.tehdmitry.gcode;

import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(chain = true)
public class M203_SetMaximumFeedrate extends GCodeCommon {

    private Double X; //nnn Maximum feedrate for X axis
    private Double Y; //nnn Maximum feedrate for Y axis
    private Double Z; //nnn Maximum feedrate for Z axis
    private Double E; //nnn Maximum feedrate for extruder drives

    public M203_SetMaximumFeedrate() {
        super("M203");
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
