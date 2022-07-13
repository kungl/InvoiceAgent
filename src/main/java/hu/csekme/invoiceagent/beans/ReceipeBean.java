package hu.csekme.invoiceagent.beans;

import hu.csekme.invoiceagent.domain.ReceiptEntry;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

  private String paymentMethod;
  private String prefix;
  private String currency;

  // Entriy
  String entriyName;
  //Item quantity
  Double entryQuantity;
  //Unit of quantity
  String entryUnit;
  //Net Unit Price
  String entryNetUnitPrice;

  List<ReceiptEntry> entries;

  @PostConstruct
  public void init() {
    paymentMethods = new ArrayList<String>(Arrays.asList("átutalás,készpénz,bankkártya,csekk,utánvét,ajándékutalvány,barion,barter,csoportos beszedés,OTP Simple,kompenzáció,kupon,PayPal,PayU,SZÉP kártya,utalvány".split(",")));
    currencies = new ArrayList<String>(Arrays.asList("Ft,HUF,EUR,USD".split(",")));
    entries = new ArrayList<>();
  }

  public void addEntry() {
    System.err.println(entriyName);
    entries.add(new ReceiptEntry(entriyName, entryQuantity, entryUnit, entryNetUnitPrice));
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

  public String getEntriyName() {
    return entriyName;
  }

  public void setEntriyName(String entriyName) {
    this.entriyName = entriyName;
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

  public String getEntryNetUnitPrice() {
    return entryNetUnitPrice;
  }

  public void setEntryNetUnitPrice(String entryNetUnitPrice) {
    this.entryNetUnitPrice = entryNetUnitPrice;
  }
}
