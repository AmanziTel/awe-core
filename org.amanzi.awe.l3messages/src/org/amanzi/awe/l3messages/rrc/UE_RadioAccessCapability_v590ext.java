
package org.amanzi.awe.l3messages.rrc;
//
// This file was generated by the BinaryNotes compiler.
// See http://bnotes.sourceforge.net 
// Any modifications to this file will be lost upon recompilation of the source ASN.1. 
//

import org.bn.*;
import org.bn.annotations.*;
import org.bn.annotations.constraints.*;
import org.bn.coders.*;
import org.bn.types.*;




    @ASN1PreparedElement
    @ASN1Sequence ( name = "UE_RadioAccessCapability_v590ext", isSet = false )
    public class UE_RadioAccessCapability_v590ext implements IASN1PreparedElement {
            
        @ASN1Element ( name = "dl-CapabilityWithSimultaneousHS-DSCHConfig", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private DL_CapabilityWithSimultaneousHS_DSCHConfig dl_CapabilityWithSimultaneousHS_DSCHConfig = null;
                
  
        @ASN1Element ( name = "pdcp-Capability-r5-ext", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private PDCP_Capability_r5_ext pdcp_Capability_r5_ext = null;
                
  
        @ASN1Element ( name = "rlc-Capability-r5-ext", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private RLC_Capability_r5_ext rlc_Capability_r5_ext = null;
                
  
        @ASN1Element ( name = "physicalChannelCapability", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private PhysicalChannelCapability_hspdsch_r5 physicalChannelCapability = null;
                
  
        @ASN1Element ( name = "multiModeRAT-Capability-v590ext", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private MultiModeRAT_Capability_v590ext multiModeRAT_Capability_v590ext = null;
                
  
        
        public DL_CapabilityWithSimultaneousHS_DSCHConfig getDl_CapabilityWithSimultaneousHS_DSCHConfig () {
            return this.dl_CapabilityWithSimultaneousHS_DSCHConfig;
        }

        
        public boolean isDl_CapabilityWithSimultaneousHS_DSCHConfigPresent () {
            return this.dl_CapabilityWithSimultaneousHS_DSCHConfig != null;
        }
        

        public void setDl_CapabilityWithSimultaneousHS_DSCHConfig (DL_CapabilityWithSimultaneousHS_DSCHConfig value) {
            this.dl_CapabilityWithSimultaneousHS_DSCHConfig = value;
        }
        
  
        
        public PDCP_Capability_r5_ext getPdcp_Capability_r5_ext () {
            return this.pdcp_Capability_r5_ext;
        }

        

        public void setPdcp_Capability_r5_ext (PDCP_Capability_r5_ext value) {
            this.pdcp_Capability_r5_ext = value;
        }
        
  
        
        public RLC_Capability_r5_ext getRlc_Capability_r5_ext () {
            return this.rlc_Capability_r5_ext;
        }

        

        public void setRlc_Capability_r5_ext (RLC_Capability_r5_ext value) {
            this.rlc_Capability_r5_ext = value;
        }
        
  
        
        public PhysicalChannelCapability_hspdsch_r5 getPhysicalChannelCapability () {
            return this.physicalChannelCapability;
        }

        

        public void setPhysicalChannelCapability (PhysicalChannelCapability_hspdsch_r5 value) {
            this.physicalChannelCapability = value;
        }
        
  
        
        public MultiModeRAT_Capability_v590ext getMultiModeRAT_Capability_v590ext () {
            return this.multiModeRAT_Capability_v590ext;
        }

        

        public void setMultiModeRAT_Capability_v590ext (MultiModeRAT_Capability_v590ext value) {
            this.multiModeRAT_Capability_v590ext = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(UE_RadioAccessCapability_v590ext.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            