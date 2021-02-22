package ru.tehdmitry.gcode;

public class CodeComment extends GCodeCommon {
    private final String comment;

    public CodeComment(String comment) {
        super(";");
        this.comment = comment;
    }

    @Override
    public String getText() {
        return codePrefix + " " + comment + "\n";
    }
}
