import java.io.File;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.List;
import java.util.ListIterator;

import lexer.Lexer;
import parser.Parser;
import token.*;

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
        String text = loadFile(System.getProperty("user.dir") + "/test2.txt");
        Lexer lexer = new Lexer();
     //   Parser parser = new Parser();

        List<Token> tokens = lexer.makeTokens(text);
      //  parser.parseTokens(list);

        ListIterator<Token> iter = tokens.listIterator();

        while(iter.hasNext()){
            System.out.println(iter.next());
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