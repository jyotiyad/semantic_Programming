package matcher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import static com.hp.hpl.jena.vocabulary.OWLResults.system;

/**
 * Created by lpoon2 on 2/26/2017.
 */
public class Syntactic {
    public static Map<String, Map<String, Vector<String>>> lookup1 = new HashMap<String , Map<String, Vector<String>>>();
    public static String   in ="input";
    public static String   out = "output";
    public static String   msg = "message";
    private static String   cmplx = "complexType";
    public static String   part = "part";
    private static EditDistance EditDistance;
    private static Vector<String> PrimType = new Vector<String>();

    /*
    separate a string by the position of the colon
     */
    public static String splitString(String s){
        if(s.contains(":")){
            String[] ss = s.split(":");
            s = ss[1];
        }
        return s;
    }

    public static String  fixTagName(String add){

        in = add+":input";
        out = add+":output";
        msg = add+":message";
        part = add+":part";
        cmplx = add + ":complexType";
        return add;
    }

    /*
        parseDOM takes in a file name in String and returns a hashmap with operation names as keys, another hashmap as values.
        The inner hashmap stores the output and input messages.
     */
    public static Map<String, Map<String, String>> parseDOM(String fileName){
         Map<String, Map<String, String>> ret = new HashMap<String , Map<String, String>>();

            try {
                File input = new File(fileName);
                DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(input);
                doc.getDocumentElement().normalize();
                XPath xPath =  XPathFactory.newInstance().newXPath();

                NodeList nList = doc.getElementsByTagName("operation");

                if(nList.getLength() == 0) {
                    nList=doc.getElementsByTagName(fixTagName("wsdl")+":operation");
                }

                for(int i=0;i < nList.getLength() ; i++) {
                    Map<String, String> io = new HashMap<String, String>();
                    Node n = nList.item(i);
                    Element ele = (Element)n;
                    String name = ele.getAttribute("name");


                    try {

                        String in_s = splitString(ele.getElementsByTagName(in).item(0).getAttributes().getNamedItem("message").getNodeValue());
                        String out_s = splitString(ele.getElementsByTagName(out).item(0).getAttributes().getNamedItem("message").getNodeValue());
                        // searching the struct of the input/output parameter
                        String xPath_in = "//*"  + "[@name=\"" +in_s+ "\"]";
                        String xPath_out = "//*" +"[@name=\""+out_s+"\"]";
                        Node inNode = (Node) xPath.compile(xPath_in).evaluate(doc, XPathConstants.NODE);
                        Element pEle = (Element)inNode ;

                        Node outNode = (Node) xPath.compile(xPath_out).evaluate(doc, XPathConstants.NODE);
                        Element pEle_out = (Element) outNode;
                        try {
                            if (pEle != null) {
                                    if(pEle.getElementsByTagName(part).getLength() != 0) {
                                        if(pEle.getElementsByTagName(part).item(0).getAttributes().getNamedItem("element") != null)
                                        io.put("input", splitString(pEle.getElementsByTagName(part).item(0).getAttributes().getNamedItem("element").getNodeValue()));
                                        else if(pEle.getElementsByTagName(part).item(0).getAttributes().getNamedItem("type") != null){
                                            String type = splitString(pEle.getElementsByTagName(part).item(0).getAttributes().getNamedItem("type").getNodeValue());
                                            if(PrimType.contains(type))
                                            io.put("input", splitString(pEle.getElementsByTagName(part).item(0).getAttributes().getNamedItem("name").getNodeValue()));

                                            else{

                                                String path_to_struct = "//*" + "[@name=\"" +type+ "\"]" ;
                                           //     Node inNode = (Node) xPath.compile(path_to_struct).evaluate(doc, XPathConstants.NODE);

                                            }

                                        }

                                    }
                            }

                            if(pEle_out != null) {
                                if(pEle_out.getElementsByTagName(part).getLength() != 0)
                                    if(pEle_out.getElementsByTagName(part).item(0).getAttributes().getNamedItem("element") != null)
                                    io.put("output", splitString(pEle_out.getElementsByTagName(part).item(0).getAttributes().getNamedItem("element").getNodeValue()));
                                    else if(pEle_out.getElementsByTagName(part).item(0).getAttributes().getNamedItem("type") != null)
                                        io.put("input", splitString(pEle_out.getElementsByTagName(part).item(0).getAttributes().getNamedItem("type").getNodeValue()));

                            }
                        }
                        catch (Exception e){
                        //    e.printStackTrace();

                            io.put("input", "result");
                            io.put("output", "response");

                        }

                        ret.put(name, io);
                    }
                    catch (Exception e){
               //         e.printStackTrace();
                    }

                }

            }
            catch(Exception e){

                e.printStackTrace();
            }
    return ret;
    }

    /*
    this method calcaltes each combination of operation compparison.
     */
    public static void makeDoc( Map<String, Map<String, String>> i ,  Map<String, Map<String, String>>  o){

        for (Map.Entry<String, Map<String, String>> ii : i.entrySet()) {
            for (Map.Entry<String, Map<String, String>> oi : o.entrySet()) {

               Double optName = EditDistance.getSimilarity(ii.getKey(), oi.getKey());
               System.out.println("Operation Name: " + ii.getKey() + " & " + oi.getKey() + " = " + optName);

               Double inoutName = EditDistance.getSimilarity(ii.getValue().get("input"), oi.getValue().get("output") );
                System.out.println("Element Name: "+ ii.getValue().get("input") + " + " + oi.getValue().get("output") + inoutName);

            }

        }
    }

    /*
    initialize primitive vector
     */
    public static void init(){
        PrimType.add("string");
        PrimType.add("long");
        PrimType.add("dateTime");
        PrimType.add("double");
        PrimType.add("boolean");
        PrimType.add("int");
        PrimType.add("byte");
        PrimType.add("short");
        PrimType.add("float");
        PrimType.add("char");
    }

    public static void main(String[] args){
        init();
        Map<String, Map<String, String>> input = parseDOM("WSDLs/LibertyReserveAPIProfile.wsdl");
        Map<String, Map<String, String>> output = parseDOM("WSDLs/Data8CreditCardValidationAPIProfile.wsdl");
        makeDoc(input,output);
    }

}
