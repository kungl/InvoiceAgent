package hu.csekme.invoiceagent.beans;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.io.Serializable;

@Named
@ApplicationScoped
public class SettingsBean implements Serializable {

  private String defaultReceiptPrefix;

  @PostConstruct
  public void init() {
    setDefaultReceiptPrefix("NYGT");
  }

  public String getDefaultReceiptPrefix() {
    return defaultReceiptPrefix;
  }

  public void setDefaultReceiptPrefix(String defaultReceiptPrefix) {
    this.defaultReceiptPrefix = defaultReceiptPrefix;
  }
}
