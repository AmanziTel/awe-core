
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
    @ASN1BoxedType ( name = "GPS_MeasurementParamList" )
    public class GPS_MeasurementParamList implements IASN1PreparedElement {
                
            @ASN1ValueRangeConstraint ( 
		
		min = 1L, 
		
		max = 16L 
		
	   )
	   
            @ASN1SequenceOf( name = "GPS-MeasurementParamList" , isSetOf = false)
	    private java.util.Collection<GPS_MeasurementParam> value = null; 
    
            public GPS_MeasurementParamList () {
            }
        
            public GPS_MeasurementParamList ( java.util.Collection<GPS_MeasurementParam> value ) {
                setValue(value);
            }
                        
            public void setValue(java.util.Collection<GPS_MeasurementParam> value) {
                this.value = value;
            }
            
            public java.util.Collection<GPS_MeasurementParam> getValue() {
                return this.value;
            }            
            
            public void initValue() {
                setValue(new java.util.LinkedList<GPS_MeasurementParam>()); 
            }
            
            public void add(GPS_MeasurementParam item) {
                value.add(item);
            }

	    public void initWithDefaults() {
	    }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(GPS_MeasurementParamList.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }


    }
            