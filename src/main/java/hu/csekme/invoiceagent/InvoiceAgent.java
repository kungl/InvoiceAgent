package hu.csekme.invoiceagent;

import hu.csekme.invoiceagent.enums.Action;
import hu.szamlazz.xmlnyugtavalasz.Xmlnyugtavalasz;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.net.ssl.SSLContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Invoice Agent
 * Implement an interface with the InvoiceAgent service of szamlazz.hu
 * The communication requires the key received from szamlazz.hu
 * api: https://www.szamlazz.hu/szamla/
 * docs: https://docs.szamlazz.hu/
 * @see #key
 * @see Action
 * @author Krisztián Csekme
 */
@Named
@SessionScoped
public class InvoiceAgent implements Serializable {

  private static final Logger logger = Logger.getLogger(InvoiceAgent.class.getName());
  private static final String INVOICE_AGENT_API = "https://www.szamlazz.hu/szamla/";

  private BasicHttpClientConnectionManager connectionManager;
  private SSLConnectionSocketFactory sslsf;
  private String key;

  /**
   * One time initialization
   * @throws Exception in case of an unexpected event
   */
  @PostConstruct
  public void init()  {
    try {
      TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
      SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
      sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
      Registry<ConnectionSocketFactory> socketFactoryRegistry =
              RegistryBuilder.<ConnectionSocketFactory>create()
                      .register("https", sslsf)
                      .register("http", new PlainConnectionSocketFactory())
                      .build();
      connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry);
    } catch (Exception err) {
      err.printStackTrace();
      logger.severe(err.getMessage());
    }
  }

  /**
   * Create request to https://www.szamlazz.hu/szamla/ endpoint with an xml file
   * @param xml valid xml based request file
   * @param action endpoint action
   * @return Xmlnyugtavalasz as response
   * @throws Exception in case of an unexpected event
   * @see Action
   */
  public synchronized  <T> T call(File xml, Action action, Class<T> responseClass) throws Exception {
    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(connectionManager).build();
    HttpPost post = new HttpPost(INVOICE_AGENT_API);
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.addBinaryBody(action.toString(), xml);
    HttpEntity multipart = builder.build();
    post.setEntity(multipart);
    CloseableHttpResponse response = httpClient.execute(post);
    HttpEntity responseEntity = response.getEntity();
    BufferedInputStream in = new BufferedInputStream(responseEntity.getContent());
    byte[] contents = new byte[1024];
    int bytesRead = 0;
    StringBuilder sb = new StringBuilder();
    while ((bytesRead = in.read(contents)) != -1) {
      sb.append(new String(contents, 0, bytesRead, StandardCharsets.UTF_8));
    }
    return deserialize(sb.toString(), responseClass);
  }

  /**
   * Produces a java object from xml content
   * @param xml xml content
   * @param clazz java class
   * @param <T> type
   * @return java object
   * @throws Exception in the event of an unsuccessful conversion
   */
  public static <T> T deserialize(String xml, Class<T> clazz) throws Exception {
    JAXBContext context = JAXBContext.newInstance(clazz);
    Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
    StringReader reader = new StringReader(xml);
    return (T) jaxbUnmarshaller.unmarshal(reader);
  }

  /**
   * Creates an xml file from a pre-mapped java object
   * @param object pre-mapped java object
   * @param temporary the file is only temporarily stored
   * @param <T> type
   * @return the xml file
   * @throws Exception in the event of an unsuccessful conversion
   */
  public static <T> File serialize(T object, boolean temporary) throws Exception {
    JAXBContext context = JAXBContext.newInstance(object.getClass());
    Marshaller mar = context.createMarshaller();
    mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    String tempFilePath = String.format("%s%s%s.xml", System.getProperty("user.home"), System.getProperty("file.separator"), UUID.randomUUID().toString());
    File file = new File(tempFilePath);
    if (temporary) {
      file.deleteOnExit();
    }
    mar.marshal(object, file);
    return file;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }
}
