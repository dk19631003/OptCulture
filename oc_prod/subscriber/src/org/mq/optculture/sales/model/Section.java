package org.mq.optculture.sales.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "urn:crystal-reports:schemas:report-detail")
@XmlAccessorType(XmlAccessType.FIELD)
public class Section
{
	@XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
    private Field[] Field;

    @XmlAttribute(name="SectionNumber")
    private String SectionNumber;

    @XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
    private Text Text;
    
    @XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
    private Subreport Subreport;
    

   

	public Subreport getSubreport() {
		return Subreport;
	}

	public void setSubreport(Subreport subreport) {
		Subreport = subreport;
	}

	public Field[] getField ()
    {
        return Field;
    }

    public void setField (Field[] Field)
    {
        this.Field = Field;
    }

    public String getSectionNumber ()
    {
        return SectionNumber;
    }

    public void setSectionNumber (String SectionNumber)
    {
        this.SectionNumber = SectionNumber;
    }

    public Text getText ()
    {
        return Text;
    }

    public void setText (Text Text)
    {
        this.Text = Text;
    }

    @Override
    public String toString()
    {
        return " [Field = "+Field+", SectionNumber = "+SectionNumber+", Text = "+Text+"]";
    }
}
			
			