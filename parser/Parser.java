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


    public Program parseProgram(List<Token> tokens){
        this.iter = new TokenIterator(tokens);

        errorOccured = false;
        ArrayList<Stmt> statements = new ArrayList<Stmt>();
       
        while(iter.hasTokens()){
            Token token = iter.getToken();
            
            if(token.getType() != TType.FN && token.getType() != TType.VAR){
                System.out.println("Parser::ParseProgram: Token "+token+" doesn't match any declaration rule");
                System.exit(0);
            }

            statements.add(parseDeclStmt());
        }

        if(errorOccured){
            return null;
        }

        return new Program(statements);
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
        // Literal value
        if(match(TType.NUM_LITERAL,TType.STRING_LITERAL,TType.TRUE,TType.FALSE,TType.NULL)){
            return new Expr.Literal(iter.getPrevToken());
        }

        // Enclosed expression
        if(match(TType.LEFT_PAREN)){
            Expr expr = parseExpr();
            forceMatch(TType.RIGHT_PAREN);
            return expr;
        }

        forceMatch(TType.IDENTIFIER);
        Token identifier = iter.getPrevToken();
        
        // Identifier
        if( !match(TType.LEFT_PAREN) ){
            return new Expr.Variable(identifier);
        }

        // Function call
        ArrayList<Expr> args = new ArrayList<Expr>();
        if( !match(TType.RIGHT_PAREN) ){
            args.add(parseExpr());
            
            while( !match(TType.RIGHT_PAREN) ){
                forceMatch(TType.COMMA);
                args.add(parseExpr());
            }
        }

        return new Expr.Call(identifier, args);
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


    // Reports and throws an error if we cannot match, so that we can handle the unexpected token properly
    private void forceMatch(TType ... types){
        if(!match(types)){
            errorOccured = true;
            System.out.println("Unexpected token '" + iter.getToken()+"' at line " + iter.getToken().getLineNumber());
            throw new UnexpectedTokenException();
        }
    }


    // If we get a syntax error, we stop parsing that particular statement altogether and continue  
    // parsing the next one. We achieve this by skipping tokens until we reach one that matches the
    // start of some statement rule. That way we can report more than one parsing errors and also avoid
    // detecting cascade of false error detections caused by a bad token in a statement.
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
