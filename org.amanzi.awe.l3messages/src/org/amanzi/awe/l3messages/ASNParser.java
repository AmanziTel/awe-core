/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.awe.l3messages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map.Entry;

import org.amanzi.awe.l3.messages.streaming.schema.nodes.ChildInfo;
import org.amanzi.awe.l3.messages.streaming.schema.nodes.NodeType;
import org.amanzi.awe.l3.messages.streaming.schema.nodes.SchemaNode;
import org.bn.utils.BitArrayInputStream;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class ASNParser {
    
    private class ParsingCancelledException extends Exception {

        /** long serialVersionUID field */
        private static final long serialVersionUID = 1L;
        
    }
    
    private ArrayList<IAsnParserListener> listeners = new ArrayList<IAsnParserListener>(1);
    
    public void addListener(IAsnParserListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(IAsnParserListener listener) {
        listeners.remove(listener);
    }
    
    public void fireEvent(SchemaNode node, ChildInfo info, Object value) throws ParsingCancelledException {
        AsnParserEvent event = new AsnParserEvent(node.getName(), info.getChildName(), node.getType(), value);
        
        for (IAsnParserListener singleListener : listeners) {
            if (!singleListener.processEvent(event)) {
                throw new ParsingCancelledException();
            }
        }
    }
    
    public void decode(BitArrayInputStream inputStream, SchemaNode rootNode) throws IOException {
        try {
            decode(inputStream, rootNode, null);
        }
        catch (ParsingCancelledException e) {
            //parsing cancelled - end of decoding
        }
        
    }
    
    private void decode(BitArrayInputStream inputStream, SchemaNode rootNode, ChildInfo childInfo) throws IOException, ParsingCancelledException {
        switch (rootNode.getType()) {
        case BIT_STRING:         
            decodeBitString(inputStream, rootNode, childInfo);
            break;
        case CHOICE:
            decodeChoice(inputStream, rootNode, childInfo);
            break;
        case BOOLEAN:
            break;
        case ENUMERATED:
            decodeEnumerated(inputStream, rootNode, childInfo);
            break;
        case INTEGER:
            decodeInteger(inputStream, rootNode, childInfo);
            break;
        case OCTET_STRING:
            throw new UnsupportedOperationException();
        case ROOT:
            throw new UnsupportedOperationException();
        case SEQUENCE:
            decodeSequence(inputStream, rootNode);
            break;
        case SEQUENCE_OF:
            decodeSequenceOf(inputStream, rootNode, childInfo);
            break;
        }
    }
    
    private void decodeSequenceOf(BitArrayInputStream inputStream, SchemaNode elementNode, ChildInfo info) throws IOException, ParsingCancelledException {
        int countOfElements = (int)decodeLength(elementNode, inputStream);
        
        Entry<ChildInfo, SchemaNode> child = elementNode.getChildren().entrySet().iterator().next();
        if (countOfElements > 0) {
            for (int i = 0; i < countOfElements; i++) {                
                decode(inputStream, child.getValue(), child.getKey());
            }
        }
    }
    
    private void decodeEnumerated(BitArrayInputStream inputStream, SchemaNode elementNode, ChildInfo info) throws IOException, ParsingCancelledException {
        int min = 0;
        int max = elementNode.getSize().intValue() - 1;
        
        int enumIndex = (int)decodeConstraintNumber(min, max, inputStream);
        
        String enumValue = elementNode.getPossibleValues().get(enumIndex);
        fireEvent(elementNode, info, enumValue);
    }
    
    private void decodeInteger(BitArrayInputStream inputStream, SchemaNode elementNode, ChildInfo info) throws IOException, ParsingCancelledException {
        long min = elementNode.getMin();
        long max = elementNode.getMax();
        
        long value = decodeConstraintNumber(min, max, inputStream);
        fireEvent(elementNode, info, value);
    }
    
    private void decodeBitString(BitArrayInputStream inputStream, SchemaNode elementNode, ChildInfo info) throws IOException, ParsingCancelledException {
        int sizeOfString = (int)decodeLength(elementNode, inputStream);
        
        int trailBits = 8 - sizeOfString % 8 == 0 ? 8 : sizeOfString % 8;
        sizeOfString = sizeOfString/8;        
        if(sizeOfString >0 || ( sizeOfString == 0 && trailBits > 0)) {
            byte[] value = new byte[ trailBits > 0 ? sizeOfString + 1 : sizeOfString];
            if(sizeOfString >0)
                inputStream.read ( value, 0, sizeOfString); 
            if(trailBits > 0 ) {
                value[sizeOfString] =  (byte)(inputStream.readBits( trailBits) << (8-trailBits)) ;
            }
            
            fireEvent(elementNode, info, value);
        } 
        else {
            
        }                    
    }
    
    private long decodeLength(SchemaNode elementNode, BitArrayInputStream inputStream) throws IOException {
        long result = 0;
        if (elementNode.getSize() != null) {
            if (elementNode.getMin() == null) {
                result = elementNode.getMax();
            }                     
            else {
                result = decodeConstraintLengthDeterminant(elementNode.getMin(), elementNode.getMax(), inputStream);
            }
        }
        else {
            result = decodeLengthDeterminant(inputStream); 
        }
        
        return result;
    }
    
    private long decodeConstraintLengthDeterminant(long min, long max, BitArrayInputStream inputStream) throws IOException {
        if( max <= 0xFFFF) {
            return (int)decodeConstraintNumber(min, max, inputStream);
        }
        else
            return decodeLengthDeterminant(inputStream);
    }
    
    private long decodeLengthDeterminant(BitArrayInputStream inputStream) throws IOException {
        long result = inputStream.read();
        if ( (result & 0x80)==0 ) {
            return result;
        }
        else {
            result = (result & 0x3f) << 8;
            result |= inputStream.read();
        }        
        return result;
    }
    
    private void decodeSequence(BitArrayInputStream inputStream, SchemaNode elementNode) throws IOException, ParsingCancelledException {
        int preambleLen = getSequencePreambleBitLen(elementNode);
        int preamble = inputStream.readBits(preambleLen);
        int preambleCurrentBit = 32 - preambleLen;
        
        for (Entry<ChildInfo, SchemaNode> entry : elementNode.getChildren().entrySet()) {
            ChildInfo info = entry.getKey();
            SchemaNode child = entry.getValue();
            
            if (info.isOptional()) {
                if ((preamble & (0x80000000 >>> preambleCurrentBit)) != 0) {
                    decodeSequenceField(inputStream, child, info);
                }
                preambleCurrentBit++;
            }
            else {
                decodeSequenceField(inputStream, child, info);
            }
        }
    }
    
    private void decodeSequenceField(BitArrayInputStream inputStream, SchemaNode elementNode, ChildInfo childInfo) throws IOException, ParsingCancelledException {
        if (elementNode.getType().equals(NodeType.NULL)) {
            decodeNull(inputStream, elementNode, childInfo);            
        }
        decode(inputStream, elementNode, childInfo);
    }
    
    private void decodeNull(BitArrayInputStream inputStream, SchemaNode elementNode, ChildInfo childInfo) {
        
    }
    
    private void decodeChoice(BitArrayInputStream inputStream, SchemaNode elementNode, ChildInfo info) throws IOException, ParsingCancelledException {
        int elementIndex = (int)decodeConstraintNumber(1, elementNode.getSize(), inputStream);
        
        Set<Entry<ChildInfo, SchemaNode>> children = elementNode.getChildren().entrySet();
        
        int index = 0;
        for (Entry<ChildInfo, SchemaNode> child : children) {
            if ((index++ + 1) == elementIndex) {
                fireEvent(elementNode, info, child.getValue().getName());
                decode(inputStream, child.getValue(), child.getKey());
                break;
            }
        }
    }

    private int getSequencePreambleBitLen(SchemaNode elementNode) {
        int preambleLen = 0;
        
        for (ChildInfo childInfo : elementNode.getChildren().keySet()) {
            if (childInfo.isOptional()) {
                preambleLen++;
            }
        }
        
        return preambleLen;
    }
    
    private long decodeConstraintNumber(long min, long max, BitArrayInputStream inputStream) throws IOException {
        int result = 0;
        long valueRange = max - min;
        
        if(valueRange == 0) {
            return max;
        }
        int maxBitLen = getMaxBitLength(valueRange);
       
        int currentBit = maxBitLen;
        while(currentBit > 7) {
            currentBit-=8;
            result |= inputStream.read() << currentBit;         
        }
        
        if(currentBit > 0) {
           result |= inputStream.readBits(currentBit);
        }
        result+=min;
        
        return result;
    }
    
    private int getMaxBitLength(long value) {
        int bitCnt = 0;
        while( value !=0 ) {
            value >>>= 1;
            bitCnt++;
        }
        return bitCnt;
    }
}
