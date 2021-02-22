package ru.tehdmitry.gcode;

//Stop the idle hold on all axis and extruder
public class M84_StopIdleHold extends GCodeCommon {
    public M84_StopIdleHold() {
        super("M84");
    }
}
