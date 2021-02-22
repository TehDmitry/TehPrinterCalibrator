package ru.tehdmitry.gcode;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class G0_G1_Move extends GCodeCommon {


    private Double X; // nnn The position to move to on the X axis
    private Double Y; // nnn The position to move to on the Y axis
    private Double Z; // nnn The position to move to on the Z axis
    private Double F; //nnn The feedrate per minute of the move between the starting point and ending point (if supplied)
    //private double E; // nnn The amount to extrude between the starting point and ending point
    private Double extrusionLength;

    // G1 F1500                 ; Feedrate 1500mm/m
    // G1 X50 Y25.3 E22.4 F3000 ; Accelerate to 3000mm/m

    public G0_G1_Move(boolean isRapid) {
        super(isRapid ? "G0" : "G1");
    }

    @Override
    public String getText() {
        ParameterBuilder parameterBuilder = new ParameterBuilder(codePrefix);

        parameterBuilder.addParameter("F", F);
        parameterBuilder.addParameter("X", X);
        parameterBuilder.addParameter("Y", Y);
        parameterBuilder.addParameter("Z", Z);
        parameterBuilder.addParameter("E", extrusionLength);


        return parameterBuilder.build();
    }
}
