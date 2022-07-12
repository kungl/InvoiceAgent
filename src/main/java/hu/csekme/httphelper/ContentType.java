package hu.csekme.httphelper;

public enum ContentType {

  MULTIPART_FORM_DATA("multipart/form-data"),
  APPLICATION_XML("application/xml"),
  SUBMIT("submit");
  String value;

  private ContentType(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }
}
