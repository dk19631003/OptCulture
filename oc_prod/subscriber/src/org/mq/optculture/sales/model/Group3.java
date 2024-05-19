package org.mq.optculture.sales.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Group3
{
    @XmlAttribute(name="Level")
	private String Level;

    @XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
    private GroupFooter GroupFooter;

    @XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
    private GroupHeader GroupHeader;

    @XmlElement(name="Group",namespace = "urn:crystal-reports:schemas:report-detail")
    private Group4[] Group;
    

	public GroupHeader getGroupHeader() {
		return GroupHeader;
	}

	public Group4[] getGroup() {
		return Group;
	}

	public void setGroup(Group4[] group) {
		Group = group;
	}

	public void setGroupHeader(GroupHeader groupHeader) {
		GroupHeader = groupHeader;
	}

	public String getLevel ()
    {
        return Level;
    }

    public void setLevel (String Level)
    {
        this.Level = Level;
    }

    public GroupFooter getGroupFooter ()
    {
        return GroupFooter;
    }

    public void setGroupFooter (GroupFooter GroupFooter)
    {
        this.GroupFooter = GroupFooter;
    }

    @Override
    public String toString()
    {
        return "[Level = "+Level+", GroupFooter = "+GroupFooter+"]";
    }
}
			