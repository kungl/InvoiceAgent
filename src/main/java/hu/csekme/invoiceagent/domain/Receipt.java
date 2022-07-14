package hu.csekme.invoiceagent.domain;
import java.util.ArrayList;
import java.util.List;

public class Receipt {

  private String paymentMethod;
  private String prefix;
  private String currency;
  private String pdfTemplate;

  List<ReceiptEntry> receiptEntryList;

  public Receipt() {
    receiptEntryList = new ArrayList<>();
  }

  public void addReceiptEntry(ReceiptEntry entry) {
    receiptEntryList.add(entry);
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

  public String getPdfTemplate() {
    return pdfTemplate;
  }

  public void setPdfTemplate(String pdfTemplate) {
    this.pdfTemplate = pdfTemplate;
  }

  public List<ReceiptEntry> getReceiptEntryList() {
    return receiptEntryList;
  }

  public void setReceiptEntryList(List<ReceiptEntry> receiptEntryList) {
    this.receiptEntryList = receiptEntryList;
  }

  @Override
  public String toString() {
    return "Receipt{" +
            "paymentMethod='" + paymentMethod + '\'' +
            ", prefix='" + prefix + '\'' +
            ", currency='" + currency + '\'' +
            ", pdfTemplate='" + pdfTemplate + '\'' +
            ", receiptEntryList=" + receiptEntryList +
            '}';
  }
}
