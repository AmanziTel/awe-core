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
   @ASN1Sequence ( name = "v5c0NonCriticalExtensions" , isSet = false )
   public class V5c0NonCriticalExtensionsSequenceType implements IASN1PreparedElement {
            
    @ASN1Element ( name = "ueCapabilityInformation-v5c0ext", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )

private UECapabilityInformation_v5c0ext ueCapabilityInformation_v5c0ext = null;
            


   @ASN1Element ( name = "v690NonCriticalExtensions", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )

private V690NonCriticalExtensionsSequenceType v690NonCriticalExtensions = null;
            

    
    public UECapabilityInformation_v5c0ext getUeCapabilityInformation_v5c0ext () {
        return this.ueCapabilityInformation_v5c0ext;
    }

    

    public void setUeCapabilityInformation_v5c0ext (UECapabilityInformation_v5c0ext value) {
        this.ueCapabilityInformation_v5c0ext = value;
    }
    

    
    public V690NonCriticalExtensionsSequenceType getV690NonCriticalExtensions () {
        return this.v690NonCriticalExtensions;
    }

    
    public boolean isV690NonCriticalExtensionsPresent () {
        return this.v690NonCriticalExtensions != null;
    }
    

    public void setV690NonCriticalExtensions (V690NonCriticalExtensionsSequenceType value) {
        this.v690NonCriticalExtensions = value;
    }
    

            
            
    public void initWithDefaults() {
        
    }

    public IASN1PreparedElementData getPreparedData() {
        return preparedData_V5c0NonCriticalExtensionsSequenceType;
    }

   private static IASN1PreparedElementData preparedData_V5c0NonCriticalExtensionsSequenceType = CoderFactory.getInstance().newPreparedElementData(V5c0NonCriticalExtensionsSequenceType.class);
            
   }