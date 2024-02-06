package lexer;
import java.util.HashMap;

import token.*;
import java.util.ArrayList;


public class Lexer {
    private ArrayList<Token> tokenList;
    private int lineNumber;
    private CharacterIterator iter;

public Lexer(){}

    public ArrayList<Token>  makeTokens(String text) throws Exception {
        this.tokenList = new ArrayList<Token>();
        this.iter = new CharacterIterator(text);
        this.lineNumber = 1;

        tokenzieText();

        // Adding this EOF token at the end makes it easier to write clean code for the Parser class
        // Parser won't have to constantly check if it has reached the end of Token list. Instead it will encounter this EOF token
        // that automatically won't match any production rule
        addToken(TType.EOF);
        return tokenList;
    }


    private void tokenzieText() throws Exception {
       
        while(iter.hasCharacters()){
            char c = iter.getChar();
            iter.advance();

            switch(c){
                case '(': addToken(TType.LEFT_PAREN);break;
                case ')': addToken(TType.RIGHT_PAREN);break;
                case '{': addToken(TType.LEFT_BRACE);break;
                case '}': addToken(TType.RIGHT_BRACE);break;
                case ';': addToken(TType.SEMICOLON);break;
                case ',': addToken(TType.COMMA);break;
                case '-': addToken(TType.MINUS);break;
                case '+': addToken(TType.PLUS);break;
                case '*': addToken(TType.STAR);break;

                case '/':
                    if(match('/')) skipComments();
                    else addToken(TType.SLASH);
                    break;

                case '=':
                    if(match('=')) addToken(TType.EQUAL_EQUAL);
                    else addToken(TType.EQUAL);
                    break;

                case '!':
                    if(match('=')) addToken(TType.BANG_EQUAL);
                    else addToken(TType.BANG);
                    break;

                case '<':
                    if(match('=')) addToken(TType.LESS_EQUAL);
                    else addToken(TType.LESS);
                    break;

                case '>':
                    if(match('=')) addToken(TType.GREATER_EQUAL);
                    else addToken(TType.GREATER);
                    break;

                case ' ':
                    break;

                case '\n':
                    lineNumber++;
                    break;

                case '"':
                    tokenizeString();
                    break;

                default:
                    if(Character.isDigit(c)) tokenizeNumber();
                    else if (Character.isLetter(c) || c == '_') tokenizeIdentifier();
                    else report("Unexpected character " + c);
            }

        }
    }

    private void tokenizeIdentifier() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(iter.getPrevChar());

        char c = iter.getChar();
        while(c == '_' || Character.isLetter(c) || Character.isDigit(c)){
            sb.append(c);
            iter.advance();
            c = iter.getChar();
        }

        String word = sb.toString();
        if(isKeyword(word)) addToken(getKeywordType(word));
        else addToken(word, TType.IDENTIFIER);
    }


    private void tokenizeNumber() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(iter.getPrevChar());

        char c = iter.getChar();
        while(Character.isDigit(c)){
            sb.append(c);
            iter.advance();
            c = iter.getChar();
        }

        addToken(sb.toString(), TType.NUM_LITERAL);
    }


    private void tokenizeString() throws Exception {
        StringBuilder sb = new StringBuilder();
        char c = iter.getChar();
        while(iter.hasCharacters() && c != '"' &&  c != '\n'){
            sb.append(c);
            iter.advance();
            c = iter.getChar();
        }

        if(!iter.hasCharacters() || iter.getChar() == '\n') report("String never terminated");

        // skip the closing quotation mark
        iter.advance();

        addToken(sb.toString(), TType.STRING_LITERAL);
    }



    private void skipComments(){
        while(iter.hasCharacters() && iter.getChar() != '\n') iter.advance();
    }



    private boolean match(char c){
        if(iter.hasCharacters() && iter.getChar() == c){
            iter.advance();
            return true;
        }
        return false;
    }



    // We overload 'addToken' function to make the code more readable

    private void addToken(TType TType){
        addToken(null, TType);
    }

    // Only tokens of type IDENTIFIER, STRING_LITERAL and NUM_LITERAL will call this one
    private void addToken(String string, TType TType){
        tokenList.add(new Token(string, TType,lineNumber));
    }

    
    
    private void report(String message) throws Exception {
        System.out.println("Lexer: Line " + String.valueOf(lineNumber) + ": " + message);
        throw new Exception();
    }


    
    private static final HashMap<String, TType> keywords;

    static {
        keywords = new HashMap<String, TType>();
        keywords.put("if",       TType.IF);
        keywords.put("else",     TType.ELSE);
        keywords.put("while",    TType.WHILE);
        keywords.put("for",      TType.FOR);
        keywords.put("else",     TType.ELSE);
        keywords.put("fn",       TType.FN);
        keywords.put("return",   TType.RETURN);
        keywords.put("void",     TType.VOID);
        keywords.put("int",      TType.INT);
        keywords.put("string",   TType.STRING);        
    }


    static boolean isKeyword(String word){
        return keywords.containsKey(word);
    }

    static TType getKeywordType(String word){
        return keywords.get(word);
    }
}
