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
import java.util.Map;

import static com.hp.hpl.jena.vocabulary.OWLResults.system;

/**
 * Created by lpoon2 on 2/26/2017.
 */
public class Syntactic {
    public static Map<String, Map<String, String>> lookup1 = new HashMap<String , Map<String, String>>();
    public static String   in ="input";
    public static String   out = "output";
    public static String   msg = "message";
    public static String   part = "part";
    public Map<String, Map<String, String>> lookup2 ;

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

        return add;
    }

    public static void parseDOM(String fileName){

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

                        String xPath_in = "//" +msg+ "[@name='" +in_s+ "']";
                        String xPath_out = "//"+msg+"[@name='"+out_s+"']";

                        Node inNode = (Node) xPath.compile(xPath_in).evaluate(doc, XPathConstants.NODE);
                        Element pEle = (Element)inNode ;

                        Node outNode = (Node) xPath.compile(xPath_out).evaluate(doc, XPathConstants.NODE);
                        Element pEle_out = (Element) outNode;
                        try {
                            io.put("input", splitString(pEle.getElementsByTagName(part).item(0).getAttributes().getNamedItem("element").getNodeValue()));
                            io.put("output", splitString(pEle_out.getElementsByTagName(part).item(0).getAttributes().getNamedItem("element").getNodeValue()));
                        }
                        catch (Exception e){
                            io.put("input", "result");
                            io.put("output", "response");

                        }

                        lookup1.put(name, io);
                    }
                    catch (Exception e){
               //         e.printStackTrace();
                    }

                }
                    System.out.println(lookup1.get("InstallPaymentInstructionBatch").get("input"));
                    System.out.println(lookup1.get("InstallPaymentInstructionBatch").get("output"));

            }
            catch(Exception e){

                e.printStackTrace();
            }

    }

    public static void main(String[] args){
        parseDOM("src/matcher/compositeAmazonFlexiblePaymentsServiceAPIProfile.wsdl");

    }

}
