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

package org.amanzi.awe.cassidian.structure;

import java.util.LinkedList;
import java.util.List;

import org.amanzi.awe.cassidian.constants.ChildTypes;

/**
 * <p>
 * describe commonTestData tag
 * </p>
 * 
 * @author Kondratenko_V
 * @since 1.0.0
 */
public class CommonTestData implements IXmlTag {
    List<ProbeIDNumberMap> probeIdNumberMap;
    List<ServingData> servingDatas;
    List<NeighborData> neighborDatas;
    List<Ntpq> ntpq;
    List<MPTSync> mptsync;

    public List<Ntpq> getNtpq() {
        return ntpq;
    }

    public void setNtpq(List<Ntpq> ntpq) {
        this.ntpq = ntpq;
    }

    public void setServingData(List<ServingData> servingDatas) {
        this.servingDatas = servingDatas;
    }

    /**
     * @return Returns the probeIdNumberMap.
     */
    public List<ProbeIDNumberMap> getProbeIdNumberMap() {
        return probeIdNumberMap;
    }

    public CommonTestData() {
        probeIdNumberMap = new LinkedList<ProbeIDNumberMap>();
        servingDatas = new LinkedList<ServingData>();
        neighborDatas = new LinkedList<NeighborData>();
        ntpq = new LinkedList<Ntpq>();
        mptsync = new LinkedList<MPTSync>();
    }

    /**
     * @param probeIdNumberMap The probeIdNumberMap to set.
     */
    public void setProbeIdNumberMap(List<ProbeIDNumberMap> probeIdNumberMap) {
        this.probeIdNumberMap = probeIdNumberMap;
    }

    public void addMemberToProbeIdNumberMap(ProbeIDNumberMap member) {
        probeIdNumberMap.add(member);
    }

    public void addMemberToMPTSync(MPTSync data) {
        mptsync.add(data);
    }

    public void addMemberToNtpq(Ntpq member) {
        ntpq.add(member);
    }

    /**
     * added member of ServingData
     * 
     * @param member
     */
    public void addMemberToServingData(ServingData member) {
        servingDatas.add(member);
    }

    @Override
    public String getType() {
        return ChildTypes.COMMON_TEST_DATA.getId();
    }

    @Override
    public void setValueByTagType(String tagName, Object value) {
        if (tagName.equals(ChildTypes.PROBE_ID_NUMBER_MAP.getId())) {
            addMemberToProbeIdNumberMap((ProbeIDNumberMap)value);
        } else if (tagName.equals(ChildTypes.SERVING_DATA.getId())) {
            addMemberToServingData((ServingData)value);
        } else if (tagName.equals(ChildTypes.NEIGHBOR_DATA.getId())) {
            addMembertoNeighborData((NeighborData)value);
        } else if (tagName.equals(ChildTypes.NTPQ.getId())) {
            addMemberToNtpq((Ntpq)value);
        } else if (tagName.equals(ChildTypes.MPT_SYNC.getId())) {
            addMemberToMPTSync((MPTSync)value);
        }
    }

    public void addMembertoNeighborData(NeighborData value) {
        neighborDatas.add(value);
    }

    @Override
    public Object getValueByTagType(String tagName) {
        if (tagName.equals(ChildTypes.PROBE_ID_NUMBER_MAP.getId())) {
            return probeIdNumberMap;
        }
        if (tagName.equals(ChildTypes.SERVING_DATA.getId())) {
            return servingDatas;
        }
        if (tagName.equals(ChildTypes.NEIGHBOR_DATA.getId())) {
            return neighborDatas;
        }
        if (tagName.equals(ChildTypes.NTPQ.getId())) {
            return ntpq;
        }
        if (tagName.equals(ChildTypes.MPT_SYNC.getId())) {
            return mptsync;
        }
        return null;
    }

    public List<ServingData> getServingData() {
        return servingDatas;
    }

    public void setServingDatas(List<ServingData> servingDatas) {
        this.servingDatas = servingDatas;
    }

    public List<NeighborData> getNeighborDatas() {
        return neighborDatas;
    }

    public void setNeighborDatas(List<NeighborData> neighborDatas) {
        this.neighborDatas = neighborDatas;
    }

    public List<MPTSync> getMptsync() {
        return mptsync;
    }

    public void setMptsync(List<MPTSync> mptsync) {
        this.mptsync = mptsync;
    }

    public List<ServingData> getServingDatas() {
        return servingDatas;
    }
}
