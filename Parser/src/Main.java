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
 *       After the parsing is complete and if it is a valid language, then the
 *       interpreting begins. Passes the entire string that was parsed to the
 *       interpret method that evaluates any variables. I found it difficult to
 *       keep track of parsing and interpreting at the same time, so I split up
 *       the processes and it was much easier.
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
	 * The starting method. Uses the scanner class to read through a file and
	 * populate a string. Then a tokenizer splits up the string into tokens.
	 * Once the tokenizer is populated, it calls the program() method with the
	 * first token as the parameter. NOTE: the input must be one line
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
			System.out.println("Interpreting: " + str);
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

	/*
	 * This is the main interpreting function. Parameter is a string of
	 * everything that needs to be interpreted. Turns the string into a string
	 * tokenizer. Loops through the tokenizer examining the tokens. Calls
	 * methods if the token is "read/write/if/for/while", or if it is an
	 * expression. Skips over "fi/rof/elihw" because the parsing already made
	 * sure they are there.
	 */
	public void interpret(String str) {
		tok2 = new StringTokenizer(str);
		String st = "";
		while (tok2.hasMoreTokens()) {
			st = tok2.nextToken();
			if (st.equalsIgnoreCase("read")) {
				intRead(tok2.nextToken());
			} else if (st.equalsIgnoreCase("write")) {
				String w = tok2.nextToken();
				intWrite(w);
			} else if (st.equalsIgnoreCase("if")) {
				interpretIf();
			} else if (st.equalsIgnoreCase("while")) {
				interpretWhile();
			} else if (st.equalsIgnoreCase("for")) {
				interpretFor();
				// Expression
			} else if (id(st) && !st.matches(startPat) && !st.matches(endPat)) {
				tok2.nextToken(); // discard the ":=" token
				String var1 = tok2.nextToken();
				String var2 = tok2.nextToken();
				String var3 = tok2.nextToken();
				evaluate(st, var1, var2, var3);
			} else
				tok2.nextToken();
		}
	}

	/*
	 * Reads a value from the user, sets the parameter S to the value specified
	 * and inserts it into the hash map.
	 */
	public void intRead(String s) {
		Scanner keys = new Scanner(System.in);
		// String st = tok.nextToken();
		System.out.println("Enter value for " + s);
		int num = keys.nextInt();
		map.put(s, num);
	}

	/*
	 * Writes the value of a variable to System.out.
	 */
	public void intWrite(String s) {
		if (map.containsKey(s))
			System.out.println(s + " = " + map.get(s));
	}

	/*
	 * Checks the condition of a conditional. Returns true or false based on the
	 * outcome. Throws an error if variable doesn't exist.
	 */
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
	 * Evaluates an expressions. Ex: a := a + 4 Takes 4 parameters, skips the
	 * ":=". Sets the value to the key in the hash map.
	 */
	public void evaluate(String var1, String var2, String operator, String var3) {
		// System.out.println("Evaluating: " + var1 + " " + var2 + " " +
		// operator
		// + " " + var3);
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

	/*
	 * This method interprets everything in the 'if' statement. First get the 3
	 * tokens of the condition and see if it's true. If the condition is true,
	 * put the entire 'if' statement into it's own string (ifStatement) and
	 * perform interpret on it. If condition is false, skip the entire if
	 * statement and put remaining tokens into a string (remainingString) and
	 * perform interpret on it.
	 */
	public void interpretIf() {
		String var1 = tok2.nextToken();
		String comparator = tok2.nextToken();
		String var2 = tok2.nextToken();
		String temp = "";
		String ifStatement = "";
		String remainingString = "";
		int n = 1;
		int i = 1;
		// get entire 'if' statement into a string
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
		// Copy the remaining tokens after the if statement
		// into it's own string (remainingString) and
		// interpret remainingString.
		if (tok2.hasMoreTokens()) {
			while (tok2.hasMoreTokens()) {
				remainingString += tok2.nextToken() + " ";
			}
		}
		// check to see if the condition is true or not.
		boolean valid = checkCondition(var1, comparator, var2);
		if (valid) {// Condition is true
			interpret(ifStatement);
		}
		interpret(remainingString);
	}

	/*
	 * This method interprets a while loop. Checks that the initial condition is
	 * true, if not then skip the entire while loop and move on. Copy the entire
	 * while loop into it's own string (whileLoopString) and copy the remaining
	 * string after the while loop to it's own string (remainingString). While
	 * the condition is true, interpret whileLoopString, otherwise interpret
	 * remainingString.
	 */
	public void interpretWhile() {
		String var1 = tok2.nextToken();
		String operator = tok2.nextToken();
		String var2 = tok2.nextToken();
		String temp = "";
		int n = 1;
		String whileLoopStr = "";
		// Get entire while loop into a string
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
		// Get the remaining string after the while loop
		String remainingStr = "";
		while (tok2.hasMoreTokens()) {
			remainingStr += tok2.nextToken() + " ";
		}
		// If the condition is true pass the while loop string
		// to the interpret method...otherwise skip the while loop
		while (checkCondition(var1, operator, var2)) {
			interpret(whileLoopStr);
		}
		interpret(remainingStr);
	}

	/*
	 * ex: for a 0 5 for (a = 0;a<=5;a++) This method interprets values inside
	 * the for loop. Copy the tokenizer, make a new tokenizer of the remaining
	 * tokens. Copy the entire for loop into it's own string (forLoopString).
	 * Copy the remaining tokens after the for loop into its own string
	 * (remainingString). Perform interpret on forLoopString the specified
	 * number of times, then perform interpret on remainingString.
	 */
	public void interpretFor() {
		String var1 = tok2.nextToken();
		int var2 = Integer.parseInt(tok2.nextToken());
		int var3 = Integer.parseInt(tok2.nextToken());
		map.put(var1, var2);
		String nextVar = tok2.nextToken();
		// Copy the remaining tokens into a string
		String copy = nextVar;
		while (tok2.hasMoreTokens()) {
			copy += " " + tok2.nextToken();
		}
		// create new tokenizer of the remaining string
		StringTokenizer st2 = new StringTokenizer(copy);
		// create a string of only the for loop
		String forLoopString = "";
		int n = 1;
		String temp = "";
		while (n > 0) {
			temp = st2.nextToken();
			// Have to account for other for loops
			// within this for loop.
			if (temp.equalsIgnoreCase("for")) {
				n++;
			}
			if (temp.equals("rof")) {
				n--;
			}
			if (n > 0)
				forLoopString += temp + " ";
		}

		// Design for loop based on an actual for loop...
		// for(int i = 0;i <= 5;i++)
		for (map.get(var1); map.get(var1) <= var3;) {
			interpret(forLoopString);
			// Increment the variable at the end of loop
			int i = map.get(var1);
			i++;
			map.put(var1, i);
		}

		// Once the for loop is complete,
		// copy all tokens after the for loop into a string,
		// and pass that to interpret().
		String remainingString = "";
		while (st2.hasMoreTokens()) {
			remainingString += st2.nextToken() + " ";
		}
		interpret(remainingString);
	}

	/*
	 * Main method, instantiates the class, then calls the start() method.
	 * Requires a one line input of the statements that are to be parsed.
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Main t = new Main();
		t.start(args[0]);
	}
}

/*
 * Testing Cases
 */

/*
 Case - Multiple expressions
 input: a := 4 + 2 b := a + 4 c := a + b 
 output:
 Parsing string: a := 4 + 2 b := a + 4 c := a + b write a write b write c
 Successful Parse... 
 Interpreting: a := 4 + 2 b := a + 4 c := a + b write a write b write c 
 a = 6 
 b = 10 
 c = 16
 


 Case - Read/Write expressions 
 input: read a read b c := a + b write a write b write c 
 output: Parsing string: read a read b c := a + b write a write b write c 
 Successful Parse... 
 Interpreting: read a read b c := a + b write a write b write c 
 Enter value for a
 10 
 Enter value for b 
 5 
 a = 10 
 b = 5 
 c = 15
 


Case - if Statement
input: a := 4 + 6 b := 15 - a if b < a b := b * 2 fi write a write b
output:
Parsing string:  a := 4 + 6 b := 15 - a if b < a b := b * 2 fi write a write b 
Successful Parse...
Interpreting:  a := 4 + 6 b := 15 - a if b < a b := b * 2 fi write a write b 
a = 10
b = 10

Case - Nested if statement
input: 
a := 4 + 6 b := 15 - a 
if b < a 
	b := b + 2
	if b == a
		b := b * 2 
	fi 
fi 
write a write b
output:
Parsing string:  a := 4 + 6 b := 15 - a if b < a b := b + 2 if b == a b := b * 2 fi fi write a write b 
Successful Parse...
Interpreting:  a := 4 + 6 b := 15 - a if b < a b := b + 2 if b == a b := b * 2 fi fi write a write b 
a = 10
b = 7

Case - while loop
input: 
a := 5 + 0
b := 5 + 5
while a < b
	a := a + 1
	write a
elihw
write a write 
output:
Parsing string:  a := 5 + 0 b := 5 + 5 while a < b a := a + 1 write a elihw write a write b 
Successful Parse...
Interpreting:  a := 5 + 0 b := 5 + 5 while a < b a := a + 1 write a elihw write a write b 
a = 6
a = 7
a = 8
a = 9
a = 10
a = 10
b = 10

Case - nested while loop
input:
a := 5 + 0
b := 5 + 5
while a < b
	a := a + 1
	write a
	while b > 5
		b := b - 1
	elihw
elihw
write a write b
output:
Parsing string:  a := 5 + 0 b := 5 + 5 while a < b a := a + 1 write a while b > 5 b := b - 1 elihw elihw write a write b 
Successful Parse...
Interpreting:  a := 5 + 0 b := 5 + 5 while a < b a := a + 1 write a while b > 5 b := b - 1 elihw elihw write a write b 
a = 6
a = 6
b = 5

Case - for loop
intput:
a := 1 + 0
for b 1 5
	a := a + 2
	write b
rof
write a
output:
Parsing string:  a := 1 + 0 for b 1 5 a := a + 2 write b rof write a 
Successful Parse...
Interpreting:  a := 1 + 0 for b 1 5 a := a + 2 write b rof write a 
b = 1
b = 2
b = 3
b = 4
b = 5
a = 11
	
Case - nested for loop
input: 
a := 1 + 0
b := 2 + 0
for c 1 5
	a := a + 2
	write c
	for d 0 3
		b := a * 2
		write d
	rof
rof
write a
write b
output:
Parsing string:  a := 1 + 0 b := 2 + 0 for c 1 5 a := a + 2 write c for d 0 3 b := a * 2 write d rof rof write a write b 
Successful Parse...
Interpreting:  a := 1 + 0 b := 2 + 0 for c 1 5 a := a + 2 write c for d 0 3 b := a * 2 write d rof rof write a write b 
c = 1
d = 0
d = 1
d = 2
d = 3
c = 2
d = 0
d = 1
d = 2
d = 3
c = 3
d = 0
d = 1
d = 2
d = 3
c = 4
d = 0
d = 1
d = 2
d = 3
c = 5
d = 0
d = 1
d = 2
d = 3
a = 11
b = 22

Case - Mixed nested statements
intput:
c := 1 + 1 
while c <= 10 
	for a 1 10
		for b 2 20 
			if a != b 
				a := b + c 
			fi 
		rof 
		c := c + 2
	rof 
elihw 
write a
write b
write c

output:
Parsing string:  c := 1 + 1 while c <= 10 for a 1 10 for b 2 20 if a != b a := b + c fi rof c := c + 2 rof elihw write a write b write c 
Successful Parse...
Interpreting:  c := 1 + 1 while c <= 10 for a 1 10 for b 2 20 if a != b a := b + c fi rof c := c + 2 rof elihw write a write b write c 
a = 31
b = 21
c = 12

*/
