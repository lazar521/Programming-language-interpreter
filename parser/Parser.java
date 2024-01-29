package parser;

import token.*;
import java.util.List;

import javax.naming.BinaryRefAddr;

import ast.*;
import interpreter.astPrinter;
import lexer.Lexer;


// The last element in the token list is of END_OF_LIST token. That way we can omit constantly checking
// if there is more tokens left in the list. Instead, the END_OF_LIST token won't match any production and the
// error will be handled like any other syntax error rule


public class Parser {
    private TokenIterator iter;


    public void setToken(List<Token> tokens){
        this.iter = new TokenIterator(tokens);
    }


    public void parseProgram(){
        
    }


    private boolean match(TType ... types){
        TType currTokenType = iter.getToken().getType();

        for (TType type : types) {
            if(type == currTokenType){
                iter.advance();
                return true;
            }
        }
        return false;
    }


    private void forceMatch(TType ... types){
        if(!match(types)){
            System.out.println("Cannot parse line " + iter.getToken().getLineNumber());
            System.exit(-1);
        }
    }

    public Expr parseExpr(){
        return parseEquality();
    }


    private Expr parseEquality(){
        Expr expr = parseComparison();

        while(match(TType.BANG_EQUAL, TType.EQUAL_EQUAL)){
            Token operation = iter.getPrevToken();
            Expr right = parseComparison();
            expr = new Expr.Binary(expr,operation,right);
        }

        return expr;
    }


    private Expr parseComparison(){
        Expr expr = parseAddition();

        while(match(TType.GREATER, TType.GREATER_EQUAL,TType.LESS,TType.LESS_EQUAL)){
            Token operation = iter.getPrevToken();
            Expr right = parseAddition();
            expr = new Expr.Binary(expr,operation,right);
        }

        return expr;
    }


    private Expr parseAddition(){
        Expr expr = parseMultiplication();

        
        while(match(TType.MINUS,TType.PLUS)){
            Token operation = iter.getPrevToken();
            Expr right = parseMultiplication();
            expr = new Expr.Binary(expr, operation, right);
        }
        
        return expr;
    }



    private Expr parseMultiplication(){
        Expr expr = parseTerm();

        while(match(TType.STAR,TType.SLASH)){
            Token operaToken = iter.getPrevToken();
            Expr right = parseTerm();
            expr = new Expr.Binary(expr, operaToken, right);
        }

        return expr;
    }


    private Expr parseTerm(){
        if(match(TType.MINUS,TType.BANG)){
            Token operation = iter.getPrevToken();
            return new Expr.Unary(operation, parsePrimary());
        }

        return parsePrimary();
    }

    
    private Expr parsePrimary(){
        if(match(TType.NUM_LITERAL,TType.STRING_LITERAL,TType.TRUE,TType.FALSE,TType.NULL)){
            return new Expr.Literal(iter.getPrevToken());
        }

        forceMatch(TType.LEFT_PAREN);
        Expr expr = parseExpr();
        forceMatch(TType.RIGHT_PAREN);

        return expr;
    }
    


}
