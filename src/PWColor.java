import javafx.scene.paint.Color;
import org.w3c.dom.*;
import org.w3c.dom.css.RGBColor;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PWColor {
    public Map<String, String> PWColors = new HashMap(); //list of hashmap would be better

    public boolean loadXML(String filepath)
    {
        //https://www.developerfusion.com/code/2064/a-simple-way-to-read-an-xml-file-in-java/
        System.out.println("PWColor.loadXML(): Loading Color XML");
        try {
            File file = new File(filepath);
            DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList colors = doc.getElementsByTagName("color");

            for( int i = 0; i < colors.getLength(); i++ )
            {
                Node color = colors.item(i);
                if( color.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element colorElement = (Element)color; //so this is the content of the element
                    NodeList colorName = colorElement.getElementsByTagName("name");
                    NodeList colorHex = colorElement.getElementsByTagName("hex");
                    if( colorName.getLength() > 0 && colorHex.getLength() > 0 )
                    {
                        Element colorHexElement = (Element)colorHex.item(0);
                        Element colorNameElement = (Element)colorName.item(0);

                        NodeList textFNList = null;
                            textFNList = colorHexElement.getChildNodes();
                            String cHexTx = ((Node)textFNList.item(0)).getNodeValue().trim();
                            textFNList = colorNameElement.getChildNodes();
                            String cNameTx = ((Node)textFNList.item(0)).getNodeValue().trim();
                            System.out.println("PWColor.loadXML(): "+cNameTx+": "+cHexTx);
                            PWColors.put(cNameTx, cHexTx);
                    }

                }
                else System.out.println("PWColor.loadXML(): Node Type is: "+color.getNodeType());
            }

        } catch (SAXParseException err) {
            System.out.println ("** Parsing error" + ", line "
                    + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());

        }catch (SAXException e) {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();

        }catch (Throwable t) {
            t.printStackTrace ();
        }
        finally
        {
            if( PWColors.size() > 0 )
            {
                return true;
            }
            else return false;
        }
    }

    public void loadDummyData()
    {
        PWColors.put("Red", RGB2Hex(255, 0 ,0));
        PWColors.put("Blue", RGB2Hex(0, 0, 255));
        PWColors.put("Green", RGB2Hex(0, 240,  0));
        PWColors.put("Black", RGB2Hex(0,0,0));
    }
    public Map<String, String> getColors() {
        //returns the Colors
        return PWColors;
    }

    public PWColor()
    {
        //loads default config
    }
    public boolean PWColor(String configFile)
    {
        return loadXML(configFile);
    }

    //Static Stuff that might or not might be useful for some stuff
    //probably some functions that already exist elsewhere
    public static int RGB2Int(int R, int G, int B) {
        if( R > 255) R = 255; else if( R < 0 ) R = 0;
        if( G > 255) G = 255; else if( G < 0 ) G = 0;
        if( B > 255) B = 255; else if( B < 0 ) B = 0;
        return R+G*256+B*256^2;//possibly totally wrong
    }
    public static String RGB2Hex(int R, int G, int B) {
        if( R > 255) R = 255; else if( R < 0 ) R = 0;
        if( G > 255) G = 255; else if( G < 0 ) G = 0;
        if( B > 255) B = 255; else if( B < 0 ) B = 0;
        return  Integer.toHexString(R)+Integer.toHexString(G)+Integer.toHexString(B);
    }


//academic value
    private static void printNote(NodeList nodeList) {
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                // get node name and value
                System.out.println("\nNode Name =" + tempNode.getNodeName() + " [OPEN]");
                System.out.println("Node Value =" + tempNode.getTextContent());
                if (tempNode.hasAttributes()) {
                    // get attributes names and values
                    NamedNodeMap nodeMap = tempNode.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node node = nodeMap.item(i);
                        System.out.println("attr name : " + node.getNodeName());
                        System.out.println("attr value : " + node.getNodeValue());
                    }
                }
                if (tempNode.hasChildNodes()) {
                    // loop again if has child nodes
                    printNote(tempNode.getChildNodes());
                }
                System.out.println("Node Name =" + tempNode.getNodeName() + " [CLOSE]");
            }
        }
    }
}
