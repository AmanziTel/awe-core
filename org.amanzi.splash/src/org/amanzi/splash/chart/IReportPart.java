package org.amanzi.splash.chart;


/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author user
 * @since 1.0.0
 */
public interface IReportPart {
    public String getScript();
    public int getIndex();
    public void setIndex(int index);
    public ReportPartType getType();
}
