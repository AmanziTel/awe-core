
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
    @ASN1BoxedType ( name = "UE_RadioAccessCapabBandFDDList_ext" )
    public class UE_RadioAccessCapabBandFDDList_ext implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 8L 
		
	   )
	   
            @ASN1SequenceOf( name = "UE-RadioAccessCapabBandFDDList-ext" , isSetOf = false)
	    private java.util.Collection<UE_RadioAccessCapabBandFDD_ext> value = null; 
    
            public UE_RadioAccessCapabBandFDDList_ext () {
            }
        
            public UE_RadioAccessCapabBandFDDList_ext ( java.util.Collection<UE_RadioAccessCapabBandFDD_ext> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<UE_RadioAccessCapabBandFDD_ext> value) {
                this.value = value;
            }
            
            public java.util.Collection<UE_RadioAccessCapabBandFDD_ext> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<UE_RadioAccessCapabBandFDD_ext>()); 
            }
            
            public void add(UE_RadioAccessCapabBandFDD_ext item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(UE_RadioAccessCapabBandFDDList_ext.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            