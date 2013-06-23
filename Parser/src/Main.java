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
	private Scanner input;
	String t1, t2, t3;
	private String idPat = "[a-zA-Z]+";
	private String numberPat = "[0-9]+";
	private String operationPat = "(\\+|-|\\*|\\/)";
	private String comparisonPat = "(==|!=|<|>|<=|>=)";
	private String endPat = "(fi|elihw|rof)";// key ending terms
	private String startPat = "(for|if|while)";// key starting terms
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
		if (program(tok.nextToken()))
			System.out.println("Successful Parse...");
		else
			System.out.println("ERROR: UNSUCCESSFUL PARSE");
		tok = new StringTokenizer(str);
		System.out.println("Interpreting: " + str);
		while (tok.hasMoreTokens()) {
			interpret(tok.nextToken());
		}
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
		// System.out.println("StatementList--> " + t);
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
		// System.out.println("StatementList: " + b);
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

	public void interpret(String st) {
		if (st.equalsIgnoreCase("read"))
			intRead();
		if (st.equalsIgnoreCase("write"))
			intWrite();
		if (st.equalsIgnoreCase("if")) {
			interpretIf();
		}
		if (st.equalsIgnoreCase("while")) {
			interpretWhile();
		}
		if(st.equalsIgnoreCase("for")){
			interpretFor();
		}
	}

	public void intRead() {
		Scanner keys = new Scanner(System.in);
		String st = tok.nextToken();
		System.out.println("Enter value for " + st);
		int num = keys.nextInt();
		map.put(st, num);
	}

	public void intWrite() {
		String st = tok.nextToken();
		if (map.containsKey(st))
			System.out.println(st + " = " + map.get(st));
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

	public void evaluate(String var1, String var2, String operator, String var3) {
		if (map.containsKey(var1)) {
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
		} else {
			System.out.println("Variable doesn't exist");
			System.exit(1);
		}

	}

	public void interpretIf() {
		String var1 = tok.nextToken();
		String comparator = tok.nextToken();
		String var2 = tok.nextToken();
		String var3 = "";
		String operator;
		int i = 1;
		boolean valid = checkCondition(var1, comparator, var2);
		if (valid) {// Condition is true
			String s = tok.nextToken();
			if (!s.matches(startPat) && id(s)) {// next element is an expression
				var1 = s;
				tok.nextToken();
				var2 = tok.nextToken();
				operator = tok.nextToken();
				var3 = tok.nextToken();
				evaluate(var1, var2, operator, var3);
			} else
				// if its not an id, then its another statement
				interpret(s);
		} else {
			while (i >= 1) {
				String s = tok.nextToken();
				if (s.equalsIgnoreCase("if")) {
					i++;
				}
				if (s.equals("fi"))
					i--;
			}
			if (tok.hasMoreTokens()) {
				interpret(tok.nextToken());
			}
		}

	}

	public void interpretWhile() {
		String var1 = tok.nextToken();
		String operator = tok.nextToken();
		String var2 = tok.nextToken();
		String nextVar = tok.nextToken();
		int i = 1;
		if (checkCondition(var1, operator, var2)) {
			// System.out.println("here");
			if (!nextVar.matches(startPat) && id(nextVar)) {
				// System.out.println("here");
				// get expression variables
				String a = nextVar;
				tok.nextToken();
				String b = tok.nextToken();
				String c = tok.nextToken();
				String d = tok.nextToken();
				do {
					// System.out.println("Evaluating: " + a + " "+ b + " "+ c +
					// " "+ d);
					evaluate(a, b, c, d);
				} while (checkCondition(var1, operator, var2));
			} else
				interpret(nextVar);
		}
		else {
			while (i >= 1) {
				String s = tok.nextToken();
				if (s.equalsIgnoreCase("if")) {
					i++;
				}
				if (s.equals("fi"))
					i--;
			}
			if (tok.hasMoreTokens()) {
				interpret(tok.nextToken());
			}
		}
	}
	
	public void interpretFor(){
		String var1 = tok.nextToken();
		int var2 = Integer.parseInt(tok.nextToken());
		int var3 = Integer.parseInt(tok.nextToken());
		String nextVar = tok.nextToken();
		String a = "", b= "", c= "", d = "";
		if (!nextVar.matches(startPat) && id(nextVar)){
			a = nextVar;
			tok.nextToken();
			b = tok.nextToken();
			c = tok.nextToken();
			d = tok.nextToken();
		}
		//for(int i = 0;i < 5;i++)
		for(int i = var2; i < var3;i++){
			if (!nextVar.matches(startPat) && id(nextVar)) {
				evaluate(a, b, c, d);
			}
			else{
				interpret(nextVar);
			}
		}
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
