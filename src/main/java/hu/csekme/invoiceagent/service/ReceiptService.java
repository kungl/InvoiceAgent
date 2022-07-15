package hu.csekme.invoiceagent.service;

import hu.csekme.invoiceagent.InvoiceAgent;
import hu.csekme.invoiceagent.beans.HomeBean;
import hu.csekme.invoiceagent.dao.XmlResponseDao;
import hu.csekme.invoiceagent.domain.Receipt;
import hu.csekme.invoiceagent.domain.ReceiptEntry;
import hu.csekme.invoiceagent.domain.ReceiptSend;
import hu.csekme.invoiceagent.enums.Action;
import hu.szamlazz.xmlnyugtacreate.*;
import hu.szamlazz.xmlnyugtaget.Xmlnyugtaget;
import hu.szamlazz.xmlnyugtasend.EmailKuldes;
import hu.szamlazz.xmlnyugtasend.Xmlnyugtasend;
import hu.szamlazz.xmlnyugtasendvalasz.Xmlnyugtasendvalasz;
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
 *
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
  HomeBean home;

  @Inject
  XmlResponseDao dao;

  /**
   * Send a filled receipt from the UI to szamlazz.hu
   * the successful receipt is stored in the dao
   *
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

    Xmlnyugtavalasz response = dataExchange(xml, Action.END_POINT_CREATE_RECEIPT, Xmlnyugtavalasz.class);

    //the successful receipt is stored in the dao
    if (response.isSikeres()) {
      home.increaseSuccessReceipt();
      dao.addReceipt(response);
    } else {
      home.increaseFailedReceipt();
    }
    return response;
  }

  /**
   * Send a filled receipt from the UI to szamlazz.hu
   * the successful receipt is stored in the dao
   *
   * @param receiptNumber
   * @return
   */
  public Xmlnyugtavalasz build(String receiptNumber) {
    Xmlnyugtaget xmlnyugtaget = new Xmlnyugtaget();
    hu.szamlazz.xmlnyugtaget.ObjectFactory objectFactory = new hu.szamlazz.xmlnyugtaget.ObjectFactory();
    hu.szamlazz.xmlnyugtaget.BeallitasokTipus settings = objectFactory.createBeallitasokTipus();
    settings.setSzamlaagentkulcs(agent.getKey());
    settings.setPdfLetoltes(true);
    hu.szamlazz.xmlnyugtaget.FejlecTipus head = new hu.szamlazz.xmlnyugtaget.FejlecTipus();
    head.setNyugtaszam(receiptNumber);
    xmlnyugtaget.setBeallitasok(settings);
    xmlnyugtaget.setFejlec(head);
    Xmlnyugtavalasz response = dataExchange(xmlnyugtaget, Action.END_POINT_GET_RECEIPT, Xmlnyugtavalasz.class);
    if (response.isSikeres()) {
      home.increaseSuccessReceipt();
      dao.addReceipt(response);
    } else {
      home.increaseFailedReceipt();
    }
    return response;
  }

  /**
   *
   * @param send
   * @return
   */
  public Xmlnyugtasendvalasz build(ReceiptSend send) {

    Xmlnyugtasend xmlnyugtasend = new Xmlnyugtasend();
    hu.szamlazz.xmlnyugtasend.BeallitasokTipus settings = new hu.szamlazz.xmlnyugtasend.BeallitasokTipus();
    hu.szamlazz.xmlnyugtasend.FejlecTipus head = new hu.szamlazz.xmlnyugtasend.FejlecTipus();
    EmailKuldes mail = new EmailKuldes();

    settings.setSzamlaagentkulcs(agent.getKey());
    head.setNyugtaszam(send.getReceiptNumber());
    mail.setEmail(send.getEmail());
    mail.setEmailReplyto(send.getReplyTo());
    mail.setEmailTargy(send.getSubject());
    mail.setEmailSzoveg(send.getMessage());

    xmlnyugtasend.setBeallitasok(settings);
    xmlnyugtasend.setFejlec(head);
    xmlnyugtasend.setEmailKuldes(mail);
    try {
      Xmlnyugtasendvalasz response = dataExchange(xmlnyugtasend, Action.END_POINT_SEND_RECEIPT, Xmlnyugtasendvalasz.class);
      return response;
    } catch (ClassCastException error) {
      //TODO: CsK:  alkalmanként előfordul ?? kivizsgálandó
      error.printStackTrace();
      logger.severe("[CRITICAL ERROR]");
      logger.severe(error.getMessage());
      Xmlnyugtasendvalasz xmlnyugtasendvalasz = new Xmlnyugtasendvalasz();
      xmlnyugtasendvalasz.setHibauzenet("Hiba történt, kérjük próbálja meg újra.");
      xmlnyugtasendvalasz.setHibakod(500);
      xmlnyugtasendvalasz.setSikeres(false);
      return  xmlnyugtasendvalasz;
    }
  }

  /**
   * data exchange with InvoiceAgent
   * errors from InvoiceAgent are included in the receipt of error message and the error code 500 will be
   *
   * @param xml request xml
   * @return response sml
   */
  private synchronized  <T> T dataExchange(Object xml, Action action, Class<T> response) {
    try {
      File file = InvoiceAgent.serialize(xml, true);
      return agent.call(file, action, response);
    } catch (Exception e) {
      e.printStackTrace();
      logger.severe(e.getMessage());
      if (response.getClass().equals(Xmlnyugtavalasz.class.getClass())) {
        Xmlnyugtavalasz xmlnyugtavalasz = new Xmlnyugtavalasz();
        xmlnyugtavalasz.setSikeres(false);
        xmlnyugtavalasz.setHibakod(500); //Internal server error
        xmlnyugtavalasz.setHibauzenet(e.getMessage());
        return (T) xmlnyugtavalasz;
      }
      if (response.getClass().equals(Xmlnyugtasendvalasz.class.getClass())){
        Xmlnyugtasendvalasz xmlnyugtasendvalasz = new Xmlnyugtasendvalasz();
        xmlnyugtasendvalasz.setSikeres(false);
        xmlnyugtasendvalasz.setHibakod(500); //Internal server error
        xmlnyugtasendvalasz.setHibauzenet(e.getMessage());
        return (T) xmlnyugtasendvalasz;
      }
    }
    throw new RuntimeException("unimplemented response type error");
  }

}
