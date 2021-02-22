package ru.tehdmitry.test.updated;

import com.fasterxml.jackson.annotation.JsonProperty;
import ru.tehdmitry.gcode.CodeComment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class CommonSquareTestWithOneParameter extends CommonSquareTest {

    @JsonProperty
    protected int layersPerTest = 10;
    @JsonProperty
    protected int numTests = 10;

    public abstract String getLayerParameterName();

    public abstract double layerTestParameterValue(int currentTest);

    @Override
    public void created() {
        setLayerTestCount(numTests);
        setLayersPerTestCount(layersPerTest);
    }

    @Override
    public List<String> getTopDescription() {
        DecimalFormat dfRound = new DecimalFormat("0.##");
        List<String> result = new ArrayList<>();
        result.add(getLayerParameterName());
        result.add(dfRound.format(layerTestParameterValue(layerTestCount - 1)));
        result.add(dfRound.format(layerTestParameterValue(0)));
        return result;
    }

    @Override
    public String getDescription() {
        DecimalFormat dfRound = new DecimalFormat("0.##");

        return getLayerParameterName() + " [" + dfRound.format(layerTestParameterValue(0)) + "..." + dfRound.format(layerTestParameterValue(layerTestCount - 1)) + "]"
                + "_" + getTestName();
    }

    @Override
    protected void generateHeader() {
        super.generateHeader();

        gCodes.add(new CodeComment(""));
        gCodes.add(new CodeComment("Variables by Height"));
        gCodes.add(new CodeComment(""));

        gCodes.add(new CodeComment("Height         " + getLayerParameterName()));
        gCodes.add(new CodeComment(""));

        for (int curTest = numTests - 1; curTest >= 0; curTest--) {
            gCodes.add(new CodeComment("" + curTest + " layers      " + layerTestParameterValue(curTest)));
        }
    }
}
