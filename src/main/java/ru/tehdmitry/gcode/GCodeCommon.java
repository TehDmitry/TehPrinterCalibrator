package ru.tehdmitry.gcode;

public abstract class GCodeCommon implements GCode {

    public final String codePrefix;

    protected GCodeCommon(String codePrefix) {
        this.codePrefix = codePrefix;
    }

    @Override
    //for command without parameters
    public String getText() {
        ParameterBuilder parameterBuilder = new ParameterBuilder(codePrefix);
        return parameterBuilder.build();
    }
}
