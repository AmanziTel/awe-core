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

package org.amanzi.neo.geoptima.loader.core.parser.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.amanzi.neo.geoptima.loader.core.IRemoteSupportConfiguration;
import org.amanzi.neo.loader.core.IMappedStringData;
import org.amanzi.neo.loader.core.parser.impl.internal.AbstractParser;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class WebDataParser extends AbstractParser<IRemoteSupportConfiguration, IMappedStringData> {
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final String URL_PATTERN_FORMAT = "%s&numEvents=10000&start=%s&end=%s";
    private static final String FIRST_URL_PATTERN = "%s/event/getEventsCount?dataset=%s&start=%s&end=%s";
    private URL url;
    private URLConnection urlConn;

    @Override
    public File getLastParsedFile() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getLastParsedLineNumber() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected File getFileFromConfiguration(final IRemoteSupportConfiguration configuration) {
        // File file=File.createTempFile(System.getProperty("user.home") + "/.amanzi/temp",
        // "web_data_temp.csv");;
        return null;
    }

    @Override
    public void init(final IRemoteSupportConfiguration configuration) {
        super.init(configuration);
        String startTime = getDateString(getConfiguration().getStartTime()) + "T"
                + TIME_FORMAT.format(getConfiguration().getStartTime().getTime());
        String endTime = getDateString(getConfiguration().getEndTime()) + "T"
                + TIME_FORMAT.format(getConfiguration().getEndTime().getTime());
        String urlString = String.format(FIRST_URL_PATTERN, getConfiguration().getUrl(), getConfiguration().getImsi().trim()
                .substring(0, 5), startTime, endTime);
        System.out.println(urlString + URL_PATTERN_FORMAT);
        try {
            url = new URL(
                    "http://explorer.amanzitel.com/geoptima/event/extractNew.csv?dataset=24001&cdate=2012-11-10T16:00:25&cimei=358506046830281&cimsi=240016008967428");

            urlConn = url.openConnection();
            urlConn.setConnectTimeout(60000);
            urlConn.setReadTimeout(60000);

            InputStream inputStream = urlConn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                System.out.println(reader.readLine());
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected IMappedStringData parseNextElement() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    String getDateString(final Calendar calendar) {
        int calendarMonth = calendar.get(Calendar.MONTH);
        return calendar.get(Calendar.YEAR) + "-" + (calendarMonth < 10 ? "0" + calendarMonth : calendarMonth) + "-"
                + calendar.get(Calendar.DATE);
    }
}
