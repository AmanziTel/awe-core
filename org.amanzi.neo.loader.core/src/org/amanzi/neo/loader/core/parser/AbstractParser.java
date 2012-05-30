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

package org.amanzi.neo.loader.core.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.loader.core.CountingFileInputStream;
import org.amanzi.neo.loader.core.ILoaderProgressListener;
import org.amanzi.neo.loader.core.config.IConfiguration;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * <p>
 * common parser actions
 * </p>
 * 
 * @author gerzog
 * @since 1.0.0
 */
public abstract class AbstractParser<T1 extends ISaver<? extends IModel, T3, T2>, T2 extends IConfiguration, T3 extends IData>
		implements IParser<T1, T2, T3> {
	private static final Logger LOGGER = Logger.getLogger(AbstractParser.class);;

	protected CountingFileInputStream is;
	protected BufferedReader reader;

	/**
	 * progress monitor listeners
	 */
	private final List<ILoaderProgressListener> listeners = new ArrayList<ILoaderProgressListener>();
	protected final int PERCENTAGE_FIRE = 1;
	/**
	 * temporary files instance
	 */
	protected File tempFile;
	/**
	 * cancel progress monitor flag
	 */
	protected boolean isCanceled = false;

	public void addProgressListener(ILoaderProgressListener listener) {
		listeners.add(listener);
	}

	public void removeProgressListener(ILoaderProgressListener listener) {
		listeners.remove(listener);
	}

	/*
	 * Configuraton data of Parser
	 */
	protected T2 config;

	/*
	 * Savers for Data
	 */
	protected List<T1> savers;

	/*
	 * Currently parsed file
	 */
	protected File currentFile;

	@Override
	public void init(T2 configuration, List<T1> saver) {
		this.config = configuration;
		this.config.computeSourceFiles();
		this.savers = saver;
	}

	/**
	 * Parses single IData element
	 * 
	 * @return next parsed IData object, or null in case if parsing finished
	 */
	protected abstract T3 parseElement(IProgressMonitor monitor)
			throws IOException;

	/**
	 * Parses single file
	 * 
	 * @param file
	 * @throws AWEException
	 */
	protected void parseFile(File file, IProgressMonitor monitor)
			throws AWEException, IOException {
		monitor.beginTask("Loading file <" + file.getName() + ">", 100);

		T3 element;
		initializeFileStreams(file);
		long startTime = System.currentTimeMillis();
		do {
			element = parseElement(monitor);
			for (ISaver<?, T3, T2> saver : savers) {
				try {
					if (element != null) {
						saver.saveElement(element);
					}
				} catch (DatabaseException e) {
					AweConsolePlugin.error("Error while saving line ");
					LOGGER.error("Error while saving line ", e);
					saver.finishUp();
					throw new DatabaseException(e);
				}
			}
			if (isCanceled) {
				break;
			}
		} while (element != null);

		monitor.done();

		LOGGER.info("File " + currentFile.getName()
				+ "  data saving finished in: " + getOperationTime(startTime));
	}

	protected long getOperationTime(long time) {
		return System.currentTimeMillis() - time;
	}

	@Override
	public void run(IProgressMonitor monitor) throws AWEException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		long globalStartTime = System.currentTimeMillis();

		try {
			for (File file : config.getFilesToLoad()) {
				currentFile = file;
				long startTime = System.currentTimeMillis();
				parseFile(file, monitor);
				LOGGER.info("File " + currentFile.getName()
						+ " Parsing/Saving data finished in: "
						+ getOperationTime(startTime));
			}
		} catch (IOException e) {
			// TODO:
			e.printStackTrace();
		}
		try {
			finishUp();
		} catch (Exception e) {
			throw new DatabaseException(e);
		}
		LOGGER.info("All files Parsing/Saving finished in: "
				+ getOperationTime(globalStartTime));
	}

	protected void finishUp() throws Exception {
		finishUpParse();
		for (ISaver<?, T3, T2> saver : savers) {
			saver.finishUp();
		}
	}

	/**
	 * finishup common parser methods;
	 */
	protected abstract void finishUpParse();

	private void initializeFileStreams(File file) {
		try {
			is = new CountingFileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(is));
			tempFile = currentFile;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
