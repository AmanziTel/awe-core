package org.amanzi.splash.chart;


/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Pechko_E
 * @since 1.0.0
 */
public enum ReportPartType {
    TEXT("text"), CHART("chart"), IMAGE("image"), TABLE("table");
    private String text;

    /**
     * @param text
     */
    private ReportPartType(String text) {
        this.text = text;
    }

    /**
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    public static String getTypesAsRegex() {
        StringBuffer sb=new StringBuffer();
        ReportPartType[] vals = values();
        for (int i=0;i<vals.length;i++){
            sb.append(vals[i].getText()).append(i<vals.length-1?"|":"");
        }
        return sb.toString();

    }

}
