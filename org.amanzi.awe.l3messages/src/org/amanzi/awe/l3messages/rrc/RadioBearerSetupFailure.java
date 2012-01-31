
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
    @ASN1Sequence ( name = "RadioBearerSetupFailure", isSet = false )
    public class RadioBearerSetupFailure implements IASN1PreparedElement {
            
        @ASN1Element ( name = "rrc-TransactionIdentifier", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private RRC_TransactionIdentifier rrc_TransactionIdentifier = null;
                
  
        @ASN1Element ( name = "failureCause", isOptional =  false , hasTag =  false  , hasDefaultValue =  false  )
    
	private FailureCauseWithProtErr failureCause = null;
                
  
        @ASN1Element ( name = "potentiallySuccesfulBearerList", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private RB_IdentityList potentiallySuccesfulBearerList = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "laterNonCriticalExtensions" , isSet = false )
       public static class LaterNonCriticalExtensionsSequenceType implements IASN1PreparedElement {
                @ASN1BitString( name = "" )
    
        @ASN1Element ( name = "radioBearerSetupFailure-r3-add-ext", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private BitString radioBearerSetupFailure_r3_add_ext = null;
                
  

       @ASN1PreparedElement
       @ASN1Sequence ( name = "nonCriticalExtensions" , isSet = false )
       public static class NonCriticalExtensionsSequenceType implements IASN1PreparedElement {
                
                
                
        public void initWithDefaults() {
            
        }

        public IASN1PreparedElementData getPreparedData() {
            return preparedData_NonCriticalExtensionsSequenceType;
        }

       private static IASN1PreparedElementData preparedData_NonCriticalExtensionsSequenceType = CoderFactory.getInstance().newPreparedElementData(NonCriticalExtensionsSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "nonCriticalExtensions", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private NonCriticalExtensionsSequenceType nonCriticalExtensions = null;
                
  
        
        public BitString getRadioBearerSetupFailure_r3_add_ext () {
            return this.radioBearerSetupFailure_r3_add_ext;
        }

        
        public boolean isRadioBearerSetupFailure_r3_add_extPresent () {
            return this.radioBearerSetupFailure_r3_add_ext != null;
        }
        

        public void setRadioBearerSetupFailure_r3_add_ext (BitString value) {
            this.radioBearerSetupFailure_r3_add_ext = value;
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
            return preparedData_LaterNonCriticalExtensionsSequenceType;
        }

       private static IASN1PreparedElementData preparedData_LaterNonCriticalExtensionsSequenceType = CoderFactory.getInstance().newPreparedElementData(LaterNonCriticalExtensionsSequenceType.class);
                
       }

       
                
        @ASN1Element ( name = "laterNonCriticalExtensions", isOptional =  true , hasTag =  false  , hasDefaultValue =  false  )
    
	private LaterNonCriticalExtensionsSequenceType laterNonCriticalExtensions = null;
                
  
        
        public RRC_TransactionIdentifier getRrc_TransactionIdentifier () {
            return this.rrc_TransactionIdentifier;
        }

        

        public void setRrc_TransactionIdentifier (RRC_TransactionIdentifier value) {
            this.rrc_TransactionIdentifier = value;
        }
        
  
        
        public FailureCauseWithProtErr getFailureCause () {
            return this.failureCause;
        }

        

        public void setFailureCause (FailureCauseWithProtErr value) {
            this.failureCause = value;
        }
        
  
        
        public RB_IdentityList getPotentiallySuccesfulBearerList () {
            return this.potentiallySuccesfulBearerList;
        }

        
        public boolean isPotentiallySuccesfulBearerListPresent () {
            return this.potentiallySuccesfulBearerList != null;
        }
        

        public void setPotentiallySuccesfulBearerList (RB_IdentityList value) {
            this.potentiallySuccesfulBearerList = value;
        }
        
  
        
        public LaterNonCriticalExtensionsSequenceType getLaterNonCriticalExtensions () {
            return this.laterNonCriticalExtensions;
        }

        
        public boolean isLaterNonCriticalExtensionsPresent () {
            return this.laterNonCriticalExtensions != null;
        }
        

        public void setLaterNonCriticalExtensions (LaterNonCriticalExtensionsSequenceType value) {
            this.laterNonCriticalExtensions = value;
        }
        
  
                    
        
        public void initWithDefaults() {
            
        }

        private static IASN1PreparedElementData preparedData = CoderFactory.getInstance().newPreparedElementData(RadioBearerSetupFailure.class);
        public IASN1PreparedElementData getPreparedData() {
            return preparedData;
        }

            
    }
            