package org.mq.optculture.sales.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Subreport")
@XmlAccessorType(XmlAccessType.FIELD)
public class Subreport
{
	@XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
	private String ReportHeader;

	@XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
	private Details Details;

	@XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
	private String ReportFooter;

	@XmlAttribute(name="Name")
	private String Name;

    public String getReportHeader ()
    {
        return ReportHeader;
    }

    public void setReportHeader (String ReportHeader)
    {
        this.ReportHeader = ReportHeader;
    }

    public Details getDetails ()
    {
        return Details;
    }

    public void setDetails (Details Details)
    {
        this.Details = Details;
    }

    public String getReportFooter ()
    {
        return ReportFooter;
    }

    public void setReportFooter (String ReportFooter)
    {
        this.ReportFooter = ReportFooter;
    }

    public String getName ()
    {
        return Name;
    }

    public void setName (String Name)
    {
        this.Name = Name;
    }

    @Override
    public String toString()
    {
        return " [ReportHeader = "+ReportHeader+", Details = "+Details+", ReportFooter = "+ReportFooter+", Name = "+Name+"]";
    }
}
			
			