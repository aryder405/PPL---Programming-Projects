import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * NOTES:
 * For Read(id) -- the user is prompted for input
 * BufferedInputStream bs = new BufferedInputStream(System.in)
 * Integer.parseInt(bs.readLine())
 * For write(id) -- output the value of the variable
 * RandomAccessFile used for loops
 */

/**
 * @author Adam Ryder
 * @date 6/10/2013 This class parses a user specified string and determines if
 *       the string is accepted by the laws given to me for this assignment.
 */
public class Main {
	private StringTokenizer tok;
	private StringTokenizer tok2;
	private Scanner input;
	String t1, t2, t3;
	private String idPat = "[a-zA-Z]+";
	private String numberPat = "[0-9]+";
	private String operationPat = "(\\+|-|\\*|\\/)";
	private String comparisonPat = "(==|!=|<|>|<=|>=)";
	private String endPat = "(fi|elihw|rof)";// key ending terms
	private String startPat = "(for|if|while|write|read)";// key starting terms
	private boolean endIf, endWhile, endFor = false; // flags for ending the
	// loop.
	private HashMap<String, Integer> map = new HashMap<String, Integer>();

	/*
	 * Constructor...not used
	 */
	public Main() {

	}

	/*
	 * The starting method. Asks user to input the language to be parsed. Uses
	 * scanner.nextLine() to populate a string. Then a tokenizer splits up the
	 * string into tokens. Once the tokenizer is populated, it calls the
	 * program() method with the first token as the parameter. NOTE: the input
	 * must be one line
	 */
	public void start(String filename) throws FileNotFoundException {
		input = new Scanner(new FileReader(filename));
		String str = " ";
		// System.out.println("Enter language to be parsed...");

		while (input.hasNext())
			str += input.next() + " ";
		if (str.equals("")) {
			System.out.println("No input received");
			System.exit(1);
		}

		System.out.println("Parsing string: " + str);
		syntaxCheck(str);
		tok = new StringTokenizer(str);
		if (program(tok.nextToken())) {
			System.out.println("Successful Parse...");
			// tok = new StringTokenizer(str);
			System.out.println("Interpretting: " + str);
			interpret(str);
		} else
			System.out.println("ERROR: UNSUCCESSFUL PARSE");

	}

	/*
	 * Checks the syntax of the input to make sure all starting key terms having
	 * matching ending key terms. Such as...(if fi, for rof, while elihw)
	 */
	public void syntaxCheck(String str) {

		tok = new StringTokenizer(str);
		int count = 0;
		int count2 = 0;
		while (tok.hasMoreTokens()) {
			if (tok.nextToken().equalsIgnoreCase("if"))
				count++;
		}
		tok = new StringTokenizer(str);
		while (tok.hasMoreTokens()) {
			if (tok.nextToken().equalsIgnoreCase("fi"))
				count2++;
		}
		if (count != count2) {
			System.out.println("invalid if statement format");
			System.exit(1);
		}
		tok = new StringTokenizer(str);
		while (tok.hasMoreTokens()) {
			if (tok.nextToken().equalsIgnoreCase("for"))
				count++;
		}
		tok = new StringTokenizer(str);
		while (tok.hasMoreTokens()) {
			if (tok.nextToken().equalsIgnoreCase("rof"))
				count2++;
		}
		if (count != count2) {
			System.out.println("invalid for loop format");
			System.exit(1);
		}
		tok = new StringTokenizer(str);
		while (tok.hasMoreTokens()) {
			if (tok.nextToken().equalsIgnoreCase("while"))
				count++;
		}
		tok = new StringTokenizer(str);
		while (tok.hasMoreTokens()) {
			if (tok.nextToken().equalsIgnoreCase("elihw"))
				count2++;
		}
		if (count != count2) {
			System.out.println("invalid while loop format");
			System.exit(1);
		}

	}

	/*
	 * This method starts the parsing process. If this method returns true then
	 * the parsed input is an accepted language. This method calls the
	 * statementList() method with the first token as the input. If
	 * statementList gets a false, an error is displayed and program shuts down.
	 */
	public boolean program(String t) {
		// System.out.println("Program -->" + t);
		boolean b = false;
		if (statementList(t))
			b = true;
		return b;
	}

	/*
	 * StatementList --> statement statementList | empty If the parameter passed
	 * to this method is a key pattern (fi, rof, elihw) it will return true. If
	 * the parameter is empty or there are no tokens left in the tokenizer, it
	 * will return true. Else, if the parameter begins a true statement and the
	 * following input returns a true statementList, then it will return true.
	 */
	public boolean statementList(String t) {
		//System.out.println("StatementList--> " + t);
		boolean b = false;
		String s = "";
		/*
		 * This switch statements sets the end of loop flags. I had to make
		 * these global because the ending loop flags are always processed by
		 * the statementList method, and therefore can not be reached by the
		 * loop method.
		 */
		String temp = t.toLowerCase();
		if (temp.equalsIgnoreCase("fi")) {
			endIf = true;
			// System.out.println("endIf = true");
			return true;
		}
		if (temp.equalsIgnoreCase("rof")) {
			endFor = true;
			// System.out.println("endFor = true");
			return true;
		}
		if (temp.equalsIgnoreCase("elihw")) {
			endWhile = true;
			// System.out.println("endWhile = true");
			return true;
		}

		if (!tok.hasMoreTokens() || t.isEmpty()) {
			b = true;
			return b;
		} else if (statement(t)) {
			if (tok.hasMoreTokens())
				s = tok.nextToken();
			if (statementList(s))
				b = true;
		}
		//System.out.println("StatementList: " + b);
		return b;
	}

	/*
	 * Statement --> read/write id | forLoop | whileLoop | conditional | id :=
	 * expression This method checks the parameter string to see if it's a
	 * read/write id and loop first. If it's not then checks to see if it's a
	 * 'id := expression'. It calls the appropriate method for any hits. If they
	 * return true, then this statement returns true.
	 */
	public boolean statement(String t) {
		// System.out.println("Statement -->" + t);
		boolean b = false;
		if (t.equalsIgnoreCase("read")) {
			if (read(t))
				b = true;
			// System.out.println("Statement: " + b);
			return b;
		}
		if (t.equalsIgnoreCase("write")) {
			if (write(t))
				b = true;
			// System.out.println("Statement: " + b);
			return b;
		}
		if (t.equalsIgnoreCase("for")) {
			if (forLoop(t))
				b = true;
			// System.out.println("Statement: " + b);
			return b;

		}
		if (t.equalsIgnoreCase("while")) {
			if (whileLoop(t))
				b = true;
			// System.out.println("Statement: " + b);
			return b;
		}
		if (t.equalsIgnoreCase("if")) {
			if (conditional(t))
				b = true;
			// System.out.println("Statement: " + b);
			return b;
		} else if (tok.hasMoreTokens())
			if (t.matches(idPat) && tok.nextToken().equals(":=")) {
				if (tok.hasMoreTokens()) {
					if (expression(tok.nextToken())) {
						b = true;
					}
				}
			}

		// System.out.println("Statement: " + b);
		return b;
	}

	/*
	 * Expression --> term operation term This method checks to see if the next
	 * strings match an expression. This method looks ahead 2 tokens. Returns
	 * true if expression matches.
	 */
	public boolean expression(String t) {
		boolean b = false;
		t1 = t;
		try {
			t2 = tok.nextToken();
			t3 = tok.nextToken();
		} catch (Exception e) {
			System.out.println("Invalid Expression");// Error output if tokens
			// don't exit
			System.exit(1);
		}

		if (term(t1) && operation(t2) && term(t3)) {
			b = true;
		}

		// System.out.println("expression: " + b);

		return b;
	}

	/*
	 * Term --> id | number This method checks to see if the parameter match a
	 * term. Returns true if strings match.
	 */
	public boolean term(String t) {
		// System.out.println("Term -->" + t);
		boolean b = false;
		if (id(t) || number(t))
			b = true;

		// System.out.println("term: " + b);
		return b;
	}

	/*
	 * Id --> a-Z This method returns true if the parameter matches an id. Also
	 * checks to make sure the id is not a key term.
	 */
	public boolean id(String t) {
		// System.out.println("id -->" + t);
		boolean b = false;
		if (t.matches(idPat) && !t.matches(endPat))
			b = true;
		// System.out.println("id: " + b);
		return b;

	}

	/*
	 * Number --> 0-9 Checks to see if the parameter matches a digit.
	 */
	public boolean number(String t) {
		// System.out.println("number -->" + t);
		boolean b = false;
		if (t.matches(numberPat))
			b = true;
		// System.out.println("number: " + b);
		return b;

	}

	/*
	 * Operation --> + - / * Returns true if the parameter matches an operator.
	 */
	public boolean operation(String t) {
		// System.out.println("Operation --> " + t);
		boolean b = false;
		if (t.matches(operationPat)) {
			// op = t;
			b = true;
		}
		// System.out.println("operation: " + b);
		return b;
	}

	/*
	 * ForLoop --> "For" id number number statementList "rof". Always set the
	 * endFor flag to false when starting a new loop.
	 */
	public boolean forLoop(String t) {
		// System.out.println("forLoop -->" + t);
		boolean b = false;
		endFor = false;
		try {
			t1 = tok.nextToken();
			t2 = tok.nextToken();
			t3 = tok.nextToken();
		} catch (Exception e) {
			System.out.println("Invalid for loop"); // error output
			System.exit(1);
		}
		if (id(t1) && number(t2) && number(t3)) {
			String s = "";
			if (tok.hasMoreTokens())
				s = tok.nextToken();
			if (statementList(s)) {
				if (endFor)
					b = true;
			}
		}
		// System.out.println("forLoop: " + b);
		return b;
	}

	/*
	 * WhileLoop --> "while" condition statementList "elihw" Returns true if the
	 * following statements match a whileLoop.
	 */
	public boolean whileLoop(String t) {
		// System.out.println("whileLoop --> " + t);
		boolean b = false;
		endWhile = false;
		if (condition(tok.nextToken())) {
			String s = "";
			if (tok.hasMoreTokens())
				s = tok.nextToken();
			if (statementList(s))
				if (endWhile)
					b = true;
		}
		// System.out.println("whileLoop: " + b);
		return b;
	}

	/*
	 * Conditional --> "if" condition statementList "fi" Returns true if the
	 * following statements match a correct conditional statement.
	 */
	public boolean conditional(String t) {
		// System.out.println("Conditional --> " + t);
		boolean b = false;
		endIf = false;
		if (condition(tok.nextToken())) {
			String s = "";
			if (tok.hasMoreTokens())
				s = tok.nextToken();
			if (statementList(s)) {
				if (endIf)
					b = true;
			}
		}
		// System.out.println("Conditional: " + b);
		return b;
	}

	/*
	 * Condition --> id comparison term Returns true if the next 3 statements
	 * match a condition. Looks ahead 2 tokens.
	 */
	public boolean condition(String t) {
		boolean b = false;
		try {
			t1 = t;
			t2 = tok.nextToken();
			t3 = tok.nextToken();
		} catch (Exception e) {
			System.out.println("Invalid condition");// Error output if tokens
			// don't exist
			System.exit(1);
		}
		// System.out.println("condition -->" + t1 + " " + t2 + " " + t3);
		if (id(t1) && comparison(t2) && term(t3)) {
			b = true;
		}
		// System.out.println("Condition: " + b);
		return b;
	}

	/*
	 * Comparison --> == != < > <= >= Returns true if the parameter matches a
	 * comparator.
	 */
	public boolean comparison(String t) {
		// System.out.println("ccomparison --> " + t);
		boolean b = false;
		if (t.matches(comparisonPat))
			b = true;
		// System.out.println("comparison: " + b);
		return b;
	}

	/*
	 * Reads in the value of a variable
	 */
	public boolean read(String t) {
		boolean b = false;
		String s;
		if (tok.hasMoreTokens()) {
			s = tok.nextToken();
			if (id(s)) {

				b = true;
			}
		}
		return b;
	}

	/*
	 * writes the value of a variable
	 */
	public boolean write(String t) {
		boolean b = false;
		String s;
		// int i = -1;
		if (tok.hasMoreTokens()) {
			s = tok.nextToken();
			if (id(s)) {
				// if (map.containsKey(s)) {
				// i = (Integer) map.get(s);
				// ystem.out.println(s + " = " + i);
				b = true;
				// }
			}
		}
		// System.out.println("Write: " + i);
		return b;
	}

	public void interpret(String str) {
		//System.out.println("Interpretting: " + str);
		tok2 = new StringTokenizer(str);
		String st = "";
		while (tok2.hasMoreTokens()) {

			st = tok2.nextToken();
			//System.out.println("Next Token: " + st);
			if (st.equalsIgnoreCase("read")) {
				// System.out.println("here");
				intRead(tok2.nextToken());
			} else if (st.equalsIgnoreCase("write")) {
				// System.out.println("here");
				String w = tok2.nextToken();
				// System.out.println(w);
				intWrite(w);
			} else if (st.equalsIgnoreCase("if")) {
				interpretIf();
			} else if (st.equalsIgnoreCase("while")) {
				interpretWhile();
			} else if (st.equalsIgnoreCase("for")) {
				interpretFor();
			} else if (id(st) && !st.matches(startPat) && !st.matches(endPat)) {
				tok2.nextToken(); // this is a := token...don't need
				String var1 = tok2.nextToken();
				// System.out.println(st);
				// System.out.println(var1);
				String var2 = tok2.nextToken();
				// System.out.println(var2);
				String var3 = tok2.nextToken();
				// System.out.println(var3);
				evaluate(st, var1, var2, var3);
			} else
				tok2.nextToken();
		}
	}

	public void intRead(String s) {
		Scanner keys = new Scanner(System.in);
		// String st = tok.nextToken();
		System.out.println("Enter value for " + s);
		int num = keys.nextInt();
		map.put(s, num);
	}

	public void intWrite(String s) {
		if (map.containsKey(s))
			System.out.println(s + " = " + map.get(s));
	}

	public boolean checkCondition(String var1, String comparator, String var2) {
		int i = 0, j = 0;
		boolean valid = false;
		if (map.containsKey(var1)) {
			i = map.get(var1);

			if (id(var2)) {
				j = map.get(var2);
			} else
				j = Integer.parseInt(var2);
		} else {
			System.out.println("Variable doesn't exist");
			System.exit(1);
		}
		if (comparator.equals("==") && i == j) {
			valid = true;
		}
		if (comparator.equals("<") && i < j) {
			valid = true;
		}
		if (comparator.equals(">") && i > j) {
			valid = true;
		}
		if (comparator.equals("<=") && i <= j) {
			valid = true;
		}
		if (comparator.equals(">=") && i >= j) {
			valid = true;
		}
		if (comparator.equals("!=") && i != j) {
			valid = true;
		}
		return valid;
	}

	/*
	 * Evaluates an expressions. Ex: a := a + 4 takes 4 parameters.
	 */
	public void evaluate(String var1, String var2, String operator, String var3) {
		//System.out.println("Evaluating: " + var1 + " " + var2 + " " + operator
		//		+ " " + var3);
		String a, b, c;
		int x, y, z = 0;
		a = var2;
		b = operator;
		c = var3;
		if (id(a)) {
			x = map.get(a);
		} else {
			x = Integer.parseInt(a);
		}
		if (id(c)) {
			y = map.get(c);
		} else {
			y = Integer.parseInt(c);
		}
		if (b.equals("+")) {
			z = x + y;
		}
		if (b.equals("-")) {
			z = x - y;
		}
		if (b.equals("/")) {
			z = x / y;
		}
		if (b.equals("*")) {
			z = x * y;
		}
		map.put(var1, z);
	}

	public void interpretIf() {
		String var1 = tok2.nextToken();
		String comparator = tok2.nextToken();
		String var2 = tok2.nextToken();
		String var3 = "";
		String operator;
		String temp = "";
		String ifStatement = "";
		int n = 1;
		int i = 1;
		// System.out.println(ifStatement);
		boolean valid = checkCondition(var1, comparator, var2);
		if (valid) {// Condition is true
			// get entire if statement into a string
			while (n > 0) {
				temp = tok2.nextToken();
				if (temp.equalsIgnoreCase("if")) {
					n++;
				}
				if (temp.equalsIgnoreCase("fi")) {
					n--;
				}
				if (n > 0)
					ifStatement += temp + " ";
			}
			//System.out.println("Interpretting ifStatement: " + ifStatement);
			interpret(ifStatement);

		} else {
			String s = "";
			while (i >= 1) {
				s = tok2.nextToken();
				if (s.equalsIgnoreCase("if")) {
					i++;
				}
				if (s.equals("fi"))
					i--;
			}
			if (tok2.hasMoreTokens()) {
				String t = "";
				while (tok2.hasMoreTokens()) {
					t += tok2.nextToken() + " ";
				}
				// System.out.println("Here: " + t);
				interpret(t);
			}
		}

	}

	public void interpretWhile() {
		// System.out.println("interpretting while");

		String var1 = tok2.nextToken();
		String operator = tok2.nextToken();
		String var2 = tok2.nextToken();
		// String nextVar = tok2.nextToken();
		String temp = "";
		int n = 1;
		String whileLoopStr = "";
		// get entire while loop into a string
		while (n > 0) {
			temp = tok2.nextToken();
			if (temp.equalsIgnoreCase("while")) {
				n++;
			}
			if (temp.equalsIgnoreCase("elihw")) {
				n--;
			}
			if (n > 0)
				whileLoopStr += temp + " ";
		}
		// get the remaining string
		String remainingStr = "";
		while (tok2.hasMoreTokens()) {
			remainingStr += tok2.nextToken() + " ";
		}
		//System.out.println("While Loop: " + whileLoopStr);
		StringTokenizer whileToken = new StringTokenizer(whileLoopStr);
		int i = 1;
		// see if the condition is true...otherwise skip the while loop

		while (checkCondition(var1, operator, var2)) {
			interpret(whileLoopStr);
		}

		interpret(remainingStr);

	}

	/*
	 * for a 0 5 for (a = 0;a<5;a++)
	 */
	public void interpretFor() {
		String var1 = tok2.nextToken();
		int var2 = Integer.parseInt(tok2.nextToken());
		int var3 = Integer.parseInt(tok2.nextToken());
		map.put(var1, var2);
		String nextVar = tok2.nextToken();
		String a = "", b = "", c = "", d = "";
		// see if there is an expression or more statementLists
		if (!nextVar.matches(startPat) && id(nextVar)) {
			// then this is an expression
			a = nextVar;
			tok2.nextToken();
			b = tok2.nextToken();
			c = tok2.nextToken();
			d = tok2.nextToken();
		}
		// copy the remaining tokens into a string
		String str = nextVar;
		while (tok2.hasMoreTokens()) {
			str += " " + tok2.nextToken();
		}
		// create new tokenizer
		// StringTokenizer st1 = new StringTokenizer(str);
		StringTokenizer st2 = new StringTokenizer(str);
		// create a string of only the for loop
		String forLoopString = "";
		int n = 1;
		String temp = "";
		while (n > 0) {
			temp = st2.nextToken();
			if (temp.equalsIgnoreCase("for")) {
				n++;
			}
			if (temp.equals("rof")) {
				n--;
			}
			if (n > 0)
				forLoopString += temp + " ";
		}
		// System.out.println(forLoopString);
		// for(int i = 0;i < 5;i++)
		for (map.get(var1); map.get(var1) < var3;) {
			//System.out.println(forLoopString);
			interpret(forLoopString);
			int i = map.get(var1);
			i++;
			map.put(var1, i);
		}

		// System.out.println("here" + st2.nextToken());
		String t = "";
		while (st2.hasMoreTokens()) {
			t += st2.nextToken() + " ";
		}
		interpret(t);
	}

	/*
	 * Main method, instantiates the class, then calls the start() method.
	 * Requires a one line input of the statements that are to be parsed.
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Main t = new Main();
		t.start("input.txt");

	}
}
