package ru.tehdmitry.gcode;

import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(chain = true)
public class G92_SetPosition extends GCodeCommon {
    private Double X; //nnn new X axis position
    private Double Y; //nnn new Y axis position
    private Double Z; //nnn new Z axis position
    private Double E; //nnn new extruder position

    public G92_SetPosition() {
        super("G92");
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
