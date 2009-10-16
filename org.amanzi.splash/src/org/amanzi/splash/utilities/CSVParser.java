/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.amanzi.splash.utilities;

import java.util.ArrayList;
import java.util.List;

public class CSVParser {  

	  public static final char DEFAULT_SEP = ',';

	  /** Construct a CSVParser parser, with the default separator (`,'). */
	  public CSVParser() {
	    this(DEFAULT_SEP);
	  }

	  /** Construct a CSVParser parser with a given separator. 
	   * @param sep The single char for the separator (not a list of
	   * separator characters)
	   */
	  public CSVParser(char sep) {
	    fieldSep = sep;
	  }

	  /** The fields in the current String */
	  protected List<String> list = new ArrayList<String>();

	  /** the separator char for this parser */
	  protected char fieldSep;

	  /** parse: break the input String into fields
	   * @return java.util.Iterator containing each field 
	   * from the original as a String, in order.
	   */
	  public List<String> parse(String line)
	  {
	    StringBuffer sb = new StringBuffer();
	    list.clear();      // recycle to initial state
	    int i = 0;

	    if (line.length() == 0) {
	      list.add(line);
	      return list;
	    }

	    do {
	            sb.setLength(0);
	            if (i < line.length() && line.charAt(i) == '"')
	                i = advQuoted(line, sb, ++i);  // skip quote
	            else
	                i = advPlain(line, sb, i);
	            list.add(sb.toString());
	      i++;
	    } while (i < line.length());

	    return list;
	  }

	  /** advQuoted: quoted field; return index of next separator */
	  protected int advQuoted(String s, StringBuffer sb, int i)
	  {
	    int j;
	    int len= s.length();
	        for (j=i; j<len; j++) {
	            if (s.charAt(j) == '"' && j+1 < len) {
	                if (s.charAt(j+1) == '"') {
	                    j++; // skip escape char
	                } else if (s.charAt(j+1) == fieldSep) { //next delimeter
	                    j++; // skip end quotes
	                    break;
	                }
	            } else if (s.charAt(j) == '"' && j+1 == len) { // end quotes at end of line
	                break; //done
	      }
	      sb.append(s.charAt(j));  // regular character.
	    }
	    return j;
	  }

	  /** advPlain: unquoted field; return index of next separator */
	  protected int advPlain(String s, StringBuffer sb, int i)
	  {
	    int j;

	    j = s.indexOf(fieldSep, i); // look for separator
	        if (j == -1) {                 // none found
	            sb.append(s.substring(i));
	            return s.length();
	        } else {
	            sb.append(s.substring(i, j));
	            return j;
	        }
	    }
	}
