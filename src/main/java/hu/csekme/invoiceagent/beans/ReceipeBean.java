package hu.csekme.invoiceagent.beans;

import hu.csekme.invoiceagent.domain.ReceiptEntry;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.*;

/**
 * Receipt editor bean
 *
 * @author Krisztián Csekme
 * @see ReceiptEntry
 */
@Named
@ViewScoped
public class ReceipeBean implements Serializable {
  /**
   * átutalás, készpénz, bankkártya, csekk, utánvét, ajándékutalvány,
   * barion, barter, csoportos beszedés, OTP Simple,
   * kompenzáció, kupon, PayPal,PayU, SZÉP kártya, utalvány
   */
  private List<String> paymentMethods;
  /**
   * Ft, HUF, EUR, USD stb.
   */
  private List<String> currencies;
  /**
   * Normál: A,
   * Keskeny: J,
   * Keskeny, logóval: L
   */
  private Map<String, String> pdfTemplates;

  private String paymentMethod;
  private String prefix;
  private String currency;
  private String pdfTemplate;

  // tétel megnevezés
  String entryName;
  // tétel mennyiség
  Double entryQuantity;
  // tétel mennyiségi egység
  String entryUnit;
  // tétel nettó egységár
  Double entryNetUnitPrice;
  // tétel nettó
  Double entryNet;
  // tétel áfakulcs
  String entryVatKey;
  // tétel áfa
  Double entryVat;
  // tétel bruttó
  Double entryGross;

  List<ReceiptEntry> entries;

  @PostConstruct
  public void init() {
    paymentMethods = new ArrayList<>(Arrays.asList("átutalás,készpénz,bankkártya,csekk,utánvét,ajándékutalvány,barion,barter,csoportos beszedés,OTP Simple,kompenzáció,kupon,PayPal,PayU,SZÉP kártya,utalvány".split(",")));
    currencies = new ArrayList<>(Arrays.asList("Ft,HUF,EUR,USD".split(",")));
    pdfTemplates = new LinkedHashMap<>();
    entries = new ArrayList<>();
    pdfTemplates.put("Normál", "A");
    pdfTemplates.put("Keskeny", "J");
    pdfTemplates.put("Keskeny, logóval", "L");
  }

  /**
   * Add new entry
   */
  public void addEntry() {
    System.err.println(pdfTemplate);
    entries.add(new ReceiptEntry(entryName, entryQuantity, entryUnit, entryNetUnitPrice, entryNet, entryVatKey, entryVat, entryGross));
    System.err.println(entries.size());
  }

  public List<String> getPaymentMethods() {
    return paymentMethods;
  }

  public void setPaymentMethods(List<String> paymentMethods) {
    this.paymentMethods = paymentMethods;
  }

  public List<String> getCurrencies() {
    return currencies;
  }

  public void setCurrencies(List<String> currencies) {
    this.currencies = currencies;
  }

  public String getPaymentMethod() {
    return paymentMethod;
  }

  public void setPaymentMethod(String paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public List<ReceiptEntry> getEntries() {
    return entries;
  }

  public void setEntries(List<ReceiptEntry> entries) {
    this.entries = entries;
  }

  public String getEntryName() {
    return entryName;
  }

  public void setEntryName(String entryName) {
    this.entryName = entryName;
  }

  public Double getEntryQuantity() {
    return entryQuantity;
  }

  public void setEntryQuantity(Double entryQuantity) {
    this.entryQuantity = entryQuantity;
  }

  public String getEntryUnit() {
    return entryUnit;
  }

  public void setEntryUnit(String entryUnit) {
    this.entryUnit = entryUnit;
  }

  public Double getEntryNetUnitPrice() {
    return entryNetUnitPrice;
  }

  public void setEntryNetUnitPrice(Double entryNetUnitPrice) {
    this.entryNetUnitPrice = entryNetUnitPrice;
  }

  public Double getEntryNet() {
    return entryNet;
  }

  public void setEntryNet(Double entryNet) {
    this.entryNet = entryNet;
  }

  public String getEntryVatKey() {
    return entryVatKey;
  }

  public void setEntryVatKey(String entryVatKey) {
    this.entryVatKey = entryVatKey;
  }

  public Double getEntryVat() {
    return entryVat;
  }

  public void setEntryVat(Double entryVat) {
    this.entryVat = entryVat;
  }

  public Double getEntryGross() {
    return entryGross;
  }

  public void setEntryGross(Double entryGross) {
    this.entryGross = entryGross;
  }

  public Map<String, String> getPdfTemplates() {
    return pdfTemplates;
  }

  public void setPdfTemplates(Map<String, String> pdfTemplates) {
    this.pdfTemplates = pdfTemplates;
  }

  public String getPdfTemplate() {
    return pdfTemplate;
  }

  public void setPdfTemplate(String pdfTemplate) {
    this.pdfTemplate = pdfTemplate;
  }
}
