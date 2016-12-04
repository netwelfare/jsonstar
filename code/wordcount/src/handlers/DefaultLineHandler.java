package handlers;
public class DefaultLineHandler implements LineHandler {

  @Override
  public String handleKey(String s) {
    String[] temp = s.split("\t");
    return temp[0];
  }

  @Override
  public String handleValue(String s) {
    String[] temp = s.split("\t");
    return s;
  }
}
