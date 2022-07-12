package hu.csekme.invoiceagent.beans;
import hu.csekme.invoiceagent.AuthorizationFilter;
import hu.csekme.invoiceagent.InvoiceAgent;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Access control bean
 * @author Krisztián Csekme
 */
@Named
@RequestScoped
public class LoginBean implements Serializable {

  private static final Logger logger = Logger.getLogger(LoginBean.class.getName());

  @Inject
  InvoiceAgent agent;

  //binded Invoice Agent key
  String key;

  /**
   * In order to continue using the application you must enter an invoice agent key
   * @return redirect to index page
   */
  public String login() {
    if (!key.isEmpty()) {
      logger.info(key);
      agent.setKey(key);
      getSession().setAttribute(AuthorizationFilter.COOKIE_INVOICE_AGENT, agent);
    }
    return "/index.xhtml?faces-redirect=true";
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  /**
   * Access to the http session
   * @return http session
   */
  private static HttpSession getSession() {
    return (HttpSession) FacesContext.getCurrentInstance()
            .getExternalContext().getSession(false);
  }
}
