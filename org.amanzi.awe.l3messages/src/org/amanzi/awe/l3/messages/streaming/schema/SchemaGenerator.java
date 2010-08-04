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

package org.amanzi.awe.l3.messages.streaming.schema;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.amanzi.awe.l3.messages.streaming.schema.nodes.ChildInfo;
import org.amanzi.awe.l3.messages.streaming.schema.nodes.NodeType;
import org.amanzi.awe.l3.messages.streaming.schema.nodes.SchemaNode;

/**
 * TODO Purpose of 
 * <p>
 *
 * </p>
 * @author Lagutko_N
 * @since 1.0.0
 */
public class SchemaGenerator {
    
    private File schemaDirectory;
    
    private HashMap<String, SchemaNode> nodeCache = new HashMap<String, SchemaNode>();
    
    private SchemaNode rootNode;
    
    public SchemaGenerator(File schemaDirectory) {
        this.schemaDirectory = schemaDirectory;
        
        rootNode = new SchemaNode("root", NodeType.ROOT);
    }
    
    public SchemaNode parse() {
        if (schemaDirectory.isDirectory()) {
            for (File singleFile : schemaDirectory.listFiles()) {
                parseSingleFile(singleFile);
            }
        }
        
        return rootNode;
    }
    
    private void parseSingleFile(File file) {
        try {
            Scanner scanner = new Scanner(file);
            
            while (scanner.hasNextLine()) {
                analyzeLine(scanner);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void analyzeLine(Scanner scanner) {
        String line = nextLine(scanner);
        
        
        if (line.contains("::=") && !line.contains("DEFINITIONS AUTOMATIC TAGS")) {
            parseAsnElement(line, scanner);
        }
    }
    
    private void parseConstant(String constantName, String constantType, String value) {
        SchemaNode constantNode = nodeCache.get(constantName);
        
        if (constantNode == null) {
            constantNode = new SchemaNode(constantName, Integer.parseInt(value));
            nodeCache.put(constantName, constantNode);
        }
        else {
        	constantNode.setContstantValue(Integer.parseInt(value));
            for (SchemaNode parent : constantNode.getParents()) {
                parent.removeChild(constantName);
                
                parent.setSize(processSizeExpression(parent, parent.getSizeExpression(), constantName, constantNode.getConstantValue()));
            }
        }
    }
    
    private Long processSizeExpression(SchemaNode elementNode, String expression, String constantName, Integer value) {
        expression = expression.replace(constantName, value.toString());
        
        String left = null;
        String right = null;
        Long iLeft = null;
        Long iRight = null;
        if (expression.contains("..")) {            
            left = expression.substring(0, expression.indexOf("..")).trim();
            iLeft = Long.parseLong(left);
            right = expression.substring(expression.indexOf("..") + 2).trim();            
        }
        else {
            right = expression;
        }
        iRight = Long.parseLong(right);
        elementNode.setMin(iLeft);
        elementNode.setMax(iRight);
        if (iLeft == null) {
            return iRight;
        }
        else {
            return iRight - iLeft + 1;
        }
    }
    
    private void parseAsnElement(String line, Scanner scanner) {
        StringTokenizer tokenizer = new StringTokenizer(line, " \t{");
        
        String elementName = tokenizer.nextToken();
        String maybeType = "";
        if (elementName.endsWith("::=")) {
        	elementName = elementName.substring(0, elementName.length() - 3);
        }
        else {
        	maybeType = tokenizer.nextToken();
        }
        
        if (!tokenizer.hasMoreTokens()) {
            tokenizer = new StringTokenizer(nextLine(scanner), " \t{");
        }
        String elementType = tokenizer.nextToken();
        
        if (elementType.equals("::=")) {
            parseConstant(elementName, maybeType, tokenizer.nextToken());            
            return;
        }
        
        
        NodeType type = getNodeType(elementType, tokenizer);
        
        SchemaNode node = nodeCache.get(elementName);
        if (node == null) {
            node = new SchemaNode(elementName, type);
            rootNode.addChild(node, new ChildInfo(elementName));
            nodeCache.put(elementName, node);
        }
        else {
            node.setType(type);
            if (node.getParents().contains(rootNode)) {
                node.getParents().remove(rootNode);
            }
        }
        
        if (type != null) {
            switch (type) {
            case SEQUENCE:
                parseSequence(line, scanner, node);
                break;
            case CHOICE:
                parseChoise(line, scanner, node);
                break;
            case ENUMERATED:
                parseEnumerated(line, scanner, node);
                break;
            case SEQUENCE_OF:
                parseSequenceOf(line, scanner, node);
                break;
            case OCTET_STRING:
            case BIT_STRING:
                getSizeNode(line, node);
//                nodeCache.remove(elementName);
                break;
            case INTEGER:
                getSizeNode(line, node);
                break;
            }            
        }
        else {
//            System.out.println(elementType);
        }
    }
    
    private void parseEnumerated(String line, Scanner scanner, SchemaNode elementNode) {
        int index = line.indexOf("{");
        if (index > 0) {
            line = line.substring(index);
        }
        
        boolean first = true;
        
        do {
            if (first) {
                first = false;
            }
            else {
                line = nextLine(scanner);
            }
            
            StringTokenizer tokenizer = new StringTokenizer(line, " \t,{}");
            while (tokenizer.hasMoreTokens()) {
                elementNode.addPossibleValue(tokenizer.nextToken());                
            }            
        }
        while (!line.contains("}"));
    }
    
    private void parseChoise(String line, Scanner scanner, SchemaNode elementNode) {
        int size = 0;
        int index = line.indexOf("{");
        if (index > 0) {
            line = line.substring(index);
        }
        
        boolean first = true;
        
        do {
            if (first) {
                first = false;
            }
            else {
                line = nextLine(scanner);
            }
            
            StringTokenizer tokenizer = new StringTokenizer(line, " \t,{}");
            while (tokenizer.hasMoreTokens()) {
                String childName = tokenizer.nextToken();
                
                if (!tokenizer.hasMoreTokens()) {
                    if (line.contains("}")) {
                        break;
                    }
                    tokenizer = new StringTokenizer((line = nextLine(scanner)), " \t,{}");
                }
                String elementName = tokenizer.nextToken();
                
                boolean isOptional = false;
                size++;
                
                NodeType type = getNodeType(elementName, tokenizer);
                if (type != null) {
                    SchemaNode internalNode = new SchemaNode(type.toString(), type);                    
                    switch (type) {
                    case CHOICE:
                        parseChoise(line, scanner, internalNode);
                        break;
                    case SEQUENCE:
                        parseSequence(line, scanner, internalNode);
                        while (tokenizer.hasMoreTokens()) {
                            String maybeOptional = tokenizer.nextToken(); 
                            isOptional = maybeOptional.contains("OPTIONAL") || maybeOptional.contains("OPTIONAL,");
                        }
                        break;
                    case SEQUENCE_OF:
                        parseSequenceOf(line, scanner, internalNode);
                        while (tokenizer.hasMoreTokens()) {
                            tokenizer.nextToken();
                        }
                        break;
                    case INTEGER:
                        getSizeNode(line, internalNode);
                        while (tokenizer.hasMoreTokens()) {
                            String maybeOptional = tokenizer.nextToken(); 
                            isOptional = maybeOptional.contains("OPTIONAL") || maybeOptional.contains("OPTIONAL,");
                        }
                        break;
                    case NULL:
//                        System.out.println("NULL");
                        break;
                    }
                    ChildInfo info = new ChildInfo(childName, isOptional);
                    elementNode.addChild(internalNode, info);
                }
                else {
                    if (tokenizer.hasMoreTokens()) {
                        isOptional = tokenizer.nextToken().equals("OPTIONAL");
                    }
                
                    SchemaNode childNode = nodeCache.get(elementName);
                    if (childNode == null) {
                        childNode = new SchemaNode(elementName);
                        nodeCache.put(elementName, childNode);
                    }
                    ChildInfo info = new ChildInfo(childName, isOptional);
                    elementNode.addChild(childNode, info);
                }
            }            
        }
        while (!line.contains("}"));
        elementNode.setSize(size);
    }
    
    private void parseSequenceOf(String line, Scanner scanner, SchemaNode elementNode) {
        String sizeLine = line;
        if (line.contains("OF")) {
            sizeLine = line.substring(0, line.indexOf("OF"));
        }
        getSizeNode(sizeLine, elementNode);
        
        if (line.endsWith("OF") || !line.contains("OF")) {
            line = nextLine(scanner);
        }
        else {
            line = line.substring(line.indexOf("OF") + 2);
            if (line.trim().isEmpty()) {
                line = nextLine(scanner);
            }
        }
        
        line = line.trim();
        StringTokenizer tokenizer = new StringTokenizer(line, " \t{,");
        String elementType = tokenizer.nextToken();
        
        NodeType type = getNodeType(elementType, tokenizer);
        if (type == null) {
            SchemaNode node = nodeCache.get(elementType);
            if (node == null) {
                node = new SchemaNode(elementType);
                nodeCache.put(elementType, node);
            }
            elementNode.addChild(node, new ChildInfo(elementType));
        }
        else {
            SchemaNode node = new SchemaNode(elementType, type);
            elementNode.addChild(node, new ChildInfo(elementType));
            switch (type) {
            case SEQUENCE:                
                parseSequence(line, scanner, node);
                break;
            case INTEGER:
                getSizeNode(line, node);
                break;
                
            }
        }
    }
    
    private boolean parseSequence(String line, Scanner scanner, SchemaNode elementNode) {
        int size = 0;
        
        int index = line.indexOf("{");
        if (index > 0) {
            line = line.substring(index);
        }
        
        boolean first = true;
        
        do {
            if (first) {
                first = false;
            }
            else {
                line = nextLine(scanner);
            }
            
            StringTokenizer tokenizer = new StringTokenizer(line, " \t,{}");
            while (tokenizer.hasMoreTokens()) {
                String childName = tokenizer.nextToken();
                
                if (!tokenizer.hasMoreElements()) {
                    if (line.contains("}") || line.contains("OPTIONAL")) {
                        return true;
                    }
                    tokenizer = new StringTokenizer((line = nextLine(scanner)), " \t,{}");
                }
                String elementName = tokenizer.nextToken();
                
                NodeType type = getNodeType(elementName, tokenizer);
                
                boolean isOptional = line.endsWith("OPTIONAL") || line.endsWith("OPTIONAL,");
                if (isOptional && tokenizer.hasMoreTokens()) {                    
                    tokenizer.nextToken();
                }
                
                if (type != null) {
                    SchemaNode internalNode = new SchemaNode(type.toString(), type);
                    switch (type) {
                    case CHOICE:
                        parseChoise(line, scanner, internalNode);
                        while (tokenizer.hasMoreTokens()) {
                            String maybeOptional = tokenizer.nextToken(); 
                            isOptional = maybeOptional.contains("OPTIONAL") || maybeOptional.contains("OPTIONAL,");
                        }
                        break;
                    case SEQUENCE:
                        isOptional = isOptional || parseSequence(line, scanner, internalNode);
                        if (!isOptional) {
                            while (tokenizer.hasMoreTokens()) {
                                String maybeOptional = tokenizer.nextToken(); 
                                isOptional = maybeOptional.contains("OPTIONAL") || maybeOptional.contains("OPTIONAL,");
                            }
                        }
                        break;
                    case SEQUENCE_OF:
                        parseSequenceOf(line, scanner, internalNode);
                        while (tokenizer.hasMoreTokens()) {
                            String maybeOptional = tokenizer.nextToken(); 
                            isOptional = maybeOptional.contains("OPTIONAL") || maybeOptional.contains("OPTIONAL,");
                        }
                        break;
                    case INTEGER:
                        getSizeNode(line, internalNode);
                        while (tokenizer.hasMoreTokens()) {
                            tokenizer.nextToken();
                        }
                        break;
                    case BIT_STRING:
                        getSizeNode(line, internalNode);
                        while (tokenizer.hasMoreTokens()) {
                            tokenizer.nextToken();
                        }
                        break;
                    case ENUMERATED:
                        parseEnumerated(line, scanner, internalNode);
                        while (tokenizer.hasMoreTokens()) {
                            tokenizer.nextToken();
                        }
                        line = line.replaceFirst("}", "");
                        break;
                    case NULL:
//                        System.out.println("NULL");
                        break;
                    }    
                    ChildInfo info = new ChildInfo(childName, isOptional);
                    elementNode.addChild(internalNode, info);
                }
                else {
                    SchemaNode childNode = nodeCache.get(elementName);
                    if (childNode == null) {
                        childNode = new SchemaNode(elementName);  
                        nodeCache.put(elementName, childNode);
                    }
                    ChildInfo info = new ChildInfo(childName, isOptional);
                    elementNode.addChild(childNode, info);
                }
            }            
        }
        while (!line.contains("}"));
        elementNode.setSize(size);
        
        return false;
    }
    
    
    
    public static void main(String[] args) {
        String directory = "D:/projects/awe/org.amanzi.awe.l3messages/schema";
        
        try {
            SchemaGenerator generator = new SchemaGenerator(new File(directory));
            SchemaNode root = generator.parse();
            
            PrintStream stream = new PrintStream(directory + "/result.txt");
        
            printNode(root, 0, stream, null);
            
            stream.close();
        }
        catch (IOException e) {
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void printNode(SchemaNode node, int offset, PrintStream stream, ChildInfo info) {
        String line = "";
        for (int i = 0; i < offset; i++) {
            line = line + " ";
        }
        
        Collection<SchemaNode> children = node.getChildren().values();
        String infoString = "";
        if (info != null) {
            infoString = info.getChildName();
            if (infoString == null) {
                infoString = "";
            }
        }        
        if (children.isEmpty()) {
            stream.println(line + infoString + " - " + node.getName() + "<" + node.getType() + ">");
        }
        else {
            stream.println(line + infoString + " - " + node.getName() + "<" + node.getType() + ">" + " {");
            for (ChildInfo childInfo : node.getChildren().keySet()) {
                SchemaNode child = node.getChildren().get(childInfo);
                printNode(child, offset + 2, stream, info);
            }
            stream.println(line + "}");
        }
    }

    private String nextLine(Scanner scanner) {
        String line = "";
        do {
            line = scanner.nextLine();
        }
        while (line.trim().startsWith("--"));
//        System.out.println(line);
        return line;
    }
    
    private NodeType getNodeType(String originalLine, StringTokenizer tokenizer) {
        if (originalLine.startsWith("BIT")) {
            String token = tokenizer.nextToken();
            int index = token.indexOf("(");
            if (index > 0) {
                token = token.substring(0, index);                
            }
            originalLine = originalLine + "_" + token;
        }
        else if (originalLine.startsWith(NodeType.SEQUENCE.toString())) {
            if (originalLine.contains("(SIZE")) {
                return NodeType.SEQUENCE_OF;
            }
            if (tokenizer.hasMoreTokens() && tokenizer.nextToken().startsWith("(SIZE")) {
                originalLine = originalLine + "_OF";
            }
            else if (tokenizer.hasMoreTokens() && tokenizer.nextToken().startsWith("SIZE")) {
                originalLine = originalLine + "_OF";
            }
        }
        else if (originalLine.startsWith("OCTET")) {
            originalLine = originalLine + "_" + tokenizer.nextToken();
        }
        return NodeType.getType(originalLine);
    }
    
    private void getSizeNode(String line, SchemaNode elementNode) {
        int beginIndex = line.lastIndexOf("(") + 1;
        int endIndex = line.indexOf(")");
        
        if (beginIndex < 0 || endIndex < 0) {
            return;
        }
        String sizeExpression = line.substring(beginIndex, endIndex);
        elementNode.setSizeExpression(sizeExpression);
        String elementName = "";
        
        try {
            if (sizeExpression.contains("..")) {
                String first = sizeExpression.substring(0, sizeExpression.indexOf("..")).trim();
                String second = sizeExpression.substring(sizeExpression.indexOf("..") + 2, sizeExpression.length()).trim();
                Long iFirst = Long.parseLong(first);
                Long iSecond = Long.parseLong(second);
                elementNode.setSize(iSecond - iFirst + 1);
                elementNode.setMin(iFirst);
                elementNode.setMax(iSecond);
            }
            else {
                Long max = Long.parseLong(sizeExpression.trim()); 
                elementNode.setSize(max);
                elementNode.setMax(max);
            }
            return;
        }
        catch (NumberFormatException e) {        
            if (sizeExpression.contains("..")) {
                elementName = sizeExpression.substring(sizeExpression.indexOf("..") + 2);
            }
            else {
                elementName = sizeExpression;
            }
        }
        
        elementName = elementName.trim();
        SchemaNode sizeNode = nodeCache.get(elementName);
        if (sizeNode == null) {
            sizeNode = new SchemaNode(elementName);
            elementNode.addChild(sizeNode, new ChildInfo(elementName));
            nodeCache.put(elementName, sizeNode);
        }
        else {
        	if (sizeNode.getConstantValue() != null) {
        		elementNode.setSize(processSizeExpression(elementNode, sizeExpression, elementName, sizeNode.getConstantValue()));
        	}
        	else {
        		elementNode.addChild(sizeNode, new ChildInfo(elementName));
        	}
        }
    }
}
