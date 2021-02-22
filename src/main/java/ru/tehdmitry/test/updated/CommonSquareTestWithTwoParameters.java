package ru.tehdmitry.test.updated;


import ru.tehdmitry.gcode.CodeComment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class CommonSquareTestWithTwoParameters extends CommonSquareTestWithOneParameter {

    public abstract String getSideParameterName();

    public abstract double sideParameterValue(int sideNumber);

    @Override
    public List<String> getBottomDescription() {
        DecimalFormat dfRound = new DecimalFormat("0.##");

        List<String> result = new ArrayList<>();
        result.add(getSideParameterName());
        result.add(dfRound.format(sideParameterValue(0)) + " - " + dfRound.format(sideParameterValue(sideTestCount - 1)));
        return result;
    }

    @Override
    public String getDescription() {
        DecimalFormat dfRound = new DecimalFormat("0.##");

        return getSideParameterName() + " [" + dfRound.format(sideParameterValue(0)) + "..." + dfRound.format(sideParameterValue(sideTestCount - 1)) + "] vs. " +
                getLayerParameterName() + " [" + dfRound.format(layerTestParameterValue(0)) + "..." + dfRound.format(layerTestParameterValue(layerTestCount - 1)) + "]"
                + "_" + getTestName();
    }

    @Override
    protected void generateHeader() {
        super.generateHeader();

        gCodes.add(new CodeComment(""));
        gCodes.add(new CodeComment("Variables by Sides (CCW)"));
        gCodes.add(new CodeComment(""));

        gCodes.add(new CodeComment("Side         " + getSideParameterName()));
        gCodes.add(new CodeComment(""));

        for (int i = 0; i < sideTestCount; i++) {
            gCodes.add(new CodeComment("" + i + " side      " + sideParameterValue(i)));
        }
    }
}
