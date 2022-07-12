package hu.csekme.invoiceagent;

import javax.faces.application.ResourceHandler;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Use the Authorization filter to force the entry of the invoice agent key
 * @author Krisztián Csekme
 * @see hu.csekme.invoiceagent.beans.LoginBean
 */
@WebFilter(filterName = "AuthFilter")
public class AuthorizationFilter implements Filter {

  private static final String AJAX_REDIRECT_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><partial-response><redirect url=\"%s\"></redirect></partial-response>";
  private static final String LOGIN_URL = "/login.xhtml";
  private static final Logger logger = Logger.getLogger(AuthorizationFilter.class.getName());
  public static final String COOKIE_INVOICE_AGENT = "cookie-invoice-agent";

  /**
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
    boolean loggedIn = (ses != null) && (ses.getAttribute(COOKIE_INVOICE_AGENT) != null);
    //meghatározzuk hogy erőforrás kérésről van-e szó
    boolean resourceRequest = req.getRequestURI().startsWith(req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER + "/");
    boolean loginRequest = req.getRequestURI().equals(loginURL);
    boolean ajaxRequest = "partial/ajax".equals(req.getHeader("Faces-Request"));
    if (loggedIn || loginRequest || resourceRequest || reqURI.contains("login.xhtml;")) {
      // Prevent browser from caching restricted resources.
      if (!resourceRequest) {
        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        res.setDateHeader("Expires", 0); // Proxies.
      }
      chain.doFilter(request, response); // továbbítjuk a kérést
    } else if (ajaxRequest) {
      try {
        res.setContentType("text/xml");
        res.setCharacterEncoding("UTF-8");
        res.getWriter().printf(AJAX_REDIRECT_XML, loginURL); // So, return special XML response instructing JSF ajax to send a redirect.
      } catch (IOException ex) {
        logger.severe(ex.getMessage());
      }
    } else {
      try {
        res.sendRedirect(req.getContextPath() + LOGIN_URL);
      } catch (IOException ex) {
        logger.severe(ex.getMessage());
      }

    }
  }
}
