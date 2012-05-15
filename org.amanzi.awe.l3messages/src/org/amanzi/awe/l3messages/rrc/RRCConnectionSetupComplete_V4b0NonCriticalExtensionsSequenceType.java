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
   @ASN1Sequence ( name = "v4b0NonCriticalExtensions" , isSet = false )
   public class RRCConnectionSetupComplete_V4b0NonCriticalExtensionsSequenceType implements IASN1PreparedElement {
            
    @ASN1Element ( name = "rrcConnectionSetupComplete-v4b0ext", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )

private RRCConnectionSetupComplete_v4b0ext_IEs rrcConnectionSetupComplete_v4b0ext = null;
            


   @ASN1Element ( name = "v590NonCriticalExtensions", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )

private RRCConnectionSetupComplete_V590NonCriticalExtensionsSequenceType v590NonCriticalExtensions = null;
            

    
    public RRCConnectionSetupComplete_v4b0ext_IEs getRrcConnectionSetupComplete_v4b0ext () {
        return this.rrcConnectionSetupComplete_v4b0ext;
    }

    

    public void setRrcConnectionSetupComplete_v4b0ext (RRCConnectionSetupComplete_v4b0ext_IEs value) {
        this.rrcConnectionSetupComplete_v4b0ext = value;
    }
    

    
    public RRCConnectionSetupComplete_V590NonCriticalExtensionsSequenceType getV590NonCriticalExtensions () {
        return this.v590NonCriticalExtensions;
    }

    
    public boolean isV590NonCriticalExtensionsPresent () {
        return this.v590NonCriticalExtensions != null;
    }
    

    public void setV590NonCriticalExtensions (RRCConnectionSetupComplete_V590NonCriticalExtensionsSequenceType value) {
        this.v590NonCriticalExtensions = value;
    }
    

            
            
    public void initWithDefaults() {
        
    }

    public IASN1PreparedElementData getPreparedData() {
        return preparedData_V4b0NonCriticalExtensionsSequenceType;
    }

   private static IASN1PreparedElementData preparedData_V4b0NonCriticalExtensionsSequenceType = CoderFactory.getInstance().newPreparedElementData(RRCConnectionSetupComplete_V4b0NonCriticalExtensionsSequenceType.class);
            
   }