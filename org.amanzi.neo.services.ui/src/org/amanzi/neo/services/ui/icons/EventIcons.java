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

package org.amanzi.neo.services.ui.icons;

/**
 * describe events icons
 * 
 * @author Vladislav_Kondratenko
 */
public enum EventIcons {
    CONNECT("event_normal"),
    CONNECT_GOOD("event_good"),
    CONNECT_BAD("event_bad"),
    CALL_BLOCKED("event_call_blocked"),
    CALL_DROPPED("event_call_dropped"),
    CALL_FAILURE("event_call_failure"),
    CALL_SUCCESS("event_call_success"),
    HANDOVER_FAILURE("event_handover_failure"),
    HANDOVER_SUCCESS("event_handover_success");

    String fileName;

    EventIcons(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * get the icon name appropriated to event
     *
     * @return file name
     */
    public String getIconName(){
        return fileName;
    }

}
