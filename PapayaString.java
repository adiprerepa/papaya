class PapayaString {

  private String name;
  private String value;

  public PapayaString() {
    this("", "");
  }

  public PapayaString(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return this.name;
  }

  public String getValue() {
    return this.value;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "(" +  name + " , "  + value + ")";
  }
}
