package ru.tehdmitry.pattern;

import ru.tehdmitry.gcode.G0_G1_Move;
import ru.tehdmitry.gcode.GCode;

import java.util.List;

public interface RetractionTowerCallbacks {

    List<GCode> getInitializationGCode();

    String getBranchParameterName();
    double branchParameterValue(int branch);

    String getLayerParameterName();
    double layerTestParameterValue(int currentTest);

    GCode onBranchStart(int currentTest, int branch, G0_G1_Move retractionMove);
    GCode onBranchEnd(int currentTest, int branch, G0_G1_Move deretractionMove);

    GCode layerTestParameter(int currentTest);


}
