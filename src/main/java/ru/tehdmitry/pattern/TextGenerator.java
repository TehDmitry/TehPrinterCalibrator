package ru.tehdmitry.pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tehdmitry.configuration.PrinterConfiguration;
import ru.tehdmitry.gcode.*;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TextGenerator {
    private final Logger log = LogManager.getLogger(TextGenerator.class);

    private final Extruder extruder;
    private final PrinterConfiguration printerConfiguration;

    public TextGenerator(Extruder extruder, PrinterConfiguration printerConfiguration) {
        this.extruder = extruder;
        this.printerConfiguration = printerConfiguration;
    }

    public List<GCode> fromText(double startX, double startY, double fontSize, String text) {
        List<GCode> result = new ArrayList<>();

        try {
            // http://imajeenyus.com/computer/20150110_single_line_fonts/index.shtml
            InputStream is = TextGenerator.class.getResourceAsStream("/cnc_v/cnc_v.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);

            FontRenderContext frc = new FontRenderContext(null, false, false);
            GlyphVector gv = font.deriveFont(7f).createGlyphVector(frc, text);

            Shape dispElement = gv.getOutline();
            PathIterator pathIterator = dispElement.getPathIterator(null);

            float[] data = new float[6];

            List<Point2D> glyphPoints = new ArrayList<>();
            while (!pathIterator.isDone()) {
                switch (pathIterator.currentSegment(data)) {
                    case PathIterator.SEG_MOVETO:
                        glyphPoints.clear();
                        glyphPoints.add(new Point2D.Double(data[0], data[1]));
                        break;

                    case PathIterator.SEG_LINETO:
                        glyphPoints.add(new Point2D.Double(data[0], data[1]));
                        break;

                    case PathIterator.SEG_QUADTO:
                        glyphPoints.add(new Point2D.Double(data[0], data[1]));
                        glyphPoints.add(new Point2D.Double(data[2], data[3]));
                        break;

                    case PathIterator.SEG_CUBICTO:
                        glyphPoints.add(new Point2D.Double(data[0], data[1]));
                        glyphPoints.add(new Point2D.Double(data[2], data[3]));
                        glyphPoints.add(new Point2D.Double(data[4], data[5]));
                        break;

                    case PathIterator.SEG_CLOSE:
                        int i = 0;
                        Point2D prevPoint = glyphPoints.get(0);
                        for (Point2D point2D : glyphPoints) {
                            if (i == 0) {
                                result.add(new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(extruder.getPrevE() - printerConfiguration.getRetractionDistance()));
                                result.add(new G0_G1_Move(true).setF(printerConfiguration.getTravelSpeed() * 60).setX(startX + point2D.getX() * fontSize).setY(startY + point2D.getY() * fontSize * -1));
                                result.add(new G0_G1_Move(false).setF(printerConfiguration.getRetractionSpeed() * 60).setExtrusionLength(extruder.getPrevE()));
                            } else {
                                result.add(new G0_G1_Move(false).setF(printerConfiguration.getPrintSpeed() * 60).setX(startX + point2D.getX() * fontSize).setY(startY + point2D.getY() * fontSize * -1).setExtrusionLength(extruder.getNextEValue(point2D.distance(prevPoint) * fontSize)));
                            }
                            prevPoint = point2D;
                            i++;
                        }
                        break;

                    default:
                        throw new IOException();
                }

                pathIterator.next();
            }
        } catch (IOException | FontFormatException e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }
}
