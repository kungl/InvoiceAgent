package hu.csekme.invoiceagent.service;

import hu.csekme.httphelper.ContentType;
import hu.csekme.httphelper.Poster;
import hu.csekme.httphelper.RequestMethod;
import hu.csekme.httphelper.Response;
import hu.szamlazz.xmlnyugtacreate.*;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
      Poster poster = Poster.Builder.create(INVOICE_AGENT_API)
              .addHeaderParam("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36")
              .addFormField("generate", ContentType.SUBMIT, "Create receipt")
              .addFileField("szamla_agent_nyugta_create", file)
              .build();
      Response response = poster.connect(RequestMethod.POST);
      logger.warning(response.getStream().toString(StandardCharsets.UTF_8.toString()));


    } catch (Exception e) {
      e.printStackTrace();
    }


  }

}
