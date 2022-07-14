package hu.csekme.invoiceagent.service;
import hu.csekme.invoiceagent.InvoiceAgent;
import hu.csekme.invoiceagent.dao.XmlResponseDao;
import hu.csekme.invoiceagent.domain.Receipt;
import hu.csekme.invoiceagent.domain.ReceiptEntry;
import hu.szamlazz.xmlnyugtacreate.*;
import hu.szamlazz.xmlnyugtavalasz.Xmlnyugtavalasz;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Receipt Management Service
 * With the help of this service, we can overcome the calling xml.
 * InvoiceAgent receives the assembled xml and provides an interface to szamlazz.hu
 * The generated receipts are stored in the XmlResponseDao repository
 * @see InvoiceAgent
 * @see XmlResponseDao
 */
@Named
@ApplicationScoped
public class ReceiptService implements Serializable {

  private static final Logger logger = Logger.getLogger(ReceiptService.class.getName());

  @Inject
  InvoiceAgent agent;

  @Inject
  XmlResponseDao dao;

  /**
   * Send a filled receipt from the UI to szamlazz.hu
   * the successful receipt is stored in the dao
   * @param receipt filled receipt from UI
   * @return the receipt is returned to the UI for evaluation (error message)
   */
  public Xmlnyugtavalasz build(Receipt receipt) {
    logger.log(Level.FINE, "generate receipt {0}", new Object[]{receipt});
    //settings
    ObjectFactory objectFactory = new ObjectFactory();
    BeallitasokTipus settings = objectFactory.createBeallitasokTipus();
    settings.setSzamlaagentkulcs(agent.getKey());
    settings.setPdfLetoltes(true);
    //head
    FejlecTipus head = objectFactory.createFejlecTipus();
    head.setHivasAzonosito(UUID.randomUUID().toString());
    head.setPdfSablon(receipt.getPdfTemplate());
    head.setElotag(receipt.getPrefix());
    head.setFizmod(receipt.getPaymentMethod());
    head.setPenznem(receipt.getCurrency());
    head.setDevizabank("MNB");
    head.setDevizaarf(0d);
    //entries
    TetelekTipus entries = objectFactory.createTetelekTipus();

    for (ReceiptEntry e : receipt.getReceiptEntryList()) {
      TetelTipus entry = objectFactory.createTetelTipus();
      entry.setMegnevezes(e.getEntryName());
      entry.setMennyiseg(e.getEntryQuantity());
      entry.setMennyisegiEgyseg(e.getEntryUnit());
      entry.setNettoEgysegar(e.getEntryNetUnitPrice());
      entry.setNetto(e.getEntryNet());
      entry.setAfakulcs(e.getEntryVatKey());
      entry.setAfa(e.getEntryVat());
      entry.setBrutto(e.getEntryGross());
      entries.getTetel().add(entry);
    }

    Xmlnyugtacreate xml = objectFactory.createXmlnyugtacreate();
    xml.setBeallitasok(settings);
    xml.setFejlec(head);
    xml.setTetelek(entries);

    Xmlnyugtavalasz response = dataExchange(xml);

    //the successful receipt is stored in the dao
    if (response.isSikeres()) {
      dao.addReceipt(response);
    }
    return response;
  }

  /**
   * data exchange with InvoiceAgent
   * errors from InvoiceAgent are included in the receipt of error message and the error code 500 will be
   * @param xml request xml
   * @return response sml
   */
  private Xmlnyugtavalasz dataExchange(Xmlnyugtacreate xml) {
    try {
       File file = InvoiceAgent.serialize(xml, true);
       return agent.createReceipt(file);
    } catch (Exception e) {
      Xmlnyugtavalasz xmlnyugtavalasz = new Xmlnyugtavalasz();
      xmlnyugtavalasz.setSikeres(false);
      xmlnyugtavalasz.setHibakod(500); //Internal server error
      xmlnyugtavalasz.setHibauzenet(e.getMessage());
      logger.severe(e.getMessage());
      e.printStackTrace();
      return xmlnyugtavalasz;
    }
  }

}
