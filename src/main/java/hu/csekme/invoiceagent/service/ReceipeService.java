package hu.csekme.invoiceagent.service;

import hu.csekme.httphelper.ContentType;
import hu.csekme.httphelper.Poster;
import hu.csekme.httphelper.RequestMethod;
import hu.csekme.httphelper.Response;
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
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class ReceipeService implements Serializable {
  private static final Logger logger = Logger.getLogger(ReceipeService.class.getName());
  private static final String INVOICE_AGENT_API = "https://www.szamlazz.hu/szamla/";

  public void generate() {
    logger.warning("generate");
    ObjectFactory o = new ObjectFactory();
    BeallitasokTipus beallitasok = o.createBeallitasokTipus();
    beallitasok.setFelhasznalo("csekme.krisztian@outlook.com");
    beallitasok.setJelszo("ssqN7Lq9q!AwAhefVn8q#");
    beallitasok.setSzamlaagentkulcs("z4dxiimpcqhb8m8fiimpcqj92dnviimpcqs2u3evii");
    beallitasok.setPdfLetoltes(true);

    FejlecTipus fejlec = o.createFejlecTipus();
    fejlec.setElotag("NYGTA");
    fejlec.setFizmod("készpénz");
    fejlec.setPenznem("Ft");
    fejlec.setDevizabank("MNB");
    fejlec.setDevizaarf(0d);

    TetelekTipus tetelek = o.createTetelekTipus();

    TetelTipus tetel = o.createTetelTipus();
    tetel.setMegnevezes("Valami csoda");
    tetel.setMennyiseg(5);
    tetel.setMennyisegiEgyseg("db");
    tetel.setNettoEgysegar(10000);
    tetel.setNetto(50000);
    tetel.setAfakulcs("27");
    tetel.setAfa(tetel.getNetto() * 0.27);
    tetel.setBrutto(tetel.getNetto() + tetel.getAfa());

    tetelek.getTetel().add(tetel);

    Xmlnyugtacreate xml = o.createXmlnyugtacreate();
    xml.setBeallitasok(beallitasok);
    xml.setFejlec(fejlec);
    xml.setTetelek(tetelek);

    try {

      JAXBContext context = JAXBContext.newInstance(Xmlnyugtacreate.class);
      Marshaller mar = context.createMarshaller();
      mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

      StringWriter writer = new StringWriter();
      File file = new File("c:/tmp/001.xml");
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
      String strFileContents = "";
      while((bytesRead = in.read(contents)) != -1) {
        strFileContents += new String(contents, 0, bytesRead);
      }

      System.out.print(strFileContents);


      /*
      Poster poster = Poster.Builder.create(INVOICE_AGENT_API)
              .addHeaderParam("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36")
              .addFormField("generate", ContentType.SUBMIT, "Create receipt")
              .addFileField("action-szamla_agent_nyugta_create", file)
              .build();
      Response response = poster.connect(RequestMethod.POST);
      logger.warning(response.getStream().toString(StandardCharsets.UTF_8.toString()));
    */

    } catch (Exception e) {
      e.printStackTrace();
    }


  }


  private void SSL() {
    try {
      SSLContext ssl_ctx = SSLContext.getInstance("TLS");
      TrustManager[] trust_mgr = new TrustManager[]{
              new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                  return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String t) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String t) {
                }
              }
      };
      ssl_ctx.init(null, // key manager
              trust_mgr,           // trust manager
              new SecureRandom()); // random number generator
      HttpsURLConnection.setDefaultSSLSocketFactory(ssl_ctx.getSocketFactory());
    } catch (Exception err) {
      throw new RuntimeException(err);
    }
  }
}
