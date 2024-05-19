package org.mq.optculture.sales.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement(name="CrystalReport",namespace = "urn:crystal-reports:schemas:report-detail") 
@XmlAccessorType(XmlAccessType.FIELD) 
public class CrystalReport
{
	@XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
	private ReportHeader ReportHeader;

	
	
	@XmlAttribute(name="xmlns")
    private String xmlns;

	@XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
    private ReportFooter ReportFooter;

	
	
	@XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
    private Group[] Group;  

   

	public Group[] getGroup() {
		return Group;
	}

	public void setGroup(Group[] group) {
		Group = group;
	}

	public ReportHeader getReportHeader ()
    {
        return ReportHeader;
    }

    public void setReportHeader (ReportHeader ReportHeader)
    {
        this.ReportHeader = ReportHeader;
    }

   
    public String getXmlns ()
    {
        return xmlns;
    }

    public void setXmlns (String xmlns)
    {
        this.xmlns = xmlns;
    }

    public ReportFooter getReportFooter ()
    {
        return ReportFooter;
    }

    public void setReportFooter (ReportFooter ReportFooter)
    {
        this.ReportFooter = ReportFooter;
    }
    
    
   
    @Override
    public String toString()
    {
        return "[ReportHeader = "+ReportHeader+", Group = "+Group+", xmlns = "+xmlns+", ReportFooter = "+ReportFooter+"]";
    }

	
}


			
			