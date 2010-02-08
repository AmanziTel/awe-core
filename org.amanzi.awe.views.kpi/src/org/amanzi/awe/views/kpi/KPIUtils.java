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

package org.amanzi.awe.views.kpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Utility class
 * </p>
 * 
 * @author Pechko_E
 * @since 1.0.0
 */
public class KPIUtils {
    /**
     * Generates a ruby method on the base of given formula name, parameters and formula text. Uses
     * given indent.
     * 
     * @param formulaName formula name
     * @param params parameters
     * @param formulaText formula text
     * @param indent indent
     * @return
     */
    public static String generateRubyMethod(String formulaName, String params, String formulaText, String indent) {
        StringBuffer sb = new StringBuffer(indent);
        sb.append("def ").append(formulaName).append("(").append(params).append(")\n");
        String[] lines = formulaText.split("\n");
        for (String line : lines) {
            sb.append(indent).append(indent).append(line).append("\n");
        }
        sb.append(indent).append("end\n");
        return sb.toString();
    }

    /**
     * Inserts or updates a ruby method to match new method text. sum2(a,b) equal to sum2(b,ô)
     * 
     * @param script
     * @param methodText
     * @return
     */
    public static String insertOrUpdateRubyMethod(String script, String methodText) {
        String mName = methodText.substring(methodText.indexOf("def") + 3, methodText.indexOf("(")).trim();
        Matcher matcher = Pattern.compile("[ ]*def\\s*" + mName + "\\((.*)\\)").matcher(script);
        if (matcher.find()) {
            // String params = matcher.group(1);
            int startInd = matcher.start();
            String substr = script.substring(startInd);
            matcher = Pattern.compile("\\s*end\\n*([ ]*def|end\\s*)").matcher(substr);
            if (matcher.find()) {
                int endInd = matcher.start(1);
                StringBuffer sb = new StringBuffer(script.substring(0, startInd));
                sb.append(methodText).append(script.substring(startInd + endInd));
                return sb.toString();
            }
        }
        int endInd = script.lastIndexOf("end");
        return new StringBuffer(script.substring(0, endInd)).append(methodText).append("end").toString();
    }

    /**
     * Reads the content of text file into a new StringBuffer instance
     * 
     * @param stream input stream to read content from
     * @return new StringBuffer instance with content
     * @throws IOException
     */
    public static StringBuffer readContentToStringBuffer(InputStream stream) throws IOException {
        return readContentToStringBuffer(stream, new StringBuffer());
    }

    /**
     * Reads the content of text file into a StringBuffer instance given
     * 
     * @param stream input stream to read content from
     * @param sb StringBuffer instance to read content into
     * @return the same StringBuffer instance appended with content of the file
     * @throws IOException
     */
    public static StringBuffer readContentToStringBuffer(InputStream stream, StringBuffer sb) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb;
    }

    public static void main(String[] args) {
        // method generating tests
        String[][] tests = new String[][] { {"a+b", "a,b", "sum2"}, {"if a>b\n  a-b\nelse\n  b-a\nend", "a,b", "difference"}};
        String[] test3 = new String[] {
                "module KPIBuilder\n  def meth1(a)\n    a\n  end\nend\n",
                "module KPIBuilder\n  def sum2(a,b)\n    a\n  end\nend\n",
                "module KPIBuilder\n  def first(a,b)\n    a\n  end\n" + "  def sum2(a,b)\n    a\n  end\n"
                        + "  def last(a,b)\n    a\n  end\nend\n"};
        int formulaText = 0;
        int params = 1;
        int formulaName = 2;
        for (String[] test : tests) {
            System.out.println("[DEBUG]----------------------");// TODO delete debug info
            String methodText = KPIUtils.generateRubyMethod(test[formulaName], test[params], test[formulaText], "  ");
            System.out.println(methodText);
            // inserting method into a script test
            for (String script : test3) {
                String newScript = KPIUtils.insertOrUpdateRubyMethod(script, methodText);
                System.out.println("[DEBUG]oldScript ->\n" + script);// TODO delete debug info
                System.out.println("[DEBUG]newScript ->\n" + newScript);// TODO delete debug info

            }
        }
        // init scipt updating test
        String[] tests2 = new String[] {"load 'f1.rb'\nload 'f2.rb'"};
        // inserting method into a script test
    }
}
