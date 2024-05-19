package org.mq.marketer.campaign.general;

import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.zkoss.zkex.zul.impl.JFreeChartEngine;
import org.zkoss.zul.Chart;

public class LineChartEngine extends JFreeChartEngine {
	 
    public int width = 2;
    public boolean showLine = true;
    public boolean showShape = true;
 
    public boolean prepareJFreeChart(JFreeChart jfchart, Chart chart) {
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) ((CategoryPlot) jfchart.getPlot()).getRenderer();
        renderer.setBaseStroke(new BasicStroke(width));
/*        renderer.setSeriesStroke(1, new BasicStroke(width));
        renderer.setSeriesStroke(2, new BasicStroke(width));
*/ 
        renderer.setBaseLinesVisible(showLine);
/*        renderer.setSeriesLinesVisible(1, showLine);
        renderer.setSeriesLinesVisible(2, showLine);
*/ 
        renderer.setBaseShapesVisible(showShape);

        renderer.setSeriesPaint(0,new Color(255)); 
        renderer.setSeriesPaint(1, new Color(32768));
        renderer.setSeriesPaint(2, new Color(10824234));
        renderer.setSeriesPaint(3, new Color(16711935));
        renderer.setSeriesPaint(4, new Color(16282135));
        
/*        renderer.setSeriesShapesVisible(1, showShape);
        renderer.setSeriesShapesVisible(2, showShape);
*/        return false;
    }
 
    public void setWidth(int width) {
        this.width = width;
    }
 
    public void setShowLine(boolean showLine) {
        this.showLine = showLine;
    }
 
    public void setShowShape(boolean showShape) {
        this.showShape = showShape;
    }
}