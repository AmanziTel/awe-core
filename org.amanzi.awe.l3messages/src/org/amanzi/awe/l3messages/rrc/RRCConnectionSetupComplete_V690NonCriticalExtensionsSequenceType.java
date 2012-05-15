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
   public class RRCConnectionSetupComplete_V690NonCriticalExtensionsSequenceType implements IASN1PreparedElement {
            
    @ASN1Element ( name = "rrcConnectionSetupComplete-v690ext", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )

private RRCConnectionSetupComplete_v690ext_IEs rrcConnectionSetupComplete_v690ext = null;
            


   @ASN1Element ( name = "nonCriticalExtensions", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )

private RRCConnectionSetupComplete_NonCriticalExtensionsSequenceType nonCriticalExtensions = null;
            

    
    public RRCConnectionSetupComplete_v690ext_IEs getRrcConnectionSetupComplete_v690ext () {
        return this.rrcConnectionSetupComplete_v690ext;
    }

    

    public void setRrcConnectionSetupComplete_v690ext (RRCConnectionSetupComplete_v690ext_IEs value) {
        this.rrcConnectionSetupComplete_v690ext = value;
    }
    

    
    public RRCConnectionSetupComplete_NonCriticalExtensionsSequenceType getNonCriticalExtensions () {
        return this.nonCriticalExtensions;
    }

    
    public boolean isNonCriticalExtensionsPresent () {
        return this.nonCriticalExtensions != null;
    }
    

    public void setNonCriticalExtensions (RRCConnectionSetupComplete_NonCriticalExtensionsSequenceType value) {
        this.nonCriticalExtensions = value;
    }
    

            
            
    public void initWithDefaults() {
        
    }

    public IASN1PreparedElementData getPreparedData() {
        return preparedData_V690NonCriticalExtensionsSequenceType;
    }

   private static IASN1PreparedElementData preparedData_V690NonCriticalExtensionsSequenceType = CoderFactory.getInstance().newPreparedElementData(RRCConnectionSetupComplete_V690NonCriticalExtensionsSequenceType.class);
            
   }