
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
    @ASN1BoxedType ( name = "CompressedModeMeasCapabFDDList2" )
    public class CompressedModeMeasCapabFDDList2 implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 8L 
		
	   )
	   
            @ASN1SequenceOf( name = "CompressedModeMeasCapabFDDList2" , isSetOf = false)
	    private java.util.Collection<CompressedModeMeasCapabFDD2> value = null; 
    
            public CompressedModeMeasCapabFDDList2 () {
            }
        
            public CompressedModeMeasCapabFDDList2 ( java.util.Collection<CompressedModeMeasCapabFDD2> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<CompressedModeMeasCapabFDD2> value) {
                this.value = value;
            }
            
            public java.util.Collection<CompressedModeMeasCapabFDD2> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<CompressedModeMeasCapabFDD2>()); 
            }
            
            public void add(CompressedModeMeasCapabFDD2 item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(CompressedModeMeasCapabFDDList2.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            