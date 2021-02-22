package ru.tehdmitry.gcode;

public class M190_WaitForBedTemperature extends GCodeCommon {

    private final Double S; //nnn minimum target temperature, waits until heating
    //Rnnn accurate target temperature, waits until heating and cooling (Marlin and Prusa)

    public M190_WaitForBedTemperature(Double s) {
        super("M190");
        S = s;
    }

    @Override
    public String getText() {
        ParameterBuilder parameterBuilder = new ParameterBuilder(codePrefix);
        parameterBuilder.addParameter("S", S);
        return parameterBuilder.build();
    }
}
