/*
 * Copyright 2006 Abdulla G. Abdurakhmanov (abdulla.abdurakhmanov@gmail.com).
 * 
 * Licensed under the LGPL, Version 2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.gnu.org/copyleft/lgpl.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * With any your questions welcome to my e-mail 
 * or blog at http://abdulla-a.blogspot.com.
 */

package org.bn.metadata;

import java.io.InputStream;
import java.io.OutputStream;

import org.bn.annotations.ASN1Boolean;
import org.bn.coders.DecodedObject;
import org.bn.coders.ElementInfo;
import org.bn.coders.IASN1TypesDecoder;
import org.bn.coders.IASN1TypesEncoder;

/**
 * @author jcfinley@users.sourceforge.net
 */
public class ASN1BooleanMetadata extends ASN1FieldMetadata {

    public ASN1BooleanMetadata() {
        
    }
    
    public ASN1BooleanMetadata(String name)
    {
        super(name);
    }
    
    public ASN1BooleanMetadata(ASN1Boolean annotation) {
        this(annotation.name());
    }
    
    public int encode(IASN1TypesEncoder encoder, Object object, OutputStream stream, 
               ElementInfo elementInfo) throws Exception {
        return encoder.encodeBoolean(object, stream, elementInfo);
    }    
    
    public DecodedObject decode(IASN1TypesDecoder decoder, DecodedObject decodedTag, Class objectClass, ElementInfo elementInfo, InputStream stream) throws Exception {
        return decoder.decodeBoolean(decodedTag,objectClass,elementInfo,stream);
    }
    
}