package ru.tehdmitry.gcode;

import ru.tehdmitry.Main;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ParameterBuilder {

    StringBuilder sb = new StringBuilder();
    DecimalFormat df = new DecimalFormat("0.#####", new DecimalFormatSymbols(Locale.US));


    public ParameterBuilder(String prefix) {
        sb.append(prefix);
    }

    public void addParameter(String name, Double value) {
        if (value != null) {
            sb.append(" ");
            sb.append(name);
            sb.append(df.format(value));
        }
    }

    public void addParameter(String name, Integer value) {
        if (value != null) {
            sb.append(" ");
            sb.append(name);
            sb.append(value);
        }
    }

    public String build() {
        sb.append("\n");
        return sb.toString();
    }

}
