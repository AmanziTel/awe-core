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

package org.amanzi.neo.loader.core.synonyms;

import java.sql.Timestamp;

import org.apache.commons.lang3.ArrayUtils;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public class Synonyms {

    public enum SynonymType {
        UNKOWN(null), STRING(String.class), INTEGER(Integer.class), DOUBLE(Double.class), BOOLEAN(Boolean.class), LONG(Long.class), TIMESTAMP(
                Timestamp.class);

        private Class< ? > clazz;

        private SynonymType(final Class< ? > clazz) {
            this.clazz = clazz;
        }

        public Class< ? > getSynonymClass() {
            return clazz;
        }

        public static SynonymType findByClass(final String clazz) {
            for (SynonymType singleType : values()) {
                if ((singleType.clazz != null) && singleType.clazz.getSimpleName().equals(clazz)) {
                    return singleType;
                }
            }

            return null;
        }
    }

    private final SynonymType synonymType;

    private final String propertyName;

    private final String[] possibleHeaders;

    private final boolean isMandatory;

    /**
	 * 
	 */
    public Synonyms(final String propertyName, final SynonymType synonymType, final boolean isMandatory,
            final String[] possibleHeaders) {
        this.propertyName = propertyName;
        this.synonymType = synonymType;
        this.possibleHeaders = trim(possibleHeaders);
        this.isMandatory = isMandatory;
    }

    public Synonyms(final String propertyName, final String[] possibleHeaders) {
        this(propertyName, SynonymType.UNKOWN, false, possibleHeaders);
    }

    private String[] trim(String[] originalArray) {
        String[] result = new String[originalArray.length];

        for (int i = 0; i < originalArray.length; i++) {
            result[i] = originalArray[i].trim();
        }

        return result;
    }

    /**
     * @return Returns the synonymType.
     */
    public SynonymType getSynonymType() {
        return synonymType;
    }

    /**
     * @return Returns the propertyName.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * @return Returns the possibleHeaders.
     */
    public String[] getPossibleHeaders() {
        return ArrayUtils.clone(possibleHeaders);
    }

    public boolean isMandatory() {
        return isMandatory;
    }

}
