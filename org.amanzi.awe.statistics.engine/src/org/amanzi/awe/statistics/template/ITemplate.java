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

package org.amanzi.awe.statistics.template;

import org.amanzi.neo.models.measurement.IMeasurementModel;
import org.jruby.RubyHash;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface ITemplate {

    String getName();

    RubyHash calculate(IRubyObject dataElement);

    boolean canResolve(IMeasurementModel model);

    ITemplateColumn getColumn(String name);

}
