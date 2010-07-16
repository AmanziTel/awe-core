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

package org.amanzi.neo.loader.nemo_enum;

import java.util.Random;

/**
 * <p>
 * Application Protocol enum for nemo data
 * </p>
 * 
 * @author Saelenchits_N
 * @since 1.0.0
 */
public enum ApplicationProtocol {

    NEMO_MODEM(0, "Nemo protocol using modem connection"), NEMO_TCP(1, "Nemo protocol using TCP"), NEMO_UPD(2, "Nemo protocol using UPD"), FTP(3, "FTP"), HTTP(4, "HTTP"), SMTP(5,
            "SMTP"), POP3(6, "POP3"), MMS(7, "MMS"), WAP_1(8, "WAP 1.0"), STREAMING(9, "Streaming"), WAP_2(10, "WAP 2.0"), HTTP_BROWSING(11, "HTTP browsing"), ICPM(12, "ICMP ping"), IPERF_TCP(
            13, "IPerf over TCP"), IPERF_UDP(14, "IPerf over UDP");

    /** The id. */
    private final int id;

    /** The description. */
    private final String description;

    /**
     * Instantiates a new application protocol.
     * 
     * @param id the id
     * @param description the description
     */
    private ApplicationProtocol(int id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Gets the description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the protocol by id.
     * 
     * @param anId the an id
     * @return the protocol by id
     */
    public static ApplicationProtocol getProtocolById(Integer anId) {
        for (ApplicationProtocol system : values()) {
            if (system.getId().equals(anId)) {
                return system;
            }
        }
        throw new IllegalArgumentException("Unknown protocol id <" + anId + ">.");
    }

    /**
     * To string.
     * 
     * @return the string
     */
    @Override
    public String toString() {
        return description;
    }

    /**
     * Gets the random protocol.
     *
     * @return the random protocol
     */
    public static ApplicationProtocol getRandomProtocol(){
        Random r= new Random();
        return ApplicationProtocol.values()[r.nextInt(ApplicationProtocol.values().length)];
    }
}
