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
  private static final String COOKIES_HEADER = "JSESSIONID";
  private static final Logger logger = Logger.getLogger(Poster.class.getName());
  private static final String LF = "\r\n";
  private final String boundary;
  private final String url;
  private final List<FormField> fields;
  private final List<FileField> files;
  private final Map<String, String> headers;
  private HttpURLConnection connection;
  private OutputStream outputStream;
 // private PrintWriter writer;
  private static CookieManager cookieManager = new CookieManager();

  static {
    CookieHandler.setDefault(cookieManager);
  }

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
    System.setProperty("javax.net.debug","all");
    logger.info("connect");
    Response response = new Response();
    try {
      URL url = new URL(this.url);
      SSL();
      this.connection = (HttpURLConnection) url.openConnection();
      this.connection.setRequestProperty("Connection", "Keep-Alive");
      if (method == RequestMethod.POST) {
        this.connection.setDoOutput(true);
        this.connection.setRequestMethod("POST");
      }
      connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + this.boundary);
      connection.setRequestProperty("Cookie", "JSESSIONID=50CD404B3D03704EEBF2FE1C72932324.sas2");
      Iterator<String> it = headers.keySet().iterator();
      while (it.hasNext()) {
        String key = it.next();
        String value = headers.get(key);
        this.connection.setRequestProperty(key, value);
      }
      this.outputStream = new BufferedOutputStream(connection.getOutputStream());
      //writer = new PrintWriter(new OutputStreamWriter(this.outputStream, StandardCharsets.UTF_8), true);

      for (FormField field : fields) {
        addFormField(field);
      }

      for (FileField field : files) {
        addFilePart(field);
      }

      this.outputStream.write(boundary.getBytes(StandardCharsets.UTF_8));
      this.outputStream.write(LF.getBytes(StandardCharsets.UTF_8));



      File file = new File("cookie.file");
      System.err.println(file.getAbsolutePath());
      //Cookie
      List<String> cookies = connection.getHeaderFields().get(COOKIES_HEADER);
      cookies = connection.getHeaderFields().get(COOKIES_HEADER);
      if (cookies != null) {
        for (String cookie : cookies) {
          cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
        }
        StringBuilder stringCookies = new StringBuilder();
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
          for (HttpCookie cookie : cookieManager.getCookieStore().getCookies()) {
            stringCookies.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
          }

          ObjectOutput out = new ObjectOutputStream(new FileOutputStream(file));
          out.writeObject(cookieManager.getCookieStore().getCookies());
        }
        // System.err.println("Cookie:" + stringCookies.toString());
        //  connection.setRequestProperty("Cookie", stringCookies.toString());
      }
      //Cookie

      this.outputStream.flush();

      response.code = this.connection.getResponseCode();
      response.stream = new ByteArrayOutputStream();

      connection.getHeaderFields().forEach((key,value)->{
        System.err.println(key + "=" + value);
      });


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


  private void addFormField(FormField field) throws IOException {
    this.outputStream.write("--".getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(boundary.getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(LF.getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(("Content-Disposition: form-data; name=\"" + field.getName() + "\"").getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(LF.getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(String.format("Content-Type: %s; charset=%s", field.getContentType(), StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(LF.getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(LF.getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(field.getValue().toString().getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(LF.getBytes(StandardCharsets.UTF_8));
    this.outputStream.write("--".getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(boundary.getBytes(StandardCharsets.UTF_8));
    this.outputStream.write("--".getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(LF.getBytes(StandardCharsets.UTF_8));
  }

  private void addFilePart(FileField fileField) throws Exception {
    String fileName = fileField.getFile().getName();
    this.outputStream.write("--".getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(boundary.getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(LF.getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(("Content-Disposition: form-data; name=\"" + fileField.getFieldName() + "\"; filename=\"" + fileName + "\"").getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(LF.getBytes(StandardCharsets.UTF_8));
    this.outputStream.write("Content-Type: text/xml".getBytes(StandardCharsets.UTF_8));// + URLConnection.guessContentTypeFromName(fileName));
    this.outputStream.write(LF.getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(LF.getBytes(StandardCharsets.UTF_8));

    FileInputStream inputStream = new FileInputStream(fileField.getFile());

    byte[] buffer = new byte[4096];
    int bytesRead = -1;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      outputStream.write(buffer, 0, bytesRead);
    }
    outputStream.flush();
    inputStream.close();
    this.outputStream.write(LF.getBytes(StandardCharsets.UTF_8));
    this.outputStream.write("--".getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(boundary.getBytes(StandardCharsets.UTF_8));
    this.outputStream.write("--".getBytes(StandardCharsets.UTF_8));
    this.outputStream.write(LF.getBytes(StandardCharsets.UTF_8));
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



