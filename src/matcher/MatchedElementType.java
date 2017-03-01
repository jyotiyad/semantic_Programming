
package matcher;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MatchedElementType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MatchedElementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OutputElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="InputElement" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Score" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MatchedElementType", namespace = "http://www.kth.se/ict/id2208/Matching", propOrder = {
    "outputElement",
    "inputElement",
    "score"
})
public class MatchedElementType {

    @XmlElement(name = "OutputElement", namespace = "http://www.kth.se/ict/id2208/Matching", required = true)
    protected String outputElement;
    @XmlElement(name = "InputElement", namespace = "http://www.kth.se/ict/id2208/Matching", required = true)
    protected String inputElement;
    @XmlElement(name = "Score", namespace = "http://www.kth.se/ict/id2208/Matching")
    protected double score;

    /**
     * Gets the value of the outputElement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutputElement() {
        return outputElement;
    }

    /**
     * Sets the value of the outputElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutputElement(String value) {
        this.outputElement = value;
    }

    /**
     * Gets the value of the inputElement property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInputElement() {
        return inputElement;
    }

    /**
     * Sets the value of the inputElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInputElement(String value) {
        this.inputElement = value;
    }

    /**
     * Gets the value of the score property.
     * 
     */
    public double getScore() {
        return score;
    }

    /**
     * Sets the value of the score property.
     * 
     */
    public void setScore(double value) {
        this.score = value;
    }

}
