package hu.csekme.invoiceagent.enums;

public enum Action {

  END_POINT_CREATE_RECEIPT("action-szamla_agent_nyugta_create"),
  END_POINT_GET_RECEIPT("action-szamla_agent_nyugta_get"),
  END_POINT_STORNO_RECEIPT("action-szamla_agent_nyugta_storno"),
  END_POINT_SEND_RECEIPT("action-szamla_agent_nyugta_send");
  private String value;

  private Action(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
