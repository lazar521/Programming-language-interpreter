import java.io.File;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.List;


import lexer.Lexer;
import parser.Parser;
import token.*;
import ast.Stmt;
import interpreter.astPrinter;


public class Main {
    public static void main(String[] args) {
        try {
            startInterpreter();
        } catch (Exception e) {
            System.out.println("\nAN ERROR HAS OCCURRED\n");
            System.out.println(e.getMessage());
        }

        System.out.println("\nINTERPRETER STOPPED\n");
    }



    private static void startInterpreter() throws Exception {
        String text = loadFile(System.getProperty("user.dir") + "/test.txt");
        
        Lexer lexer = new Lexer();

        List<Token> tokens = lexer.makeTokens(text);

        Parser parser = new Parser();
        parser.setToken(tokens);

        List<Stmt> stmts = parser.parseProgram();
        if(stmts == null) return;

        astPrinter ap = new astPrinter();
        for(Stmt s: stmts){
            s.execute(ap);
        }

        // System.out.println("\n\n");
        // ListIterator<Token> iter = tokens.listIterator();
        // while(iter.hasNext()){
        //     System.out.println(iter.next());
        // }

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