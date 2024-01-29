package parser;

import token.*;
import java.util.List;

public class Parser {
    private TokenIterator iter;


    public void parseProgram(List<Token> tokens){
        this.iter = new TokenIterator(tokens);
    }
}
