package org.mq.optculture.sales.model;

public class ReportFooter
{
    private Section Section;

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
        return " [Section = "+Section+"]";
    }
}