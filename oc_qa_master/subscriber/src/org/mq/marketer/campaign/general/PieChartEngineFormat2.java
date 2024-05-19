package org.mq.marketer.campaign.general;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.PiePlot;
import org.zkoss.zkex.zul.impl.JFreeChartEngine;
import org.zkoss.zul.Chart;
import org.zkoss.zul.event.ChartAreaListener;
 
/*
 * you are able to do many advanced chart customization by extending ChartEngine
 */
public class PieChartEngineFormat2 extends JFreeChartEngine {
     
    private boolean explode = false;
     
    public boolean prepareJFreeChart(JFreeChart jfchart, Chart chart) {
        jfchart.setBackgroundPaint(Color.white);
 
       PiePlot pieModel = (PiePlot) jfchart.getPlot();
     pieModel.setLabelBackgroundPaint(ChartColors.COLOR_31);
 
        //override some default colors
        Paint[] colors = new Paint[] {ChartColors.COLOR_11, ChartColors.COLOR_12, ChartColors.COLOR_13, ChartColors.COLOR_14, ChartColors.COLOR_15, ChartColors.COLOR_16,
        		ChartColors.COLOR_17, ChartColors.COLOR_18, ChartColors.COLOR_19, ChartColors.COLOR_20};
        DefaultDrawingSupplier defaults = new DefaultDrawingSupplier();
        pieModel.setDrawingSupplier(new DefaultDrawingSupplier(colors, new Paint[]{defaults.getNextFillPaint()}, new Paint[]{defaults.getNextOutlinePaint()},
                new Stroke[]{defaults.getNextStroke()}, new Stroke[] {defaults.getNextOutlineStroke()}, new Shape[] {defaults.getNextShape()}));
         
        pieModel.setShadowPaint(null);
 
       pieModel.setSectionOutlinesVisible(false);
 
        pieModel.setExplodePercent("Java", explode ? 0.2 : 0);
 
        return false;
    }
}