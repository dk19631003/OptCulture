package org.mq.marketer.campaign.general;

import java.awt.Color;

import org.apache.commons.lang.StringUtils;

public class ChartColors {
	  //chart1 colors
    public static Color COLOR_1 = new Color(0x91D8F7);
    public static Color COLOR_2 = new Color(0x4796D0);
    public static Color COLOR_3 = new Color(0x2660AB);
    public static Color COLOR_4 = new Color(0x333F71);
    public static Color COLOR_5 = new Color(0x1E5E8A);
    public static Color COLOR_6 = new Color(0x3E4095);
    public static Color COLOR_7 = new Color(0x399ABF);
    public static Color COLOR_8 = new Color(0x3E80A9);
    public static Color COLOR_9 = new Color(0x65B0C8);
    public static Color COLOR_10 = new Color(0x225672);
    
    //chart2 colors
    public static Color COLOR_11 = new Color(0xFED8A3);
    public static Color COLOR_12 = new Color(0xCE7F39);
    public static Color COLOR_13 = new Color(0xAC6238);
    public static Color COLOR_14 = new Color(0xAF3D36);
    public static Color COLOR_15 = new Color(0xF16436);
    public static Color COLOR_16 = new Color(0x6B2E2B);
    public static Color COLOR_17 = new Color(0xB5845D);
    public static Color COLOR_18 = new Color(0x916946);
    public static Color COLOR_19 = new Color(0xF79843);
    public static Color COLOR_20 = new Color(0xFCB527);
    
    //chart3 colors
    public static Color COLOR_21 = new Color(0xA8518A);
    public static Color COLOR_22 = new Color(0xCF3692);
    public static Color COLOR_23 = new Color(0xE775AD);
    public static Color COLOR_24 = new Color(0x815AA4);
    public static Color COLOR_25 = new Color(0x6E4D8B);
    public static Color COLOR_26 = new Color(0xAF90BD);
    public static Color COLOR_27 = new Color(0x873261);
    public static Color COLOR_28 = new Color(0x9A5AA2);
    public static Color COLOR_29 = new Color(0x483063);
    public static Color COLOR_30 = new Color(0x5C3977);
    
    //background
    public static Color COLOR_31 = new Color(0xFFFFFF);
   
     
    public static String toHtmlColor(Color color) {
        return "#" + toHexColor(color);
    }
 
    public static String toHexColor(Color color) {
        return StringUtils.leftPad(Integer.toHexString(color.getRGB() & 0xFFFFFF), 6, '0');
    }
}
