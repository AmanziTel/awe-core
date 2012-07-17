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

import java.util.Arrays;

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
        UNKOWN(null), STRING(String.class), INTEGER(Integer.class), DOUBLE(Double.class), BOOLEAN(Boolean.class), LONG(Long.class);

        private Class< ? > clazz;

        private SynonymType(Class< ? > clazz) {
            this.clazz = clazz;
        }

        public Class< ? > getSynonymClass() {
            return clazz;
        }

        public static SynonymType findByClass(String clazz) {
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

    /**
     * 
     */
    public Synonyms(String propertyName, SynonymType synonymType, String[] possibleHeaders) {
        this.propertyName = propertyName;
        this.synonymType = synonymType;
        this.possibleHeaders = Arrays.copyOf(possibleHeaders, possibleHeaders.length);
    }

    public Synonyms(String propertyName, String[] possibleHeaders) {
        this(propertyName, SynonymType.UNKOWN, possibleHeaders);
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
        return possibleHeaders;
    }

}
