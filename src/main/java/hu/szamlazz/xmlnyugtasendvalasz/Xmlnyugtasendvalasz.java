//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.07.15 at 08:51:13 AM CEST 
//


package hu.szamlazz.xmlnyugtasendvalasz;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="sikeres" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="hibakod" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="hibauzenet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "xmlnyugtasendvalasz")
public class Xmlnyugtasendvalasz {

    protected boolean sikeres;
    protected Integer hibakod;
    protected String hibauzenet;

    /**
     * Gets the value of the sikeres property.
     * 
     */
    public boolean isSikeres() {
        return sikeres;
    }

    /**
     * Sets the value of the sikeres property.
     * 
     */
    public void setSikeres(boolean value) {
        this.sikeres = value;
    }

    /**
     * Gets the value of the hibakod property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHibakod() {
        return hibakod;
    }

    /**
     * Sets the value of the hibakod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHibakod(Integer value) {
        this.hibakod = value;
    }

    /**
     * Gets the value of the hibauzenet property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHibauzenet() {
        return hibauzenet;
    }

    /**
     * Sets the value of the hibauzenet property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHibauzenet(String value) {
        this.hibauzenet = value;
    }

    @Override
    public String toString() {
        return "Xmlnyugtasendvalasz{" +
                "sikeres=" + sikeres +
                ", hibakod=" + hibakod +
                ", hibauzenet='" + hibauzenet + '\'' +
                '}';
    }
}