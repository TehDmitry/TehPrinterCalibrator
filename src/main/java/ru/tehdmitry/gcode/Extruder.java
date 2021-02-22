package ru.tehdmitry.gcode;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Extruder {
    private final double extrusionWidth;
    private final double heightLayer;
    private final double diameterFilament;
    @Setter
    private double multiplierExtrusion;

    private double prevE = 0;

    public Extruder(double extrusionWidth, double heightLayer, double diameterFilament, double multiplierExtrusion) {
        this.extrusionWidth = extrusionWidth;
        this.heightLayer = heightLayer;
        this.diameterFilament = diameterFilament;
        this.multiplierExtrusion = multiplierExtrusion;
    }

    // Generate E Value  https://3dprinting.stackexchange.com/questions/10171/how-is-e-value-calculated-in-slic3r
    public double getEValue(double extrusionLength) {
        double extrudedArea = (extrusionWidth - heightLayer) * heightLayer + Math.PI * Math.pow((heightLayer / 2), 2);
        double filamentArea = Math.PI * Math.pow(diameterFilament / 2, 2);
        double e = (extrudedArea * extrusionLength) / filamentArea;

        e *= multiplierExtrusion;
        return e;
    }

    public double getNextEValue(double extrusionLength) {
        prevE += getEValue(extrusionLength);
        return prevE;
    }

    public double currentE() {
        return prevE;
    }

    public double increaseE(double value) {
        prevE += value;
        return prevE;
    }


    public void resetE() {
        prevE = 0;
    }
}
