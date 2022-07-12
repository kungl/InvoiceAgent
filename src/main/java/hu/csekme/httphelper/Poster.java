package hu.csekme.httphelper;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.logging.Logger;

public class Poster {
  private static final Logger logger = Logger.getLogger(Poster.class.getName());
  private static final String LF = "\r\n";
  private final String boundary;
  private final String url;
  private final List<FormField> fields;
  private final List<FileField> files;
  private final Map<String, String> headers;

  private HttpURLConnection connection;
  private OutputStream outputStream;
  private PrintWriter writer;


  private Poster(Builder builder) {
    this.boundary = builder.boundary;
    this.url = builder.url;
    this.fields = builder.fields;
    this.headers = builder.headers;
    this.files = builder.files;
  }

  private void SSL() {
    try {
      SSLContext ssl_ctx = SSLContext.getInstance("TLS");
      TrustManager[] trust_mgr = new TrustManager[]{
              new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                  return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String t) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String t) {
                }
              }
      };
      ssl_ctx.init(null, // key manager
              trust_mgr,           // trust manager
              new SecureRandom()); // random number generator
      HttpsURLConnection.setDefaultSSLSocketFactory(ssl_ctx.getSocketFactory());
    } catch (Exception err) {
      throw new RuntimeException(err);
    }
  }

  public Response connect(RequestMethod method) {
    logger.info("connect");
    Response response = new Response();
    try {
      URL url = new URL(this.url);
      SSL();
      CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
      this.connection = (HttpURLConnection) url.openConnection();
      List<String> cookies = connection.getHeaderFields().get("Set-Cookie");
      connection = (HttpURLConnection) new URL(this.url).openConnection();
      for (String cookie : cookies) {
        connection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);
      }
      connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"); // Do as if you're using Chrome 41 on Windows 7.
      this.connection.setUseCaches(true);
      this.connection.setDoInput(true);
      if (method == RequestMethod.POST) {
        this.connection.setDoOutput(true);
      }
      connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + this.boundary);
      Iterator<String> it = headers.keySet().iterator();
      while (it.hasNext()) {
        String key = it.next();
        String value = headers.get(key);
        this.connection.setRequestProperty(key, value);
      }
      this.outputStream = this.connection.getOutputStream();
      writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);

      for (FormField field : fields) {
        addFormField(field);
      }

      for (FileField field : files) {
        addFilePart(field);
      }

      writer.flush();
      writer.append("--");
      writer.append(boundary);
      writer.append("--");
      writer.append(LF);
      writer.close();

      response.code = this.connection.getResponseCode();
      response.stream = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int length;
      while ((length = this.connection.getInputStream().read(buffer)) != -1) {
        response.stream.write(buffer, 0, length);
      }
      connection.disconnect();

    } catch (Exception ex) {
      response.code = 500;
      response.reasonPhrase = ex.getMessage();
      ex.printStackTrace();
    }
    return response;
  }


  private void addFormField(FormField field) {
    writer.append("--");
    writer.append(boundary);
    writer.append(LF);
    writer.append("Content-Disposition: form-data; name=\"" + field.getName() + "\"");
    writer.append(LF);
    writer.append(String.format("Content-Type: %s; charset=%s", field.getContentType(), StandardCharsets.UTF_8));
    writer.append(LF);
    writer.append(field.getValue().toString());
    writer.append(LF);
    writer.flush();
  }

  private void addFilePart(FileField fileField) throws Exception {
    String fileName = fileField.getFile().getName();
    writer.append("--");
    writer.append(boundary);
    writer.append(LF);
    writer.append("Content-Disposition: form-data; name=\"" + fileField.getFieldName() + "\"; filename=\"" + fileName + "\"");
    writer.append(LF);
    writer.append("Content-Type: file");// + URLConnection.guessContentTypeFromName(fileName));
    writer.append(LF);
    writer.append("Content-Transfer-Encoding: binary");
    writer.append(LF);
    writer.append(LF);
    writer.flush();

    FileInputStream inputStream = new FileInputStream(fileField.getFile());
    byte[] buffer = new byte[4096];
    int bytesRead = -1;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      outputStream.write(buffer, 0, bytesRead);
    }
    outputStream.flush();
    inputStream.close();
    writer.append(LF);
    writer.flush();
  }


  public static class Builder {
    private List<FormField> fields;
    private List<FileField> files;
    private final Map<String, String> headers;
    private final String boundary;
    private String url;

    private Builder() {
      boundary = UUID.randomUUID().toString();
      headers = new HashMap<>();
      fields = new ArrayList<>();
      files = new ArrayList<>();
    }

    public static Builder create(String url) {
      Builder builder = new Builder();
      builder.url = url;
      return builder;
    }

    public Builder addHeaderParam(String key, String value) {
      headers.put(key, value);
      return this;
    }

    public Builder addFormField(String name, ContentType type, Object value) {
      fields.add(new FormField(name, type, value));
      return this;
    }

    public Builder addFileField(String fieldName, File file) {
      files.add(new FileField(fieldName, file));
      return this;
    }

    public Poster build() {
      return new Poster(this);
    }

  }

}

class FormField {

  private String name;
  private ContentType contentType;
  private Object value;

  public FormField(String name, ContentType contentType, Object value) {
    this.name = name;
    this.contentType = contentType;
    this.value = value;
  }

  public String getName() {
    return name;
  }

  public ContentType getContentType() {
    return contentType;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

}

class FileField {
  String fieldName;
  File file;

  public FileField(String fieldName, File file) {
    this.fieldName = fieldName;
    this.file = file;
  }

  public String getFieldName() {
    return fieldName;
  }

  public File getFile() {
    return file;
  }
}



