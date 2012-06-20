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

import org.bn.annotations.ASN1Element;
import org.bn.coders.DecodedObject;
import org.bn.coders.ElementInfo;
import org.bn.coders.IASN1TypesDecoder;
import org.bn.coders.IASN1TypesEncoder;
import org.bn.coders.TagClass;

/**
 * @author jcfinley@users.sourceforge.net
 */
public class ASN1ElementMetadata
    extends ASN1FieldMetadata
{
    private String  name             = "";
    private boolean isOptional      = true;
    private boolean hasTag          = false;
    private boolean isImplicitTag   = false;
    private int     tagClass        = TagClass.ContextSpecific;
    private int     tag             = 0;
    private boolean hasDefaultValue = false;

    public ASN1ElementMetadata(ASN1Element annotation) {
        this(
            annotation.name(),
            annotation.isOptional(),
            annotation.hasTag(),
            annotation.isImplicitTag(),
            annotation.tagClass(),
            annotation.tag(),
            annotation.hasDefaultValue()
        );
    }
    
    public ASN1ElementMetadata(String  name,
                               boolean isOptional,
                               boolean hasTag,
                               boolean isImplicitTag,
                               int     tagClass,
                               int     tag,
                               boolean hasDefaultValue)
    {
        super(name);

        this.isOptional      = isOptional;
        this.hasTag          = hasTag;
        this.isImplicitTag   = isImplicitTag;
        this.tagClass        = tagClass;
        this.tag             = tag;
        this.hasDefaultValue = hasDefaultValue;
    }

    public boolean isOptional()
    {
        return isOptional;
    }

    public boolean hasTag()
    {
        return hasTag;
    }

    public boolean isImplicitTag()
    {
        return isImplicitTag;
    }

    public int getTagClass()
    {
        return tagClass;
    }

    public int getTag()
    {
        return tag;
    }

    public boolean hasDefaultValue()
    {
        return hasDefaultValue;
    }

    public int encode(IASN1TypesEncoder encoder, Object object, OutputStream stream, 
               ElementInfo elementInfo) throws Exception {
        return encoder.encodePreparedElement(object,stream,elementInfo);
    }    
    
    public DecodedObject decode(IASN1TypesDecoder decoder, DecodedObject decodedTag, Class objectClass, ElementInfo elementInfo, InputStream stream) throws Exception {    
        //return decoder.decodePreparedElement(decodedTag,objectClass,elementInfo,stream);        
        elementInfo.setPreparedInstance(null);
        return decoder.decodeElement(decodedTag,objectClass,elementInfo,stream);
    }
    
}
