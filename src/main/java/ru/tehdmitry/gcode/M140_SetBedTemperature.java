package ru.tehdmitry.gcode;

import lombok.Setter;

public class M140_SetBedTemperature extends GCodeCommon {

    // Pnnn Bed heater index1
    // Hnnn Heater number1
    // Tnnn Tool number2
    @Setter
    private Double ActiveTemperature; //nnn Active/Target temperature
    @Setter
    private Double StandbyTemperature; //nnn Standby temperature

    public M140_SetBedTemperature(Double ActiveTemperature) {
        super("M140");
        this.ActiveTemperature = ActiveTemperature;
    }

    @Override
    public String getText() {
        ParameterBuilder parameterBuilder = new ParameterBuilder(codePrefix);
        parameterBuilder.addParameter("S", ActiveTemperature);
        parameterBuilder.addParameter("R", StandbyTemperature);
        return parameterBuilder.build();
    }
}
