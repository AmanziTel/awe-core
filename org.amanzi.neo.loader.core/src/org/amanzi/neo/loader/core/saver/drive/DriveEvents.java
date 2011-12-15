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

package org.amanzi.neo.loader.core.saver.drive;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * DriveEvents - enum of possible events
 * </p>
 * 
 * @author Cinkel_A
 * @since 1.0.0
 */
public enum DriveEvents {
    // unknown bad event
    UNKNOWN_BAD {

        @Override
        public boolean haveEvents(String aProperty) {
            return false;
        }

        @Override
        public String getDescription() {
            return "unknown bad event";
        }
    },
    // unknown good event
    UNKNOWN_GOOD {

        @Override
        public boolean haveEvents(String aProperty) {
            return false;
        }

        @Override
        public String getDescription() {
            return "unknown good event";
        }
    },
    // unknown neutral event
    UNKNOWN_NEUTRAL {

        @Override
        public boolean haveEvents(String aProperty) {
            return false;
        }

        @Override
        public String getDescription() {
            return "unknown neutral event";
        }
    },
    // HANDOVER_FAILURE
    HANDOVER_FAILURE {

        @Override
        public boolean haveEvents(String aProperty) {
            return aProperty != null && aProperty.toLowerCase().contains("handover failure");
        }

        @Override
        public String getDescription() {
            return "handower failure";
        }
    },
    // HANDOVER_SUCCESS
    HANDOVER_SUCCESS {

        @Override
        public boolean haveEvents(String aProperty) {
            final String property = aProperty == null ? null : aProperty.toLowerCase();
            return property != null && (property.contains("handover complete") || property.contains("ho command"));
        }

        @Override
        public String getDescription() {
            return "handower success";
        }
    };
    
    /**
     * Checks that events by types
     * 
     * @param aProperty - event string
     * @return true if string contains event of necessary types
     */
    public abstract boolean haveEvents(String aProperty);
    
    public static DriveEvents getEnumById(String eventType) {
        if (StringUtils.isEmpty(eventType)) {
            return null;
        }
        return DriveEvents.valueOf(eventType);
    }

    /**
     * Gets event description
     * 
     * @return
     */
    public abstract String getDescription();
}
