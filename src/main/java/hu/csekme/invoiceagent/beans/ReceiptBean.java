package hu.csekme.invoiceagent.beans;
import hu.csekme.invoiceagent.domain.ReceiptEntry;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 * Receipt editor bean
 * @author Krisztián Csekme
 * @see ReceiptEntry
 */
@Named
@ViewScoped
public class ReceiptBean implements Serializable {
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
  // items
  List<ReceiptEntry> entries;

  @PostConstruct
  public void init() {
    entries = new ArrayList<>();
  }
}
