/* CanvasBoardController.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		May 20, 2010 12:08:37 PM , Created by simon
}}IS_NOTE

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.mq.marketer.campaign.controller.program;

import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.zkforge.canvas.*;
import org.zkforge.canvas.Drawable.LineCap;
import org.zkforge.canvas.Drawable.LineJoin;
import org.zkoss.json.JSONValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkex.zul.Colorbox;
import org.zkoss.zul.*;

/**
 * @author simon
 *
 */
public class CanvasBoardController extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Window zkpaintWindow;
	private Window toolWindow;
	private Window shapeListWindow;
	
	private Listbox shapeBox;
	private Listbox strokeTypeBox;
	private Listbox fillTypeBox;
	private Colorbox strokeColorBox;
	private Colorbox fillColorBox;
	//private Slider alphaSlider;
	private double alpha;
	
	private Textbox textBox;
	private Listbox fontBox;
	private Intbox fontSizeBox;
	
	private Canvas cvs1;
	private Canvas cvs2;
	private Label shapeDataLb;
	private Label selectedShapeDataLb;
	
	private Listbox shapeListBox;
	private ListModelList shapeListModel;
	
	private List<Shape> _shapes;
	private List<Shape> _selectedShape;
	
	private List<String> _shapeNames;
	
	
	
	public void onAddShape$cvsDiv(Event event){
		double[] data = getDataValues(event);
		alpha = data[5];
		
		// add shape
		Shape s;
		int shapeIndex = (int) data[0];
		
		switch(shapeIndex){
		case 0:
			s = new Rectangle(data[1], data[2], data[3], data[4]);
			
			// sets colors
			//s.setFillStyle("#000080");
			//s.setStrokeStyle("#0000FF");
			 
			// sets color transparency
			//s.setAlpha(0.5);
			 
			// rotates the shape by setting transformation
			//s.setTransformation(0.87, 0.5, -0.5, 0.87, 0, 0);
			 
			// sets outline width
			//s.setLineWidth(1);
			
			
			
			break;
		default:
			Path p = new Path((Path)_shapes.get(shapeIndex));
			p.transform(new AffineTransform(data[3]/1000,0,0,data[4]/1000,data[1],data[2]));
			s = p;
		}
		
		// sets shadow effect
		s.setShadowOffset(3, 3);
		s.setShadowColor("#808080");
		s.setShadowBlur(5);
		
		setDrawingState(s);
		s.setLineWidth(2); // default for shapes to look better
		cvs1.add(s);
		Object[] objs = new Object[2];
		objs[0] = _shapeNames.get(shapeIndex);
		objs[1] = s;
		shapeListModel.add(objs);
		logger.info("shapeListModel Size="+shapeListModel.size());
		
		// Window obj = (Window) Executions.createComponents("zul/general/window.zul", cvs1, null);
	}
	
	public void onAddText$cvsDiv(Event event){
		double[] data = getDataValues(event);
		alpha = data[2];
		
//		Text txt = new Text(textBox.getValue(), 0, 0);
		Text txt = new Text("Krishna", 0, 0);
//		txt.setFont(fontSizeBox.getValue() + "px " + fontBox.getSelectedItem().getValue());
		txt.setFont("30px Sans Serif");
		
		txt.setPosition(data[0], data[1]);
		
		setDrawingState(txt);
		cvs1.add(txt);
		Object[] objs = new Object[2];
		objs[0] = "Text";
		objs[1] = txt;
		shapeListModel.add(objs);
	}
	
	public void onSelect$cvsDiv(Event event){
		
		logger.info("In OnSelect");
		
		double[] data = getDataValues(event);
		double startX = Math.min(data[0], data[2]);
		double startY = Math.min(data[1], data[3]);
		double sizeX = Math.abs(data[0] - data[2]);
		double sizeY = Math.abs(data[1] - data[3]);
		_selectedShape = new ArrayList<Shape>();
		
		String selectedShapeStr="";
		for(int i=cvs1.size()-1; i>-1; i--) {
			Drawable d = cvs1.get(i);
			boolean intersected = false;
			
			if(d instanceof Shape) {
				Shape s = (Shape) d;
				intersected = s.intersects(startX, startY, sizeX+1, sizeY+1);
				if(intersected) {
					_selectedShape.add(s);
					selectedShapeStr = JSONValue.toJSONString(_selectedShape);
					logger.info("Value="+selectedShapeStr);
					cvs1.remove(d);
					break;
				}
			} 
			else if (d instanceof Text) {
				Text t = (Text) d;
				double x = t.getX();
				double y = t.getY();
//				double fontSize = fontSizeBox.getValue();
				double fontSize = 30;
				double textWidth = t.getText().length() * fontSize / 2.5;
				intersected = data[0] < x + textWidth && x < data[2] && 
							  data[1] < y && y - fontSize < data[3];
			}
			
			if(intersected) {
				
				logger.info("Selected Shapes ="+_selectedShape);
				logger.info("Size csv1 ="+cvs1.size());
				logger.info("Size csv2 ="+cvs2.size());
				//cvs1.remove(d);
				// cvs2.add(d);
				logger.info("Size csv1 ="+cvs1.size());
				logger.info("Size csv2 ="+cvs2.size());
				// Clients.evalJavaScript("setShape(1)");
				logger.info("shapeListModel="+shapeListModel.size());
				
/*				for (Object tempObj : shapeListModel) {
					Object[] obj =(Object[])tempObj;
					logger.info("  ,"+obj[0]+"  "+obj[1]);
				}
*/			}
			
			//shapeListBox.getItemAtIndex(i).setSelected(intersected);
			//alert("Selected = "+intersected);
		} // for
		
		selectedShapeDataLb.setValue(selectedShapeStr);
		
		logger.info("Calling setShape with value :"+selectedShapeStr);
		Clients.evalJavaScript("setShape("+selectedShapeStr+")");
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
/*		toolWindow  = (Window) zkpaintWindow.getFellow("toolInc").getFellow("toolWindow");
		shapeListWindow = (Window) zkpaintWindow.getFellow("shapeListWindow");
		
		strokeTypeBox  = (Listbox)  toolWindow.getFellow("strokeTypeBox");
		fillTypeBox    = (Listbox)  toolWindow.getFellow("fillTypeBox");
		strokeColorBox = (Colorbox) toolWindow.getFellow("strokeColorBox");
		fillColorBox   = (Colorbox) toolWindow.getFellow("fillColorBox");
		//alphaSlider    = (Slider)   toolWindow.getFellow("alphaSlider");
		
		shapeBox       = (Listbox)  toolWindow.getFellow("shapeBox");
		
		textBox     = (Textbox) toolWindow.getFellow("textBox");
		fontBox     = (Listbox) toolWindow.getFellow("fontBox");
		fontSizeBox = (Intbox)  toolWindow.getFellow("fontSizeBox");
		
		constructShapes();
		
		// generate shape options
		for(int i=0; i<_shapeNames.size(); i++){
			shapeBox.appendItem(_shapeNames.get(i), ""+i);
		}
		shapeBox.setSelectedIndex(0);
		
		// send all shapes to client side
		shapeDataLb.setValue(JSONValue.toJSONString(_shapes));
		
		shapeListModel = new ListModelList();
		shapeListBox = (Listbox) shapeListWindow.getFellow("shapeListBox");
		shapeListBox.setModel(shapeListModel);
		shapeListBox.setItemRenderer(new ListitemRenderer(){
			public void render(Listitem item, Object data) throws Exception {
				Object[] objs = (Object[]) data;
				Listcell lc = new Listcell();
				lc.appendChild(new Label(objs[0].toString()));
				item.appendChild(lc);
			}
		});
		*/
		
		constructShapes();
		// send all shapes to client side
		shapeDataLb.setValue(JSONValue.toJSONString(_shapes));
		shapeListModel = new ListModelList();
	}
	
	private void setDrawingState(Drawable drawable){
		// get drawing type
		/*int doStroke = (strokeTypeBox.getSelectedIndex() > 0) ? 1 : 0;
		int doFill = (fillTypeBox.getSelectedIndex() > 0) ? 2 : 0;*/
		int doStroke = 1;
		int doFill = 0;
		String drawingType = "";
		
		switch(doStroke + doFill){
			case 0:
				drawingType = Drawable.DrawingType.NONE;
				break;
			case 1:
				drawingType = Drawable.DrawingType.STROKE;
				break;
			case 2:
				drawingType = Drawable.DrawingType.FILL;
				break;
			case 3:
			default:
				drawingType = Drawable.DrawingType.BOTH;
				break;
		}
		
//		String storkeColor = strokeColorBox.getValue();
//		String fillColor = fillColorBox.getValue(); 
		
		String storkeColor = "#000000";
		String fillColor = "#000000"; 
		
		//double alpha = alphaSlider.getCurpos() / 100.0;
		//bug #3006313: getCurpos() does not work
		
		drawable.setDrawingType(drawingType);
		drawable.setStrokeStyle(storkeColor);
		drawable.setFillStyle(fillColor);
		drawable.setAlpha(alpha);
		
	}
	
	private double[] getDataValues(Event event){
		Event evt = ((ForwardEvent) event).getOrigin();
		Object[] data = (Object[]) evt.getData();
		double[] result = new double[data.length];
		for(int i=0; i<result.length; i++) {
			logger.info("---"+data[i]);
			
			if(data[i] instanceof Double) {
				result[i] = (Double) data[i];
			} else {
				result[i] = (Integer) data[i];
			}
		}
		return result;
	}
	
	private void constructShapes(){
		_shapes = new ArrayList<Shape>();
		_shapeNames = new ArrayList<String>();
		
		_shapeNames.add("Rectangle");
		_shapes.add(new Rectangle(0,0,1000,1000));
		
		_shapeNames.add("Line");
		_shapes.add(new Path().moveTo(0,0).lineTo(1000,1000).closePath());
		
		
		_shapeNames.add("Triangle");
		_shapes.add(new Path().moveTo(0,0).lineTo(0,1000).lineTo(1000,500)
				.lineTo(0,0).closePath());
		
		_shapeNames.add("Circle");
		_shapes.add(new Path(new Arc2D.Double(0, 0, 1000, 1000, 0, 360, 
				Arc2D.CHORD)));
		
		_shapeNames.add("Hexagon");
		_shapes.add(ngon(500,6));
		
		_shapeNames.add("Condition");
		_shapes.add(new Path().moveTo(0,0).lineTo(0,1000).lineTo(1000,1000).lineTo(1000,0).lineTo(0,0)
				.moveTo(0,300).lineTo(1000,300).moveTo(0,700).lineTo(1000,700)
				.closePath());
		
		_shapeNames.add("DHexagon");
		_shapes.add(ngon(500,4));
		
		_shapeNames.add("Star");
		_shapes.add(nstar(500,5,43.5));
		
		_shapeNames.add("Heart");
		_shapes.add(heart(1000));
		
	}
	
	private Path ngon(double r, int n){
		Path p = new Path().moveTo(r, 0);
		for(int i=1; i<n+1; i++){
			double arg = Math.PI * (1.5 + (2.0*i)/n);
			p.lineTo(r + r * Math.cos(arg), r + r * Math.sin(arg));
		}
		p.closePath();
		return p;
	}
	
	private Path nstar(double r, int n, double theta){
		Path p = new Path().moveTo(r, 0);
		double r2 = r * Math.sin(Math.PI*theta/360) / Math.sin(Math.PI*(theta/360 + 2.0/n));
		
		for(int i=1; i<n+1; i++){
			double arg1 = Math.PI * (1.5 + (2.0*i)/n);
			double arg2 = arg1 - Math.PI/n;
			p.lineTo(r + r2 * Math.cos(arg2), r + r2 * Math.sin(arg2));
			p.lineTo(r + r * Math.cos(arg1), r + r * Math.sin(arg1));
		}
		
		p.closePath();
		return p;
	}
	
	private Path heart(double r){
		double ctrp1X = 0.1 * r;
		double ctrp1Y = -0.2 * r;
		double ctrp2X = -0.04 * r;
		double ctrp2Y = 0.2 * r;
		double ctrp3X = 0.08 * r;
		double ctrp3Y = -0.4 * r;
		double ctrp4X = 0;
		double ctrp4Y = -0.34 * r;
		
		double midY = 0.4 * r;
		double midYC = 0.34 * r;
		
		double p1X = 0.5 * r;
		double p1Y = r;
		double p2X = 0.96 * r;
		double p2Y = midY;
		double p3X = 0.5 * r;
		double p3Y = midYC;
		double p4X = 0.04 * r;
		double p4Y = midY;
		
		Path p = new Path().moveTo(p1X, p1Y);
		p.curveTo(p1X + ctrp1X, p1Y + ctrp1Y,
				  p2X + ctrp2X, p2Y + ctrp2Y, p2X, p2Y);
		p.curveTo(p2X + ctrp3X, p2Y + ctrp3Y,
				  p3X + ctrp4X, p3Y + ctrp4Y, p3X, p3Y);
		p.curveTo(p3X - ctrp4X, p3Y + ctrp4Y,
				  p4X - ctrp3X, p4Y + ctrp3Y, p4X, p4Y);
		p.curveTo(p4X - ctrp2X, p4Y + ctrp2Y,
				  p1X - ctrp1X, p1Y + ctrp1Y, p1X, p1Y);
		
		return p;
	}
	
}


