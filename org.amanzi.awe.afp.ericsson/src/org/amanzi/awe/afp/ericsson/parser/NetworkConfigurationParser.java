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

package org.amanzi.awe.afp.ericsson.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.amanzi.neo.loader.core.CommonConfigData;
import org.amanzi.neo.loader.core.CountingFileInputStream;
import org.amanzi.neo.loader.core.ProgressEventImpl;
import org.amanzi.neo.loader.core.parser.AbstractCSVParser;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.apache.commons.lang.StringUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkConfigurationParser extends AbstractCSVParser<NetworkConfigurationTransferData> {
    private Collection<File> bsmFiles = new LinkedHashSet<File>();
    private Pattern bscPattern = Pattern.compile("(^.*)(_BSM.log*$)", Pattern.CASE_INSENSITIVE);

    private NetworkConfigurationTransferData initdata;
    private String bsc;

    public NetworkConfigurationParser() {
        super();
        delimeters = ' ';
    }

    @Override
    public void init(CommonConfigData properties, ISaver<NetworkConfigurationTransferData> saver) {
        super.init(properties, saver);
        Collection<File> bsmList = (Collection<File>)properties.getAdditionalProperties().get("BSM_FILES");
        bsmFiles.clear();
        if (bsmList != null) {
            bsmFiles.addAll(bsmList);
        }
    }

    @Override
    protected List<org.amanzi.neo.loader.core.parser.CommonFilesParser.FileElement> getElementList() {
        List<FileElement> result = super.getElementList();
        String descr = getDescriptionFormat();
        for (File file : bsmFiles) {
            result.add(new FileElement(file, descr));
        }
        return result;
    }

    @Override
    protected NetworkConfigurationTransferData createTransferData(FileElement element, String[] header, String[] nextLine, long line) {
        NetworkConfigurationTransferData data = createEmptyTransferData();
        data.setLine(line);
        data.setFileName(element.getFile().getName());
        for (int i = 0; i < header.length; i++) {

            String value = nextLine[i];
            if ("NULL".equalsIgnoreCase(value)) {
                continue;
            }
            data.put(header[i], value);
        }
        data.setHeaders(header);
        data.setValuesData(nextLine);
        return data;
    }

    @Override
    protected boolean parseElement(org.amanzi.neo.loader.core.parser.CommonFilesParser.FileElement element) {
        if (isCNAFile(element)) {
            return super.parseElement(element);
        } else {
            try {

                checkBsc(element);
                if (StringUtils.isEmpty(bsc)) {
                    return false;
                }
                BSA_MODE mode = BSA_MODE.NONE;
                BufferedReader reader = null;
                try {
                    CountingFileInputStream is = new CountingFileInputStream(element.getFile());
                    reader = new BufferedReader(new InputStreamReader(is, getCharacterSet()));
                    int persentageOld = 0;
                    long line = 0;
                    String nextLine;
                    while ((nextLine = reader.readLine()) != null) {
                        try {
                            line++;
                            nextLine = nextLine.trim();

                            switch (mode) {
                            case NONE:
                                if (nextLine.equalsIgnoreCase("<rxmop:moty=rxotg;")) {
                                    mode = BSA_MODE.TG;
                                    continue;
                                } else if (nextLine.equalsIgnoreCase("<rxmop:moty=rxotrx;")) {
                                    mode = BSA_MODE.TRX;
                                    continue;
                                } else {
                                    continue;
                                }
                            case TG:
                                if (nextLine.equalsIgnoreCase("END")) {
                                    mode = BSA_MODE.NONE;
                                    continue;
                                } else if (nextLine.startsWith("MO ")) {
                                    mode = BSA_MODE.TG_FIND;
                                    continue;
                                } else {
                                    continue;
                                }
                            case TG_FIND:

                                mode = BSA_MODE.TG;

                                break;
                            case TRX_FIND:
                                mode = BSA_MODE.TRX;
                                break;
                            case TRX:
                                if (nextLine.equalsIgnoreCase("END")) {
                                    mode = BSA_MODE.NONE;
                                    continue;
                                } else if (nextLine.startsWith("MO ")) {
                                    mode = BSA_MODE.TRX_FIND;
                                    continue;
                                } else {
                                    continue;
                                }
                            default:
                                continue;
                            }
                            NetworkConfigurationTransferData data = createEmptyTransferData();
                            data.setLine(line);
                            data.setFileName(element.getFile().getName());
                            data.setBsc(bsc);
                            data.setMode(mode);
                            switch (mode) {
                            case TG: {
                                StringTokenizer st = new StringTokenizer(nextLine, " ");
                                if (st.countTokens() < 4) {
                                    error(String.format("Line %s: incorrect TG information structure", line));
                                    continue;
                                }
                                String tg = st.nextToken();
                                st.nextToken();
                                st.nextToken();
                                String fhop = st.nextToken();
                                data.setTG(tg);
                                data.setFhop(fhop);
                                break;
                            }
                            case TRX: {
                                StringTokenizer st = new StringTokenizer(nextLine, " ");
                                if (st.countTokens() < 3) {
                                    error(String.format("Line %s: incorrect TRX information structure", line));
                                    continue;
                                }
                                String tg = st.nextToken();
                                String cell= st.nextToken();
                                String group= st.nextToken();
                                data.setTG(tg);
                                data.setCell(cell);
                                data.setGroup(Integer.valueOf(group));
                                break;
                            }

                            default:
                                break;
                            }
                            getSaver().save(data);
                        } finally {
                            int persentage = is.percentage();
                            if (persentage - persentageOld > PERCENTAGE_FIRE) {
                                persentageOld = persentage;
                                if (fireSubProgressEvent(element, new ProgressEventImpl(String.format(getDescriptionFormat(), element.getFile().getName()), persentage / 100d))) {
                                    return true;
                                }
                            }
                        }
                    }

                } catch (IOException e) {
                    exception(e);
                } finally {
                    closeStream(reader);
                }
            } finally {
                bsc = null;
            }
            return false;
        }
    }

    /**
     * @return
     */
    private String getCharacterSet() {
        return Charset.defaultCharset().name();
    }

    @Override
    protected NetworkConfigurationTransferData getStartupElement(FileElement element) {
        NetworkConfigurationTransferData result = super.getStartupElement(element);
        NetworkConfigurationFileTypes type = isCNAFile(element) ? NetworkConfigurationFileTypes.CNA : NetworkConfigurationFileTypes.BSM;
        result.setType(type);
        if (type == NetworkConfigurationFileTypes.BSM) {
            checkBsc(element);
            result.setBsc(bsc);
        }
        return result;
    }

    /**
     * @param element
     * @param result
     */
    protected void checkBsc(FileElement element) {
        String fileName = element.getFile().getName();
        Matcher matcher = bscPattern.matcher(fileName);
        if (!matcher.find(0)) {
            bsc = null;
            error(String.format("Incorrect BSA file name: %s", fileName));

        } else {
            bsc = matcher.group(1);
        }
    }

    private boolean isCNAFile(org.amanzi.neo.loader.core.parser.CommonFilesParser.FileElement element) {
        return !bsmFiles.contains(element.getFile());
    }

    @Override
    protected NetworkConfigurationTransferData createEmptyTransferData() {
        return new NetworkConfigurationTransferData();
    }

    public static void main(String[] args) {
        StringTokenizer st = new StringTokenizer("d d      d", " ");
        System.out.println(st.countTokens());
        while (st.hasMoreTokens()) {
            System.out.println(st.nextToken());
        }
    }

    public enum BSA_MODE {
        NONE, TG, TRX, TG_FIND, TRX_FIND;
    }
}
