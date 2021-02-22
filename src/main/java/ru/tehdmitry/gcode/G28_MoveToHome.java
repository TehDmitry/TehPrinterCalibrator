package ru.tehdmitry.gcode;

import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(chain = true)
public class G28_MoveToHome extends GCodeCommon {
    private boolean X; // Flag to go back to the X axis origin
    private boolean Y; // Flag to go back to the Y axis origin
    private boolean Z; // Flag to go back to the Z axis origin

    public G28_MoveToHome() {
        super("G28");
    }

    @Override
    public String getText() {
        StringBuilder sb = new StringBuilder();
        sb.append(codePrefix);

        if(X) {
            sb.append(" X");
        }
        if(Y) {
            sb.append(" Y");
        }
        if(Z) {
            sb.append(" Z");
        }

        sb.append("\n");
        return sb.toString();
    }

}
