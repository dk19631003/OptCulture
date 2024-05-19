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
 
/*
 * you are able to do many advanced chart customization by extending ChartEngine
 */
public class PieChartEngineFormat1 extends JFreeChartEngine {
     
    private boolean explode = false;
     
    public boolean prepareJFreeChart(JFreeChart jfchart, Chart chart) {
        jfchart.setBackgroundPaint(Color.white);
 
        PiePlot pieModel = (PiePlot) jfchart.getPlot();
       pieModel.setLabelBackgroundPaint(ChartColors.COLOR_31);
 
        //override some default colors
        Paint[] colors = new Paint[] {ChartColors.COLOR_1, ChartColors.COLOR_2, ChartColors.COLOR_3, ChartColors.COLOR_4,ChartColors.COLOR_5,ChartColors.COLOR_6,
        		ChartColors.COLOR_7, ChartColors.COLOR_8, ChartColors.COLOR_9, ChartColors.COLOR_10};
        DefaultDrawingSupplier defaults = new DefaultDrawingSupplier();
        	pieModel.setDrawingSupplier(new DefaultDrawingSupplier(colors, new Paint[]{defaults.getNextFillPaint()}, new Paint[]{defaults.getNextOutlinePaint()},
                new Stroke[]{defaults.getNextStroke()}, new Stroke[] {defaults.getNextOutlineStroke()}, new Shape[] {defaults.getNextShape()}));
         
        pieModel.setShadowPaint(null);
 
       pieModel.setSectionOutlinesVisible(false);
 
        pieModel.setExplodePercent("Java", explode ? 0.2 : 0);
 
        return false;
    }
 
    public void setExplode(boolean explode) {
        this.explode = explode;
    }
}


