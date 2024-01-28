package parser;

import token.*;

public class Parser {
    private TokenListIterator iter;

    public void parseTokens(TokenList tokens){
        this.iter = new TokenListIterator(tokens);
    }
}
