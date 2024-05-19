package org.mq.optculture.sales.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Field
{
	@XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
	private String FormattedValue;

	@XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
	private String Value;

	@XmlAttribute(name="FieldName")
	private String FieldName;

	@XmlAttribute(name="Name")
	private String Name;

    public String getFormattedValue ()
    {
        return FormattedValue;
    }

    public void setFormattedValue (String FormattedValue)
    {
        this.FormattedValue = FormattedValue;
    }

    public String getValue ()
    {
        return Value;
    }

    public void setValue (String Value)
    {
        this.Value = Value;
    }

    public String getFieldName ()
    {
        return FieldName;
    }

    public void setFieldName (String FieldName)
    {
        this.FieldName = FieldName;
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
        return " [FormattedValue = "+FormattedValue+", Value = "+Value+", FieldName = "+FieldName+", Name = "+Name+"]";
    }
}
			
			