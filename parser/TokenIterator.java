package parser;

import java.util.List;
import java.util.ListIterator;
import token.*;


// We just define a custom iterator that can peek at previous, current and next element in the list

class TokenIterator{
    private ListIterator<Token> iter;
    private Token curr;
    private Token prev;

    TokenIterator(List<Token> tokens){
        this.iter = tokens.listIterator();
        
        prev = null;
        curr = iter.next();         
    }


    public boolean hasTokens(){
        return (curr.getType() != TType.EOF);
    }


    public void advance(){
        if(!hasTokens()){
            internalError("advance: Iterator cannot advance anymore");
        }
        prev = curr;
        curr = iter.next();
    }

    public Token getToken(){
        return curr;
    }

    public Token getPrevToken(){

        return prev;
    }

    public Token getNextToken(){
        if(!hasTokens()){
            internalError("getNextToken: No more tokens");
        }
        Token t = iter.next();
        iter.previous();
        return t;
    }


    private static void internalError(String message){
        System.out.println("Internal error: TokenIterator." + message);
    }

}