package org.mq.optculture.sales.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Text
{
	 @XmlElement(name="TextValue")
	 private String TextValue;

	 @XmlAttribute(name="Name")
	 private String Name;

    public String getTextValue ()
    {
        return TextValue;
    }

    public void setTextValue (String TextValue)
    {
        this.TextValue = TextValue;
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
        return " [TextValue = "+TextValue+", Name = "+Name+"]";
    }
}
