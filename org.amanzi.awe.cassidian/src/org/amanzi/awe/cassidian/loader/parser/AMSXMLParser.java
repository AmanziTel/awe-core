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

package org.amanzi.awe.cassidian.loader.parser;

import java.io.File;

import org.amanzi.awe.cassidian.collector.CallCollector;
import org.amanzi.awe.cassidian.collector.CallPreparator;
import org.amanzi.awe.cassidian.structure.TNSElement;
import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.newparser.IParser;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.core.newsaver.ISaver;
import org.amanzi.neo.services.networkModel.IModel;

/**
 * <p>
 * AMSXML parser
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class AMSXMLParser implements IParser<ISaver<IModel, IData, IConfiguration>, IConfiguration, IData> {
    /**
     * common configuration
     */
    private IConfiguration config;
    /**
     * AMS XML Handler
     */
    private Handler handler;
    /**
     * common saver;
     */
    private ISaver<IModel, IData, IConfiguration> saver;

    public AMSXMLParser() {
        handler = new Handler();
    }

    /**
     * parse file from path
     * 
     * @param filePath
     * @return
     */
    public TNSElement parse(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return handler.parseElement(file);
        }
        return null;
    }

    /**
     * parse xml file and wraps data in TNSElement
     * 
     * @param file
     * @return
     */
    public TNSElement parse(File file) {
        if (file.exists()) {
            return handler.parseElement(file);
        }
        return null;
    }

    @Override
    public void init(IConfiguration configuration, ISaver<IModel, IData, IConfiguration> saver) {
        config = configuration;
        this.saver = saver;
    }

    @Override
    public void run() {
        CallPreparator callPreparator = new CallPreparator();
        CallCollector collector;
        for (File f : config.getFilesToLoad()) {
            TNSElement tns = parse(f);
            if (tns != null) {
                collector = callPreparator.extractCallsFromEvents(tns.getCtd().get(0).getProbeIdNumberMap(),tns.getEvents(), tns.getGps(), tns.getCtd().get(0).getNtpq());
                collector.setFile(f);
                saver.saveElement(collector);
            }
        }
    }
}
