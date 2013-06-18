import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private boolean endIf, endWhile, endFor = false; // flags for ending the
														// loop.

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
	public void start() {
		input = new Scanner(System.in);
		String str = " ";
		System.out.println("Enter language to be parsed...");
		while (input.hasNext())
			str += input.next() + " ";
		if (str.equals("")) {
			System.out.println("No input received");
			System.exit(1);
		}
		tok = new StringTokenizer(str);
		System.out.println("Parsing string: " + str);
		syntaxCheck(str);
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
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Main t = new Main();

		// t.statementList("");
		t.start();

	}
	
/*
Testing Cases

Standard if statement: if a > b a := a - b fi

Enter language to be parsed...
if a > b a := a - b fi
Parsing...if a > b a := a - b fi
Program -->if
StatementList--> if
Statement -->if
Conditional --> if
condition -->a > b
id -->a
id: true
ccomparison --> >
comparison: true
Term -->b
id -->b
id: true
term: true
Condition: true
StatementList--> a
Statement -->a
expression -->a - b
Term -->a
id -->a
id: true
term: true
Operation --> -
operation: true
Term -->b
id -->b
id: true
term: true
expression: true
Statement: true
StatementList--> fi
endIf = true
StatementList: true
Conditional: true
StatementList--> 
StatementList: true
Program: true
Successful Parse

nested if statement : if a > b if a > b a := a - b fi fi
Enter language to be parsed...
if a > b if a > b a := a - b fi fi
Parsing...if a > b if a > b a := a - b fi fi
Program -->if
StatementList--> if
Statement -->if
Conditional --> if
condition -->a > b
id -->a
id: true
ccomparison --> >
comparison: true
Term -->b
id -->b
id: true
term: true
Condition: true
StatementList--> if
Statement -->if
Conditional --> if
condition -->a > b
id -->a
id: true
ccomparison --> >
comparison: true
Term -->b
id -->b
id: true
term: true
Condition: true
StatementList--> a
Statement -->a
expression -->a - b
Term -->a
id -->a
id: true
term: true
Operation --> -
operation: true
Term -->b
id -->b
id: true
term: true
expression: true
Statement: true
StatementList--> fi
endIf = true
StatementList: true
Conditional: true
StatementList--> fi
endIf = true
StatementList: true
Conditional: true
StatementList--> 
StatementList: true
Program: true
Successful Parse

Standard For Loop: for a 0 5 a := 4 * b rof

Enter language to be parsed...
for a 0 5 a := 4 * b rof
Parsing...for a 0 5 a := 4 * b rof
Program -->for
StatementList--> for
Statement -->for
forLoop -->for
id -->a
id: true
number -->0
number: true
number -->5
number: true
StatementList--> a
Statement -->a
expression -->4 * b
Term -->4
id -->4
id: false
number -->4
number: true
term: true
Operation --> *
operation: true
Term -->b
id -->b
id: true
term: true
expression: true
Statement: true
StatementList--> rof
endFor = true
StatementList: true
forLoop: true
StatementList--> 
StatementList: true
Program: true
Successful Parse

Nested For Loop: for a 0 5 for b 5 55 b := 4 / a rof rof

Enter language to be parsed...
for a 0 5 for b 5 55 b := 4 / a rof rof
Parsing...for a 0 5 for b 5 55 b := 4 / a rof rof
Program -->for
StatementList--> for
Statement -->for
forLoop -->for
id -->a
id: true
number -->0
number: true
number -->5
number: true
StatementList--> for
Statement -->for
forLoop -->for
id -->b
id: true
number -->5
number: true
number -->55
number: true
StatementList--> b
Statement -->b
expression -->4 / a
Term -->4
id -->4
id: false
number -->4
number: true
term: true
Operation --> /
operation: true
Term -->a
id -->a
id: true
term: true
expression: true
Statement: true
StatementList--> rof
endFor = true
StatementList: true
forLoop: true
StatementList--> rof
endFor = true
StatementList: true
forLoop: true
StatementList--> 
StatementList: true
Program: true
Successful Parse

Standard while loop: while a <= 4 b := 4 / 2 elihw

Enter language to be parsed...
while a <= 4 b := 4 / 2 elihw
Parsing...while a <= 4 b := 4 / 2 elihw
Program -->while
StatementList--> while
Statement -->while
whileLoop --> while
condition -->a <= 4
id -->a
id: true
ccomparison --> <=
comparison: true
Term -->4
id -->4
id: false
number -->4
number: true
term: true
Condition: true
StatementList--> b
Statement -->b
expression -->4 / 2
Term -->4
id -->4
id: false
number -->4
number: true
term: true
Operation --> /
operation: true
Term -->2
id -->2
id: false
number -->2
number: true
term: true
expression: true
Statement: true
StatementList--> elihw
endWhile = true
StatementList: true
whileLoop: true
StatementList--> 
StatementList: true
Program: true
Successful Parse

Nested while Loop: while a <= 4 while b >= 4 while d != b c := a + 4 elihw elihw elihw

Enter language to be parsed...
while a <= 4 while b >= 4 while d != b c := a + 4 elihw elihw elihw
Parsing...while a <= 4 while b >= 4 while d != b c := a + 4 elihw elihw elihw
Program -->while
StatementList--> while
Statement -->while
whileLoop --> while
condition -->a <= 4
id -->a
id: true
ccomparison --> <=
comparison: true
Term -->4
id -->4
id: false
number -->4
number: true
term: true
Condition: true
StatementList--> while
Statement -->while
whileLoop --> while
condition -->b >= 4
id -->b
id: true
ccomparison --> >=
comparison: true
Term -->4
id -->4
id: false
number -->4
number: true
term: true
Condition: true
StatementList--> while
Statement -->while
whileLoop --> while
condition -->d != b
id -->d
id: true
ccomparison --> !=
comparison: true
Term -->b
id -->b
id: true
term: true
Condition: true
StatementList--> c
Statement -->c
expression -->a + 4
Term -->a
id -->a
id: true
term: true
Operation --> +
operation: true
Term -->4
id -->4
id: false
number -->4
number: true
term: true
expression: true
Statement: true
StatementList--> elihw
endWhile = true
StatementList: true
whileLoop: true
StatementList--> elihw
endWhile = true
StatementList: true
whileLoop: true
StatementList--> elihw
endWhile = true
StatementList: true
whileLoop: true
StatementList--> 
StatementList: true
Program: true
Successful Parse

Mixed nested statements: while a != b if a > c a := a - b fi for c 0 55 b :=  4 / c rof elihw

Enter language to be parsed...
while a != b if a > c a := a - b fi for c 0 55 b :=  4 / c rof elihw
Parsing...while a != b if a > c a := a - b fi for c 0 55 b :=  4 / c rof elihw
Program -->while
StatementList--> while
Statement -->while
whileLoop --> while
condition -->a != b
id -->a
id: true
ccomparison --> !=
comparison: true
Term -->b
id -->b
id: true
term: true
Condition: true
StatementList--> if
Statement -->if
Conditional --> if
condition -->a > c
id -->a
id: true
ccomparison --> >
comparison: true
Term -->c
id -->c
id: true
term: true
Condition: true
StatementList--> a
Statement -->a
expression -->a - b
Term -->a
id -->a
id: true
term: true
Operation --> -
operation: true
Term -->b
id -->b
id: true
term: true
expression: true
Statement: true
StatementList--> fi
endIf = true
StatementList: true
Conditional: true
StatementList--> for
Statement -->for
forLoop -->for
id -->c
id: true
number -->0
number: true
number -->55
number: true
StatementList--> b
Statement -->b
expression -->4 / c
Term -->4
id -->4
id: false
number -->4
number: true
term: true
Operation --> /
operation: true
Term -->c
id -->c
id: true
term: true
expression: true
Statement: true
StatementList--> rof
endFor = true
StatementList: true
forLoop: true
StatementList--> elihw
endWhile = true
StatementList: true
StatementList: true
whileLoop: true
StatementList--> 
StatementList: true
Program: true
Successful Parse

Invalid statements: while a != b if a > c 4 := a - b fi for c 0 55 b :=  4 / c rof elihw

Enter language to be parsed...
while a != b if a > c 4 := a - b fi for c 0 55 b :=  4 / c rof elihw
Parsing...while a != b if a > c 4 := a - b fi for c 0 55 b :=  4 / c rof elihw
Program -->while
StatementList--> while
Statement -->while
whileLoop --> while
condition -->a != b
id -->a
id: true
ccomparison --> !=
comparison: true
Term -->b
id -->b
id: true
term: true
Condition: true
StatementList--> if
Statement -->if
Conditional --> if
condition -->a > c
id -->a
id: true
ccomparison --> >
comparison: true
Term -->c
id -->c
id: true
term: true
Condition: true
StatementList--> 4
Statement -->4
Statement: false
StatementList: false
Conditional: false
StatementList: false
whileLoop: false
StatementList: false
Invalid statementList

Invalid statements: while a != b if a > c a := a - b for c 0 55 b :=  4 / c rof elihw

Enter language to be parsed...
while a != b if a > c a := a - b for c 0 55 b :=  4 / c rof elihw
Parsing...while a != b if a > c a := a - b for c 0 55 b :=  4 / c rof elihw
invalid if statement format
*/


}
