/* Chapter No.7
   File Name:          Parser.java
   Programmer:         Durell,Yunan,Ruoyu,Jiaming
   Date Last Modified: May 4, 2017 
*/
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Parser {
	
	//	constant variables
	public static final String DEF_ARITHMETHIC = "add sub neg eq gt lt and or not";
    private String currentCmd;
    public static final int ARITHMETIC = 0;
    public static final int PUSH = 1;
    public static final int POP = 2;
    public static final int LABEL = 3;
    public static final int GOTO = 4;
    public static final int IF = 5;
    public static final int FUNCTION = 6;
    public static final int RETURN = 7;
    public static final int CALL = 8;
    
    //	instance variables
    private int argType;
    private String arg1;
    private int arg2;
	private String fileName = "";
	Scanner inputStream;
	
	//	default constructor
	public Parser(File fileName)
	{
		try {
			inputStream = new Scanner(new FileInputStream(fileName));
            String preprocessed = "";
            String line = "";
            while(inputStream.hasNext()){
                line = noComments(inputStream.nextLine()).trim();
                if (line.length() > 0) {
                    preprocessed += line + "\n";
                }
            }
            inputStream = new Scanner(preprocessed.trim());
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find the file");		
		}
	}
	
	//DESCRIPTION: to check it has more commands or not.
	//PRECONDITION: N/A
	//POSTCONDITION: returns true for more commands, or return false for no more commands.
	public boolean hasMoreCommands()
	{
		return inputStream.hasNextLine();
	}
	
	//DESCRIPTION: processing the commands
	//PRECONDITION: make sure the called this method after hasMoreCommands();
	//POSTCONDITION: will stored the current command information.
	public void advance()
	{
		arg1 = "";
		arg2 = -1;
		currentCmd = "";
		
		currentCmd = inputStream.nextLine();
		String[] line = currentCmd.split(" ");
		
		if(line.length > 3)
		{
			  throw new IllegalArgumentException("Too much arguments!");
		}
		
		if(DEF_ARITHMETHIC.indexOf(line[0]) != -1)
		{
			argType = 0;
			arg1 = line[0];
		}
		else if(line[0].equals("return"))
		{
			argType = 8;
			arg1= line[0];
		}
		else 
		{
			arg1 = line[1];
			switch(line[0])
			{
			case "push":
				argType = PUSH;
				break;
			case "pop":
				argType = POP;
				break;
			case "label":
				argType = LABEL;
				break;
			case "goto":
				argType = GOTO;
				break;
			case "if-goto":
				argType = IF;
				break;
			case "function":
				argType = FUNCTION;
				break;
			case "call":
				argType = CALL;
				break;
			default:
				throw new IllegalArgumentException("Unknown Command Type!");
				//	might add a default case for throw Exception TODO
			}
			if (argType == PUSH || argType == POP || argType == FUNCTION || argType == CALL){
                try {
                    arg2 = Integer.parseInt(line[2]);
                }catch (Exception e){
                    throw new IllegalArgumentException("Argument2 is not an integer!");
                }

            }
        }
	}
	
	
	//DESCRIPTION: set the fileName
	//PRECONDITION: n/a
	//POSTCONDITION: n/a
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	//DESCRIPTION: get the fileName
	//PRECONDITION: n/a
	//POSTCONDITION: return the name of the file. return null for no file name
	public String getFileName()
	{
		return this.fileName;
	}
	
	//DESCRIPTION: get the command type
	//PRECONDITION: call this method after advance();
	//POSTCONDITION: return the int represent the type.
	public int commandType()
	{
		if (argType != -1) {
            return argType;
        }else {
            throw new IllegalStateException("No command!");
        }
	}
	//DESCRIPTION: get the command argument 1 for local/static/argument/constant
	//PRECONDITION: call this method after advance();
	//POSTCONDITION: return the a string to telling us is local or static or argument or constant
	public String arg1()
	{
		return this.arg1;
	}
	//DESCRIPTION: get the command argument 2 for the number
	//PRECONDITION: call this method after advance();
	//POSTCONDITION: return the integer number.
	public int arg2()
	{
		return this.arg2;
	}
	
	//DESCRIPTION: clean the comments for string
	//PRECONDITION: n/a
	//POSTCONDITION: return a no comments line.
	public static String noComments(String line){
        int position = line.indexOf("//");
        if (position != -1){
            line = line.substring(0, position);
        }
        return line;	
	}
	
	//DESCRIPTION: clean the comments for string
	//PRECONDITION: n/a
	//POSTCONDITION: return a no space line.
	public static String noSpaces(String strIn){
        String result = "";
        if (strIn.length() != 0){
            String[] segs = strIn.split(" ");
            for (String s: segs){
                result += s;
            }
        }
        return result;
    }
	
	//DESCRIPTION: return the fileName without file type
	//PRECONDITION: n/a
	//POSTCONDITION: return empty string for no file.
    public static String getExt(String fileName){
        int index = fileName.lastIndexOf('.');
        if (index != -1){
            return fileName.substring(index);
        }else {
            return "";
        }
    }

}