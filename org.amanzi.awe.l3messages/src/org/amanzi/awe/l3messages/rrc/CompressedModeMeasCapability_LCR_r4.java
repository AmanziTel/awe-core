
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
    @ASN1Sequence ( name = "CompressedModeMeasCapability_LCR_r4", isSet = false )
    public class CompressedModeMeasCapability_LCR_r4 implements IASN1PreparedElement {
            @ASN1Boolean( name = "" )
    
        @ASN1Element ( name = "tdd128-Measurements", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private Boolean tdd128_Measurements = null;
                
  
        
        public Boolean getTdd128_Measurements () {
            return this.tdd128_Measurements;
        }

        
        public boolean isTdd128_MeasurementsPresent () {
            return this.tdd128_Measurements != null;
        }
        

        public void setTdd128_Measurements (Boolean value) {
            this.tdd128_Measurements = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CompressedModeMeasCapability_LCR_r4.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            