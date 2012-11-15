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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.amanzi.awe.nem.export.ExportedDataContainer;
import org.amanzi.awe.nem.export.ExportedDataItems;
import org.amanzi.awe.nem.export.SynonymsWrapper;
import org.amanzi.neo.dto.IDataElement;
import org.amanzi.neo.models.exceptions.ModelException;
import org.amanzi.neo.models.network.INetworkModel;
import org.apache.commons.lang3.StringUtils;
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
    private final Map<ExportedDataItems, Map<String, Integer>> fileColumns;
    private final INetworkModel model;

    /**
     * @param name
     */
    public NetworkExportJob(final ExportedDataContainer container) {
        super("export networkData");
        this.container = container;
        this.model = container.getModel();
        fileColumns = new HashMap<ExportedDataItems, Map<String, Integer>>();
        writerStreams = new HashMap<ExportedDataItems, CSVWriter>();
    }

    /**
     * @param line
     * @param columns
     * @return
     */
    private String[] buildLine(final Map<String, Object> line, final Map<String, Integer> columns) {
        String[] values = new String[columns.size()];
        for (Entry<String, Object> lineData : line.entrySet()) {
            Integer index = columns.get(lineData.getKey());
            if (index == null) {
                continue;
            }
            values[index] = lineData.getValue().toString();
        }
        return values;
    }

    /**
     * @param values
     */
    private boolean checkValues(final String[] values) {
        for (String value : values) {
            if (!StringUtils.isEmpty(value)) {
                return true;
            }
        }
        return false;

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
     * @param line
     * @param children
     * @param monitor
     * @throws ModelException
     */
    private void collectLine(final Map<String, Object> line, final Iterable<IDataElement> children, final IProgressMonitor monitor)
            throws ModelException {
        for (IDataElement inner : children) {
            prepareLine(line, inner);
            if (!model.getChildren(inner).iterator().hasNext()) {
                writeLine(line, inner);
            }
            monitor.worked(1);
            collectLine(line, model.getChildren(inner), monitor);
        }
    }

    /**
     * @param file
     * @throws IOException
     */
    private void openWriterStream(final Entry<ExportedDataItems, List<SynonymsWrapper>> file) throws IOException {
        String filePath = container.getDirectoryPath() + File.separator
                + MessageFormat.format(file.getKey().getFileNameFormat(), model.getName());
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filePath), container.getCharset());
        CSVWriter writer = new CSVWriter(osw, container.getSeparator(), container.getQuoteChar());
        List<String> headers = prepareHeadersFromSynonyms(file.getKey(), file.getValue());
        writer.writeNext(headers.toArray(new String[0]));
        writerStreams.put(file.getKey(), writer);
    }

    /**
     * @param exportedDataItems
     * @param list
     * @return
     */
    private List<String> prepareHeadersFromSynonyms(final ExportedDataItems exportedDataItems, final List<SynonymsWrapper> synonyms) {
        Map<String, Integer> association = new HashMap<String, Integer>();
        List<String> headers = new ArrayList<String>();
        int i = 0;
        for (SynonymsWrapper synonym : synonyms) {
            association.put(synonym.getType() + synonym.getProperty(), i);
            headers.add(synonym.getHeader());
            i++;
        }
        fileColumns.put(exportedDataItems, association);
        return headers;
    }

    /**
     * @param line
     * @param element
     */
    private void prepareLine(final Map<String, Object> line, final IDataElement element) {
        for (Entry<String, Object> entry : element.asMap().entrySet()) {
            line.put(element.getNodeType().getId() + entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        monitor.beginTask("Network" + model.getName() + " export", model.getPropertyStatistics().getCount());
        try {
            for (Entry<ExportedDataItems, List<SynonymsWrapper>> file : container.getSynonyms().entrySet()) {
                openWriterStream(file);
            }
            for (IDataElement element : model.getChildren(model.asDataElement())) {
                Map<String, Object> line = new HashMap<String, Object>();
                prepareLine(line, element);
                collectLine(line, model.getChildren(element), monitor);

            }
        } catch (Exception e) {
            LOGGER.error("can't export network", e);
            return new Status(Status.ERROR, "org.amanzi.awe.nem", "can't export network");
        } finally {
            try {
                monitor.done();
                closeStreams();
            } catch (IOException e) {
                LOGGER.error("can't close input stream", e);
                return new Status(Status.ERROR, "org.amanzi.awe.nem", "can't export network");
            }
        }
        return Status.OK_STATUS;
    }

    /**
     * @param line
     * @param inner
     */
    private void writeLine(final Map<String, Object> line, final IDataElement inner) {
        for (Entry<ExportedDataItems, CSVWriter> entry : writerStreams.entrySet()) {
            Map<String, Integer> columns = this.fileColumns.get(entry.getKey());
            String[] values = buildLine(line, columns);
            if (checkValues(values)) {
                entry.getValue().writeNext(values);
            }
        }
        clearLine(line, inner);
    }

    /**
     * @param line
     * @param inner
     */
    private void clearLine(final Map<String, Object> line, final IDataElement inner) {
        for (String key : inner.asMap().keySet()) {
            line.remove(key);
        }
    }
}
