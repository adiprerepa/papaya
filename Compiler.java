/*
  Aditya Prerepa
  Jan Period 1
  Second Semester Coding Project
  Compiler
  March 2021

  Language Name: Papaya
  this was SO FUN
 */

import java.io.*;
import java.util.*;

class Compiler {

  private static ArrayList<PapayaInteger> ints = new ArrayList<>();
  private static ArrayList<PapayaString> strings = new ArrayList<>();

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("include a file to run");
      System.exit(1);
    }
    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    boolean eof = false;
    boolean ignoreLines = false;
    // only supports 1 level of loops, no nested loops
    boolean inLoop = false;
    int loopLineCounter = 0;
    ArrayList<String> loopLines = new ArrayList<>();
    String loopConditionRaw = "";
    BufferedReader stdio = new BufferedReader(new InputStreamReader(System.in));
    while (!eof) {
      // the lines we execute are either in a loop or not.
      // if we are in a loop, we set lines to the current line
      // in that iteration of the loop.
      // If not, we just read from the file like we normally would.
      String[] line;
      if (inLoop) {
        // if we reached the end of a loop we are supposed to be in,
        // check if the condition is finally satisfied.
        if (loopLineCounter == loopLines.size()) {
          String[] arrRawCond = PapayaCondition.parseCondition(loopConditionRaw);
          ArrayList<String> l = arrToList(arrRawCond);
          replaceExprWithVals(l);
          String[] replacedCond = new String[l.size()];
          replacedCond = l.toArray(replacedCond);
          PapayaCondition loopCond = new PapayaCondition(replacedCond);
          // if the condition is satisfied, stop looping and keep reading
          // the file
          if (!loopCond.evaluate()) {
            inLoop = false;
            continue;
          // otherwise, reset the loop counter and continue
          } else {
            loopLineCounter = 0;
          }
        }
        line = cleanupLine(loopLines.get(loopLineCounter));
        loopLineCounter++;
      } else {
        // get new lines from file
        line = getLine(br);
      }
      // this shouldnt happen, exit
      if (line.length == 0) {
        eof = true;
        continue;
      }
      // if we reached the end of the if and we were ignoring lines, stop ignoring.
      if (ignoreLines && line[0].equals("endif")) {
        ignoreLines=false;
        continue;
      }
      if (ignoreLines) {
        continue;
      }
      // empty line or comments, skip
      if ((line.length == 1 && line[0].isEmpty()) || line[0].charAt(0) == '~') continue;
      // definitions
      if (line[0].equals("def")) {
        if (!processDefinition(line[1], sub(line, 3, line.length - 1), stdio)) {
          System.exit(1);
        }
      }
      // if statements
      if (line[0].equals("if")) {
        // get into form for the papaya condition
        String[] c = PapayaCondition.parseCondition(merge(line, 1, line.length - 1));
        // do some arrayList magic to fit the parameters for replaceExprWithVals
        ArrayList<String> l = arrToList(c);
        replaceExprWithVals(l);
        String[] replaced = new String[l.size()];
        replaced = l.toArray(replaced);
        PapayaCondition ifCond = new PapayaCondition(replaced);
        // if the condition is not true, ignore all lines until the endif
        if (!ifCond.evaluate()) {
          ignoreLines = true;
        }
      }
      // while loops
      if (line[0].equals("while")) {
        loopConditionRaw = merge(line, 1, line.length - 1);
        boolean end = false;
        while (!end) {
          String loopLine = br.readLine();
          if (loopLine.startsWith("endwhile")) {
            end = true;
          } else {
            loopLines.add(loopLine);
          }
        }
        inLoop = true;
      }
      // printing
      if (line[0].equals("print")) {
        if (line.length != 2) {
          System.err.println("use print like: print $a");
          System.exit(1);
        }
        String key = line[1].replace("$", "");
        PapayaInteger i = findPapayaInt(key);
        if (i == null) {
          PapayaString ps = findPapayaString(key);
          if (ps == null) {
            System.err.printf("could not find variable %s to print\n", key);
            System.exit(1);
          } else {
            System.out.println(ps.getValue());
          }
        } else {
          System.out.println(i.getValue());
        }
      }

      // printing a string (quick)
      if (line[0].equals("printqstr")) {
        String[] qstr = sub(line, 1, line.length-1);
        String base =  "";
        for (String qstrq: qstr) {
          base += qstrq;
          base += " ";
        }
        System.out.println(base.replace("\"", ""));
      }
    }
  }

  // subarray utility function
  private static String[] sub(String[] f, int a, int b) {
    String[] g = new String[b-a+1];
    for (int i = a; i <= b; i++) {
      g[i-a] = f[i];
    }
    return g;
  }

  // merge array into string
  private static String merge(String[] f, int from, int to) {
    String g = "";
    for (int i = from; i <= to; i++) {
      g += f[i];
    }
    return g;
  }


  // 55+10/2 -> ["55", "+", "10", "/", "2"]
  private static ArrayList<String> exprToArrayList(String s) {
    // remove all whitespace
    s = s.replaceAll("\\s+","");
    ArrayList<String> f = new ArrayList<>();
    // the index in the arraylist of the number we need to append to
    int c = 0;
    for (int i = 0; i < s.length(); i++) {
      if (s.charAt(i) == '+' || s.charAt(i) == '-' || s.charAt(i) == '*' || s.charAt(i) == '/' || s.charAt(i) == '%') {
        // move onto the next token
        f.add(String.valueOf(s.charAt(i)));
        c += 2;
      } else {
        // is this a new element?
        if (f.size() <= c) {
          f.add(String.valueOf(s.charAt(i)));
        } else {
          // if not, just append
          f.set(c, f.get(c) + String.valueOf(s.charAt(i)));
        }
      }
    }
    return f;
  }

  // get a line from stdin and split it into an array
  private static String[] getLine(BufferedReader br) {
    try {
      String st = br.readLine();
      return cleanupLine(st);
    } catch (IOException e) {
      // This is probably an EOF
      return new String[]{};
    }
  }

  private static String[] cleanupLine(String st) {
    if (st == null) {
      return new String[]{};
    }
    String[] f = st.split(" ");
    for (int i = 0; i < f.length; i++) {
      f[i] = f[i].trim();
    }
    return st.split(" ");
  }

  private static void addIntIfNew(String key, int value) {
    for (int i = 0; i < ints.size(); i++) {
      if (ints.get(i).getName().equals(key)) {
        ints.set(i, new PapayaInteger(key, value));
        return;
      }
    }
    ints.add(new PapayaInteger(key, value));
  }

  private static boolean processDefinition(String key, String[] value, BufferedReader br) {
    // if there is just one token after the define:
    // def a = 10
    // or
    // def b = "foo"
    // just add them without evaluation. This is the place to check for stdin calls.
    if (value.length == 1) {
      // its a string
      if (value[0].contains("\"")) {
        strings.add(new PapayaString(key, value[0].replaceAll("\"", "")));
        return true;
      }
      try {
        // get stuff from java stdin and feed to variables
        if (value[0].equals("readint()")) {
          addIntIfNew(key, Integer.parseInt(br.readLine()));
          return true;
        }
        if (value[0].equals("readstring()")) {
          strings.add(new PapayaString(key, br.readLine()));
          return true;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        addIntIfNew(key, Integer.parseInt(value[0]));
        return true;
      } catch (NumberFormatException e) {
        System.err.println("integers need to be integers :)");
        return false;
      }
    }
    // this is where it gets complex
    // for multicharacter evaluations, we replace all the variables with their values
    // and then use a variant of the shunting yard algorithm to evaluate.
    ArrayList<String> lExpr = exprToArrayList(String.join("", value));
    replaceExprWithVals(lExpr);
    addIntIfNew(key, evaluateExpr(lExpr));
    return true;
  }

  // utility function
  private static ArrayList<String> arrToList(String[] f) {
    ArrayList<String> g = new ArrayList<>();
    for (String ff: f)
      g.add(ff);
    return g;
  }

  // replace a list with variables to their actual values.
  // ["$a", "+", "$b"] -> ["5", "+", "10"]
  static void replaceExprWithVals(ArrayList<String> expr) {
    for (int i = 0; i < expr.size(); i++) {
      if (expr.get(i).charAt(0) == '$') {
        String key = expr.get(i).substring(1);
        PapayaInteger pint = findPapayaInt(key);
        String val = "";
        if (pint == null) {
          PapayaString pstring = findPapayaString(key);
          if (pstring==null) {
            // throw error here - variable does not exist
            System.err.printf("variable %s does not exist\n", key);
            System.exit(1);
          } else {
            val = pstring.getValue();
          }
        } else {
          val = String.valueOf(pint.getValue());
        }
        expr.set(i, val);
      }
    }
  }

  // find variables from the static lists
  static PapayaInteger findPapayaInt(String key) {
    for (int i = 0; i < ints.size(); i++) {
      if (ints.get(i).getName().equals(key))
        return ints.get(i);
    }
    return null;
  }

  static PapayaString findPapayaString(String key) {
    for (int i = 0; i < strings.size(); i++) {
      if (strings.get(i).getName().equals(key))
        return strings.get(i);
    }
    return null;
  }

  // very dumbed down version of order of operations
  static String[] firstPrecedence = new String[]{"*", "/", "%"};
  static String[] secondPrecedence = new String[]{"+", "-"};

  // recursively evaluates a mathematical expression
  // "5+5*2" -> 15
  // no parenthesis yet
  private static int evaluateExpr(ArrayList<String> expr) {
    // base case
    if (expr.size() == 1) {
      return Integer.parseInt(expr.get(0));
    }
    String fp = "";
    // first pref overwrites second
    for (String f: secondPrecedence) {
      if (expr.contains(f)) {
        fp = f;
      }
    }
    for (String f: firstPrecedence) {
      if (expr.contains(f)) {
        fp = f;
      }
    }

    int i = expr.indexOf(fp);
    if (i == -1) {
      System.err.printf("fatal error: could not find %s in %s, exiting\n", fp, expr);
      System.exit(1);
    }
    // simple evaluate the sub problem
    int r = evalSimple(expr.subList(i-1, i+2));
    // replace the entire expression with this value
    expr.set(i-1, String.valueOf(r));
    expr.remove(i+1);
    expr.remove(i);
    // recursively do it again
    return evaluateExpr(expr);
  }

  // evaluates a simple expression
  // "5+5"-> 10
  private static int evalSimple(List<String> simpleExpr) {
    int a = Integer.parseInt(simpleExpr.get(0));
    int b = Integer.parseInt(simpleExpr.get(2));
    switch (simpleExpr.get(1)) {
      case "+":
        return a+b;
      case "-":
        return a-b;
      case "*":
        return a*b;
      case "/":
        return a/b;
      case "%":
        return a%b;
    }
    System.err.println("shouldnt be at the end of evalsimple");
    System.err.println("Tried evaling: "  + simpleExpr);
    return 0;
  }
}
