//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.07.15 at 02:01:18 PM CEST 
//


package hu.szamlazz.xmlnyugtavalasz;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for osszegekTipus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="osszegekTipus"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="afakulcsossz" type="{http://www.szamlazz.hu/xmlnyugtavalasz}afakulcsosszTipus" maxOccurs="unbounded"/&gt;
 *         &lt;element name="totalossz" type="{http://www.szamlazz.hu/xmlnyugtavalasz}totalosszTipus"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "osszegekTipus", propOrder = {
    "afakulcsossz",
    "totalossz"
})
public class OsszegekTipus {

    @XmlElement(required = true)
    protected List<AfakulcsosszTipus> afakulcsossz;
    @XmlElement(required = true)
    protected TotalosszTipus totalossz;

    /**
     * Gets the value of the afakulcsossz property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the afakulcsossz property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAfakulcsossz().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AfakulcsosszTipus }
     * 
     * 
     */
    public List<AfakulcsosszTipus> getAfakulcsossz() {
        if (afakulcsossz == null) {
            afakulcsossz = new ArrayList<AfakulcsosszTipus>();
        }
        return this.afakulcsossz;
    }

    /**
     * Gets the value of the totalossz property.
     * 
     * @return
     *     possible object is
     *     {@link TotalosszTipus }
     *     
     */
    public TotalosszTipus getTotalossz() {
        return totalossz;
    }

    /**
     * Sets the value of the totalossz property.
     * 
     * @param value
     *     allowed object is
     *     {@link TotalosszTipus }
     *     
     */
    public void setTotalossz(TotalosszTipus value) {
        this.totalossz = value;
    }

}
