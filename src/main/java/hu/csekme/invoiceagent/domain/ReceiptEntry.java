package hu.csekme.invoiceagent.domain;
import java.util.UUID;
/**
 * ReceiptEntry is a receipt item
 * @author Krisztián Csekme
 * @see Receipt
 */
public class ReceiptEntry {
  //ID
  String uuid;
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

  public ReceiptEntry() {
    UUID.randomUUID().toString();
  }

  public ReceiptEntry(String entryName, Double entryQuantity, String entryUnit, Double entryNetUnitPrice, Double entryNet, String entryVatKey, Double entryVat, Double entryGross) {
    this.uuid = UUID.randomUUID().toString();
    this.entryName = entryName;
    this.entryQuantity = entryQuantity;
    this.entryUnit = entryUnit;
    this.entryNetUnitPrice = entryNetUnitPrice;
    this.entryNet = entryNet;
    this.entryVatKey = entryVatKey;
    this.entryVat = entryVat;
    this.entryGross = entryGross;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
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

  @Override
  public String toString() {
    return "ReceiptEntry{" +
            "uuid='" + uuid + '\'' +
            ", entryName='" + entryName + '\'' +
            ", entryQuantity=" + entryQuantity +
            ", entryUnit='" + entryUnit + '\'' +
            ", entryNetUnitPrice=" + entryNetUnitPrice +
            ", entryNet=" + entryNet +
            ", entryVatKey='" + entryVatKey + '\'' +
            ", entryVat=" + entryVat +
            ", entryGross=" + entryGross +
            '}';
  }
}
