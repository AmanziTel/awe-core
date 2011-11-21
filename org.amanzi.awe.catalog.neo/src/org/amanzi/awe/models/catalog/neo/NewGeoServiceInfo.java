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
package org.amanzi.awe.models.catalog.neo;

import net.refractions.udig.catalog.IService;
import net.refractions.udig.catalog.IServiceInfo;

public class NewGeoServiceInfo extends IServiceInfo {
    private IService service;

    public NewGeoServiceInfo(IService service) {
        this.service = service;
        this.title = this.service.getIdentifier().toString();
        this.description = "Neo4J Database (" + this.title + ")";
        this.keywords = new String[] {"Neo4j", "Database", "File"};
    }
}
