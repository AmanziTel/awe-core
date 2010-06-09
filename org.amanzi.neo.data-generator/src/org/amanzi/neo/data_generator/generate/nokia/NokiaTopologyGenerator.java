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

package org.amanzi.neo.data_generator.generate.nokia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.amanzi.neo.data_generator.data.IGeneratedData;
import org.amanzi.neo.data_generator.data.nokia.AbstractTagData;
import org.amanzi.neo.data_generator.data.nokia.BSCData;
import org.amanzi.neo.data_generator.data.nokia.ExternalCellData;
import org.amanzi.neo.data_generator.data.nokia.NokiaDataConstants;
import org.amanzi.neo.data_generator.data.nokia.NokiaTopologyData;
import org.amanzi.neo.data_generator.data.nokia.SectorData;
import org.amanzi.neo.data_generator.data.nokia.SiteData;
import org.amanzi.neo.data_generator.generate.IDataGenerator;
import org.amanzi.neo.data_generator.utils.RandomValueGenerator;
import org.amanzi.neo.data_generator.utils.nokia.NokiaFileBuilder;
import org.amanzi.neo.data_generator.utils.xml_data.SavedTag;

/**
 * <p>
 * DataGenerator for NokiaTopology.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class NokiaTopologyGenerator implements IDataGenerator {
    
    private String fileName;
    private String path;
    
    private Integer bscCount;
    private Integer siteCount;
    private Integer sectorCount;
    private Integer umtsCount;
    private Float[] latitudeBorders;
    private Float[] longitudeBorders;
    
    private List<SectorData> sectorsList;
    
    private long nameCounter;
    private long idCounter;
    
    /**
     * Constructor.
     * @param aPath String (path to save file)
     * @param aFileName String (file name)
     * @param bscs Integer (BSCs count) 
     * @param sites Integer (maximum sites count for one BSC)
     * @param sectors Integer (maximum sectors count for one site)
     * @param extUmtsCount Integer (external UMTS sectors count)
     * @param latBorders Float[] (must be like {min_latitude,max_latitude}) 
     * @param lonBorders Float[] (must be like {min_longitude,max_longitude})
     */
    public NokiaTopologyGenerator(String aPath, String aFileName, Integer bscs, Integer sites, Integer sectors, Integer extUmtsCount,
            Float[] latBorders, Float[] lonBorders) {
        path = aPath;
        fileName = aFileName;
        bscCount = bscs;
        siteCount = sites;
        sectorCount = sectors;
        umtsCount = extUmtsCount;
        latitudeBorders = latBorders;
        longitudeBorders = lonBorders;
        nameCounter = 0;
        idCounter = 0;
    }
    
    @Override
    public IGeneratedData generate() {
        NokiaTopologyData result = initData();
        SavedTag root = buildTagsByData(result);
        saveData(root);
        return result;
    }
    
    /**
     * Initialize data.
     *
     * @return NokiaTopologyData
     */
    private NokiaTopologyData initData(){
        NokiaTopologyData result = new NokiaTopologyData();
        if(bscCount<1){
            return result;
        }
        sectorsList = new ArrayList<SectorData>();
        List<BSCData> bscList = new ArrayList<BSCData>(bscCount);
        for(int i=0; i<bscCount; i++){
            bscList.add(initBSC(i));
        }
        result.setBscList(bscList);
        if(umtsCount>0){
            String distName = NokiaDataConstants.MO_ATTR_PLMN_PREFIX+"/"+NokiaDataConstants.MO_EXCC+"-1";
            String id = getMoId();
            ExternalCellData externalCell = new ExternalCellData(distName, id);            
            for(int i= 0; i<umtsCount; i++){
                externalCell.addSectror(initSector(distName, NokiaDataConstants.MO_EWCE, i+1, 0, 0));
            }
            result.setExternalCell(externalCell);
        }
        initNeighbours();
        return result;
    }
    
    /**
     * Initialize BCS. 
     *
     * @param number int
     * @return BSCData
     */
    private BSCData initBSC(int number){
        String distName = NokiaDataConstants.MO_ATTR_PLMN_PREFIX+"/"+NokiaDataConstants.MO_BSC+"-"+number;
        BSCData result = new BSCData(distName, getMoId());
        result.addProperty(NokiaDataConstants.P_ATTR_NAME, buildName(NokiaDataConstants.MO_BSC));
        int balCount = getRandomGenerator().getIntegerValue(1, 10);
        for(int i=0; i<balCount; i++){
            int freqCount = getRandomGenerator().getIntegerValue(1, 20); 
            for (int j = 0; j < freqCount; j++) {
                result.addBalFrequency(i, getRandomFrequency());
            }
        }
        int malCount = getRandomGenerator().getIntegerValue(1, 10);
        for(int i=0; i<malCount; i++){
            int freqCount = getRandomGenerator().getIntegerValue(1, 20); 
            for (int j = 0; j < freqCount; j++) {
                result.addMalFrequency(i, getRandomFrequency());
            }
        }
        balCount = getFrecCount(result.getBalFrequency());
        malCount = getFrecCount(result.getMalFrequency());
        int realSiteCount = getRandomGenerator().getIntegerValue(1, siteCount);
        for (int i = 0; i < realSiteCount; i++) {
            result.addSite(initSite(distName, i+1,balCount,malCount));
        }        
        return result;
    }
    
    private int getRandomFrequency(){
        return getRandomGenerator().getLongValue(1L, 999L).intValue();
    }
    
    private Integer getFrecCount(HashMap<Integer, Set<Integer>> freqs){
        Set<Integer> all = new HashSet<Integer>();
        for(Integer key : freqs.keySet()){
            all.addAll(freqs.get(key));
        }
        return all.size();
    }
    
    /**
     * Initialize site
     *
     * @param parentDist String (parent distName)
     * @param number int
     * @return SiteData
     */
    private SiteData initSite(String parentDist, int number, int balCount, int malCount){
        String distName = parentDist+"/"+NokiaDataConstants.MO_BCF+"-"+number;
        Float lat = getRandomGenerator().getDoubleValue(latitudeBorders[0].doubleValue(), latitudeBorders[1].doubleValue()).floatValue();
        Float lon = getRandomGenerator().getDoubleValue(longitudeBorders[0].doubleValue(), longitudeBorders[1].doubleValue()).floatValue();
        SiteData result = new SiteData(distName, getMoId(), lat, lon);
        result.addProperty(NokiaDataConstants.P_ATTR_NAME, buildName(NokiaDataConstants.MO_BCF));
        result.addProperty("latitude", "0");
        result.addProperty("longitude", "0");
        int realSectorCount = getRandomGenerator().getIntegerValue(1, sectorCount);
        for (int i = 0; i < realSectorCount; i++) {
            result.addSectror(initSector(distName, NokiaDataConstants.MO_BTS, i+1, balCount, malCount));
        }
        return result;
    }
    
    /**
     * Initialize sector.
     *
     * @param parentDist
     * @param className
     * @param number
     * @return SectorData
     */
    private SectorData initSector(String parentDist, String className, int number, int balCount, int malCount){
        String distName = parentDist+"/"+className+"-"+number;
        SectorData result;        
        if(className.equals(NokiaDataConstants.MO_BTS)){
            Integer azimuth = getRandomGenerator().getIntegerValue(0, 359);
            Integer beamwidth = getRandomGenerator().getIntegerValue(10, 360);
            result = new SectorData(className, distName, getMoId(),azimuth,beamwidth);
            result.addProperty("idleStateBcchAllocListId", getRandomGenerator().getIntegerValue(0, balCount-1).toString());
            result.addProperty("usedMobileAllocIdUsed", getRandomGenerator().getIntegerValue(0, malCount-1).toString());
            result.addProperty("underlayMaIdUsed", getRandomGenerator().getIntegerValue(0, malCount-1).toString());
        }else{
            result = new SectorData(className, distName, getMoId());
        }            
        result.addProperty(NokiaDataConstants.P_ATTR_NAME, buildName(className));
        sectorsList.add(result);
        return result;
    }
    
    /**
     * Initialize neighbors.
     */
    private void initNeighbours(){
        int allSectorCount = sectorsList.size();
        if(allSectorCount==1){
            return;
        }
        for(int i=0; i<allSectorCount; i++){
            SectorData sector = sectorsList.get(i);
            if(sector.getClassName().equals(NokiaDataConstants.MO_EWCE)){
                continue;
            }
            RandomValueGenerator randomGenerator = getRandomGenerator();
            int neibCount = randomGenerator.getIntegerValue(0, allSectorCount-1);
            for(int j=0; j<neibCount; j++){
                int neibNum = randomGenerator.getIntegerValue(0, allSectorCount-1);
                while(neibNum == i){
                    neibNum = randomGenerator.getIntegerValue(0, allSectorCount-1);
                }
                sector.addNeighbor(sectorsList.get(neibNum));
            }
        }
    }
    
    /**
     * Build random name
     *
     * @param className String
     * @return String
     */
    private String buildName(String className){
        return "TST"+className+(nameCounter++);
    }
    
    /**
     * Build tags by data.
     *
     * @param aData NokiaTopologyData
     * @return SavedTag (root tag)
     */
    private SavedTag buildTagsByData(NokiaTopologyData aData){
        SavedTag raml = new SavedTag(NokiaDataConstants.TAG_RAML, false);
        raml.addAttribute(NokiaDataConstants.ATTR_VERSION, "2.0");
        raml.addAttribute("xmlns", "raml20.xsd");
        
        SavedTag cmData = new SavedTag(NokiaDataConstants.TAG_CM_DATA, false);
        cmData.addAttribute("type", "actual");
        
        SavedTag header = new SavedTag(NokiaDataConstants.TAG_HEADER, false);
        SavedTag log = new SavedTag(NokiaDataConstants.TAG_LOG, true);
        log.addAttribute("dateTime", new Date().toString());
        log.addAttribute("action", "created");
        log.addAttribute("appInfo", "ActualExporter");
        header.addInnerTag(log);
        cmData.addInnerTag(header);
        
        ExternalCellData externalCell = aData.getExternalCell();        
        if(externalCell != null){
            SavedTag EXCC = getMOTag(externalCell);
            cmData.addInnerTag(EXCC);
            for(SectorData ewceData : externalCell.getSectors()){
                SavedTag EWCE = getMOTag(ewceData);
                cmData.addInnerTag(EWCE);
            }
        }
        
        List<BSCData> allBSC = aData.getBscList();
        HashMap<SavedTag,List<SavedTag>> locationsMap = new HashMap<SavedTag,List<SavedTag>>();
        List<SavedTag> neighbors = new ArrayList<SavedTag>();
        for(BSCData data : allBSC){
            SavedTag BSC = getMOTag(data);
            cmData.addInnerTag(BSC);
            SavedTag SMLS = getSMLCTag(data);
            String smlsName = SMLS.getAttribute(NokiaDataConstants.MO_ATTR_DIST_NAME);
            int locNum = 1;
            List<SavedTag> locations = new ArrayList<SavedTag>();
            HashMap<Integer, Set<Integer>> balFrequency = data.getBalFrequency();
            for(Integer balNum : balFrequency.keySet()){
               SavedTag bal = getFreqTag(data.getDistName(), NokiaDataConstants.MO_BAL, balNum+1, balFrequency.get(balNum));
               cmData.addInnerTag(bal);
            }
            HashMap<Integer, Set<Integer>> malFrequency = data.getMalFrequency();
            for(Integer malNum : malFrequency.keySet()){
               SavedTag mal = getFreqTag(data.getDistName(), NokiaDataConstants.MO_MAL, malNum+1, malFrequency.get(malNum));
               cmData.addInnerTag(mal);
            }
            for(SiteData siteData : data.getSites()){
                SavedTag BCF = getMOTag(siteData);
                cmData.addInnerTag(BCF);
                for(SectorData sectorData : siteData.getSectors()){
                    SavedTag BTS = getMOTag(sectorData);
                    cmData.addInnerTag(BTS);
                    locations.add(getLCSETag(siteData, sectorData,smlsName,locNum++));
                    int neibNum = 1;
                    for(SectorData neibData : sectorData.getNeighbors()){
                        neighbors.add(getNeighborTag(sectorData, neibData,neibNum++));
                    }
                }
                if(!locations.isEmpty()){
                    locationsMap.put(SMLS, locations);
                }
            }
        }
        
        for(SavedTag smls : locationsMap.keySet()){
            cmData.addInnerTag(smls);
            for(SavedTag loc : locationsMap.get(smls)){
                cmData.addInnerTag(loc);
            }
        }
        
        
        for(SavedTag neib : neighbors){
            cmData.addInnerTag(neib);
        }
        
        raml.addInnerTag(cmData);
        return raml;
    }
    
    /**
     * Returns 'managedObject' tag by data.
     *
     * @param aData AbstractTagData
     * @return SavedTag
     */
    private SavedTag getMOTag(AbstractTagData aData){
        return getMOTag(aData.getClassName(),aData.getDistName(),aData.getId(),aData.getProperties());
    }
    
    /**
     * Returns 'managedObject' tag by data.
     *
     * @param className  String (value for tag attribute 'class') 
     * @param distName String (value for tag attribute 'distName')
     * @param id String (value for tag attribute 'id')
     * @param properties Map of properties.
     * @return SavedTag
     */
    private SavedTag getMOTag(String className,String distName,String id, HashMap<String, String> properties){
        SavedTag result = new SavedTag(NokiaDataConstants.TAG_MO, properties.isEmpty());
        result.addAttribute(NokiaDataConstants.ATTR_VERSION, "1.0");
        result.addAttribute(NokiaDataConstants.MO_ATTR_CLASS, className);
        result.addAttribute(NokiaDataConstants.MO_ATTR_DIST_NAME, distName);
        result.addAttribute(NokiaDataConstants.MO_ATTR_ID, id);
        for(String name : properties.keySet()){
            SavedTag prop = getPropertyTag(name, properties.get(name));
            result.addInnerTag(prop);
        }
        return result;
    }
    
    /**
     * Returns 'managedObject' tag by data.
     *
     * @param className  String (value for tag attribute 'class') 
     * @param distName String (value for tag attribute 'distName')
     * @param id String (value for tag attribute 'id')
     * @param properties Map of properties.
     * @return SavedTag
     */
    private SavedTag getMOTag(String className,String distName,String id, HashMap<String, String> properties,HashMap<String, Set<Integer>> lists){
        SavedTag result = new SavedTag(NokiaDataConstants.TAG_MO, false);
        result.addAttribute(NokiaDataConstants.ATTR_VERSION, "1.0");
        result.addAttribute(NokiaDataConstants.MO_ATTR_CLASS, className);
        result.addAttribute(NokiaDataConstants.MO_ATTR_DIST_NAME, distName);
        result.addAttribute(NokiaDataConstants.MO_ATTR_ID, id);
        for(String name : properties.keySet()){
            SavedTag prop = getPropertyTag(name, properties.get(name));
            result.addInnerTag(prop);
        }
        for(String name : lists.keySet()){
            SavedTag list = getListTag(name, lists.get(name));
            result.addInnerTag(list);
        }
        return result;
    }
    
    /**
     * Get tag for property.
     *
     * @param name String
     * @param value String
     * @return SavedTag
     */
    private SavedTag getPropertyTag(String name, String value){
        SavedTag prop = new SavedTag(NokiaDataConstants.TAG_PROPERTY, false);
        if (name!=null && name.length()>0) {
            prop.addAttribute(NokiaDataConstants.P_ATTR_NAME, name);
        }
        prop.setData(value);
        return prop;
    }
    
    /**
     * Get tag for property.
     *
     * @param name String
     * @param value String
     * @return SavedTag
     */
    private SavedTag getListTag(String name, Set<Integer> values){
        SavedTag list = new SavedTag(NokiaDataConstants.TAG_LIST, false);
        list.addAttribute(NokiaDataConstants.P_ATTR_NAME, name);
        for(Integer value : values){
            SavedTag prop = getPropertyTag(null, value.toString());
            list.addInnerTag(prop);
        }
        return list;
    }
    
    /**
     * Build parent for location tags.
     *
     * @param bsc BSCData
     * @return SavedTag
     */
    private SavedTag getSMLCTag(BSCData bsc){
        String distName = bsc.getDistName()+"/"+NokiaDataConstants.MO_SMLC+"-1";
        String id = getMoId();
        HashMap<String, String> properties = new HashMap<String, String>();
        String bscName = bsc.getProperties().get(NokiaDataConstants.P_ATTR_NAME);
        properties.put(NokiaDataConstants.P_ATTR_NAME, bscName);
        properties.put(NokiaDataConstants.PROP_ADDRES, bscName);
        return getMOTag(NokiaDataConstants.MO_SMLC,distName,id,properties);
    }

    /**
     * Returns random id for managedObject;
     *
     * @return String
     */
    private String getMoId() {
        String id = ""+(idCounter++);
        return id;
    }
    
    /**
     * Build location tag.
     *
     * @param site SiteData
     * @param sector SectorData
     * @param smlcName String
     * @param number int
     * @return SavedTag
     */
    private SavedTag getLCSETag(SiteData site, SectorData sector, String smlcName, int number){
        String distName = smlcName+"/"+NokiaDataConstants.MO_LCSE+"-"+number;
        String id = getMoId();
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("linkedCellDN", sector.getDistName());
        properties.put("antBearing", sector.getAzimuth().toString());
        properties.put("antHorHalfPwrBeam", sector.getBeamwidth().toString());
        Float lat = site.getLatitude();
        Integer degr = lat.intValue();
        properties.put("latDegrees", degr.toString());
        Float rest = (lat-degr)*60.0f;
        Integer min = rest.intValue();
        properties.put("latMinutes", min.toString());
        rest = (rest-min)*60.0f;
        Integer sec = rest.intValue();
        properties.put("latSeconds", sec.toString());
        Float lon = site.getLongitude();
        degr = lon.intValue();
        properties.put("lonDegrees", degr.toString());
        rest = (lon-degr)*60.0f;
        min = rest.intValue();
        properties.put("lonMinutes", min.toString());
        rest = (rest-min)*60.0f;
        sec = rest.intValue();
        properties.put("lonSeconds", sec.toString());        
        return getMOTag(NokiaDataConstants.MO_LCSE,distName,id,properties);
    }
    
    /**
     * Build neighbor tag.
     *
     * @param sector SectorData
     * @param neighbor SectorData
     * @return SavedTag
     */
    private SavedTag getNeighborTag(SectorData sector, SectorData neighbor, int number){
        String className = neighbor.getClassName().equals(NokiaDataConstants.MO_BTS)?NokiaDataConstants.MO_ADCE:NokiaDataConstants.MO_ADJW;
        String distName = sector.getDistName()+"/"+className+"-"+number;
        String id = getMoId();
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("targetCellDN", neighbor.getDistName());
        properties.put(NokiaDataConstants.P_ATTR_NAME, neighbor.getProperties().get(NokiaDataConstants.P_ATTR_NAME));
        return getMOTag(className,distName,id,properties);
    }
    
    /**
     * Build BAL or MAL tag
     *
     * @param parentName String
     * @param className String
     * @param number int
     * @param freqs Set of Integer
     * @return SavedTag
     */
    private SavedTag getFreqTag(String parentName, String className, int number, Set<Integer> freqs){
        String distName = parentName+"/"+className+"-"+number;
        String id = getMoId();
        HashMap<String, String> properties = new HashMap<String, String>();
        HashMap<String, Set<Integer>> lists = new HashMap<String, Set<Integer>>();
        lists.put("frequency", freqs);
        return getMOTag(className, distName, id, properties, lists);
    }
    
    /**
     * Save data to file.
     *
     * @param aRoot SavedTag (root tag)
     */
    private void saveData(SavedTag aRoot){
        NokiaFileBuilder fileBuilder = new NokiaFileBuilder(path, fileName);
        try {
            fileBuilder.saveData(aRoot);
        } catch (IOException e) {
            throw (RuntimeException) new RuntimeException().initCause( e );
        }
    }
    
    /**
     * Getter for random generator.
     *
     * @return {@link RandomValueGenerator}
     */
    protected RandomValueGenerator getRandomGenerator() {
        return RandomValueGenerator.getGenerator();
    }
}
