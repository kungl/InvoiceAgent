package hu.csekme.invoiceagent.service;
import hu.csekme.invoiceagent.InvoiceAgent;
import hu.csekme.invoiceagent.domain.Receipt;
import hu.csekme.invoiceagent.domain.ReceiptEntry;
import hu.szamlazz.xmlnyugtacreate.*;
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
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class ReceiptService implements Serializable {
  private static final Logger logger = Logger.getLogger(ReceiptService.class.getName());
  private static final String INVOICE_AGENT_API = "https://www.szamlazz.hu/szamla/";

  @Inject
  InvoiceAgent agent;

  public void generate(Receipt receipt) {
    logger.log(Level.INFO, "generate receipt {0}", new Object[]{receipt});
    //settings
    ObjectFactory objectFactory = new ObjectFactory();
    BeallitasokTipus settings = objectFactory.createBeallitasokTipus();
    settings.setSzamlaagentkulcs(agent.getKey());
    settings.setPdfLetoltes(true);
    //head
    FejlecTipus head = objectFactory.createFejlecTipus();
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

    Xmlnyugtavalasz valasz =  createReceipt(xml);
    if (valasz.isSikeres()) {
      logger.info("siker");
    } else {
      logger.warning(valasz.toString());
    }
  }



  public Xmlnyugtavalasz createReceipt(Xmlnyugtacreate xml) {
    logger.info("generate");
    try {

      JAXBContext context = JAXBContext.newInstance(Xmlnyugtacreate.class);
      Marshaller mar = context.createMarshaller();
      mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      String tempFilePath = String.format("%s%s%s.xml", System.getProperty("user.home"), System.getProperty("file.separator"), UUID.randomUUID().toString());
      File file = new File(tempFilePath);
      logger.log(Level.INFO, "temporary file {0}", new Object[]{tempFilePath});
      file.deleteOnExit();
      mar.marshal(xml, file);
      TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
      SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
      SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
              NoopHostnameVerifier.INSTANCE);

      Registry<ConnectionSocketFactory> socketFactoryRegistry =
              RegistryBuilder.<ConnectionSocketFactory> create()
                      .register("https", sslsf)
                      .register("http", new PlainConnectionSocketFactory())
                      .build();

      BasicHttpClientConnectionManager connectionManager =
              new BasicHttpClientConnectionManager(socketFactoryRegistry);
      CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
              .setConnectionManager(connectionManager).build();
      HttpPost post = new HttpPost(INVOICE_AGENT_API);
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();

      builder.addBinaryBody("action-szamla_agent_nyugta_create", file);

      HttpEntity multipart = builder.build();
      post.setEntity(multipart);
      CloseableHttpResponse response = httpClient.execute(post);
      HttpEntity responseEntity = response.getEntity();
      BufferedInputStream in = new BufferedInputStream(responseEntity.getContent());
      byte[] contents = new byte[1024];

      int bytesRead = 0;

      StringBuilder sb = new StringBuilder();
      while((bytesRead = in.read(contents)) != -1) {
        sb.append(new String(contents, 0, bytesRead, StandardCharsets.UTF_8));
      }

      context = JAXBContext.newInstance(Xmlnyugtavalasz.class);
      Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
      StringReader reader = new StringReader(sb.toString());
      return (Xmlnyugtavalasz)jaxbUnmarshaller.unmarshal(reader);

    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}
