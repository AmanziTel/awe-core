
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
    @ASN1BoxedType ( name = "GERANIu_MessageList" )
    public class GERANIu_MessageList implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 4L 
		
	   )
	   
            @ASN1SequenceOf( name = "GERANIu-MessageList" , isSetOf = false)
	    private java.util.Collection<GERANIu_MessageListEntry> value = null; 
    
            public GERANIu_MessageList () {
            }
        
            public GERANIu_MessageList ( java.util.Collection<GERANIu_MessageListEntry> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<GERANIu_MessageListEntry> value) {
                this.value = value;
            }
            
            public java.util.Collection<GERANIu_MessageListEntry> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<GERANIu_MessageListEntry>()); 
            }
            
            public void add(GERANIu_MessageListEntry item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(GERANIu_MessageList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            