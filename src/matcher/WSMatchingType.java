
package matcher;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.*;


/**
 * <p>Java class for WSMatchingType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="WSMatchingType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Macthing" type="{http://www.kth.se/ict/id2208/Matching}MatchedWebServiceType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSMatchingType", namespace = "http://www.kth.se/ict/id2208/Matching", propOrder = {
    "macthing"
})
@XmlRootElement
public class WSMatchingType {

    @XmlElement(name = "Macthing", namespace = "http://www.kth.se/ict/id2208/Matching", required = true)
    protected List<MatchedWebServiceType> macthing;

    /**
     * Gets the value of the macthing property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the macthing property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMacthing().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MatchedWebServiceType }
     * 
     * 
     */
    public List<MatchedWebServiceType> getMacthing() {
        if (macthing == null) {
            macthing = new ArrayList<MatchedWebServiceType>();
        }
        return this.macthing;
    }

}
