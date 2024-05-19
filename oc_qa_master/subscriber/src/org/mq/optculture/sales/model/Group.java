package org.mq.optculture.sales.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Group")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "level")
public class Group
{
    @XmlAttribute(name="Level")
	private String Level;

    @XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
    private GroupFooter GroupFooter;

    @XmlElement(namespace = "urn:crystal-reports:schemas:report-detail")
    private GroupHeader GroupHeader;

    @XmlElement(name="Group", namespace = "urn:crystal-reports:schemas:report-detail")
    private Group2 Group2;

   
	
    @XmlElement(name="Group",namespace = "urn:crystal-reports:schemas:report-detail")
    private Group[] Group;
	

	public Group[] getGroup() {
		return Group;
	}

	public void setGroup(Group[] group) {
		Group = group;
	}

	public Group2 getGroup2() {
		return Group2;
	}

	public void setGroup2(Group2 group2) {
		Group2 = group2;
	}

	

	public GroupHeader getGroupHeader() {
		return GroupHeader;
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
			