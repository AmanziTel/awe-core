/*
 * @(#)TextObjectEntry.java   09/03/2003
 *
 * Copyright (c) 1998-2003 Emmanuel PUYBARET / eTeks <info@eteks.com>. All Rights Reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Visit eTeks web site for up-to-date versions of this file and other
 * Java tools and tutorials : http://www.eteks.com/
 */
/**
 * Data used by lists and combo boxes of format panels, associating a displayed
 * text to an object.
 *
 * @version 1.1
 * @author  Emmanuel Puybaret
 * @since   Jeks 1.1
 */
package com.eteks.openjeks.format;

class TextObjectEntry
{
  private String displayedText;
  private Object value;

  public TextObjectEntry (String displayedText, Object value)
  {
    this.displayedText = displayedText;
    this.value         = value;
  }

  public String getText ()
  {
    return displayedText;
  }

  public Object getValue ()
  {
    return value;
  }

  public String toString ()
  {
    return displayedText;
  }

  public boolean equals (Object obj)
  {
    return    obj instanceof TextObjectEntry
           && ((TextObjectEntry)obj).value.equals(this.value);
  }
}