package hu.csekme.invoiceagent.domain;

import java.rmi.server.UID;

public class ReceiptEntry {
  //ID
  String uuid;
  //Name
  String name;
  //Item quantity
  Double quantity;
  //Unit of quantity
  String unit;
  //Net Unit Price
  String netUnitPrice;

  public ReceiptEntry() {
  }

  public ReceiptEntry(String name, Double quantity, String unit, String netUnitPrice) {
    this.name = name;
    this.quantity = quantity;
    this.unit = unit;
    this.netUnitPrice = netUnitPrice;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getQuantity() {
    return quantity;
  }

  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getNetUnitPrice() {
    return netUnitPrice;
  }

  public void setNetUnitPrice(String netUnitPrice) {
    this.netUnitPrice = netUnitPrice;
  }
}
