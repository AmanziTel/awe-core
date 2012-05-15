/**
 * 
 */
package org.amanzi.awe.l3messages.rrc;

import org.bn.CoderFactory;
import org.bn.annotations.ASN1Element;
import org.bn.annotations.ASN1PreparedElement;
import org.bn.annotations.ASN1Sequence;
import org.bn.coders.IASN1PreparedElement;
import org.bn.coders.IASN1PreparedElementData;

@ASN1PreparedElement
   @ASN1Sequence ( name = "v690NonCriticalExtensions" , isSet = false )
   public class V690NonCriticalExtensionsSequenceType implements IASN1PreparedElement {
            
    @ASN1Element ( name = "ueCapabilityInformation-v690ext", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )

private UECapabilityInformation_v690ext_IEs ueCapabilityInformation_v690ext = null;
            


   @ASN1Element ( name = "nonCriticalExtensions", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )

private NonCriticalExtensionsSequenceType nonCriticalExtensions = null;
            

    
    public UECapabilityInformation_v690ext_IEs getUeCapabilityInformation_v690ext () {
        return this.ueCapabilityInformation_v690ext;
    }

    

    public void setUeCapabilityInformation_v690ext (UECapabilityInformation_v690ext_IEs value) {
        this.ueCapabilityInformation_v690ext = value;
    }
    

    
    public NonCriticalExtensionsSequenceType getNonCriticalExtensions () {
        return this.nonCriticalExtensions;
    }

    
    public boolean isNonCriticalExtensionsPresent () {
        return this.nonCriticalExtensions != null;
    }
    

    public void setNonCriticalExtensions (NonCriticalExtensionsSequenceType value) {
        this.nonCriticalExtensions = value;
    }
    

            
            
    public void initWithDefaults() {
        
    }

    public IASN1PreparedElementData getPreparedData() {
        return preparedData_V690NonCriticalExtensionsSequenceType;
    }

   private static IASN1PreparedElementData preparedData_V690NonCriticalExtensionsSequenceType = CoderFactory.getInstance().newPreparedElementData(V690NonCriticalExtensionsSequenceType.class);
            
   }