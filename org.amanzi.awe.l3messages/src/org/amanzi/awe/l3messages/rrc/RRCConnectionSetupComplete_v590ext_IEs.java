
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
    @ASN1Sequence ( name = "RRCConnectionSetupComplete_v590ext_IEs", isSet = false )
    public class RRCConnectionSetupComplete_v590ext_IEs implements IASN1PreparedElement {
            
        @ASN1Element ( name = "ue-RadioAccessCapability-v590ext", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private UE_RadioAccessCapability_v590ext ue_RadioAccessCapability_v590ext = null;
                
  
        @ASN1Element ( name = "ue-RATSpecificCapability-v590ext", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private InterRAT_UE_RadioAccessCapability_v590ext ue_RATSpecificCapability_v590ext = null;
                
  
        
        public UE_RadioAccessCapability_v590ext getUe_RadioAccessCapability_v590ext () {
            return this.ue_RadioAccessCapability_v590ext;
        }

        
        public boolean isUe_RadioAccessCapability_v590extPresent () {
            return this.ue_RadioAccessCapability_v590ext != null;
        }
        

        public void setUe_RadioAccessCapability_v590ext (UE_RadioAccessCapability_v590ext value) {
            this.ue_RadioAccessCapability_v590ext = value;
        }
        
  
        
        public InterRAT_UE_RadioAccessCapability_v590ext getUe_RATSpecificCapability_v590ext () {
            return this.ue_RATSpecificCapability_v590ext;
        }

        
        public boolean isUe_RATSpecificCapability_v590extPresent () {
            return this.ue_RATSpecificCapability_v590ext != null;
        }
        

        public void setUe_RATSpecificCapability_v590ext (InterRAT_UE_RadioAccessCapability_v590ext value) {
            this.ue_RATSpecificCapability_v590ext = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(RRCConnectionSetupComplete_v590ext_IEs.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            