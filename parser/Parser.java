package parser;


import java.util.List;
import java.util.ArrayList;

import token.*;
import ast.*;


// The last element in the token list is of EOF token. We don't have to constantly check if we've reached the end of the token list
// Instead, the EOF token won't match any production rule and the error will be automaticlly handled like any other

// Every parsing function in the Parser class corresponds to exactly one production rule from the language syntax specification

public class Parser {

    private static class UnexpectedTokenException extends RuntimeException{}

    private TokenIterator iter;
    private boolean ERROR_OCCURED;
    


    // PRODUCTION RULE:
    // program -> declStmt*

    public Program parseProgram(List<Token> tokens) throws Exception{
        if(tokens.size() == 0) throw new Exception();
        
        this.iter = new TokenIterator(tokens);

        ERROR_OCCURED = false;

        // funcDeclarations is a list of all function declaration statements
        ArrayList<Stmt.DeclStmt> funcDeclarations = new ArrayList<Stmt.DeclStmt>();
        // varDeclarations is list of all variable declaration statements that aren't inside any code block. Those are considered to be global variables
        ArrayList<Stmt.DeclStmt>  varDeclarations = new ArrayList<Stmt.DeclStmt>();

        while(iter.hasTokens()){
            // Try to parse a declaration statement
            // If we encounter an unexpected token, we throw UnexpectedTokenException and catch it here so that we can handle it properly
            try{            
                Stmt.DeclStmt statement = parseDeclStmt();

                // We separate function declarations statements from variable declaration statements 
                if(statement.declaration instanceof Decl.Func) funcDeclarations.add(statement);
                else if(statement.declaration instanceof Decl.Var) varDeclarations.add(statement);
                else internalError("parseProgram: An invalid declaration statement");
                
            }
            catch(UnexpectedTokenException e){
                synchronize();
            }

        }

        if(ERROR_OCCURED){
            throw new Exception();
        }

        return new Program(funcDeclarations,varDeclarations);
    }




    
    //=============== STATEMENTS =====================


    // PRODUCTION RULE:
    // stmt -> exprStmt | declStmt | whileStmt | forStmt | ifStmt | retStmt 

    public Stmt parseStmt(){
        
        // Try to parse a statement
        // If we encounter an unexpected token, we throw UnexpectedTokenException and catch it here so that we can handle it properly
        try{
            switch(iter.getToken().getType()){
                case FN:
                case INT:
                case STRING:
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


    // PRODUCTION RULE:
    // exprStmt -> expression ';'                                                      

    private Stmt parseExprStmt(){
        Expr expr;
        int lineNumber = iter.getToken().getLineNumber();

        expr = parseExpr();
        forceMatch(TType.SEMICOLON);

        return new Stmt.ExprStmt(expr,lineNumber);
    }


    // PRODUCTION RULE:
    // declStmt -> ( varDecl ';' | 'fn' funcDecl  )                             

    private Stmt.DeclStmt parseDeclStmt(){
        int lineNumber = iter.getToken().getLineNumber();
        Decl declaration;

        Token token = iter.getToken();
        if(token.getType() == TType.INT || token.getType() == TType.STRING){
            declaration = parseVarDecl();
            forceMatch(TType.SEMICOLON);
        }
        else{
            forceMatch(TType.FN);
            declaration = parseFuncDecl();
        }

        return new Stmt.DeclStmt(declaration,lineNumber);
    }


    // PRODUCTION RULE:
    // whileStmt -> 'while' '(' expression ')' '{' stmt* '}'

    private Stmt.While parseWhileStmt(){
        int lineNumber = iter.getToken().getLineNumber();

        forceMatch(TType.WHILE);
        
        forceMatch(TType.LEFT_PAREN);
        Expr condition = parseExpr();
        forceMatch(TType.RIGHT_PAREN);

        forceMatch(TType.LEFT_BRACE);

        ArrayList<Stmt> statements = new ArrayList<Stmt>();
        while( !match(TType.RIGHT_BRACE) ){
            statements.add(parseStmt());
        }

        return new Stmt.While(condition, statements,lineNumber);
    }


    // PRODUCTION RULE:
    // forStmt -> for '(' varDecl? ';' expression? ';' expression? ')' '{' stmt* '}'

    private Stmt.For parseForStmt(){
        int lineNumber = iter.getToken().getLineNumber();

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

        return new Stmt.For(varDecl, condition, update,statements,lineNumber);
    }



    // PRODUCTION RULE:
    // ifStmt -> 'if' '(' expr ')' '{' stmt* '}'  

    private Stmt.If parseIfStmt(){
        int lineNumber = iter.getToken().getLineNumber();

        forceMatch(TType.IF);
        
        forceMatch(TType.LEFT_PAREN);
        Expr condition = parseExpr();
        forceMatch(TType.RIGHT_PAREN);

        forceMatch(TType.LEFT_BRACE);
        ArrayList<Stmt> statements = new ArrayList<Stmt>();
        while( !match(TType.RIGHT_BRACE) ){
            statements.add(parseStmt());
        }

        ArrayList<Stmt> elseStatements = null;
        if(match(TType.ELSE)){
            forceMatch(TType.LEFT_BRACE);
            elseStatements = new ArrayList<Stmt>();
            while( !match(TType.RIGHT_BRACE) ){
                elseStatements.add(parseStmt());
            }
        }

        return new Stmt.If(condition, statements,elseStatements,lineNumber);
    }


    // PRODUCTION RULE:
    // retStmt -> 'return' expression? ';'

    private Stmt.Ret parseRetStmt(){
        int lineNumber = iter.getToken().getLineNumber();

        forceMatch(TType.RETURN);

        Expr expr = null;
        if( !match(TType.SEMICOLON)){
            expr = parseExpr();
            forceMatch(TType.SEMICOLON);
        }

        return new Stmt.Ret(expr,lineNumber);
    }



    
    
    //============= DECLARATIONS ===================


    // PRODUCTION RULE:
    // varDecl -> typeSpecifier identifier ( '=' expression )? 

    private Decl.Var parseVarDecl(){
        int lineNumber = iter.getToken().getLineNumber();

        forceMatch(TType.INT,TType.STRING);
        ASTEnums type = toAstType(iter.getPrevToken().getType());

        forceMatch(TType.IDENTIFIER);
        String identifier = iter.getPrevToken().getString();

        Expr expr = null;
        if(match(TType.EQUAL)){
            expr = parseExpr();
        }

        return new Decl.Var(type,identifier,expr,lineNumber);
    }


    // PRODUCTION RULE:
    // funcDecl -> typeSpecifier identifier '(' (param (',' param)* )? ')' '{' stmt* '}'

    private Decl.Func parseFuncDecl(){
        int lineNumber = iter.getToken().getLineNumber();

        forceMatch(TType.INT,TType.STRING,TType.VOID);
        ASTEnums type = toAstType(iter.getPrevToken().getType());

        forceMatch(TType.IDENTIFIER);
        String identifier = iter.getPrevToken().getString();
        
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
    
        return new Decl.Func(type, identifier, parameters,statements,lineNumber);
    }


    // PRODUCTION RULE:
    // param -> typeSpecifier identifier 

    private Decl.Param parseParamDecl(){
        int lineNumber = iter.getToken().getLineNumber();

        forceMatch(TType.IDENTIFIER,TType.INT,TType.STRING);
        ASTEnums type = toAstType(iter.getPrevToken().getType());

        forceMatch(TType.IDENTIFIER);
        String identifier = iter.getPrevToken().getString();

        return new Decl.Param(type, identifier,lineNumber);
    }





    //============= EXPRESSIONS ===============


    // PRODUCTION RULE:
    // expression -> assignment 

    public Expr parseExpr(){
        return parseAssign();
    }


    // PRODUCTION RULE:
    // assignment -> (identifier '=')? equality                 

    public Expr parseAssign(){
        int lineNumber = iter.getToken().getLineNumber();

        if(iter.hasTokens() && iter.getNextToken().getType() == TType.EQUAL){
            forceMatch(TType.IDENTIFIER);
            String identifier = iter.getPrevToken().getString();
            forceMatch(TType.EQUAL);
            
            return new Expr.Assign(identifier, parseEquality(), lineNumber); 
        }
        else{
            return parseEquality();
        }


    }


    // PRODUCTION RULE:
    // equality -> comparison ( equality_operator comparison )*

    private Expr parseEquality(){
        int lineNumber = iter.getToken().getLineNumber();
        Expr expr = parseComparison();

        while(match(TType.BANG_EQUAL, TType.EQUAL_EQUAL)){
            ASTEnums operation = toAstType(iter.getPrevToken().getType());
            Expr right = parseComparison();
            expr = new Expr.Binary(expr,operation,right,lineNumber);
        }

        return expr;
    }


    // PRODUCTION RULE:
    // comparison -> addition ( comparison_operator comparison )*

    private Expr parseComparison(){
        int lineNumber = iter.getToken().getLineNumber();
        Expr expr = parseAddition();

        while(match(TType.GREATER, TType.GREATER_EQUAL,TType.LESS,TType.LESS_EQUAL)){
            ASTEnums operation = toAstType(iter.getPrevToken().getType());
            Expr right = parseAddition();
            expr = new Expr.Binary(expr,operation,right,lineNumber);
        }

        return expr;
    }


    // PRODUCTION RULE:
    // addition -> multiplication ( addition_operator multiplication )*

    private Expr parseAddition(){
        int lineNumber = iter.getToken().getLineNumber();
        Expr expr = parseMultiplication();

        while(match(TType.MINUS,TType.PLUS)){
            ASTEnums operation = toAstType(iter.getPrevToken().getType());
            Expr right = parseMultiplication();
            expr = new Expr.Binary(expr, operation, right,lineNumber);
        }
        
        return expr;
    }


    // PRODUCTION RULE:
    // multiplication -> term ( multiplication_operator term )*

    private Expr parseMultiplication(){
        int lineNumber = iter.getToken().getLineNumber();
        Expr expr = parseTerm();

        while(match(TType.STAR,TType.SLASH)){
            ASTEnums operation = toAstType(iter.getPrevToken().getType());
            Expr right = parseTerm();
            expr = new Expr.Binary(expr, operation, right,lineNumber);
        }

        return expr;
    }


    // PRODUCTION RULE:
    // term -> unary_operator? primary

    private Expr parseTerm(){
        int lineNumber = iter.getToken().getLineNumber();

        if(match(TType.MINUS,TType.BANG)){
            ASTEnums operation = toAstType(iter.getPrevToken().getType());
            return new Expr.Unary(operation, parsePrimary(),lineNumber);
        }

        return parsePrimary();
    }

    
    // PRODUCTION RULE:
    // primary- > identifier
    //          | literal 
    //          | '(' equality ')'
    //          | identifier '(' identifier (',' identifier)* ')'

    private Expr parsePrimary(){
        int lineNumber = iter.getToken().getLineNumber();

        // Literal value
        if(match(TType.NUM_LITERAL,TType.STRING_LITERAL)){
            String value = iter.getPrevToken().getString();
            ASTEnums type = toAstType(iter.getPrevToken().getType());
            return new Expr.Literal(value,type,lineNumber);
        }

        // Enclosed expression
        if(match(TType.LEFT_PAREN)){
            Expr expr = parseExpr();
            forceMatch(TType.RIGHT_PAREN);
            return expr;
        }

        forceMatch(TType.IDENTIFIER);
        String identifier = iter.getPrevToken().getString();
        
        // Identifier
        if( !match(TType.LEFT_PAREN) ){
            return new Expr.Variable(identifier,lineNumber);
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

        return new Expr.Call(identifier, args,lineNumber);
    }
    




    //================ UTLITY =====================


    // Checks if the current token matches any of the given types

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


    // Reports and throws an UnexpectedTokenError error if we cannot match 
    private void forceMatch(TType ... types){
        if(!match(types)){

            // Construct a message to inform the user of what tokens were expected 
            StringBuilder sb = new StringBuilder();
            sb.append("Unexpected token '");

            if(iter.getToken().getType() == TType.IDENTIFIER) sb.append(iter.getToken().getString());
            else sb.append(iter.getToken().getType() );
            
            sb.append("' . Expected tokens: ");

            for(int i=0;i<types.length;i++){
                sb.append(types[i]);
                if(i != types.length - 1) sb.append(" or ");
            }

            report(sb.toString());

            throw new UnexpectedTokenException();
        }
    }


    // If we get a syntax error, we stop parsing that particular statement altogether and continue  
    // parsing the next one. We achieve this by skipping tokens until we reach one that matches the
    // start of some statement production rule. That way we can report more than one parsing errors
    // Number of possible syntax error combinations is infinite so it is really hard for any method (incluiding this one) 
    // to always work effectively and not report a cascade of false errors after encountering the first one

    private void synchronize(){
        iter.advance();

        switch (iter.getPrevToken().getType()) {
            case SEMICOLON:
            case RIGHT_BRACE:
            case LEFT_BRACE:
                return;

            default:
                break;
        }

        while(iter.hasTokens()){
            
            switch(iter.getToken().getType()){
                case IF:
                case WHILE:
                case FOR:
                case RETURN:
                case FN:
                case INT:
                case STRING:
                    return;

                case SEMICOLON:
                case RIGHT_BRACE:
                case LEFT_BRACE:
                    iter.advance();
                    return;

                default:
                    break;
            }

            iter.advance();
        }
    }


    // We translate every TokenType enum to its corresponding enum in ASTEnums
    // This way ast nodes and modules that use them  don't even have to know of the existence of the Token class
    // We make our interpreter more modular

    private ASTEnums toAstType(TType type){
        switch (type) {

            // Data types
            case STRING:
            case STRING_LITERAL:
                return ASTEnums.STRING;
            case NUM_LITERAL:
            case INT:
                return ASTEnums.INT;
            case VOID:
                return ASTEnums.VOID;

            // Operators
            case PLUS:
                return ASTEnums.PLUS;
            case MINUS:
                return ASTEnums.MINUS;
            case STAR:
                return ASTEnums.MULTIPLY;
            case SLASH:
                return ASTEnums.DIVIDE;
            case EQUAL_EQUAL:
                return ASTEnums.EQUAL;
            case BANG_EQUAL:
                return ASTEnums.NOT_EQUAL;
            case BANG:
                return ASTEnums.NOT;
            case GREATER:
                return ASTEnums.GREATER;
            case GREATER_EQUAL:
                return ASTEnums.GREATER_EQ;
            case LESS:
                return ASTEnums.LESS;
            case LESS_EQUAL:
                return ASTEnums.LESS_EQ;
            

            default:
                internalError("toAstType: cannot translate " + type + " into any ASTEnums type");
                return ASTEnums.UNDEFINED;
        }
    }


    private void internalError(String message){
        System.out.println("Internal error: Parser." + message);
        System.exit(0);
    }

    private void report(String message){
        System.out.println("Line " + iter.getToken().getLineNumber() +" : " + message);
        ERROR_OCCURED = true;
    }

}
