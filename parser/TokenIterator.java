package parser;

import java.util.List;
import java.util.ListIterator;
import token.*;


// With ListIterator we cannot get current element without advancing to the next one in the list with the ListIterator.next() method
// We want to be able to get current element in the list without advancing to the next one so we make a wrapper class

class TokenIterator{
    private ListIterator<Token> iter;
    private Token curr;
    private Token prev;

    TokenIterator(List<Token> tokens){
        if(tokens.size() == 0 ){
            System.out.println("\n\n ERROR: Making iterator on an empty list");
            System.exit(-1);
        }
        
        this.iter = tokens.listIterator();
        
        // iter.next() automatically advances to the next element in the list
        // which we don't want to in the constructor, so we call iter.previous()
        prev = null;
        curr = iter.next();         
    }


    public boolean hasTokens(){
        return (curr.getType() != TType.EOF);
    }


    public void advance(){
        if(!hasTokens()){
            System.out.println("\n\n ERROR: Iterator cannot advance anymore");
            System.exit(-1);
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
        Token t = iter.next();
        iter.previous();
        return t;
    }

}