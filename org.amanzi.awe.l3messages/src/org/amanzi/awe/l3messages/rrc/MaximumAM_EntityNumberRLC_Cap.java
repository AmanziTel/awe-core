
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
    @ASN1Enum (
        name = "MaximumAM_EntityNumberRLC_Cap"
    )
    public class MaximumAM_EntityNumberRLC_Cap implements IASN1PreparedElement {        
        public enum EnumType {
            
            @ASN1EnumItem ( name = "dummy", hasTag = true , tag = 0 )
            dummy , 
            @ASN1EnumItem ( name = "am4", hasTag = true , tag = 1 )
            am4 , 
            @ASN1EnumItem ( name = "am5", hasTag = true , tag = 2 )
            am5 , 
            @ASN1EnumItem ( name = "am6", hasTag = true , tag = 3 )
            am6 , 
            @ASN1EnumItem ( name = "am8", hasTag = true , tag = 4 )
            am8 , 
            @ASN1EnumItem ( name = "am16", hasTag = true , tag = 5 )
            am16 , 
            @ASN1EnumItem ( name = "am30", hasTag = true , tag = 6 )
            am30 , 
        }
        
        private EnumType value;
        private Integer integerForm;
        
        public EnumType getValue() {
            return this.value;
        }
        
        public void setValue(EnumType value) {
            this.value = value;
        }
        
        public Integer getIntegerForm() {
            return integerForm;
        }
        
        public void setIntegerForm(Integer value) {
            integerForm = value;
        }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(MaximumAM_EntityNumberRLC_Cap.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            