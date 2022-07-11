package hu.csekme.invoiceagent;

import javax.faces.application.ResourceHandler;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthFilter", urlPatterns = {"*.xhtml"})
public class AuthorizationFilter implements Filter {

  private static final String AJAX_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><partial-response><redirect url=\"%s\"></redirect></partial-response>";
  private static final String LOGIN_URL = "/login.xhtml";
  private static final String TOKEN_INVOICE_AGENT_KEY = "invoice-agent-key";

  /**
   *
   * @param request  the <code>ServletRequest</code> object contains the client's request
   * @param response the <code>ServletResponse</code> object contains the filter's response
   * @param chain    the <code>FilterChain</code> for invoking the next filter or the resource
   * @throws IOException      if an I/O related error has occurred during the processing
   * @throws ServletException if an exception occurs that interferes with the
   *                          filter's normal operation
   * @see UnavailableException
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse res = (HttpServletResponse) response;
    HttpSession ses = req.getSession(true);
    String loginURL = req.getContextPath() + LOGIN_URL;
    String reqURI = req.getRequestURI();
    boolean loggedIn = (ses != null) && (ses.getAttribute(TOKEN_INVOICE_AGENT_KEY) != null);
    //meghatározzuk hogy erőforrás kérésről van-e szó
    boolean rsRequest = req.getRequestURI().startsWith(req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER + "/");
    if (loggedIn) {
      InvoiceAgent invoiceAgent = (InvoiceAgent) ses.getAttribute(TOKEN_INVOICE_AGENT_KEY);
    }
  }
}
