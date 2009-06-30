package com.eteks.openjeks.format;

import java.text.Format;
import java.text.ParsePosition;
import java.text.FieldPosition;

/**
 * Number or Date format. This class encapsulates a <code>java.text.Format</code> with a
 * Jeks type of format.
 *
 * @version 1.1
 * @author  Emmanuel Puybaret
 * @since   Jeks 1.1
 */
public class JeksFormat extends Format
{
  private Format format;
  private String type;

  private JeksFormat (Format format, String type)
  {
    this.format = format;
    this.type   = type;
  }

  public Object parseObject (String source, ParsePosition pos)
  {
    return format.parseObject(source, pos);
  }

  public StringBuffer format (Object obj, StringBuffer toAppendTo, FieldPosition pos)
  {
    return format.format (obj, toAppendTo, pos);
  }

  public boolean equals (Object obj)
  {
    return    obj instanceof JeksFormat
           && ((JeksFormat)obj).type.equals(this.type);
  }
/*
  private class StandardFormat extends Format
  {
    public Object parseObject (String source, ParsePosition pos)
    {
      return format.parseObject(source, pos);
    }

    public StringBuffer format (Object obj, StringBuffer toAppendTo, FieldPosition pos)
    {
      return format.format (obj, toAppendTo, pos);
    }
  }

  public final static JeksFormat STANDARD_FORMAT = new JeksFormat ();
*/
}