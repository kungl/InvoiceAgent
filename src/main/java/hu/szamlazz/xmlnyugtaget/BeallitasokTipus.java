//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.07.14 at 11:04:20 PM CEST 
//


package hu.szamlazz.xmlnyugtaget;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for beallitasokTipus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="beallitasokTipus"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="felhasznalo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="jelszo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="szamlaagentkulcs" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="pdfLetoltes" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "beallitasokTipus", propOrder = {

})
public class BeallitasokTipus {

    protected String felhasznalo;
    protected String jelszo;
    protected String szamlaagentkulcs;
    protected boolean pdfLetoltes;

    /**
     * Gets the value of the felhasznalo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFelhasznalo() {
        return felhasznalo;
    }

    /**
     * Sets the value of the felhasznalo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFelhasznalo(String value) {
        this.felhasznalo = value;
    }

    /**
     * Gets the value of the jelszo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJelszo() {
        return jelszo;
    }

    /**
     * Sets the value of the jelszo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJelszo(String value) {
        this.jelszo = value;
    }

    /**
     * Gets the value of the szamlaagentkulcs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSzamlaagentkulcs() {
        return szamlaagentkulcs;
    }

    /**
     * Sets the value of the szamlaagentkulcs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSzamlaagentkulcs(String value) {
        this.szamlaagentkulcs = value;
    }

    /**
     * Gets the value of the pdfLetoltes property.
     * 
     */
    public boolean isPdfLetoltes() {
        return pdfLetoltes;
    }

    /**
     * Sets the value of the pdfLetoltes property.
     * 
     */
    public void setPdfLetoltes(boolean value) {
        this.pdfLetoltes = value;
    }

}