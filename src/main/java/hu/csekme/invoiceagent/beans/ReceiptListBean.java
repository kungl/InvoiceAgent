package hu.csekme.invoiceagent.beans;
import hu.csekme.invoiceagent.dao.XmlResponseDao;
import hu.szamlazz.xmlnyugtavalasz.Xmlnyugtavalasz;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

@Named
@RequestScoped
public class ReceiptListBean {

  @Inject
  XmlResponseDao dao;

  Xmlnyugtavalasz selected;

  /**
   * Download Receipe PDF
   * @throws IOException
   */
  public void download() throws IOException {
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
}
