class PapayaInteger {

  private String name;
  private int value;

  public PapayaInteger() {
    this("", 0);
  }

  public PapayaInteger(String name, int value) {
    this.name = name;
    this.value = value;
  }

  public String getName() {
    return this.name;
  }

  public int getValue() {
    return this.value;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setValue(int value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "(" +  name + " , "  + value + ")";
  }
}
