package parser;


import java.util.List;
import java.util.ArrayList;

import token.*;
import ast.*;


// The last element in the token list is of END_OF_LIST token. That way we can omit constantly checking
// if there is more tokens left in the list. Instead, the END_OF_LIST token won't match any production and the
// error will be handled like any other syntax error rule


public class Parser {

    private static class UnexpectedTokenException extends RuntimeException{}

    private TokenIterator iter;
    private boolean errorOccured;

    public void setToken(List<Token> tokens){
        this.iter = new TokenIterator(tokens);
    }

    public ArrayList<Stmt> parseProgram(){
        errorOccured = false;
        ArrayList<Stmt> statements = new ArrayList<Stmt>();
       
        while(iter.hasTokens()){
            statements.add(parseStmt());
        }

        if(errorOccured){
            return null;
        }

        return statements;
    }




    //=============== STATEMENTS =====================

    public Stmt parseStmt(){
        
        try{
            switch(iter.getToken().getType()){
                case FN:
                case VAR:
                    return parseDeclStmt();
                
                case WHILE:
                    return parseWhileStmt();
                
                case FOR:
                    return parseForStmt();

                case IF:
                    return parseIfStmt();
                
                case RETURN:
                    return parseRetStmt();

                default:
                    return parseExprStmt();
            }
        }
        catch(UnexpectedTokenException exc){
            synchronize();
            return null;
        }
    }


    private Stmt parseExprStmt(){
        Expr expr;
        Token identifier = null;

        if(iter.getNextToken().getType() == TType.EQUAL){
            forceMatch(TType.IDENTIFIER);
            identifier = iter.getPrevToken();
            forceMatch(TType.EQUAL);
        }

        expr = parseExpr();
        forceMatch(TType.SEMICOLON);

        return new Stmt.ExprStmt(identifier,expr);
    }


    private Stmt parseDeclStmt(){
        Decl declaration;

        if(match(TType.VAR)){
            declaration = parseVarDecl();
            forceMatch(TType.SEMICOLON);
        }
        else{
            forceMatch(TType.FN);
            declaration = parseFuncDecl();
        }

        return new Stmt.DeclStmt(declaration);
    }


    private Stmt parseWhileStmt(){
        forceMatch(TType.WHILE);
        
        forceMatch(TType.LEFT_PAREN);
        Expr condition = parseExpr();
        forceMatch(TType.RIGHT_PAREN);

        forceMatch(TType.LEFT_BRACE);

        ArrayList<Stmt> statements = new ArrayList<Stmt>();
        while( !match(TType.RIGHT_BRACE) ){
            statements.add(parseStmt());
        }

        return new Stmt.While(condition, statements);
    }


    private Stmt parseForStmt(){
        forceMatch(TType.FOR);
        forceMatch(TType.LEFT_PAREN);

        Decl.Var varDecl = null;
        Expr condition = null;
        Expr update = null;

        if( !match(TType.SEMICOLON) ){
            varDecl = parseVarDecl();
            forceMatch(TType.SEMICOLON);
        }

        if( !match(TType.SEMICOLON) ){
            condition = parseExpr();
            forceMatch(TType.SEMICOLON);
        }

        if( !match(TType.RIGHT_PAREN) ){
            update = parseExpr();
            forceMatch(TType.RIGHT_PAREN);
        }

        forceMatch(TType.LEFT_BRACE);

        ArrayList<Stmt> statements = new ArrayList<Stmt>();
        while( !match(TType.RIGHT_BRACE) ){
            statements.add(parseStmt());
        }

        return new Stmt.For(varDecl, condition, update,statements);
    }


    private Stmt parseIfStmt(){
        forceMatch(TType.IF);
        
        forceMatch(TType.LEFT_PAREN);
        Expr condition = parseExpr();
        forceMatch(TType.RIGHT_PAREN);

        forceMatch(TType.LEFT_BRACE);
        ArrayList<Stmt> statements = new ArrayList<Stmt>();
        while( !match(TType.RIGHT_BRACE) ){
            statements.add(parseStmt());
        }

        return new Stmt.If(condition, statements);
    }


    private Stmt parseRetStmt(){
        forceMatch(TType.RETURN);
        Expr expr = parseExpr();
        forceMatch(TType.SEMICOLON);

        return new Stmt.Ret(expr);
    }



    
    
    //============= DECLARATIONS ===================

    private Decl.Var parseVarDecl(){
        forceMatch(TType.IDENTIFIER,TType.TYPE_INT,TType.TYPE_STR);
        Token type = iter.getPrevToken();

        forceMatch(TType.IDENTIFIER);
        Token identifier = iter.getPrevToken();

        Expr expr = null;
        if(match(TType.EQUAL)){
            expr = parseExpr();
        }

        return new Decl.Var(type,identifier,expr);
    }


    private Decl parseFuncDecl(){
        forceMatch(TType.IDENTIFIER,TType.TYPE_INT,TType.TYPE_STR,TType.TYPE_VOID);
        Token type = iter.getPrevToken();

        forceMatch(TType.IDENTIFIER);
        Token identifier = iter.getPrevToken();
        
        forceMatch(TType.LEFT_PAREN);

        ArrayList<Decl.Param> parameters = new ArrayList<Decl.Param>();
        if( !match(TType.RIGHT_PAREN) ){
            parameters.add(parseParamDecl());
        
            while( !match(TType.RIGHT_PAREN) ){
                forceMatch(TType.COMMA);
                parameters.add(parseParamDecl());
            }
        }

        forceMatch(TType.LEFT_BRACE);

        ArrayList<Stmt> statements = new ArrayList<Stmt>();
        while( !match(TType.RIGHT_BRACE)){
            statements.add(parseStmt());
        }
    
        return new Decl.Func(type, identifier, parameters,statements);
    }


    private Decl.Param parseParamDecl(){
        forceMatch(TType.IDENTIFIER,TType.TYPE_INT,TType.TYPE_STR);
        Token type = iter.getPrevToken();

        forceMatch(TType.IDENTIFIER);
        Token identifier = iter.getPrevToken();

        return new Decl.Param(type, identifier);
    }





    //============= EXPRESSIONS ===============

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
        if(match(TType.IDENTIFIER,TType.NUM_LITERAL,TType.STRING_LITERAL,TType.TRUE,TType.FALSE,TType.NULL)){
            return new Expr.Literal(iter.getPrevToken());
        }

        forceMatch(TType.LEFT_PAREN);
        Expr expr = parseExpr();
        forceMatch(TType.RIGHT_PAREN);

        return expr;
    }
    




    //================ UTLITY =====================

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
            errorOccured = true;
            System.out.println("Unexpected token '" + iter.getToken()+"' at line " + iter.getToken().getLineNumber());
            throw new UnexpectedTokenException();
        }
    }


    // Synchronization is a type of error recovery when encountering a token that cannot match a rule
    // We could, in theory, ignore just this one token and continue parsing
    // The problem is which rule should we continue parsing at? And even worse which part of the rule?  
    
    // Let's say we just omit this one token and continue parsing where we left off 
    // What if we immediately encouter even more unexpected tokens?
    // Keep in mind that there is an infinite number of combinations in which these tokens could appear  
    // Also, even one badly placed token can cause a cascade of error detections after it that aren't even real errors
    // Suddenly, we don't know what's going on anymore and if we're even matching the right rule
    
    // We could report just this one error and quit parsing entirely
    // A better way to handle this would be to try to detect as many of these errors as possible in one passing
    // A possible solution to this problem would be that we limit the scopes in which these unexpected tokens
    // can cause cascade of fake errors
    
    // For example, if we're matching some statement rule and we encounter an error, we can just
    // report that error and omit that particular statement altogether and continue parsing the next statement
    
    // The way we accomplish this is by throwing away all the tokens until we find one that occurs at the start of some statement rule
    // That way we can skip the tokens that would be considered part of the bad previous statement and continue parsing the next one

    private void synchronize(){
        iter.advance();

        while(iter.hasTokens()){
            
            switch(iter.getToken().getType()){
                case IF:
                case WHILE:
                case FOR:
                case RETURN:
                case FN:
                case VAR:
                    return;

                case SEMICOLON:
                    iter.advance();
                    return;

                default:
                    break;
            }

            iter.advance();
        }
    }


}
