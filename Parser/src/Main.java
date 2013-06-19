import java.io.FileNotFoundException;
import java.io.FileReader;
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
 * @date 6/10/2013
 * This class parses a user specified string and
 * determines if the string is accepted by the laws given 
 * to me for this assignment.
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
	private boolean endIf, endWhile, endFor = false; // flags for ending the loop.
	

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
		//System.out.println("Enter language to be parsed...");
		
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
			System.out.println("Successful Parse");
		else
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
		System.out.println("Program -->" + t);
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
		System.out.println("StatementList--> " + t);
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
			System.out.println("endIf = true");
			return true;
		}
		if (temp.equalsIgnoreCase("rof")) {
			endFor = true;
			System.out.println("endFor = true");
			return true;
		}
		if (temp.equalsIgnoreCase("elihw")) {
			endWhile = true;
			System.out.println("endWhile = true");
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
		System.out.println("StatementList: " + b);
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
		System.out.println("Statement -->" + t);
		boolean b = false;
		if (t.equalsIgnoreCase("read")) {
			if (read(t))
				b = true;
			System.out.println("Statement: " + b);
			return b;
		}
		if (t.equalsIgnoreCase("write")) {
			if (write(t))
				b = true;
			System.out.println("Statement: " + b);
			return b;
		}
		if (t.equalsIgnoreCase("for")) {
			if (forLoop(t))
				b = true;
			System.out.println("Statement: " + b);
			return b;

		}
		if (t.equalsIgnoreCase("while")) {
			if (whileLoop(t))
				b = true;
			System.out.println("Statement: " + b);
			return b;
		}
		if (t.equalsIgnoreCase("if")) {
			if (conditional(t))
				b = true;
			System.out.println("Statement: " + b);
			return b;
		} else if (tok.hasMoreTokens())
			if (t.matches(idPat) && tok.nextToken().equals(":=")
					&& expression(tok.nextToken()))
				b = true;
		System.out.println("Statement: " + b);
		return b;
	}

	/*
	 * Expression --> term operation term This method checks to see if the next
	 * strings match an expression. This method looks ahead 2 tokens. Returns
	 * true if expression matches.
	 */
	public boolean expression(String t) {
		t1 = t;
		try {
			t2 = tok.nextToken();
			t3 = tok.nextToken();
		} catch (Exception e) {
			System.out.println("Invalid Expression");// Error output if tokens
														// don't exit
			System.exit(1);
		}
		System.out.println("expression -->" + t1 + " " + t2 + " " + t3);
		boolean b = false;
		if (term(t1) && operation(t2) && term(t3))
			b = true;
		System.out.println("expression: " + b);
		return b;
	}

	/*
	 * Term --> id | number This method checks to see if the parameter match a
	 * term. Returns true if strings match.
	 */
	public boolean term(String t) {
		System.out.println("Term -->" + t);
		boolean b = false;
		if (id(t) || number(t))
			b = true;

		System.out.println("term: " + b);
		return b;
	}

	/*
	 * Id --> a-Z This method returns true if the parameter matches an id. Also
	 * checks to make sure the id is not a key term.
	 */
	public boolean id(String t) {
		System.out.println("id -->" + t);
		boolean b = false;
		if (t.matches(idPat) && !t.matches(endPat))
			b = true;
		System.out.println("id: " + b);
		return b;

	}

	/*
	 * Number --> 0-9 Checks to see if the parameter matches a digit.
	 */
	public boolean number(String t) {
		System.out.println("number -->" + t);
		boolean b = false;
		if (t.matches(numberPat))
			b = true;
		System.out.println("number: " + b);
		return b;

	}

	/*
	 * Operation --> + - / * Returns true if the parameter matches an operator.
	 */
	public boolean operation(String t) {
		System.out.println("Operation --> " + t);
		boolean b = false;
		if (t.matches(operationPat))
			b = true;
		System.out.println("operation: " + b);
		return b;
	}

	/*
	 * ForLoop --> "For" id number number statementList "rof". Always set the
	 * endFor flag to false when starting a new loop.
	 */
	public boolean forLoop(String t) {
		System.out.println("forLoop -->" + t);
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
				// if (tok.hasMoreTokens())
				// if (tok.nextToken().equalsIgnoreCase("for"))
				// endFor = true;
				if (endFor)
					b = true;
			}
		}
		System.out.println("forLoop: " + b);
		return b;
	}

	/*
	 * WhileLoop --> "while" condition statementList "elihw" Returns true if the
	 * following statements match a whileLoop.
	 */
	public boolean whileLoop(String t) {
		System.out.println("whileLoop --> " + t);
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
		System.out.println("whileLoop: " + b);
		return b;
	}

	/*
	 * Conditional --> "if" condition statementList "fi" Returns true if the
	 * following statements match a correct conditional statement.
	 */
	public boolean conditional(String t) {
		System.out.println("Conditional --> " + t);
		boolean b = false;
		endIf = false;
		if (condition(tok.nextToken())) {
			String s = "";
			if (tok.hasMoreTokens())
				s = tok.nextToken();
			if (statementList(s)) {
				// if (tok.hasMoreTokens())
				// if (tok.nextToken().equalsIgnoreCase("fi"))
				// endIf = true;
				if (endIf)
					b = true;
			}
		}
		System.out.println("Conditional: " + b);
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
		System.out.println("condition -->" + t1 + " " + t2 + " " + t3);
		if (id(t1) && comparison(t2) && term(t3))
			b = true;
		System.out.println("Condition: " + b);
		return b;
	}

	/*
	 * Comparison --> == != < > <= >= Returns true if the parameter matches a
	 * comparator.
	 */
	public boolean comparison(String t) {
		System.out.println("ccomparison --> " + t);
		boolean b = false;
		if (t.matches(comparisonPat))
			b = true;
		System.out.println("comparison: " + b);
		return b;
	}

	/*
	 * Reads in the value of a variable
	 */
	public boolean read(String t) {
		boolean b = false;
		if (tok.hasMoreTokens())
			if (id(tok.nextToken()))
				b = true;
		return b;
	}

	/*
	 * writes the value of a variable
	 */
	public boolean write(String t) {
		boolean b = false;
		if (tok.hasMoreTokens())
			if (id(tok.nextToken()))
				b = true;
		System.out.println("Write: " + b);
		return b;
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
