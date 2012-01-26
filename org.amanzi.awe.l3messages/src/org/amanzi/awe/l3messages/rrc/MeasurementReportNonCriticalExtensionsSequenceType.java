/**
 * 
 */
package org.amanzi.awe.l3messages.rrc;

import org.bn.CoderFactory;
import org.bn.annotations.ASN1PreparedElement;
import org.bn.annotations.ASN1Sequence;
import org.bn.coders.IASN1PreparedElement;
import org.bn.coders.IASN1PreparedElementData;

@ASN1PreparedElement
   @ASN1Sequence ( name = "nonCriticalExtensions" , isSet = false )
   public class MeasurementReportNonCriticalExtensionsSequenceType implements IASN1PreparedElement {
            
            
            
    public void initWithDefaults() {
        
    }

    public IASN1PreparedElementData getPreparedData() {
        return preparedData_NonCriticalExtensionsSequenceType;
    }

   private static IASN1PreparedElementData preparedData_NonCriticalExtensionsSequenceType = CoderFactory.getInstance().newPreparedElementData(MeasurementReportNonCriticalExtensionsSequenceType.class);
            
   }