import java.io.File;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.List;


import lexer.Lexer;
import parser.Parser;
import token.*;
import ast.Program;
import interpreter.Executor;
import interpreter.SemanticChecker;
import interpreter.AstPrinter;


public class Interpreter {
    
    private AstPrinter treePrinter;
    private Executor codeExecutor;
    private SemanticChecker semanticChecker;
    private Lexer lexer;
    private Parser parser;


    public Interpreter(){
        treePrinter = new AstPrinter();
        codeExecutor = new Executor();
        semanticChecker = new SemanticChecker();
        lexer = new Lexer();
        parser = new Parser();
    }



    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        try {
            interpreter.start();
            System.out.println("\nFINISHED SUCCESFULLY\n");
        } catch (Exception e) {
            System.out.println("\nAN ERROR HAS OCCURRED\n");
            System.out.println(e.getMessage());
        }

    }



    private void start() throws Exception {
        String text = loadFile(System.getProperty("user.dir") + "/test.c");
        
        List<Token> tokens = lexer.makeTokens(text);
        Program program = parser.parseProgram(tokens);
        
        if(program == null){
            System.out.println("Syntax error. Cannot continue");
            return;
        }

        
        program.accept(treePrinter);

        System.out.println("CHECKING SEMANTICS");
        if( semanticChecker.checkSemantics(program) ){
            System.out.println("\n\nEXECUTING CODE");
            String val = (String) program.accept(codeExecutor);
            System.out.println("The program returned ===> " + val);
        }
        else{
            System.out.println("Cannot execute. Semantic errors in code");
            return;
        }
    }



    private static String loadFile(String path) throws FileNotFoundException {
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