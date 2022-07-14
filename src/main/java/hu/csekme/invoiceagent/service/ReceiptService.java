package hu.csekme.invoiceagent.service;
import hu.csekme.invoiceagent.domain.Receipt;
import hu.csekme.invoiceagent.domain.ReceiptEntry;
import hu.szamlazz.xmlnyugtacreate.*;
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
import org.primefaces.shaded.commons.io.IOUtils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class ReceiptService implements Serializable {
  private static final Logger logger = Logger.getLogger(ReceiptService.class.getName());
  private static final String INVOICE_AGENT_API = "https://www.szamlazz.hu/szamla/";


  public void generate(Receipt receipt) {
    logger.log(Level.INFO, "generate receipt {0}", new Object[]{receipt});
    //settings
    ObjectFactory objectFactory = new ObjectFactory();
    BeallitasokTipus settings = objectFactory.createBeallitasokTipus();
    settings.setFelhasznalo("csekme.krisztian@outlook.com");
    settings.setJelszo("ssqN7Lq9q!AwAhefVn8q#");
    settings.setSzamlaagentkulcs("z4dxiimpcqhb8m8fiimpcqj92dnviimpcqs2u3evii");
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

    createReceipt(xml);
  }



  public void createReceipt(Xmlnyugtacreate xml) {
    logger.info("generate");
    try {

      JAXBContext context = JAXBContext.newInstance(Xmlnyugtacreate.class);
      Marshaller mar = context.createMarshaller();
      mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      mar.marshal(xml, bo);
      bo.flush();

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

      builder.addBinaryBody("action-szamla_agent_nyugta_create", new BufferedInputStream(new ByteArrayInputStream(bo.toByteArray())));

      HttpEntity multipart = builder.build();
      post.setEntity(multipart);
      CloseableHttpResponse response = httpClient.execute(post);
      HttpEntity responseEntity = response.getEntity();
      BufferedInputStream in = new BufferedInputStream(responseEntity.getContent());
      byte[] contents = new byte[1024];

      int bytesRead = 0;
      String strFileContents = "";
      while((bytesRead = in.read(contents)) != -1) {
        strFileContents += new String(contents, 0, bytesRead, StandardCharsets.UTF_8);
      }

      System.out.print(strFileContents);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
