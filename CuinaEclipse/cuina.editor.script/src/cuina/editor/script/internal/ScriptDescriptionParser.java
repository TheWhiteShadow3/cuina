package cuina.editor.script.internal;
 
import cuina.editor.script.library.ClassDefinition;
import cuina.editor.script.library.FunctionDefinition;
import cuina.editor.script.library.ValueDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
 
/**
 * Parser für die Skript-Beschreibungsdateien.
 * 
 * @author TheWhiteShadow
 */
public class ScriptDescriptionParser
{
	private static DocumentBuilder builder;
	
	static
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);  // keine Kommentare
        factory.setCoalescing(true);        // kombiniere CDATA-Blöcke mit umliegenden Text
        
        try
		{
			builder = factory.newDocumentBuilder();
//			builder.setEntityResolver(new EntityResolver()
//			{
//				@Override
//				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException
//				{
//					System.out.println(publicId + " : " + systemId);
////					if (systemId.endsWith("func-desc.dtd"))
////					{
////						return new InputSource(Scripts.getBundleFile("func-desc.dtd").toString());
////					}
//					return null;
//				}
//			});
		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
	}
	
    private ScriptDescriptionParser() {} 

	public static ArrayList<ClassDefinition> parse(File file) throws IOException, SAXException
	{
		if (builder == null) throw new NullPointerException();

		Document document = builder.parse(file);
		ArrayList<ClassDefinition> definitions = parseDoc(document);
		return definitions;
	}

	public static ArrayList<ClassDefinition> parse(InputStream stream) throws IOException, SAXException
	{
		if (builder == null) throw new NullPointerException();
		
		Document document = builder.parse(stream);
		ArrayList<ClassDefinition> definitions = parseDoc(document);
		return definitions;
	}
    
    public static ArrayList<ClassDefinition> parseDoc(Document root) throws SAXException
    {
        NodeList nodes = root.getChildNodes();
        
        for(int i = 0; i < nodes.getLength(); i++)
        {
            Node node = nodes.item(i);
            if (!isValidNode(node)) continue;
            
            return parseDescription(node);
        }
        return null;
    }
    
	private static ArrayList<ClassDefinition> parseDescription(Node descNode) throws SAXException
	{
		final ArrayList<ClassDefinition> definitions = new ArrayList<ClassDefinition>();
		final NodeList nodes = descNode.getChildNodes();

		Node node;
		for (int i = 0; i < nodes.getLength(); i++)
		{
			node = nodes.item(i);
			if (!isValidNode(node)) continue;

			if (equalsName(node, "class"))
			{
				String classID = getAttributValue(node, "id");
				String label = getAttributValue(node, "label");
				if (classID != null && classID.isEmpty()) classID = null;
				ClassDefinition clazz = new ClassDefinition(classID, label);

				NodeList funcNodes = node.getChildNodes();
				for (int j = 0; j < funcNodes.getLength(); j++)
				{
					node = funcNodes.item(j);
					if (!isValidNode(node)) continue;

					if (equalsName(node, "function"))
					{
						FunctionDefinition func = parseFunctionDefinition(node);
						clazz.add(func);
					}
					else if (equalsName(node, "attribut"))
					{
						ValueDefinition field = parseValueDefinition(node);
						clazz.add(field);
					}
					else throw new SAXException("illegal node: " + node);
				}
				definitions.add(clazz);
			}
			else throw new SAXException("illegal node: " + node);
		}

		return definitions;
	}
    
    private static FunctionDefinition parseFunctionDefinition(Node funcNode) throws SAXException
    {
        final FunctionDefinition func = new FunctionDefinition();
        final NodeList nodes = funcNode.getChildNodes();
//        func.sectionID = sID;
//        func.sectionLabel = sLabel;
        
        NamedNodeMap atts = funcNode.getAttributes();
        func.id     = getAttributValue(atts, "id");
        func.label  = getAttributValue(atts, "label");
        if (func.id == null) throw new SAXException("node function missing required attribute: id");
        
        Node node;
        for(int i = 0; i < nodes.getLength(); i++)
        {
            node = nodes.item(i);
            if (!isValidNode(node)) continue;
            
            if (equalsName(node, "code"))
            {
                func.code = getInnerText(node);
            }
            else if (equalsName(node, "text"))
            {
                func.text = getInnerText(node);
            }
            else if (equalsName(node, "help"))
            {
                func.help = getInnerText(node);
            }
            else if (equalsName(node, "param"))
            {
                func.params.add(parseValueDefinition(node));
            }
            else if (equalsName(node, "return"))
            {
                func.returnType = getInnerText(node);
            }
        }
        // Defaultwerte setzen
        if (func.label == null) func.label = func.id;
        
        return func;
    }
    
    private static ValueDefinition parseValueDefinition(Node fieldNode) throws SAXException
    {
        final ValueDefinition attr = new ValueDefinition();
        final NodeList nodes = fieldNode.getChildNodes();
        
        NamedNodeMap atts = fieldNode.getAttributes();
        attr.id    = getAttributValue(atts, "id");
        attr.label = getAttributValue(atts, "label");
        attr.type  = getAttributValue(atts, "type");
        attr.def   = getAttributValue(atts, "default");
        if (attr.id == null) throw new SAXException("node param missing required attribute: id");
        if (attr.type == null) throw new SAXException("node param missing required attribute: type");
        
        for(int i = 0; i < nodes.getLength(); i++)
        {
            Node node = nodes.item(i);
            if (equalsName(node, "filter"))
                parseParamFilter(attr, node);
        }
        // Defaultwerte setzen
        if (attr.label == null) attr.label = attr.id;
        
        return attr;
    }
    
    private static void parseParamFilter(ValueDefinition field, Node filterNode) throws SAXException
    {
        final NodeList nodes = filterNode.getChildNodes();
        Node node;
        for(int i = 0; i < nodes.getLength(); i++)
        {
            node = nodes.item(i);
            if (!isValidNode(node)) continue;
            
            if (equalsName(node, "values"))
            {
                if ( !"string".equals(field.type) && !"int".equals(field.type))
                    throw new SAXException("attribut values is not allowed for parameter-type " + field.type);
                
                setAttributeValues(field, node);
            }
            else if (equalsName(node, "lenght"))
            {
                if ( !"string".equals(field.type))
                    throw new SAXException("attribut lenght is not allowed for parameter-type " + field.type);
                
                setAttributeLenght(field, node);
            }
            else if (equalsName(node, "range"))
            {
                if ( !"int".equals(field.type) && !"float".equals(field.type))
                    throw new SAXException("attribut range is not allowed for parameter-type " + field.type);
                
                setAttributeRange(field, node);
            }
            else if (equalsName(node, "pattern"))
            {
                field.pattern = getInnerText(node);
            }
        }
    }
    
    private static void setAttributeValues(ValueDefinition field, Node node) throws SAXException
    {
        List<String> list = Arrays.asList(getInnerText(node).split(","));
        for (int j = 0; j < list.size(); j++)
        {
            list.set(j, list.get(j).trim());
        }
        field.validValues = list;
    }
    
    private static void setAttributeLenght(ValueDefinition field, Node node) throws SAXException
    {
        NamedNodeMap atts = node.getAttributes();
        try
        {
        	field.minLenght = Integer.parseInt(getAttributValue(atts, "min"));
        	field.maxLenght = Integer.parseInt(getAttributValue(atts, "max"));
        }
        catch(NumberFormatException e)
        {
            throw new SAXException("invalid attribut for lenght");
        }
    }
    
    private static void setAttributeRange(ValueDefinition field, Node node) throws SAXException
    {
        NamedNodeMap atts = node.getAttributes();
        try
        {
        	field.minRange = Double.parseDouble(getAttributValue(atts, "min"));
            field.maxRange = Double.parseDouble(getAttributValue(atts, "max"));
        }
        catch(NumberFormatException e)
        {
            throw new SAXException("invalid attribut for range");
        }
    }
    
    private static boolean isValidNode(Node node)
    {
        return (node != null && node.getNodeType() != Node.TEXT_NODE && node.getNodeType() != Node.COMMENT_NODE);
    }
    
    private static boolean equalsName(Node node, String name)
    {
        if (node == null || name == null) return false;
        return name.equals(node.getNodeName());
    }
    
    private static String getAttributValue(Node node, String name) throws SAXException
    {
        return getAttributValue(node.getAttributes(), name);
    }
    
    private static String getAttributValue(NamedNodeMap atts, String name) throws SAXException
    {
        if (atts == null) return null;
        Node att = atts.getNamedItem(name);
        if (att == null)
            return null;
        else
            return att.getNodeValue();
    }
    
    private static String getInnerText(Node node)
    {
        return node.getChildNodes().item(0).getNodeValue();
    }
    
//    public static void main(String[] argv)
//    {
//        try
//        {
//        	HashMap<String, FunctionDefinition> desc = EventDescriptionParser.parse(new File("plugins/func-desc.xml"));
//            
//            ArrayList<String> objects = new ArrayList<String>();
//            objects.add("Self");
//            objects.add("Objekt 1");
//            objects.add("Objekt 2");
////            EventDialog.listData.put("Map-Object", objects);
//            
//            new ScriptFunctionDialog(new DummyEditor(), desc.get("tolle_fuktion"));
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
}