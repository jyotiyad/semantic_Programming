package matcher;

import jdk.internal.org.xml.sax.ErrorHandler;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.SAXParseException;
import org.w3c.dom.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

import static com.hp.hpl.jena.vocabulary.OWLResults.system;
import static com.hp.hpl.jena.vocabulary.RDFSyntax.doc;

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
    private static String inputXSDL = "";
    private static String outputXSDL = "";
    private static Map<String, Map<String, Vector<String>>> input ;
    private static Map<String, Map<String, Vector<String>>> output ;
    private static Vector<String> PrimType = new Vector<String>();


    public static Node getStruct(String name){
        XPath xPath =  XPathFactory.newInstance().newXPath();
        Node inNode = null;
        String path = "//*"  + "[@name=\"" +name+ "\"]";
        try {
             inNode = (Node) xPath.compile(path).evaluate(doc, XPathConstants.NODE);
        }
        catch (Exception e){}


        return inNode;
    }



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
    check if the input/out file is a valid xml file
     */
    public static boolean isXML(String fileName){

        try {
            File input = new File(fileName);
            DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
            dFactory.setValidating(false);
            dFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(input);
            boolean ret =doc.getElementsByTagName("operation").getLength() != 0 || doc.getElementsByTagName("wsdl:operation").getLength() != 0;
          //  System.out.println(fileName + "---xml :" + ret);

            return  doc.getElementsByTagName("operation").getLength() != 0 || doc.getElementsByTagName("wsdl:operation").getLength() != 0;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }



    }

    /*
        parseDOM takes in a file name in String and returns a hashmap with operation names as keys, another hashmap as values.
        The inner hashmap stores the output and input messages.
     */
    public static Map<String, Map<String, Vector<String>>> parseDOM(String fileName){
         Map<String, Map<String, Vector<String>>> ret = new HashMap<String , Map<String, Vector<String>>>();


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
                    Map<String, Vector<String>> io = new HashMap<String, Vector<String>>();
                    Vector<String> v_in = new Vector<>();
                    Vector<String> v_out = new Vector<>();
                    Node n = nList.item(i);
                    Element ele = (Element)n;
                    String name = ele.getAttribute("name");


                    try {
                        String in_s = splitString(ele.getElementsByTagName(in).item(0).getAttributes().getNamedItem("message").getNodeValue());
                        String out_s = splitString(ele.getElementsByTagName(out).item(0).getAttributes().getNamedItem("message").getNodeValue());
                        // searching the struct of the input/output parameter
                        String xPath_in = "//*" +"[@name=\""+in_s+"\"]";
                        String xPath_out = "//*" +"[@name=\""+out_s+"\"]";
                        Node inNode = (Node) xPath.compile(xPath_in).evaluate(doc, XPathConstants.NODE);
                        Element pEle = (Element)inNode ;

                        Node outNode = (Node) xPath.compile(xPath_out).evaluate(doc, XPathConstants.NODE);
                        Element pEle_out = (Element) outNode;
                        try {
                            if (pEle != null) {
                                    if(pEle.getElementsByTagName(part).getLength() != 0) {
                                        if(pEle.getElementsByTagName(part).item(0).getAttributes().getNamedItem("element") != null) {
                                            v_in.add( splitString(pEle.getElementsByTagName(part).item(0).getAttributes().getNamedItem("element").getNodeValue()));
                                        }
                                        else if(pEle.getElementsByTagName(part).item(0).getAttributes().getNamedItem("type") != null){
                                            String type = splitString(pEle.getElementsByTagName(part).item(0).getAttributes().getNamedItem("type").getNodeValue());
                                            if(PrimType.contains(type)) {
                                                v_in.add(splitString(pEle.getElementsByTagName(part).item(0).getAttributes().getNamedItem("name").getNodeValue()));
                                            }
                                            else{

                                                String path_to_struct = "//*" + "[@name=\"" +type+ "\"]" ;
                                                Node sNode = (Node) xPath.compile(path_to_struct).evaluate(doc, XPathConstants.NODE);
                                                //System.out.println(sNode.getChildNodes().item(1).getChildNodes().item(1).getAttributes()
                                                 //       .getNamedItem("name").getNodeValue() + " length of node list : " + sNode.getChildNodes().item(1).getChildNodes().getLength());

                                                int parm_len = sNode.getChildNodes().item(1).getChildNodes().getLength();
                                                for(int j  = 1 ; j < parm_len ; j++) {
                                                    if(j %2 != 0 ) {
                                                        //System.out.println(sNode.getChildNodes().item(1).getChildNodes().item(j).getAttributes().getNamedItem("name").getNodeValue());

                                                          v_in.add(sNode.getChildNodes().item(1).getChildNodes().item(j).getAttributes().getNamedItem("name").getNodeValue());
                                                    }
                                                }
                                            }

                                        }


                                    }


                                io.put("input", v_in);
                            }
                            if(pEle_out != null) {
                                if(pEle_out.getElementsByTagName(part).getLength() != 0)
                                    if(pEle_out.getElementsByTagName(part).item(0).getAttributes().getNamedItem("element") != null)
                                    v_out.add(splitString(pEle_out.getElementsByTagName(part).item(0).getAttributes().getNamedItem("element").getNodeValue()));
                                    else if(pEle_out.getElementsByTagName(part).item(0).getAttributes().getNamedItem("type") != null) {
                                        String type = splitString(pEle_out.getElementsByTagName(part).item(0).getAttributes().getNamedItem("type").getNodeValue());
                                        if(PrimType.contains(type)) {
                                            v_out.add(splitString(pEle_out.getElementsByTagName(part).item(0).getAttributes().getNamedItem("type").getNodeValue()));
                                        }
                                        else{

                                            String path_to_struct = "//*" + "[@name=\"" +type+ "\"]" ;
                                            Node sNode = (Node) xPath.compile(path_to_struct).evaluate(doc, XPathConstants.NODE);
                                         //   System.out.println(sNode.getAttributes().getNamedItem("name").getNodeValue());
                                            v_out.add("to be implemented");

                                        }

                                    }
                                io.put("output", v_out);

                            }
                        }
                        catch (Exception e){
                         //   e.printStackTrace();

                        }

                        ret.put(name, io);
                    }
                    catch (Exception e){
                     //   e.printStackTrace();
                    }

                }

            }
            catch(Exception e){

            //    e.printStackTrace();
            }
    return ret;
    }

    public static void buildDoc(WSMatchingType wsm){
        try {
            File file = new File("src/matcher/output/output.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(WSMatchingType.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(wsm, file);
            jaxbMarshaller.marshal(wsm, System.out);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /*
    this method calcaltes each combination of operation compparison.
     */
    public static MatchedWebServiceType buildWSObject( Map<String, Map<String, Vector<String>>> i ,  Map<String, Map<String, Vector<String>>>  o,String in_serviceName, String out_serviceName){
        MatchedWebServiceType mwst = new MatchedWebServiceType();
        List<MatchedOperationType> list_matchO = mwst.getMacthedOperation();
        MatchedOperationType mot = new MatchedOperationType();
        mwst.setInputServiceName(in_serviceName);
        mwst.setOutputServiceName(out_serviceName);
        try {
            double sum = 0;
            int counter =0;
            for (Map.Entry<String, Map<String, Vector<String>>> ii : i.entrySet()) {
                for (Map.Entry<String, Map<String, Vector<String>>> oi : o.entrySet()) {

                    // setting matching elements

                    mot = new MatchedOperationType();
                    mot.setInputOperationName(ii.getKey());
                    mot.setOutputOperationName(oi.getKey());
                    Double optName = EditDistance.getSimilarity(ii.getKey(), oi.getKey());
                    mot.setOpScore(optName);
                    sum+=optName; counter ++;
                    List<MatchedElementType> list_met = mot.getMacthedElement();

                    //System.out.println("Operation Name: " + ii.getKey() + " & " + oi.getKey() + " = " + optName);
                    int len = Math.min(ii.getValue().get("input").size(), oi.getValue().get("output").size());
                    if (len != 0)
                        for (int j = 0; j < len; j++) {
                            Double inoutName = EditDistance.getSimilarity(ii.getValue().get("input").get(j), oi.getValue().get("output").get(j));
                    //        System.out.println("Element Name: " + ii.getValue().get("input").get(j) + " + " + oi.getValue().get("output").get(j) + " = " + inoutName);
                            MatchedElementType met = new MatchedElementType();
                            met.setInputElement(ii.getValue().get("input").get(j));
                            met.setOutputElement(oi.getValue().get("output").get(j));
                            met.setScore(inoutName);
                            sum+=inoutName; counter++;
                            list_met.add(met);

                        }

                    list_matchO.add(mot) ;
                }
            }
            mwst.setWsScore(sum/counter);
        }
        catch(Exception e){}
                return mwst;
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

    public static void SynMatchign(final File folder){
        Vector<String> history = new Vector<String>();
        WSMatchingType wsm = new WSMatchingType();
        List<MatchedWebServiceType> list_matchT = wsm.getMacthing();

        for(final File file1 : folder.listFiles()){
            history.add(file1.getName());
            for(final File file2 : folder.listFiles()){
                if(! history.contains(file2.getName())){

                    if(isXML("WSDLs/" +file1.getName()) && isXML("WSDLs/" +file2.getName())) {
                        input = parseDOM("WSDLs/" + file1.getName());
                        output = parseDOM("WSDLs/" + file2.getName());
                        list_matchT.add(buildWSObject(input, output, file1.getName(), file2.getName()));
                    }

                }

            }

        }
        buildDoc(wsm);

    }


    public static void main(String[] args){
        init();

        SynMatchign(new File("WSDLs"));

    }

}
