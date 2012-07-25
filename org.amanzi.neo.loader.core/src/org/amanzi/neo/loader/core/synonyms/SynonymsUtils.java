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

import java.util.List;

/**
 * TODO Purpose of
 * <p>
 * </p>
 * 
 * @author Nikolay Lagutko (nikolay.lagutko@amanzitel.com)
 * @since 1.0.0
 */
public final class SynonymsUtils {

    /**
     * 
     */
    private SynonymsUtils() {

    }

    public static boolean checkHeaders(final Synonyms synonym, final String[] headers) {
        boolean found = false;
        check_synonyms: for (String possibleHeader : synonym.getPossibleHeaders()) {

            for (String header : headers) {
                if (header.matches(possibleHeader)) {
                    found = true;
                    break check_synonyms;
                }
            }
        }

        return found;
    }

    public static Synonyms findAppropriateSynonym(final String header, final List<Synonyms> synonymsList) {
        Synonyms synonym = null;

        check_synonyms: for (Synonyms singleSynonym : synonymsList) {
            for (String pattern : singleSynonym.getPossibleHeaders()) {
                if (header.matches(pattern)) {
                    synonym = singleSynonym;
                    break check_synonyms;
                }
            }
        }

        return synonym;
    }

}
