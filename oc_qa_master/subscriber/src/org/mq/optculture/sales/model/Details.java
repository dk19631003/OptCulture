package org.mq.optculture.sales.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Details
{
	@XmlAttribute(name="Level")
	private String Level;

	@XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
    private Section Section;

    public String getLevel ()
    {
        return Level;
    }

    public void setLevel (String Level)
    {
        this.Level = Level;
    }

    public Section getSection ()
    {
        return Section;
    }

    public void setSection (Section Section)
    {
        this.Section = Section;
    }

    @Override
    public String toString()
    {
        return " [Level = "+Level+", Section = "+Section+"]";
    }
}