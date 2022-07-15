package hu.csekme.invoiceagent.domain;

public class ReceiptSend {

  String receiptNumber;
  String email;
  String replyTo;
  String subject;
  String message;

  public ReceiptSend(){}

  public ReceiptSend(String receiptNumber, String email, String replyTo, String subject, String message) {
    this.receiptNumber = receiptNumber;
    this.email = email;
    this.replyTo = replyTo;
    this.subject = subject;
    this.message = message;
  }

  public String getReceiptNumber() {
    return receiptNumber;
  }

  public void setReceiptNumber(String receiptNumber) {
    this.receiptNumber = receiptNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getReplyTo() {
    return replyTo;
  }

  public void setReplyTo(String replyTo) {
    this.replyTo = replyTo;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public String toString() {
    return "ReceiptSend{" +
            "receiptNumber='" + receiptNumber + '\'' +
            ", email='" + email + '\'' +
            ", replyTo='" + replyTo + '\'' +
            ", subject='" + subject + '\'' +
            ", message='" + message + '\'' +
            '}';
  }
}
