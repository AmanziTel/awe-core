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

package org.amanzi.awe.afp.ericsson.parser;

import java.util.Arrays;

import org.amanzi.awe.afp.ericsson.parser.NetworkConfigurationParser.BSA_MODE;
import org.amanzi.neo.loader.core.parser.BaseTransferData;


/**
 * <p>
 * Network Configuration Transfer Data
 * </p>.
 *
 * @author TsAr
 * @since 1.0.0
 */
public class NetworkConfigurationTransferData extends BaseTransferData{

    /** long serialVersionUID field. */
    private static final long serialVersionUID = 2931945506259938193L;

    /** The type. */
    private NetworkConfigurationFileTypes type;
    
    /** The headers. */
    private String[]headersData;
    
    /** The values. */
    private String[]valuesData;

    private String bsc;

    private BSA_MODE mode;

    private String tg;

    private String fhop;

    private String cell;

    private Integer ch_group;


    /**
     * Gets the headers.
     *
     * @return the headers
     */
    public String[] getHeaders() {
        return headersData;
    }

    /**
     * Sets the headers.
     *
     * @param headers the new headers
     */
    public void setHeaders(String[] headers) {
        this.headersData = Arrays.copyOf(headers,headers.length);
    }

    /**
     * Gets the values.
     *
     * @return the values
     */
    public String[] getValuesData() {
        return valuesData;
    }

    /**
     * Sets the values.
     *
     * @param values the new values
     */
    public void setValuesData(String[] values) {
        this.valuesData = Arrays.copyOf(values,values.length);
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public NetworkConfigurationFileTypes getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(NetworkConfigurationFileTypes type) {
        this.type = type;
    }

    /**
     *
     * @param object
     */
    public void setBsc(String   bsc) {
        this.bsc = bsc;
    }

    public String getBsc() {
        return bsc;
    }

    /**
     *
     * @param mode
     */
    public void setMode(BSA_MODE mode) {
        this.mode = mode;
    }

    public BSA_MODE getMode() {
        return mode;
    }

    /**
     *
     * @param tg
     */
    public void setTG(String tg) {
        this.tg = tg;
    }

    /**
     *
     * @param fhop
     */
    public void setFhop(String fhop) {
        this.fhop = fhop;
    }

    public String getTg() {
        return tg;
    }

    public void setTg(String tg) {
        this.tg = tg;
    }

    public String getFhop() {
        return fhop;
    }

    /**
     *
     * @param cell
     */
    public void setCell(String cell) {
        this.cell = cell;
    }

    /**
     *
     * @param valueOf
     */
    public void setGroup(Integer ch_group) {
        this.ch_group = ch_group;
    }

    public Integer getChGroup() {
        return ch_group;
    }

    public void setCh_group(Integer ch_group) {
        this.ch_group = ch_group;
    }

    public String getCell() {
        return cell;
    }
    
    
    

}
