package hu.csekme.invoiceagent.beans;
import hu.csekme.invoiceagent.dao.XmlResponseDao;
import hu.csekme.invoiceagent.domain.ReceiptSend;
import hu.csekme.invoiceagent.service.ReceiptService;
import hu.szamlazz.xmlnyugtasendvalasz.Xmlnyugtasendvalasz;
import hu.szamlazz.xmlnyugtavalasz.Xmlnyugtavalasz;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Base64;

@Named
@ViewScoped
public class ReceiptListBean implements Serializable {

  @Inject
  ReceiptService service;

  @Inject
  XmlResponseDao dao;

  Xmlnyugtavalasz selected;

  String receiptNumber;

  /**
   * Get receipt from szamlazz.hu
   */
  public String get() {
    if (receiptNumber==null || receiptNumber.isEmpty()) {
      addMessage(FacesMessage.SEVERITY_WARN, "Figyelem", "Kérem adja meg a nyugtaszámot");
      return "receipts.xhtml";
    }
    Xmlnyugtavalasz response =  service.build(receiptNumber);
    if (!response.isSikeres()) {
      addMessage(FacesMessage.SEVERITY_ERROR, "Hiba történt", response.getHibauzenet());
    }
    setReceiptNumber(null);
    FacesContext fc = FacesContext.getCurrentInstance();
    ExternalContext ec = fc.getExternalContext();
    // Clear headers
    ec.responseReset();
    return "receipts.xhtml?faces-redirect=true";
  }

  /**
   * Download Receipe PDF
   * @throws IOException
   */
  public void download() throws IOException {
    if (selected!=null && selected.getNyugtaPdf()!=null) {
      byte[] pdf = Base64.getDecoder().decode(selected.getNyugtaPdf());
      FacesContext fc = FacesContext.getCurrentInstance();
      ExternalContext ec = fc.getExternalContext();
      // Clear headers
      ec.responseReset();
      // Set new header
      ec.setResponseContentType("application/octet-stream");
      ec.setResponseContentLength(pdf.length);
      ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + selected.getNyugta().getAlap().getNyugtaszam() + ".pdf\""); // The Save As popup magic is done here. You can give it any file name you want, this only won't work in MSIE, it will use current request URL as file name instead.
      // Write data to output stream
      OutputStream output = ec.getResponseOutputStream();
      output.write(pdf);
      fc.responseComplete();
    }
  }

  public void send() {
    if (selected!=null) {
      ReceiptSend send = new ReceiptSend();
      send.setReceiptNumber(selected.getNyugta().getAlap().getNyugtaszam());
      send.setReplyTo("csekme.krisztian@outlook.com");
      send.setEmail("csekme.krisztian@outlook.com");
      send.setSubject("Próba levél");
      send.setMessage("Teszt üzenet");

        Xmlnyugtasendvalasz response = service.build(send);
        System.out.println(response);
        if (!response.isSikeres()) {
          addMessage(FacesMessage.SEVERITY_ERROR, "Hiba történt", response.getHibauzenet());
        } else {
          addMessage(FacesMessage.SEVERITY_INFO, "Levél küldése", "A nyugtát sikeresen elküldtük!");
        }

    }
  }

  /**
   * Send message to the UI
   * @param severity Level
   * @param summary as Title
   * @param detail as Body
   */
  public void addMessage(FacesMessage.Severity severity, String summary, String detail) {
    FacesContext.getCurrentInstance().
            addMessage(null, new FacesMessage(severity, summary, detail));
  }

  public XmlResponseDao getDao() {
    return dao;
  }

  public void setDao(XmlResponseDao dao) {
    this.dao = dao;
  }

  public Xmlnyugtavalasz getSelected() {
    return selected;
  }

  public void setSelected(Xmlnyugtavalasz selected) {
    this.selected = selected;
  }

  public String getReceiptNumber() {
    return receiptNumber;
  }

  public void setReceiptNumber(String receiptNumber) {
    this.receiptNumber = receiptNumber;
  }
}
