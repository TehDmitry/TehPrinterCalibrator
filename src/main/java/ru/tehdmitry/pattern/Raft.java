package ru.tehdmitry.pattern;

import ru.tehdmitry.gcode.CodeComment;
import ru.tehdmitry.gcode.Extruder;
import ru.tehdmitry.gcode.G0_G1_Move;
import ru.tehdmitry.gcode.GCode;

import java.util.ArrayList;
import java.util.List;

public class Raft {
    private final Extruder extruder;

    public Raft(Extruder extruder) {
        this.extruder = extruder;
    }

    public List<GCode> generate(double raftSizeX ,double raftSizeY, double raftStep, double startX, double startY, double layerHeight, double feedRatePrint, double feedRateMove) {
        List<GCode> gCodes = new ArrayList<>();
        gCodes.add(new CodeComment("Raft start"));

        gCodes.add(new G0_G1_Move(true).setX(startX).setY(startY).setF(feedRateMove));

        // Vertical
        for (int i = 0; i <= (int) Math.round(raftSizeY / raftStep) / 2; i++) {
            double curX = startX + (i * 2 * raftStep);
            gCodes.add(new G0_G1_Move(false).setX(curX).setY(startY + raftSizeY).setF(feedRatePrint * 0.5).setExtrusionLength(extruder.getNextEValue(raftSizeY)));
            gCodes.add(new G0_G1_Move(true).setX(curX + raftStep).setY(startY + raftSizeY).setF(feedRateMove));
            gCodes.add(new G0_G1_Move(false).setX(curX + raftStep).setY(startY).setF(feedRatePrint * 0.5).setExtrusionLength(extruder.getNextEValue(raftSizeY)));
            gCodes.add(new G0_G1_Move(true).setX(curX + 2 * raftStep).setY(startY).setF(feedRateMove));
        }

        // Bring back to raft origin

        gCodes.add(new G0_G1_Move(true).setZ(layerHeight * 2).setF(feedRateMove));
        gCodes.add(new G0_G1_Move(true).setX(startX).setY(startY).setF(feedRateMove));

        gCodes.add(new CodeComment("Layer 2"));

        // Horizontal
        for (int i = 0; i <= (int) Math.round(raftSizeX / raftStep) / 2; i++) {
            double curY = startY + (i * 2 * raftStep);
            gCodes.add(new G0_G1_Move(false).setX(startX + raftSizeX).setY(curY).setF(feedRatePrint * 0.65).setExtrusionLength(extruder.getNextEValue(raftSizeX)));
            gCodes.add(new G0_G1_Move(true).setX(startX + raftSizeX).setY(curY + raftStep).setF(feedRateMove));
            gCodes.add(new G0_G1_Move(false).setX(startX).setY(curY + raftStep).setF(feedRatePrint * 0.65).setExtrusionLength(extruder.getNextEValue(raftSizeX)));
            gCodes.add(new G0_G1_Move(true).setX(startX).setY(curY + 2 * raftStep).setF(feedRateMove));
        }

        gCodes.add(new CodeComment("Raft end"));
        gCodes.add(new CodeComment(""));

        return gCodes;
    }
}
