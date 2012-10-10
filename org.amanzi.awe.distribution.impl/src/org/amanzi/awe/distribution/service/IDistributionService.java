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

package org.amanzi.awe.distribution.service;

import org.amanzi.awe.distribution.model.type.IDistributionType;
import org.amanzi.neo.services.IService;
import org.amanzi.neo.services.exceptions.ServiceException;
import org.neo4j.graphdb.Node;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public interface IDistributionService extends IService {

    Node findDistributionNode(Node rootNode, IDistributionType< ? > distributionType) throws ServiceException;

    Node createDistributionNode(Node rootNode, IDistributionType< ? > distributionType) throws ServiceException;

    Node getCurrentDistribution(Node rootNode) throws ServiceException;

    void setCurrentDistribution(Node rootNode, Node currentDistributionNode) throws ServiceException;

    Node createDistributionBarNode(Node rootNode, String name, int[] color) throws ServiceException;

    Node findDistributionBar(Node rootNode, Node sourceNode) throws ServiceException;

}
