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

package org.amanzi.neo.loader;

/**
 * Enumeration of systems for NemoEvents.
 * <p>
 *
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public enum TechnologySystems {

    GSM(1,"GSM"),
    TETRA(2,"TETRA"),
    UMTS_FDD(5,"UMTS FDD"),
    UMTS_TD_SCDMA(6,"UMTS TD-SCDMA"),
    CDMA_ONE(10,"cdmaOne"),
    CDMA_ONE_X(11,"CDMA 1x"),
    EVDO(12,"EVDO"),
    WLAN(20,"WLAN"),
    GAN_WLAN(21,"GAN WLAN"),
    WIMAX(25,"WiMAX"),
    NMT(50,"NMT"),
    AMPS(51,"AMPS"),
    NAMPS(52,"NAMPS"),
    DAMPS(53,"DAMPS"),
    ETACS(54,"ETACS"),
    IDEN(55,"iDEN"),
    PSTN(60,"PSTN"),
    ISDN(61,"ISDN"),
    DVB_H(65,"DVB-H");
    
    
    
    private Integer id;
    private String name;
    
    private TechnologySystems(Integer anId, String aName) {
        id = anId;
        name = aName;
    }
    
    public Integer getId(){
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public static TechnologySystems getSystemById(Integer anId){
        for(TechnologySystems system : values()){
            if(system.getId().equals(anId)){
                return system;
            }
        }
        throw new IllegalArgumentException("Unknown system id <"+anId+">.");
    }
    
    @Override
    public String toString() {
        return name;
    }
}
