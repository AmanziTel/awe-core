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

package org.amanzi.awe.statistics.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.amanzi.awe.scripting.JRubyRuntimeWrapper;
import org.amanzi.awe.scripting.exceptions.ScriptingException;
import org.amanzi.awe.statistics.engine.StatisticsEngine;
import org.amanzi.awe.statistics.exceptions.StatisticsEngineException;
import org.amanzi.awe.statistics.internal.StatisticsPlugin;
import org.amanzi.awe.statistics.model.IStatisticsModel;
import org.amanzi.awe.statistics.template.ITemplate;
import org.amanzi.neo.core.period.Period;
import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class StatisticsManager {

    private static final Logger LOGGER = Logger.getLogger(StatisticsManager.class);

    private static Map<IMeasurementModel, StatisticsManager> managerCache = new HashMap<IMeasurementModel, StatisticsManager>();

    private static Map<String, File> templateFiles;

    private static JRubyRuntimeWrapper jRubyWrapper;

    private static Collection<ITemplate> allTemplates;

    private static final String DATASET_DEFAULT_PROPERTY = "dataset";

    private final IMeasurementModel model;

    private Collection<ITemplate> availableTemplates;

    private ITemplate template;

    private String property;

    private Period period;

    private boolean isBuilt;

    private Set<String> defaultProperties = null;

    private StatisticsManager(final IMeasurementModel measurementModel) {
        this.model = measurementModel;
    }

    public static synchronized StatisticsManager getManager(final IMeasurementModel model) {
        StatisticsManager result = managerCache.get(model);

        if ((result == null) && (model != null)) {
            result = new StatisticsManager(model);

            managerCache.put(model, result);
        }

        return result;
    }

    public Collection<ITemplate> getAvailableTemplates() {
        if ((availableTemplates == null) && (model != null)) {

            BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {

                @Override
                public void run() {
                    availableTemplates = new ArrayList<ITemplate>();
                    for (final ITemplate singleTemplate : getAllTemplates()) {
                        if (singleTemplate.canResolve(model)) {
                            availableTemplates.add(singleTemplate);
                        }
                    }
                }
            });
        }

        return availableTemplates;
    }

    private static Collection<ITemplate> getAllTemplates() {
        if (allTemplates == null) {
            allTemplates = new ArrayList<ITemplate>();

            for (final File file : getTemplateFiles().values()) {
                try {
                    final ITemplate template = (ITemplate)getJRubyWrapper().executeScript(file);
                    allTemplates.add(template);
                } catch (final ScriptingException e) {
                    LOGGER.error("Cannot create a Template from file <" + file.getName() + ">", e);
                }
            }
        }

        return allTemplates;
    }

    private static JRubyRuntimeWrapper getJRubyWrapper() {
        if (jRubyWrapper == null) {
            try {
                jRubyWrapper = StatisticsPlugin.getDefault().getRuntimeWrapper();
            } catch (final ScriptingException e) {
                LOGGER.fatal("Unable to initialize JRuby Environment", e);
            }
        }

        return jRubyWrapper;
    }

    private static Map<String, File> getTemplateFiles() {
        if (templateFiles == null) {
            templateFiles = StatisticsPlugin.getDefault().getAllScripts();
        }

        return templateFiles;

    }

    public void setTemplate(final ITemplate template) {
        this.template = template;
    }

    public void setProperty(final String property) {
        this.property = property;
    }

    public void setPeriod(final Period period) {
        this.period = period;
    }

    public IStatisticsModel build(final IProgressMonitor progressMonitor) throws StatisticsEngineException {
        try {
            return StatisticsEngine.getEngine(model, template, period, property).build(progressMonitor);
        } finally {
            isBuilt = true;
        }
    }

    public Iterable<String> getDefaultProperties() {
        if (defaultProperties == null) {
            defaultProperties = new HashSet<String>();
            defaultProperties.add(DATASET_DEFAULT_PROPERTY);
        }
        return defaultProperties;
    }

    public boolean isBuilt() {
        return isBuilt;
    }
}
