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

package org.amanzi.neo.loader.core.newparser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.amanzi.neo.loader.core.IConfiguration;
import org.amanzi.neo.loader.core.ILoaderProgressListener;
import org.amanzi.neo.loader.core.IProgressEvent;
import org.amanzi.neo.loader.core.ProgressEventImpl;
import org.amanzi.neo.loader.core.newsaver.IData;
import org.amanzi.neo.loader.core.newsaver.ISaver;
import org.amanzi.neo.services.exceptions.AWEException;
import org.amanzi.neo.services.exceptions.DatabaseException;
import org.amanzi.neo.services.model.IModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;

//TODO: LN: comments
/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author gerzog
 * @since 1.0.0
 */
public abstract class AbstractParser<T1 extends ISaver<? extends IModel, T3, T2>, T2 extends IConfiguration, T3 extends IData>
		implements IParser<T1, T2, T3> {
	// TODO: LN: comments
	protected static Logger LOGGER;
	private final List<ILoaderProgressListener> listeners = new ArrayList<ILoaderProgressListener>();
	protected final int PERCENTAGE_FIRE = 2;
	private double percentage = 0.0;
	protected File tempFile;
	private boolean isNewFile = false;
	private double commonPercentage = 0;
	protected boolean isCanceled = false;

	@Override
	public void addProgressListener(ILoaderProgressListener listener) {
		listeners.add(listener);
	}

	@Override
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
		this.savers = saver;
	}

	/**
	 * Parses single IData element
	 * 
	 * @return next parsed IData object, or null in case if parsing finished
	 */
	protected abstract T3 parseElement();

	/**
	 * Parses single file
	 * 
	 * @param file
	 * @throws AWEException
	 */
	protected void parseFile(File file) throws AWEException {
		currentFile = file;
		if (tempFile == null || tempFile != currentFile) {
			isNewFile = true;
		}
		T3 element = parseElement();

		long startTime = System.currentTimeMillis();
		while (element != null) {
			for (ISaver<?, T3, T2> saver : savers) {
				try {
					saver.saveElement(element);
				} catch (DatabaseException e) {
					LOGGER.error("Error while saving line ", e);
					saver.finishUp();
					throw new DatabaseException(e);
				}
			}
			element = parseElement();
			if (isCanceled) {
				break;
			}
		}
		LOGGER.info("File " + currentFile.getName()
				+ "  data saving finished in: " + getOperationTime(startTime));
	}

	protected long getOperationTime(long time) {
		return System.currentTimeMillis() - time;
	}

	/**
	 * Fire sub progress event.
	 * 
	 * @param element
	 *            the element
	 * @param event
	 *            the event
	 */
	protected boolean fireSubProgressEvent(File element,
			final IProgressEvent event) {
		if (isNewFile) {
			percentage += commonPercentage;
			isNewFile = false;
		}
		commonPercentage = event.getPercentage() / 100;
		isCanceled = fireProgressEvent(new ProgressEventImpl(
				event.getProcessName(),
				(percentage + (event.getPercentage()) / 100)
						/ config.getFilesToLoad().size()));
		return isCanceled;
	}

	@Override
	public void run() throws AWEException {
		long globalStartTime = System.currentTimeMillis();
		for (File file : config.getFilesToLoad()) {
			long startTime = System.currentTimeMillis();
			parseFile(file);
			LOGGER.info("File " + currentFile.getName()
					+ " Parsing/Saving data finished in: "
					+ getOperationTime(startTime));
		}
		for (ISaver<?, T3, T2> saver : savers) {
			saver.finishUp();
		}
		LOGGER.info("All files Parsing/Saving finished in: "
				+ getOperationTime(globalStartTime));
	}

	@Override
	public boolean fireProgressEvent(final IProgressEvent event) {
		Object[] allListeners = listeners.toArray();
		for (Object listener : allListeners) {
			final ILoaderProgressListener singleListener = (ILoaderProgressListener) listener;

			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void run() throws Exception {
					singleListener.updateProgress(event);
				}

				@Override
				public void handleException(Throwable exception) {
					LOGGER.error("Error while SafeRunner execute ", exception);
				}
			});
		}

		return event.isCanseled();
	}
}
