class PapayaCondition {

  private String[] baseExpr;

  // expr example:
  // 5 > 2 -> true
  // 5 == 2 -> false
  // 5 != 2 -> true
  // 5 < 2 -> false
  public PapayaCondition(String[] expr) {
    this.baseExpr = expr;
  }

  public static String[] parseCondition(String condition) {
    // remove brackets and whitespace
    condition = condition.replaceAll("[\\[\\]]", "");
    condition = condition.replaceAll("\\s+","");
    int fNumEnd = -1;
    int opLen = -1;
    if (condition.contains(">")) {
      fNumEnd = condition.indexOf(">");
      opLen = 1;
    }
    if (condition.contains("<")) {
      fNumEnd = condition.indexOf("<");
      opLen = 1;
    }
    if (condition.contains("==")) {
      fNumEnd = condition.indexOf("==");
      opLen = 2;
    }
    if (condition.contains("!=")) {
      fNumEnd = condition.indexOf("!=");
      opLen = 2;
    }
    if (opLen == -1) {
      System.err.printf("could not logicaly evaluate condition, no operators in %s\n", condition);
      System.exit(1);
    }
    String a = condition.substring(0, fNumEnd);
    String b = condition.substring(fNumEnd+opLen, condition.length());
    String cond = condition.substring(fNumEnd, fNumEnd+opLen);
    return new String[]{a, cond, b};
  }

  // assumed baseExpr is evaluated to be simple
  public boolean evaluate() {
    int a = Integer.parseInt(baseExpr[0]);
    int b = Integer.parseInt(baseExpr[2]);
    String op = baseExpr[1];
    switch (op) {
      case ">":
        return a > b;
      case "<":
        return a < b;
      case "==":
        return a == b;
      case "!=":
        return a != b;
    }
    // we shouldnt be here, it wasnt one of the four ops
    System.err.printf("operation %s unknown, exiting..\n", op);
    System.exit(1);
    return false;
  }
}
