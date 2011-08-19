package org.amanzi.awe.cassidian.structure;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public interface IXmlTag {
    public final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss,SSS Z");
    public static final Calendar calendar = Calendar.getInstance();

    public String getType();

    public void setValueByTagType(String tagName, Object value);

    public Object getValueByTagType(String tagName);

}
