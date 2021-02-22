package ru.tehdmitry.test.updated;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.geom.Point2D;

public class CubeTest extends CommonSquareTest {

    @JsonProperty
    protected int layers = 11;

    @Override
    public void created() {
        setLayersPerTestCount(layers);
    }


    @Override
    protected void moveToTestPosition(int currentTest, int currentTestLayer, int sideTestNum, Point2D.Double position) {
        extrudeTo(new Point2D.Double(position.getX(), position.getY()), getFeedRatePrint());
    }
}
