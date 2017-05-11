/* Chapter No.7
   File Name:          AssemblyConverter.java
   Programmer:         Durell,Yunan,Ruoyu,Jiaming
   Date Last Modified: May 4, 2017
 
   Overall Plan:
   
   DATA DICTIONARY
   ---------------
   NAME	TYPE		VALUE RANGE	DESCRIPTION
   ======== ===========    ===============================
   jumpCounter  int     	counter for the jump command
   outPrinter   PrintWriter	ourput
    
*/


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class AssemblyConverter {

	private int jumpCounter;
	private PrintWriter outPrinter;
	int temp1, temp2, countCall;
	String funcName,
				 returnAddress = "return";

	/**
	 * Open an output file and be ready to write content
	 * 
	 * @param fileOut
	 *            can be a directory!
	 *description: opens output file/stream and prepares to parse
	 *precondition: provided file is VM file
	 *postcondition: if file can't be opened, ends program with error message
	 */
	public AssemblyConverter(File fileOut) {

		try {

			outPrinter = new PrintWriter(fileOut);
			jumpCounter = 0;

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		}

	}

	/**
	 * “If the programs argument is a directory name rather than a file
	 * name, the main program should process all the .vm files in this
	 * directory. In doing so, it should use a separate Parser for handling each
	 * input file and a single AssemblyConverter for handling the output.
	 *
	 * Inform the CodeWrither that the translation of a new VM file is started
	 */
	public void setFileName(File fileOut) {

	}

	/**
	 * Write the assembly code that is the translation of the given arithmetic
	 * command
	 * 
	 * @param command
	 */
	public void writeArithmetic(String command) {

		switch (command) {
		case ("add"):
			outPrinter.print(arithmeticTemplate() + "M=M+D\n@SP\nM=M+1\n");
			break;

		case ("sub"):
			outPrinter.print(arithmeticTemplate() + "M=M-D\n@SP\nM=M+1\n");
			break;

		case ("and"):
			outPrinter.print(arithmeticTemplate() + "M=M&D\n@SP\nM=M+1\n");
			break;

		case ("or"):
			outPrinter.print(arithmeticTemplate() + "M=M|D\n@SP\nM=M+1\n");
			break;

		case ("gt"):
			outPrinter.print(compareTemplate("JLE"));// not <=
			jumpCounter++;

			break;
		case ("lt"):
			outPrinter.print(compareTemplate("JGE"));// not >=
			jumpCounter++;
			break;

		case ("eq"):
			outPrinter.print(compareTemplate("JNE"));// not <>
			jumpCounter++;

			break;
		case ("not"):
			outPrinter.print("@SP\nA=M\nM=!M\n");
			break;

		case ("neg"):
			outPrinter.print("D=0\n@SP\nA=M\nM=D-M\n");

			break;
		default:
			throw new IllegalArgumentException("Non-arithmetic command exception");
		}
	}

	/**
	 * Write the assembly code that is the translation of the given command
	 * where the command is either PUSH or POP
	 * 
	 * @param command   PUSH or POP
	 * @param segment
	 * @param index
	 */
	public void writePushPop(int command, String segment, int index) {

		if (command == Parser.PUSH) {

			if (segment.equals("constant")) {

				outPrinter.print("@" + index + "\n" + "D=A\n@SP\nAM=M-1\nM=D\n");

			} else if (segment.equals("local")) {

				outPrinter.print(pushTemplate("LCL", index, false));

			} else if (segment.equals("argument")) {

				outPrinter.print(pushTemplate("ARG", index, false));

			} else if (segment.equals("this")) {

				outPrinter.print(pushTemplate("THIS", index, false));

			} else if (segment.equals("that")) {

				outPrinter.print(pushTemplate("THAT", index, false));

			} else if (segment.equals("temp")) {

				outPrinter.print(pushTemplate("R5", index + 5, false));

			} else if (segment.equals("pointer") && index == 0) {

				outPrinter.print(pushTemplate("THIS", index, true));

			} else if (segment.equals("pointer") && index == 1) {

				outPrinter.print(pushTemplate("THAT", index, true));

			} else if (segment.equals("static")) {

				outPrinter.print(pushTemplate(String.valueOf(16 + index), index, true));

			}

		} else if (command == Parser.POP) {

			if (segment.equals("local")) {

				outPrinter.print(popTemplate("LCL", index, false));

			} else if (segment.equals("argument")) {

				outPrinter.print(popTemplate("ARG", index, false));

			} else if (segment.equals("this")) {

				outPrinter.print(popTemplate("THIS", index, false));

			} else if (segment.equals("that")) {

				outPrinter.print(popTemplate("THAT", index, false));

			} else if (segment.equals("temp")) {

				outPrinter.print(popTemplate("R5", index + 5, false));

			} else if (segment.equals("pointer") && index == 0) {

				outPrinter.print(popTemplate("THIS", index, true));

			} else if (segment.equals("pointer") && index == 1) {

				outPrinter.print(popTemplate("THAT", index, true));

			} else if (segment.equals("static")) {

				outPrinter.print(popTemplate(String.valueOf(16 + index), index, true));

			}

		} else {

			throw new IllegalArgumentException("Non-pushpop command exception");

		}

	}

	/**
	 * Close the output file
	 */
	public void close() {

		outPrinter.close();

	}

	/**
	 * Template for add sub and or
	 * 
	 * @return
	 */
	private String arithmeticTemplate() { // change the method name

		return "@SP\n" + "A=M\n" +
				"D=M\n" + "A=A+1\n";
	}

	/**
	 * Template for gt lt eq
	 * 
	 * @param type JLE JGT JEQ
	 * @return
	 */
	private String compareTemplate(String type) {// change the method name

		return 
				"@SP\n" + 
				"A=M\n" + 
				"D=M\n" + 
				"A=A+1\n" +
				"D=M-D\n" +
				"@FALSE" + jumpCounter + "\n" + 
				"D;" + type+ "\n" + 
				"@SP\n" + 
				"A=M+1\n"
				+ "M=-1\n"
				+ "@CONTINUE" + jumpCounter + "\n" 
				+ "0;JMP\n" 
				+ "(FALSE"+ jumpCounter + ")\n"
				+ "@SP\n" +
				"A=M+1\n" + 
				"M=0\n" + 
				"(CONTINUE" + jumpCounter + ")\n" + 
				"@SP\n" + 
				"M=M+1\n";

	}

	/**
	 * Template for push local,this,that,argument,temp,pointer,static
	 * 
	 * @param segment
	 * @param index
	 * @param isDirect
	 *            Is this command a direct addressing?
	 * @return
	 */
	private String pushTemplate(String segment, int index, boolean isDirect) {

		// When it is a pointer, just read the data stored in THIS or THAT
		// When it is static, just read the data stored in that address
		String noPointerCode = (isDirect) ? "" : "@" + index + "\n" + "A=D+A\nD=M\n";// look
																						// down
																						// in
																						// return
		return 
				"@" + segment + "\n" + 
				"D=M\n" + 
				noPointerCode + "@SP\n" + // next																		// available																		// index
				"AM=M-1\n" + 
				"M=D\n";
	}

	/**
	 * Template for pop local,this,that,argument,temp,pointer,static
	 * 
	 * @param segment
	 * @param index
	 * @param isDirect
	 *            Is this command a direct addressing?
	 * @return
	 */
	private String popTemplate(String segment, int index, boolean isDirect) {

		// When it is a pointer R13 will store the address of THIS or THAT
		// When it is a static R13 will store the index address
		String noPointerCode = (isDirect) ? "D=A\n" : "D=M\n@" + index + "\nD=D+A\n";

		return "@" + segment + "\n" + 
				noPointerCode +
				"@R13\n" + 
				"M=D\n" 
				+ "@SP\n" 
				+ "A=M\n" 
				+ "D=M\n" 
				+ "@R13\n"
				+ "A=M\n" 
				+ "M=D\n"
				+ "@SP\n"
				+ "M=M+1\n";

	}

	public String writeInit() {
			return "@2048\n" +
						 "D = A\n" +
						 "@SP\n" +
						 "M = D\n" +
						 this.writeCall("Sys.init", 0);
	}

	public String writeLabel(String label) {
			return "(" + funcName + "$" + label + ")\n");
	}

	public String writeGoto(String label) { 
			return "@" + funcName + "$" + label + ")\n"
						 "0;JMP\n";

	}

	public String writeIf(String label) { 
		return "@SP\n" +
					 "M =  M + 1\n" +
					 "A = M\n"
					 "D = M\n"
					 "@" + funcName + "$" + label + "\n" +
					 "D;JNE\n";
	}

	public String writeCall(String functionName, int numArgs) {
		String retString = 
				// push return address
				"@" + functionName + "return" + Integer.toString(countCall) + "\n" +
				"D = A\n" + 
				this.pushTemplate();
				//push LCL
			 	"@LCL\n" +
			 	"D = M\n" +
				this.pushTemplate() +
				//push ARG
				"@ARG\n" +
				"D = M\n" +
				this.pushTemplate() +
				//push THIS
				"@THIS\n" +
				"D = M\n" + 
				this.pushTemplate() +
				//push THAT
				"@THAT\n" +
				"D = M\n" +
				this.pushTemplate() +
				//Set ARG = SP + nargs + 5
				"@SP\n" +
				"D = M\n" +
				"@" + Integer.toString(5 + numArgs) + "\n" +
				"D = D + A\n" +
				"@ARG\n" +
				"M = D\n" +
				//Set LCL = SP
				"@SP\n" +
				"D = M\n" +
				"@LCL\n" +
				"M = D\n" +
				//go to function
				"@" + functionName + "\n" +
				"0;JMP" +
				//set return address label
				"(" + functionName + "return" + Integer.toString(countCall) + ")";
				countCall++;
				return retString;
	}

	public String writeReturn() {
		//store address in LCL to a temp location called FRAME
		"@LCL\n" +
		"D = M\n" +
		"@FRAME\n" +
		"M = D\n" +
		"@5\n" +
		"D = D - A\n" + //D = FRAME - 5
		"A = D\n" + //A = FRAME - 5
		"D = M\n" + //D = Mem[FRAME - 5]
		"@RET\n" +
		"M = D\n" + //Mem[RET] = Mem[FRAME - 5]
		this.popTemplate(); //pops return value and stores in register D
		"@ARG\n" +
		"A = M\n" + //A = Mem[ARG]
		"M = D\n" + //memory contains return value
		//set SP = ARG + 1
		"@ARG\n" +
		"D = M\n" +
		"@SP\n" +
		"M = D + 1\n" +
		//restore THAT of caller
		"@FRAME\n" +
		"M = M - 1\n" +
		"D = M\n" +
		"A = D\n" +
		"D = M\n" +
		"@THAT\n" +
		"M = D\n" +
		//restore THIS of caller
		"@FRAME\n" +
		"M = M - 1\n" +
		"D = M\n" +
		"A = D\n" +
		"D = M\n" +
		"@THIS\n" +
		"M = D\n" +
		//restore ARG of caller
		"@FRAME\n" +
		"M = M - 1\n" +
		"D = M\n" +
		"A = D\n" +
		"D = M\n" +
		"@ARG\n" +
		"M = D\n" +
		//restore LCL of caller
		"@FRAME\n" +
		"M = M - 1\n" +
		"D = M\n" +
		"A = D\n" +
		"D = M\n" +
		"@LCL\n" +
		"M = D\n" +
		//go to return address
		"@RET\n" +
		"A = M\n" +
		"0;JMP\n";
	}
	
	public String writeFunction(String functionName, int numLocals) {
		funcName = functionName;
		//declare label for function entry
		String retString = "(" + funcName + ")\n"; 
		//check if there are any local variables, if so set D register to 0
		if (numLocals != 0) {
			retString += "D = 0\n";
		}
		//if there are k local variables, push 0 onto stack k times
		for (int i = numLocals; i != 0; i--) {
			retString += this.pushCommand();
		}
		return retString;
	}
	
	public void Close() throws IOException {
		fw.close();
	}
	
	public void setStaticSubString(String test) {
		staticSubString = test.replace(".vm", ".");
	}

}
}
