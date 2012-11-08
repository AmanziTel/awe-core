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

package org.amanzi.awe.nem.managers.network;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.nem.export.ExportedDataContainer;
import org.amanzi.awe.nem.export.ExportedDataItems;
import org.amanzi.awe.nem.export.SynonymsWrapper;
import org.amanzi.neo.dto.IDataElement;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Vladislav_Kondratenko
 * @since 1.0.0
 */
public class NetworkExportJob extends Job {
    private static final Logger LOGGER = Logger.getLogger(NetworkExportJob.class);

    private final ExportedDataContainer container;
    private final Map<ExportedDataItems, CSVWriter> writerStreams;

    /**
     * @param name
     */
    public NetworkExportJob(final ExportedDataContainer container) {
        super("export networkData");
        this.container = container;
        writerStreams = new HashMap<ExportedDataItems, CSVWriter>();
    }

    /**
     * @throws IOException
     */
    private void closeStreams() throws IOException {
        for (CSVWriter writer : writerStreams.values()) {
            writer.flush();
            writer.close();
        }

    }

    /**
     * @param file
     * @throws IOException
     */
    private void openWriterStream(final Entry<ExportedDataItems, List<SynonymsWrapper>> file) throws IOException {
        String filePath = container.getDirectoryPath() + File.separator
                + MessageFormat.format(file.getKey().getFileNameFormat(), container.getModel().getName());
        CSVWriter writer = new CSVWriter(new FileWriter(filePath));
        String[] headers = prepareHeadersFromSynonyms(file.getValue());
        writer.writeNext(headers);
        writerStreams.put(file.getKey(), writer);
    }

    /**
     * @param list
     * @return
     */
    private String[] prepareHeadersFromSynonyms(final List<SynonymsWrapper> synonyms) {
        String[] headers = new String[synonyms.toArray().length];
        for (int i = 0; i < headers.length; i++) {
            headers[i] = synonyms.get(i).getHeader();
        }
        return headers;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        monitor.beginTask("Network" + container.getModel().getName() + " export", container.getModel().getPropertyStatistics()
                .getCount());
        try {
            for (Entry<ExportedDataItems, List<SynonymsWrapper>> file : container.getSynonyms().entrySet()) {
                openWriterStream(file);
            }
            for (IDataElement element : container.getModel().getChildren(container.getModel().asDataElement())) {

                monitor.worked(1);
            }
        } catch (Exception e) {
            LOGGER.error("can't export network", e);
            return new Status(Status.ERROR, "org.amanzi.awe.nem", "can't export network");
        } finally {
            try {
                closeStreams();
            } catch (IOException e) {
                LOGGER.error("can't close input stream", e);
                return new Status(Status.ERROR, "org.amanzi.awe.nem", "can't export network");
            }
        }

        return Status.OK_STATUS;
    }
}
