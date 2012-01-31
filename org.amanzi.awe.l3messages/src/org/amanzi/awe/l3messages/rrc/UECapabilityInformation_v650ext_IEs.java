
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
    @ASN1Sequence ( name = "UECapabilityInformation_v650ext_IEs", isSet = false )
    public class UECapabilityInformation_v650ext_IEs implements IASN1PreparedElement {
            
        @ASN1Element ( name = "ue-RadioAccessCapability-v650ext", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private UE_RadioAccessCapability_v650ext ue_RadioAccessCapability_v650ext = null;
                
  
        
        public UE_RadioAccessCapability_v650ext getUe_RadioAccessCapability_v650ext () {
            return this.ue_RadioAccessCapability_v650ext;
        }

        

        public void setUe_RadioAccessCapability_v650ext (UE_RadioAccessCapability_v650ext value) {
            this.ue_RadioAccessCapability_v650ext = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(UECapabilityInformation_v650ext_IEs.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            