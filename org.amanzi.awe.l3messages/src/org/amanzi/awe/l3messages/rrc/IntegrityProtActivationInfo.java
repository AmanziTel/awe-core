
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
    @ASN1Sequence ( name = "IntegrityProtActivationInfo", isSet = false )
    public class IntegrityProtActivationInfo implements IASN1PreparedElement {
            
        @ASN1Element ( name = "rrc-MessageSequenceNumberList", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private RRC_MessageSequenceNumberList rrc_MessageSequenceNumberList = null;
                
  
        
        public RRC_MessageSequenceNumberList getRrc_MessageSequenceNumberList () {
            return this.rrc_MessageSequenceNumberList;
        }

        

        public void setRrc_MessageSequenceNumberList (RRC_MessageSequenceNumberList value) {
            this.rrc_MessageSequenceNumberList = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(IntegrityProtActivationInfo.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            