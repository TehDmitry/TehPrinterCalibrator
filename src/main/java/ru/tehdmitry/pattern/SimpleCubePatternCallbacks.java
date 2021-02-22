package ru.tehdmitry.pattern;

import ru.tehdmitry.gcode.GCode;

import java.util.List;

public interface SimpleCubePatternCallbacks {

    List<GCode> getInitializationGCode();
    String getLayerParameterName();
    double layerTestParameterValue(int currentTest);
    GCode layerTestParameter(int currentTest);


}
