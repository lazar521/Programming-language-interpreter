import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner; 


import lexer.Lexer;
import parser.Parser;
import ast.Program;
import interpreter.Executor;
import interpreter.SemanticChecker;
import interpreter.AstPrinter;



public class Dub {    
    private AstPrinter astPrinter;
    private Executor executor;
    private SemanticChecker semanticChecker;
    private Lexer lexer;
    private Parser parser;

    private boolean printAST;
    private boolean running;
    private String testFolder = "tests";

    private static String horizontalLine = "=====================================================================";

    public Dub(){
        astPrinter = new AstPrinter();
        executor = new Executor();
        semanticChecker = new SemanticChecker();
        lexer = new Lexer();
        parser = new Parser();
        printAST = false;
        running = true;
    }



    public static void main(String[] args) {
        Dub interpreter = new Dub();
        interpreter.start();

    }


    // Loop for inputting user commands

    private void start() {
        System.out.println("\nUse 'help' to show available commands");
        Scanner scanner = new Scanner(System.in);
        String command;

        while(running){
            System.out.printf("enter command: ");
            
            command = scanner.nextLine();

            executeCommand(command);

            System.out.println(horizontalLine);
        }

        scanner.close();
    }



    // Definition of CLI commands

    public void executeCommand(String cmd){
        if(cmd.length() == 0) return;

        String[] words = cmd.split(" ");

        if(words.length == 1){
            switch(words[0]){
                case "help":
                    System.out.println("print on/off         ==>  Whether to print the abstract syntax tree after parsing or not");
                    System.out.println("run [FILE_NAME]      ==>  It looks for the file in the 'tests' folder and runs it");
                    System.out.println("run tests            ==>  Runs all the files from the 'tests' folder");
                    System.out.println("conf                 ==>  Shows current configuration");
                    System.out.println("exit                 ==>  Exits the interpreter");
                    break;
                
                case "conf":
                    System.out.println("print: " + printAST);
                    break;

                case "exit":
                    running = false;
                    break;

                default:
                    System.out.println("Invalid command. Use 'help' to show available commands");
                    break;
            }

        }
        else if(words.length == 2){
            switch(words[0]){
                case "print":
                    if(words[1].equals("on")) printAST = true;
                    else if(words[1].equals("off")) printAST = false;
                    else System.out.println("Invalid print mode. You can do 'print on' or 'print off'");
                    break;

                case "run":
                    if(words[1].equals("tests")) runTests();
                    else runFile(words[1]);
                    break;

                default:
                    System.out.println("Invalid command. Use 'help' to show available commands");
                    break;
            }
        }
        else{
            System.out.println("Invalid command. Use 'help' to show available commands");
        }

        return;
    }



    // Runs all files in the 'tests' folder

    private void runTests(){
        File folder =  new File( System.getProperty("user.dir") + File.separator + testFolder );
        
        if ( !folder.exists() || !folder.isDirectory() ){
            System.out.println("The '" + testFolder + "' folder doesn't exist");
            return;
        }

        File[] files = folder.listFiles();

        if(files == null){
            System.out.println("The '" + testFolder + "' folder is empty");
        }

        for(File file: files){
            System.out.println(horizontalLine);
            runFile(file.getName());
        }
    }


    // Runs a specific file

    private void runFile(String fileName){
        String code;
        try{
            code = loadFile( System.getProperty("user.dir") + File.separator + testFolder + File.separator + fileName);
        }
        catch(FileNotFoundException e){
            System.out.println("File '" + fileName + "' cannot be found in the '" + testFolder + "' folder");
            return;
        }

        System.out.println("\nRUNNING '" + fileName + "'");
        executeCode(code);
    }


    // Here's the workflow of our interpreter

    private void executeCode(String code){
        try{
            Program ast = parser.parseProgram( lexer.makeTokens(code) );

            if(printAST) astPrinter.printAST(ast);

            System.out.println();

            semanticChecker.checkSemantics(ast);

            Object value = executor.executeProgram(ast);;

            if(value instanceof String) System.out.println("\nFinished: The program returned: \"" + value + "\"");
            else System.out.println("\nFinished: The program returned " + value);
        }
        catch(Exception e){
            System.out.println("\nAN ERROR HAS OCCURED");
        }

    }


    // Returns the contents of a file

    private String loadFile(String path) throws FileNotFoundException {
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        StringBuilder fileContent = new StringBuilder();

        while(scanner.hasNext()){
            fileContent.append(scanner.nextLine()).append("\n");
        }
        scanner.close();

        return fileContent.toString();
    }


    
    
}