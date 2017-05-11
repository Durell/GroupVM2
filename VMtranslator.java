
/* Chapter No.7
File Name:          VMtranslator.java
Programmer:         Durell,Yunan,Ruoyu,Jiaming
Date Last Modified: May 4, 2017

Overall Plan:
//algorithm: the main method to initalize the reading the inputFile
//get commandType, arg1 and arg2 from parse class
//put those 3 into code class
//print out the corresponding commands.
//That is the main part of this project
*/

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
public class VMtranslator {

    /**
     * Return all the .vm files in a directory
     * @param dir
     * @return
     */
    public static ArrayList<File> getVMFiles(File dir){

        File[] files = dir.listFiles();

        ArrayList<File> result = new ArrayList<File>();

        for (File f:files){

            if (f.getName().endsWith(".vm")){

                result.add(f);
            }
        }
        return result;
    }

    public static void main(String[] args) {

        if (args.length != 1){

            System.out.println("Set your programing argument as the file name(.vm)");

        }else {

            File fileIn = new File(args[0]);

            String fileOutPath = "";

            File fileOut;

            AssemblyConverter writer;

            ArrayList<File> vmFiles = new ArrayList<File>();

            if (fileIn.isFile()) {

                //if it is a single file, see whether it is a vm file
                String path = fileIn.getAbsolutePath();

                if (!Parser.getExt(path).equals(".vm")) {

                    throw new IllegalArgumentException(".vm file is required!");

                }

                vmFiles.add(fileIn);

                fileOutPath = fileIn.getAbsolutePath().substring(0, fileIn.getAbsolutePath().lastIndexOf(".")) + ".asm";

            } else if (fileIn.isDirectory()) {

                //if it is a directory get all vm files under this directory
                vmFiles = getVMFiles(fileIn);

                //if no VM file are found in this directory
                if (vmFiles.size() == 0) {

                    throw new IllegalArgumentException("No vm file in this directory");

                }

                fileOutPath = fileIn.getAbsolutePath() + "/" +  fileIn.getName() + ".asm";
            }

            fileOut = new File(fileOutPath);
            writer = new AssemblyConverter(fileOut);

            for (File f : vmFiles) {

                Parser parser = new Parser(f);

                int type = -1;

                //start parsing
                while (parser.hasMoreCommands()) {

                    parser.advance();

                    type = parser.commandType();

                    if (type == Parser.ARITHMETIC) {

                        writer.writeArithmetic(parser.arg1());

                    } else if (type == Parser.POP || type == Parser.PUSH) {

                        writer.writePushPop(type, parser.arg1(), parser.arg2());
                    }
                }
            }
            //save file
            writer.close();
            System.out.println("File created and saved in : " + fileOutPath);
        }
    }

}
