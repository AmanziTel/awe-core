/**
 * 
 */
package org.amanzi.neo.loader.core.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.amanzi.awe.console.AweConsolePlugin;
import org.amanzi.neo.loader.core.CountingFileInputStream;
import org.amanzi.neo.loader.core.config.IConfiguration;
import org.amanzi.neo.loader.core.saver.ISaver;
import org.amanzi.neo.loader.core.saver.nemo.NemoEvents;
import org.amanzi.neo.services.model.IModel;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Nemo file parser
 * 
 * @author Bondoronok_P
 */
public class NemoParser<T1 extends ISaver<IModel, MappedData, T2>, T2 extends IConfiguration>
		extends AbstractParser<T1, T2, MappedData> {

	private static final Logger LOGGER = Logger.getLogger(NemoParser.class);
	/**
	 * Separators
	 */
	private static final String COMMA_SEPARATOR = ",";

	/**
	 * Constants
	 */
	private static final String NEMO_VERSION = "2.01";
	private static final int EVENT_ID_INDEX = 0;
	private static final int TIMESTAMP_INDEX = 1;
	private static final int CONTEXT_IDS_COUNT_INDEX = 2;
	private static final int COLLECTION_INITIAL_CAPACITY = 0;
	private static final int DEFAULT_PARAMETER_POSITION = 3;
	private static final SimpleDateFormat EVENT_DATE_FORMAT = new SimpleDateFormat(
			"dd.MM.yyyy");
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat(
			"HH:mm:ss.S");
	private static final String EVENT = "event_type";
	private static final String DATE = "Date";
	private static final String TIMESTAMP = "timestamp";

	/**
	 * Fields
	 */
	private List<Map<String, Object>> subNodes;
	private List<Integer> contextIds;
	private Calendar workDate;

	private double percentageOld = 0;

	public NemoParser() {
		super();
		if (currentFile != null) {
			try {
				is = new CountingFileInputStream(currentFile);
				reader = new BufferedReader(new InputStreamReader(is));
			} catch (FileNotFoundException e) {
				throw (RuntimeException) new RuntimeException().initCause(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.amanzi.neo.loader.core.parser.AbstractParser#parseElement(org.eclipse
	 * .core.runtime.IProgressMonitor)
	 */
	@Override
	protected MappedData parseElement(IProgressMonitor monitor)
			throws IOException {
		try {
			String currentString;
			while ((currentString = reader.readLine()) != null) {
				String[] stringDataList = currentString.split(COMMA_SEPARATOR);
				Map<String, String> parsedData = parseNemoEvent(stringDataList);
				if (parsedData == null) {
					continue;
				}
				parsedData.put(EVENT, stringDataList[EVENT_ID_INDEX]);

				MappedData mappedData = new MappedData(parsedData);
				mappedData.setFile(currentFile);
				return mappedData;
			}

		} catch (Exception e) {
			return null;
		} finally {
			double percentage = is.percentage();
			if (percentage - percentageOld >= PERCENTAGE_FIRE) {
				percentageOld = percentage;
				monitor.worked(PERCENTAGE_FIRE);
			}
		}
		return null;
	}

	/**
	 * Parse current data line and create MappedData object for saving
	 * 
	 * @param data
	 *            current data
	 * @return MappedData object for saving
	 */
	private Map<String, String> parseNemoEvent(String[] data) {

		if (data.length == 0) {
			return null;
		}

		NemoEvents event = NemoEvents.getEventById(data[EVENT_ID_INDEX]);
		String time = data[TIMESTAMP_INDEX];

		initializeContextIds(data);

		int firstParameterPosition = DEFAULT_PARAMETER_POSITION
				+ contextIds.size();
		List<String> parameters = new ArrayList<String>(
				COLLECTION_INITIAL_CAPACITY);
		for (int i = firstParameterPosition; i < data.length; i++) {
			parameters.add(data[i]);
		}
		Map<String, String> parsedParameters = analyseParameters(event,
				parameters);

		if (parsedParameters == null) {
			return null;
		} else {
			Long timestamp;
			try {
				timestamp = getTimeStamp(timeFormat.parse(time));
			} catch (ParseException e) {
				timestamp = 0L;
			}
			parsedParameters.put(TIMESTAMP, String.valueOf(timestamp));
			return parsedParameters;
		}
	}

	/**
	 * Initialize context IDs
	 * 
	 * @param data
	 *            data array
	 */
	private void initializeContextIds(String[] data) {
		contextIds = new ArrayList<Integer>(COLLECTION_INITIAL_CAPACITY);
		String contextIdsCount = StringUtils.EMPTY;
		if (data.length > 2) {
			contextIdsCount = data[CONTEXT_IDS_COUNT_INDEX];
		}
		if (!contextIdsCount.isEmpty()) {
			int idsCount = Integer.parseInt(contextIdsCount);
			int count = DEFAULT_PARAMETER_POSITION;
			for (int i = 0; i < idsCount; i++) {
				int id = 0;
				String currentId = data[count++];
				if (!currentId.isEmpty()) {
					id = Integer.parseInt(currentId);
				}
				contextIds.add(id);
			}
		}
	}

	/**
	 * Analyze event parameters
	 * 
	 * @param event
	 *            Event
	 * @param parameters
	 *            Event parameters
	 * @return Map<Stirng, String>
	 */
	private Map<String, String> analyseParameters(NemoEvents event,
			List<String> parameters) {
		if (event == null || parameters.isEmpty()) {
			return null;
		}
		Map<String, Object> nemoParameters = new HashMap<String, Object>(
				COLLECTION_INITIAL_CAPACITY);
		nemoParameters = event.fill(NEMO_VERSION, parameters);
		if (nemoParameters.isEmpty()) {
			return null;
		}
		nemoParameters = storeSubNodes(nemoParameters);
		setWorkDate(event, nemoParameters);
		return getParsedParameters(nemoParameters);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> storeSubNodes(Map<String, Object> parameters) {
		subNodes = (List<Map<String, Object>>) parameters
				.remove(NemoEvents.SUB_NODES);
		if (subNodes != null) {
			int i = 0;
			for (Map<String, Object> subNode : subNodes) {
				i++;
				for (Map.Entry<String, Object> entry : subNode.entrySet()) {
					parameters.put(new StringBuilder(entry.getKey()).append(i)
							.toString(), entry.getValue());
				}
			}
			subNodes.clear();
		}
		return addContextField(parameters);

	}

	/**
	 * Add context filed
	 * 
	 * @param parameters
	 *            parsed parameters
	 * @return Parameters
	 */
	private Map<String, Object> addContextField(Map<String, Object> parameters) {
		if (parameters.containsKey(NemoEvents.FIRST_CONTEXT_NAME)) {
			@SuppressWarnings("unchecked")
			List<String> contextName = (List<String>) parameters
					.get(NemoEvents.FIRST_CONTEXT_NAME);
			parameters.remove(NemoEvents.FIRST_CONTEXT_NAME);
			if (contextIds != null) {
				for (int i = 0; i < contextIds.size() && i < contextName.size(); i++) {
					if (contextIds.get(i) != 0) {
						parameters.put(contextName.get(i), contextIds.get(i));
					}
				}
			}
		}
		return parameters;
	}

	/**
	 * Return initialized map
	 * 
	 * @param parameters
	 *            event parameters
	 * @return Parsed parameters
	 */
	private Map<String, String> getParsedParameters(
			Map<String, Object> parameters) {
		Set<Entry<String, Object>> entrySet = parameters.entrySet();
		Map<String, String> parsedParameters = new HashMap<String, String>(
				parameters.size());
		for (Entry<String, Object> entry : entrySet) {
			parsedParameters.put(entry.getKey(),
					String.valueOf(entry.getValue()));
		}
		return parsedParameters;
	}

	/**
	 * Set work date
	 * 
	 * @param event
	 *            Nemo event
	 * @param parameters
	 *            Map with parsed parameters
	 */
	private void setWorkDate(NemoEvents event, Map<String, Object> parameters) {
		if (workDate == null && event == NemoEvents.START) {
			workDate = new GregorianCalendar();
			Date date;
			try {
				date = EVENT_DATE_FORMAT.parse((String) parameters.get(DATE));

			} catch (Exception e) {
				date = new Date();
			}
			workDate.setTime(date);
		}
	}

	@SuppressWarnings("deprecation")
	protected long getTimeStamp(Date nodeDate) {
		if (nodeDate == null || workDate == null) {
			return 0L;
		}
		final int nodeHours = nodeDate.getHours();
		workDate.set(Calendar.HOUR_OF_DAY, nodeHours);
		workDate.set(Calendar.MINUTE, nodeDate.getMinutes());
		workDate.set(Calendar.SECOND, nodeDate.getSeconds());
		final long timestamp = workDate.getTimeInMillis();
		return timestamp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.amanzi.neo.loader.core.parser.AbstractParser#finishUpParse()
	 */
	@Override
	protected void finishUpParse() {
		try {
			is.close();
			reader.close();
		} catch (IOException e) {
			AweConsolePlugin.error("Cannt't close stream");
			LOGGER.error("Cannt't close stream", e);
		}
	}
}
