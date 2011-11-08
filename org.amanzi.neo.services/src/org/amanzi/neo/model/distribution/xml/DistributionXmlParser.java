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

package org.amanzi.neo.model.distribution.xml;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.amanzi.neo.model.distribution.IDistribution;
import org.amanzi.neo.model.distribution.IDistributionalModel;
import org.amanzi.neo.model.distribution.IRange;
import org.amanzi.neo.model.distribution.types.impl.AbstractDistribution;
import org.amanzi.neo.model.distribution.types.ranges.impl.ColorRange;
import org.amanzi.neo.model.distribution.types.ranges.impl.SimpleRange;
import org.amanzi.neo.model.distribution.xml.schema.Bar;
import org.amanzi.neo.model.distribution.xml.schema.Distribution;
import org.amanzi.neo.model.distribution.xml.schema.ObjectFactory;
import org.amanzi.neo.services.NodeTypeManager;
import org.amanzi.neo.services.enums.INodeType;
import org.amanzi.neo.services.filters.ExpressionType;
import org.amanzi.neo.services.filters.Filter;
import org.amanzi.neo.services.filters.FilterType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * <p>
 * Class for parsing distribution xml and creating IDistribution
 * </p>
 * 
 * @author kostyukovich_n
 * @since 1.0.0
 */
public class DistributionXmlParser {

    private Distribution xmlDistr;

    /**
     * Take as a parameter url to xml resource, create new JAXBContext instance and Unmarshaller,
     * parse stream from url and gets Distribution value. After check constraints,
     * DistributionXmlParsingException will be thrown if constraints violated
     * 
     * @param url
     * @throws DistributionXmlParsingException
     * @throws IOException
     */
    public DistributionXmlParser(URL url) throws DistributionXmlParsingException, IOException {
        try {
            JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            @SuppressWarnings("unchecked")
            JAXBElement<Distribution> jaxb = (JAXBElement<Distribution>)unmarshaller.unmarshal(url.openStream());
            xmlDistr = jaxb.getValue();
            checkConstraints(xmlDistr);
        } catch (JAXBException e) {
            throw new DistributionXmlParsingException(url.getFile() + " unmarshalling was failed");
        }
    }

    public boolean checkCompatibility(IDistributionalModel model, INodeType nodeType, String propertyName) {
        return checkCompatibility(model, nodeType, propertyName, xmlDistr);
    }

    /**
     * Check compatibility of distribution DataType of distribution must be equals to
     * model.getType() NodeType of distribution must be equals to nodeType property name must be
     * null or equals to PropertyName of Distribution
     * 
     * @param model
     * @param nodeType
     * @param propertyName
     * @return
     */
    public static boolean checkCompatibility(IDistributionalModel model, INodeType nodeType, String propertyName, Distribution distr) {
        INodeType xmlDataType = NodeTypeManager.getType(distr.getData().getDataType());
        INodeType xmlNodeType = NodeTypeManager.getType(distr.getData().getNodeType());
        String xmlProperty = distr.getData().getPropertyName();
        return xmlDataType.equals(model.getType()) && xmlNodeType.equals(nodeType)
                && (StringUtils.isEmpty(xmlProperty) || xmlProperty.equals(propertyName));
    }

    /**
     * Check mandatory fields of distr, if mandatory field is empty
     * DistributionXmlParsingException will be thrown
     * 
     * @param distr
     * @throws DistributionXmlParsingException
     */
    public static void checkConstraints(Distribution distr) throws DistributionXmlParsingException {
        checkNull(distr);
        checkNull(distr.getBars(), distr.getData());
        checkNull(distr.getData().getDataType(), distr.getData().getName(), distr.getData().getNodeType());
        if (distr.getBars().getBar().size() == 0) {
            throw new DistributionXmlParsingException("At least 1 'bar' element should be");
        }
        for (Bar bar : distr.getBars().getBar()) {
            checkNull(bar.getName());
            if (bar.getColor() != null) {
                checkNull(bar.getColor().getRed(), bar.getColor().getGreen(), bar.getColor().getRed());
            }
            checkNull(bar.getFilter());
            checkFilterConstraints(bar.getFilter());
        }
    }

    private static void checkFilterConstraints(org.amanzi.neo.model.distribution.xml.schema.Filter filter)
            throws DistributionXmlParsingException {
        checkNull(filter.getNodeType(), filter.getNodeType());
        if (filter.getUnderlyingFilter() != null) {
            checkFilterConstraints(filter.getUnderlyingFilter());
        }
    }

    private static void checkNull(Object... objects) throws DistributionXmlParsingException {
        for (Object obj : objects) {
            if (obj == null) {
                throw new DistributionXmlParsingException("At least one mandatory field is empty");
            }
        }
    }

    /**
     * Convert XML Filter Object to service Filter Object 
     * @param xmlFilter
     * @return
     */
    private Filter parseFilter(org.amanzi.neo.model.distribution.xml.schema.Filter xmlFilter) {
        FilterType filterType = FilterType.getByName(xmlFilter.getFilterType());
        ExpressionType expType = ExpressionType.getByName(xmlFilter.getExpressionType());
        Filter filter;
        if (filterType != null && expType != null) {
            filter = new Filter(filterType, expType);
        } else if (filterType != null) {
            filter = new Filter(filterType);
        } else if (expType != null) {
            filter = new Filter(expType);
        } else {
            filter = new Filter();
        }
        Serializable value = xmlFilter.getValue();
        if (value != null) {
            filter.setExpression(NodeTypeManager.getType(xmlFilter.getNodeType()), xmlFilter.getPropertyName(), value);
        } else {
            filter.setExpression(NodeTypeManager.getType(xmlFilter.getNodeType()), xmlFilter.getPropertyName());
        }
        if (xmlFilter.getUnderlyingFilter() != null) {
            filter.addFilter(parseFilter(xmlFilter.getUnderlyingFilter()));
        }
        return filter;
    }

    /**
     * Getting IDistribution for model, nodeType and propertyName If color was defined in 'bar'
     * element of xml that ColorRange will be created, else SimpleRange will be created
     * 
     * @param model
     * @param nodeType
     * @param propertyName
     * @return
     */
    public IDistribution<IRange> getDistribution(IDistributionalModel model, INodeType nodeType, String propertyName) {
        return new AbstractDistribution<IRange>(model, nodeType, propertyName) {

            private final Logger LOGGER = Logger.getLogger(AbstractDistribution.class);

            private int count = 0;

            @Override
            public String getName() {
                return xmlDistr.getData().getName();
            }

            @Override
            public int getCount() {
                return count;
            }

            @Override
            public org.amanzi.neo.model.distribution.IDistribution.Select[] getPossibleSelects() {
                return new Select[] {Select.EXISTS};
            }

            @Override
            protected void createRanges() {
                LOGGER.debug("start createRange(), distribution " + getName());
                for (Bar bar : xmlDistr.getBars().getBar()) {
                    setCanChangeColors(bar.getColor() == null);
                    if (bar.getColor() == null) {
                        ranges.add(new SimpleRange(bar.getName(), parseFilter(bar.getFilter())));
                    } else {
                        ranges.add(new ColorRange(bar.getName(), parseFilter(bar.getFilter()), bar.getColor().getRed(), bar
                                .getColor().getGreen(), bar.getColor().getBlue()));
                    }
                }
            }

            @Override
            protected org.amanzi.neo.model.distribution.IDistribution.Select getDefaultSelect() {
                return null;
            }
        };
    }
}