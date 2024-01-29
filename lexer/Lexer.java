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
                case '[': addToken(TType.LEFT_BRACKET);break;
                case ']': addToken(TType.RIGHT_BRACKET);break;
                case ';': addToken(TType.SEMICOLON);break;
                case '.': addToken(TType.DOT);break;
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
                    else error("Unexpected character " + c);
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

        if(!iter.hasCharacters() || iter.getChar() == '\n') error("String never terminated");

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

    private void addToken(TType TType){
        addToken(null, TType);
    }

    private void addToken(String value, TType TType){
        tokenList.add(new Token(value, TType,lineNumber));
    }

    private void error(String message) throws Exception {
        throw new Exception("Lexer: Line " + String.valueOf(lineNumber) + ": " + message);
    }


    
    private static final HashMap<String, TType> keywords;

    static {
        keywords = new HashMap<String, TType>();
        keywords.put("if",       TType.IF);
        keywords.put("else",     TType.ELSE);
        keywords.put("while",    TType.WHILE);
        keywords.put("for",      TType.FOR);
        keywords.put("class",    TType.CLASS);
        keywords.put("def",      TType.DEF);
        keywords.put("null",     TType.NULL);
        keywords.put("or",       TType.OR);
        keywords.put("and",      TType.AND);
        keywords.put("this",     TType.THIS);
        keywords.put("super",    TType.SUPER );
        keywords.put("return",   TType.RETURN);
        keywords.put("true",     TType.TRUE);
        keywords.put("false",    TType.FALSE);
        keywords.put("void",     TType.TYPE_VOID);
        keywords.put("int",      TType.TYPE_INT);
        keywords.put("string",   TType.TYPE_STR);
        
    }


    static boolean isKeyword(String word){
        return keywords.containsKey(word);
    }

    static TType getKeywordType(String word){
        return keywords.get(word);
    }
}
