package org.mq.marketer.campaign.general;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.controller.program.ProgramEnum;
import org.zkforge.canvas.Canvas;
import org.zkforge.canvas.Drawable;
import org.zkforge.canvas.Path;
import org.zkforge.canvas.Shape;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

/**
 * 
 */

public class ProgramUtility {
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public static final String LINE_SELECTED_COLOR="#FF0000";
	public static final String LINE_DRAW_COLOR="#116CAE";
	
	public static final String SWITCH_LINES_USED="swith_lines_used";
	
	
	/**
	 * Validates and Add the Windows Ids
	 * @param lineFromWin
	 * @param lineToWin
	 * @return
	 */
	public static List<String> validateAndAddWindowIds(Window lineFromWin, Window lineToWin) {
		
		if(lineFromWin==null || lineToWin==null || (lineToWin==lineFromWin)) return null;
		
		Label fromNextLbl = (Label)lineFromWin.getFellow("nextCompLblId");
		Label fromPrevLbl = (Label)lineFromWin.getFellow("prevCompLblId");
		Label toNextLbl = (Label)lineToWin.getFellow("nextCompLblId");
		Label toPrevLbl = (Label)lineToWin.getFellow("prevCompLblId");
		
		logger.info("FromNextLabel="+fromNextLbl.getValue());
		logger.info("FromPrevLabel="+fromPrevLbl.getValue());
		logger.info("ToNextLabel="+toNextLbl.getValue());
		logger.info("ToPrevLabel="+toPrevLbl.getValue());
		
		if(fromNextLbl.getValue().contains(lineToWin.getId()) || 
			fromPrevLbl.getValue().contains(lineToWin.getId()) ||
			toPrevLbl.getValue().contains(lineFromWin.getId()) || 
			toNextLbl.getValue().contains(lineFromWin.getId())) {

			logger.info("Alread line Exists....");
			return null;
		}
		
		//TODO need to check for the Max allowed lines
		
		String fromWinEnumName = lineFromWin.getId();
			fromWinEnumName = fromWinEnumName.substring(0,fromWinEnumName.indexOf('-'));
			
		String toWinEnumName = lineToWin.getId();
			toWinEnumName = toWinEnumName.substring(0,toWinEnumName.indexOf('-'));
		
		ProgramEnum fromEnum = ProgramEnum.valueOf(fromWinEnumName);
		ProgramEnum toEnum = ProgramEnum.valueOf(toWinEnumName);
		
		logger.info("From Name="+	fromEnum.name()+" : "+fromEnum.getNext_size());
		logger.info("To Name="+	toEnum.name()+" : "+toEnum.getPrev_size());

		logger.info("fromNextLbl Size = "+ countOccurrences(fromNextLbl.getValue(), ','));
		logger.info("fromPrevLbl Size = "+ countOccurrences(fromPrevLbl.getValue(), ','));
		logger.info("toNextLbl Size = "+	countOccurrences(toNextLbl.getValue(), ','));
		logger.info("toPrevLbl Size = "+	countOccurrences(toPrevLbl.getValue(), ','));
		
		if(countOccurrences(fromNextLbl.getValue(), ',') >= fromEnum.getNext_size() ||
		   countOccurrences(toPrevLbl.getValue(), ',') >= toEnum.getPrev_size()) {
			
			logger.info("Line limit reached...");
			return null;
		}
		
	
/*		fromNextLbl.setValue(fromNextLbl.getValue() + lineToWin.getId() + ",");
		toPrevLbl.setValue(toPrevLbl.getValue() + lineFromWin.getId() + ",");
*/		

		List<String> retList= new ArrayList<String>();
		
		//************  Testing Code ********************
		String tempLineToId = getAvailableId(lineToWin);
		String tempLineFromId = getAvailableId(lineFromWin);

		fromNextLbl.setValue(fromNextLbl.getValue() + tempLineToId + ",");
		toPrevLbl.setValue(toPrevLbl.getValue() + tempLineFromId + ",");

		logger.info("----- NextLabel="+fromNextLbl.getValue());
		logger.info("----- PrevLabel="+toPrevLbl.getValue());
	
		retList.add(tempLineFromId);
		retList.add(tempLineToId);
		
		//***************************************************
		
		return retList;
		
		//drawLineForWindws(lineFromWin, lineToWin);
		
	} // validateAndAddWindowIds
	

	/**
	 * Get the available window Ids (For Switch_ type of windows)
	 * @param win
	 * @return
	 */
	public static String getAvailableId(Window win) {
		
		String winIdStr = win.getId();
		if(!winIdStr.startsWith("SWITCH_"))	return winIdStr+"0";
		
		String linesUsed = (String)win.getAttribute(SWITCH_LINES_USED);
		if(linesUsed==null || linesUsed.trim().length()==0) {
			linesUsed="";
		}
		

//		win.setAttribute(SWITCH_LINES_USED, linesUsed);
		
/*		Label nextLbl = (Label)win.getFellow("nextCompLblId");
		Label prevLbl = (Label)win.getFellow("prevCompLblId");
		String allLinesStr = nextLbl.getValue()+prevLbl.getValue();
*/		
		boolean checkFlags[]={false,false,false,false};
		
		
		String idsArr[] = linesUsed.split(",");
		
		for (String tempId : idsArr) {
			logger.info(">>> tempId="+tempId);
			if(tempId.trim().length()==0) continue;
			
			int usedVal = Integer.parseInt(tempId);
			logger.info("Used Value is : "+usedVal);
			if(usedVal<0 || usedVal>3) {
				logger.info("Used Value is exceded: "+usedVal);
				return null;
			}
			checkFlags[ usedVal ]=true;
		} // for

		for (int i = 0; i < checkFlags.length; i++) {
			if(checkFlags[i]==false) {
				
				win.setAttribute(SWITCH_LINES_USED, linesUsed+i+",");
				
				logger.info(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+winIdStr+i +" : And Attribute : "+linesUsed+i+",");
				return winIdStr+i;
			}
		} // for 

		logger.info("Error Unable to get the AvailableId for: "+win.getId());
		return null;
	} //

	
	public static void removeUsedLineAttr(Window win, Label fromLbl, String winId) {
		
		String winIdStr = win.getId();
		logger.info("&&&&&&&&&&&& input Window type :"+winIdStr+" :: "+winId);
		if(!winIdStr.startsWith("SWITCH_"))	return;
		
		logger.info("&&&&&&&&&&&& in Remove winId   :"+winId);
		
		String linesUsed = (String)win.getAttribute(SWITCH_LINES_USED);
		
		if(linesUsed==null || linesUsed.trim().length()==0) {
			return;
		}
		logger.info("&&&&&&&&&&&& in RemoveUsedLine :"+winId);
		
		char lastChar = winId.charAt(winId.length()-1);
		
		if(!Character.isDigit(lastChar)) {
			return;
		}
		
		logger.info("LinesUsed = "+linesUsed +"  Last Char="+lastChar);
		
		linesUsed = linesUsed.replace(lastChar+",", "");
		win.setAttribute(SWITCH_LINES_USED, linesUsed);
		
	} //
	
	
	public static String oldgetAvailableId(Window win) {
		
		String winIdStr = win.getId();
		if(!winIdStr.startsWith("SWITCH_"))	return winIdStr;
		
		Label nextLbl = (Label)win.getFellow("nextCompLblId");
		Label prevLbl = (Label)win.getFellow("prevCompLblId");
		
		boolean checkFlags[]={false,false,false,false};
		String allLinesStr = nextLbl.getValue()+prevLbl.getValue();
		String idsArr[] = allLinesStr.split(",");
		
		for (String tempId : idsArr) {
			logger.info(">>> tempId="+tempId);
			if(tempId.trim().length()==0) continue;
			char lastChar = (tempId.charAt(tempId.length()-1));
			
//			Character.isDigit()
			checkFlags[ (tempId.charAt(tempId.length()-1)) ]=true;
		} // for

		for (int i = 0; i < checkFlags.length; i++) {
			if(checkFlags[i]==false) {
				logger.info(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+winIdStr+i);
				return winIdStr+i;
			}
		} // for 

		return null;
	} //

	
	
	/**
	 * Get line shape for given windows
	 * @param lineFromWin
	 * @param lineToWin
	 * @return
	 */
	public static Shape getLineForWindws(Window lineFromWin, Window lineToWin) {
		
		if(lineFromWin==null || lineToWin==null || (lineToWin==lineFromWin)) return null;
		
		int[] fromWinPoints = ProgramUtility.getWindowPoints(lineFromWin);
		int[] toWinPoints = ProgramUtility.getWindowPoints(lineToWin);
		
		int[] tempFromPoints = {(fromWinPoints[0]+fromWinPoints[2])/2, fromWinPoints[1],
								(fromWinPoints[0]+fromWinPoints[2])/2, fromWinPoints[3],
								fromWinPoints[0], (fromWinPoints[1]+fromWinPoints[3])/2,
								fromWinPoints[2], (fromWinPoints[1]+fromWinPoints[3])/2};
		
		int[] tempToPoints = {(toWinPoints[0]+toWinPoints[2])/2, toWinPoints[1],
				(toWinPoints[0]+toWinPoints[2])/2, toWinPoints[3],
				toWinPoints[0], (toWinPoints[1]+toWinPoints[3])/2,
				toWinPoints[2], (toWinPoints[1]+toWinPoints[3])/2};

		//int[] shortPoints = findShotDistance(tempFromPoints, tempToPoints);
		int[] shInd = ProgramUtility.findShotDistance(tempFromPoints, tempToPoints);
		
		//logger.info("Data-->"+fromWinPoints[0]+","+fromWinPoints[1]+"  :: "+fromWinPoints[2]+","+fromWinPoints[3]);
		//logger.info("Data-->"+toWinPoints[0]+","+toWinPoints[1]+"  :: "+toWinPoints[2]+","+toWinPoints[3]);
		
//		Path p = new Path().moveTo(fromWinPoints[0], fromWinPoints[1]).lineTo(toWinPoints[0], toWinPoints[1]);
//		Path p = new Path().moveTo(shortPoints[0], shortPoints[1]).lineTo(shortPoints[2], shortPoints[3]);
	
		int x1 = tempFromPoints[shInd[0]];
		int y1 = tempFromPoints[shInd[0]+1];
		int x2 = tempToPoints[shInd[1]];
		int y2 = tempToPoints[shInd[1]+1];
		
		/*
		 *  shInd[0] is Starting point index
		 *  shInd[1] is Ending point index
		 *  
		 *  if(shInd[0] is 0/2 Starting point is TOP / BOTTOM respectively
		 *  if(shInd[0] is 4/6 Starting point is LEFT / RIGHT respectively
		 *  if(shInd[1] is 0/2 Ending   point is TOP / BOTTOM respectively
		 *  if(shInd[1] is 4/6 Ending   point is LEFT / RIGHT respectively
		 *    
		 *     ___0____
		 *    |        |
		 *  4 |  Comp  | 6 
		 *    |________|
		 *        2
		 */
		
		logger.info("shInd[0]="+shInd[0]+"  :: shInd[1]"+shInd[1]);
		
//		Path p = new Path().moveTo(x1,y1).lineTo(x1, y1-4).lineTo(x1+8, y1-4).lineTo(x1+8, y1+4)
//						   .lineTo(x1, y1+4).lineTo(x1, y1).moveTo(x1+8, y1).lineTo(x2, y2);

		Path p;
		
		int xArrow = (x2>x1) ? -1 : 1;
		int yArrow = (y2>y1) ? -1 : 1;
		
		if(shInd[0]<=2 && shInd[1]>=4) { // TOP/BOTTOM starting point and LEFT/RIGHT ending point
			
			p = new Path().moveTo(x1,y1).lineTo(x1-3, y1).lineTo(x1-3, y1+(-6*yArrow)).lineTo(x1+3, y1+(-6*yArrow)).
 			lineTo(x1+3, y1).lineTo(x1,y1).moveTo(x1, y1+(-6*yArrow))
						
						.lineTo(x1, y2).lineTo(x2, y2)
						.lineTo(x2+(5*xArrow), y2-5).moveTo(x2, y2).lineTo(x2+(5*xArrow), y2+5);
		}
		else if(shInd[0]>=4 && shInd[1]<=2) { // LEFT/RIGHT starting point and  TOP/BOTTOM ending point
			
			p = new Path().moveTo(x1,y1).lineTo(x1, y1-3).lineTo(x1+(-6*xArrow), y1-3).lineTo(x1+(-6*xArrow), y1+3).
 			lineTo(x1, y1+3).lineTo(x1,y1).moveTo(x1+(-6*xArrow), y1)
			
			.lineTo(x2, y1).lineTo(x2, y2)
			.lineTo(x2-5, y2+(5*yArrow)).moveTo(x2, y2).lineTo(x2+5, y2+(5*yArrow));
		}
		else if(shInd[0]<=2 && shInd[1]<=2) { // TOP/BOTTOM starting point and TOP/BOTTOM ending point
			
			p = new Path().moveTo(x1,y1).lineTo(x1-3, y1).lineTo(x1-3, y1+(-6*yArrow)).lineTo(x1+3, y1+(-6*yArrow)).
 			lineTo(x1+3, y1).lineTo(x1,y1).moveTo(x1, y1+(-6*yArrow))
			
			.lineTo(x1, (y1+y2)/2).lineTo(x2, (y1+y2)/2).lineTo(x2, y2)
			.lineTo(x2-5, y2+(5*yArrow)).moveTo(x2, y2).lineTo(x2+5, y2+(5*yArrow));
		}
		else if(shInd[0]>=4 && shInd[1]>=4) { // LEFT/RIGHT starting point and  LEFT/RIGHT ending point
			
			p = new Path().moveTo(x1,y1).lineTo(x1, y1-3).lineTo(x1+(-6*xArrow), y1-3).lineTo(x1+(-6*xArrow), y1+3).
 			lineTo(x1, y1+3).lineTo(x1,y1).moveTo(x1+(-6*xArrow), y1)

			.lineTo((x1+x2)/2, y1).lineTo((x1+x2)/2, y2).lineTo(x2, y2)
			.lineTo(x2+(5*xArrow), y2-5).moveTo(x2, y2).lineTo(x2+(5*xArrow), y2+5);
		}
		else {
		
			p = new Path().moveTo(x1,y1).lineTo(x2, y2);
		}
		   
		Shape s = p;
		
		// sets shadow effect
/*			s.setShadowOffset(3, 3);
			s.setShadowColor("#808080");
			s.setShadowBlur(2);
*/			
		setDrawingState(s, LINE_DRAW_COLOR, 1);
		s.setLineWidth(2); // default for shapes to look better
		
		return s;
		
/*		centerDiv.add(s);
		lineWindows.put(s, lineFromWin.getId()+":"+lineToWin.getId());
*/		
	} // drawLineForWindows
	
	
	
	public static void setDrawingState(Drawable drawable, String storkeColor, double alphaVal) {
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
		
		//String storkeColor = LINE_DRAW_COLOR;
		String fillColor = "#000000"; 
		
		//double alpha = alphaSlider.getCurpos() / 100.0;
		//bug #3006313: getCurpos() does not work
		
		drawable.setDrawingType(drawingType);
		drawable.setStrokeStyle(storkeColor);
		drawable.setFillStyle(fillColor);
		drawable.setAlpha(alphaVal);
		
	}
	
	
	/**
	 * Searches for the given character
	 * @param inputStr
	 * @param searchChar
	 * @return
	 */
	public static int countOccurrences(String inputStr, char searchChar) {
		int count = 0;
		for (int i=0; i < inputStr.length(); i++) {
			if (inputStr.charAt(i) == searchChar) {
				count++;
			}
		}
		return count;
	} // countOccurrences

	/**
	 * Get the coordinates of the given window
	 * @param win
	 * @return
	 */
	public static int[] getWindowPoints(Window win) {
		int[] ret=new int[4];
		
		try {
			ret[0] = Integer.parseInt(win.getLeft().substring(0, win.getLeft().indexOf("px")));
			ret[1] = Integer.parseInt(win.getTop().substring(0, win.getTop().indexOf("px")));
			
			if(win.getId().startsWith("EVENT_")) {
				ret[2]= ret[0] + ProgramEnum.EVENT_CUST_ACTIVATED.getWidth();
				ret[3]= ret[1] + ProgramEnum.EVENT_CUST_ACTIVATED.getHeight();
			}
			else if(win.getId().startsWith("ACTIVITY_")) {
				ret[2]= ret[0] +ProgramEnum.ACTIVITY_SEND_EMAIL.getWidth();
				ret[3]= ret[1] +ProgramEnum.ACTIVITY_SEND_EMAIL.getHeight();
			}
			else if(win.getId().startsWith("SWITCH_")) {
				ret[2]= ret[0] +ProgramEnum.SWITCH_ALLOCATION.getWidth();
				ret[3]= ret[1] +ProgramEnum.SWITCH_ALLOCATION.getHeight();
			}
			
			return ret;
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		
	} // getWindowPoints
	
	
	/**
	 * Returns the shortest distance indexes as an int array
	 * @param fromPoints
	 * @param toPoints
	 * @return
	 */
	public static int[] findShotDistance(int[] fromPoints, int[] toPoints) {
		

		int dist = Integer.MAX_VALUE;
		int fromInd=-1;
		int toInd=-1;
		int xdif, ydif;
		
		int tempDist;
		
		for (int i = 0; i < fromPoints.length; i+=2) {
			for (int j = 0; j < toPoints.length; j+=2) {
				
				xdif = (toPoints[j] - fromPoints[i]);
				ydif = (toPoints[j+1] - fromPoints[i+1]);
				
				tempDist = (xdif * xdif) + (ydif* ydif);
				
				if(tempDist < dist) {
					dist = tempDist;
					logger.info("dist="+dist);
					fromInd=i;
					toInd=j;
				}
			} // for j
		} // for i
		
		int[] retIndexs = {fromInd, toInd};
		return retIndexs;
	}
	
	
	/**
	 * Get data values
	 * @param event
	 * @return
	 */
	public static double[] getDataValues(Event event){
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
	} // getDataValues
	
	
	/**
	 * Creates a ngon shape
	 * @param r
	 * @param n
	 * @return
	 */
	public static Path ngon(double r, int n){
		Path p = new Path().moveTo(r, 0);
		for(int i=1; i<n+1; i++){
			double arg = Math.PI * (1.5 + (2.0*i)/n);
			p.lineTo(r + r * Math.cos(arg), r + r * Math.sin(arg));
		}
		p.closePath();
		return p;
	}
	
	/**
	 * Creates an star shape
	 * @param r
	 * @param n
	 * @param theta
	 * @return
	 */
	public static Path nstar(double r, int n, double theta){
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
	} // nstar
	
	
	/**
	 * Creates and heart shape.
	 * @param r
	 * @return
	 */
	public static Path heart(double r) {
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
	
	
	
	public static Window getWindowFromCenterDiv(String actualWinId, Canvas centerDiv) {
		
		logger.info("actualWinId="+actualWinId);
		if(actualWinId==null || actualWinId.trim().length()==0) return null;
		
		String winId = (actualWinId.endsWith("w")) ? actualWinId : actualWinId.substring(0, actualWinId.length()-1);
		logger.info("winId="+winId);

		return (Window)centerDiv.getFellowIfAny(winId);
	}
	

} // class
