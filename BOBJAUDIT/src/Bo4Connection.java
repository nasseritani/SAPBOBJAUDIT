import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class Bo4Connection {
  
  private String biprws;
  private String token;
  
  public Bo4Connection(String biprws) {
    this.biprws = biprws;
  }
  
  public String query(String method, String link, String format) throws Exception {
    return query(method, biprws + "/raylight/v1" + link, format, token, null);
  }
  
  public void connect(String username, String password, String auth) throws Exception {
    String link = biprws + "/logon/long/";    
    String method = "POST";
    String format = "application/json";
    String body = "<attrs xmlns=\"http://www.sap.com/rws/bip\">"
      + "<attr name=\"userName\" type=\"string\">" + username + "</attr>"
      + "<attr name=\"password\" type=\"string\">" + password + "</attr>"
      + "<attr name=\"auth\" type=\"string\">" + auth + "</attr>"
      + "</attrs>";
    JSONObject json = new JSONObject(query(method, link, format, null, body));
    token = json.getString("logonToken");
  }
  
  public void disconnect() throws Exception {
    String link = biprws + "/logoff/";    
    String method = "POST";
    String format = "application/json";
    query(method, link, format, null, null);
  }

  public static String query(String method, String link, String format, 
      String token, String content) throws Exception {
    HttpURLConnection conn = null;
    try {
      URL url = new URL(link);    
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod(method);
      conn.setRequestProperty("Accept", format);
      if (token != null) {
        String logonToken = "\"" + token + "\"";
        conn.setRequestProperty("X-SAP-LogonToken", logonToken);
      }
      conn.setDoOutput(true);
      conn.setDoInput(true);
      if (content != null) {
        conn.setRequestProperty("Content-Type", 
            "application/xml; charset=utf-8");
        conn.setRequestProperty("Content-Length", 
            Integer.toString(content.length()));
        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
        out.write(content, 0, content.length());
        out.flush();
      }
      conn.connect();
      if (conn.getResponseCode() != 200) {
        throw new Exception("HTTP Error Code: " + conn.getResponseCode() 
          + " " + conn.getResponseMessage());
      }
      BufferedReader br = new BufferedReader(
        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
      StringBuilder result = new StringBuilder(); 
      String output;
      while ((output = br.readLine()) != null) {
        result.append(output);
        result.append('\n');
      }
      br.close();
      return result.toString();
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
  }
}