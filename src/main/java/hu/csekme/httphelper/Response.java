package hu.csekme.httphelper;
import java.io.ByteArrayOutputStream;

public class Response {

  int code;
  String reasonPhrase;
  ByteArrayOutputStream stream;

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getReasonPhrase() {
    return reasonPhrase;
  }

  public void setReasonPhrase(String reasonPhrase) {
    this.reasonPhrase = reasonPhrase;
  }

  public ByteArrayOutputStream getStream() {
    return stream;
  }

  public void setStream(ByteArrayOutputStream stream) {
    this.stream = stream;
  }
}