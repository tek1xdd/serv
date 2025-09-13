package pro.gamely.license.util;
public final class MacUtils {
  private MacUtils(){}
  public static String normalize(String s){
    if(s==null) return null;
    return s.replace("-", "").replace(":", "").trim().toUpperCase();
  }
  public static boolean isInvalidMacHeader(String s){
    if (s == null) return true;
    String t = s.trim();
    if (t.isEmpty()) return true;
    String up = t.toUpperCase();
    return up.equals("NONE") || up.equals("NULL") || up.equals("N/A")
        || up.equals("NA") || up.equals("UNDEFINED") || up.equals("UNKNOWN");
  }
}
