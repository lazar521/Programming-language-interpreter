package interpreter.modules;

import java.util.ArrayList;

import ast.ASTEnums;
import ast.ASTVisitor;
import ast.Decl;
import ast.Decl.*;
import ast.Expr.*;
import ast.Expr;
import ast.Program;
import ast.Stmt;
import ast.Stmt.*;
import interpreter.environment.Environment;
import interpreter.environment.BuiltIns;

public class Executor implements ASTVisitor<Object>{
    private Environment env;


    public Object executeProgram(Program program) throws Exception{
        env = new Environment();        
        
        return program.accept(this);
    }


    @Override
    public Object visitProgram(Program prog) throws Exception {

        for(Stmt varDeclStmt: prog.varDeclStatements){
            varDeclStmt.accept(this);
        }

        // Declaring built-in functions
        BuiltIns.declareBuiltIns(env);

        // Declaring the rest of the functions
        for(Stmt.DeclStmt funcDeclStmt: prog.funcDeclStatements){
            Decl.Func decl = (Decl.Func) funcDeclStmt.declaration;

            env.declareFunction(decl.identifier, decl, decl.type);
        }

        
        Decl.Func mainFunc = env.fetchFunc("main");
        env.enterFunction(mainFunc);

        return mainFunc.accept(this);
    }


    @Override
    public Object visitExprStmt(Stmt.ExprStmt exprStmt) throws Exception {
        exprStmt.expr.accept(this);
        return null;
    }


    @Override
    public Object visitDeclStmt(Stmt.DeclStmt declStmt) throws Exception {
        declStmt.declaration.accept(this);
        return null;
    }



    @Override
    public Object visitWhileStmt(While whileStmt) throws Exception {
        env.enterCodeBlock();
        
        while( (int) whileStmt.condition.accept(this) != 0){
            for(Stmt stmt: whileStmt.body){
                stmt.accept(this);
            }
        }

        env.exitCodeBlock();

        return null;
    }


    @Override
    public Object visitForStmt(For forStmt) throws Exception {
        
        env.enterCodeBlock();

        if(forStmt.varDeclaration != null) forStmt.varDeclaration.accept(this);

        while(true){
            if(forStmt.condition != null &&  ((int)forStmt.condition.accept(this) == 0) ){
                break;
            }

            for(Stmt stmt: forStmt.body){
                stmt.accept(this);
            }

            if(forStmt.update != null) forStmt.update.accept(this);
        }

        env.exitCodeBlock();

        return null;
    }


    @Override
    public Object visitIfStmt(If ifStmt) throws Exception {
        int res = (int) ifStmt.condition.accept(this);

        env.enterCodeBlock();
        
        if(res != 0){
            for(Stmt stmt: ifStmt.body){
                stmt.accept(this);
            }
        }
        else if(ifStmt.elseBody != null){
            for(Stmt stmt: ifStmt.elseBody){
                stmt.accept(this);
            }
        }

        env.exitCodeBlock();
 
        return null; 
    }


    // Functions can have multiple return statements and the control flow could go multiple ways
    // We return a value by wrapping it inside a runtime error
    @Override
    public Object visitRetStmt(Ret retStmt) throws Exception{
        Object retVal = null;
        
        if(retStmt.expr != null){
            retVal = retStmt.expr.accept(this);
        }
        
        throw new ReturnValueException(retVal);
    }


    @Override
    public Object visitVarDecl(Var decl) throws Exception {
        env.declareVar(decl.identifier, decl.type);
        
        if(decl.expr != null){
            env.assignVar(decl.identifier, decl.expr.accept(this));
        }

        return null;
    }


    @Override
    public Object visitFuncDecl(Func funcDeclaration) throws Exception {
        
        // First we execute the funciton body
        // When we encounter a return statement we throw an exception that holds the return value.
        // We catch it here and extract the return value. If exception is not thrown, 
        // then the function's return type is void so it doesn't matter what we return 
        // Functions can have multiple return statements and the control flow could go multiple ways
        try{
            for(Stmt stmt: funcDeclaration.body){
                stmt.accept(this);
            }
            return null;
        }
        catch(ReturnValueException returnException){
            return returnException.value;
        }

    }



    @Override
    public Object visitParamDecl(Param decl) {
        env.declareVar(decl.identifier, decl.type);
        return null;
    }



    @Override
    public Object visitBinaryExpr(Binary expr) throws Exception {        
        Object left = expr.left.accept(this);
        Object right = expr.right.accept(this);

        // This should not ever happen here if the code passed the syntax check, but we check just in case for debug purposes if something goes wrong
        if(expr.left.type != expr.right.type){
            internalError("visitBinaryExpr: Binary expression non-matching types");
        }

        switch(expr.operator){
            case MULTIPLY:
                return (int)left * (int)right;
            case DIVIDE:
                return (int)left / (int)right;
            case MINUS:
                return (int)left - (int)right;
           
            case PLUS:
                if(expr.left.type == ASTEnums.STRING) return (String)left + (String)right;
                else return (int)left + (int)right;
            
            case EQUAL:
                if(expr.left.type == ASTEnums.STRING) return boolToInt( ((String)left).equals((String)right) );
                else return boolToInt( (int)left == (int) right );
            
            case NOT_EQUAL:
                if(expr.left.type == ASTEnums.STRING) return boolToInt( !((String)left).equals((String)right) );
                else return boolToInt( !((int)left == (int) right) );
                
            case LESS:
                return boolToInt( (int)left < (int) right );
            case LESS_EQ:
                return boolToInt( (int)left <= (int) right );
            case GREATER:
                return boolToInt( (int)left > (int) right );
            case GREATER_EQ:
                return boolToInt( (int)left >= (int) right );
            default:
                internalError("visitBinaryExpr: Unrecognized operator " + expr.operator);
        }
        return null;
    }

    @Override
    public Object visitUnaryExpr(Unary unary) throws Exception {
        int operand = (int) unary.expr.accept(this);

        switch(unary.operator){
            case MINUS:
                return -operand;
            case NOT:
                if(operand == 0) return 1;
                else return 0;
            default:
                internalError("visitUnaryExpr: Unrecognized operator " + unary.operator);
        }

        return null;
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        String value = expr.value; 

        switch(expr.type){
            case INT:
                return Integer.parseInt(value);
            case STRING:
                return value;
            default:
                internalError("visitLiteralExpr: Invalid literal type " + expr.type);
        }

        return null;
    }



    @Override
    public Object visitAssignExpr(Assign assignment) throws Exception {
        Object exprValue = assignment.expr.accept(this);
        
        env.assignVar(assignment.identifier, exprValue);

        return exprValue;
    }



    @Override
    public Object visitCallExpr(Call call) throws Exception {
        if(env.isMaxCallstackReached()){
            runtimeError(call.lineNumber,"Cannot call function '" + call.funcIdentifier + "' . Maximum function call stack size reached ");
        }

        // Calculating argument expressions before entering new function scope
        ArrayList<Object> argValues = new ArrayList<>();
        for(Expr arg:call.arguments){
            argValues.add(arg.accept(this));
        }

        // If the function is a built-in function then we call a predefined routine
        if(BuiltIns.isFuncBuiltIn(call.funcIdentifier)){
            return BuiltIns.executeFunction(call.funcIdentifier,argValues);
        }

        
        Decl.Func funcNode = env.fetchFunc(call.funcIdentifier);

        // Declaring new function scope 
        env.enterFunction(funcNode);
        
        // Assigning passed values to parameters
        for(int i=0; i< funcNode.params.size();i++){
            Object argVal = argValues.get(i);    
            Param param = funcNode.params.get(i);
            
            env.declareVar(param.identifier, param.type);
            env.assignVar(param.identifier, argVal);
        }
        
        // Calling function body
        Object retVal = funcNode.accept(this);
        
        // 
        env.exitFunction();
        
        return retVal;
    }




    @Override
    public Object visitVariableExpr(Variable variable) {
        return env.fetchVar(variable.identifier);
    }




    private int boolToInt(boolean bool){
        if(bool) return 1;
        return 0;
    }



    private void internalError(String message){
        System.out.println("Internal error: Executor." + message);
        System.exit(0);
    }

    private void runtimeError(int lineNumber,String message) throws Exception{
        System.out.println("Line " + lineNumber +": Runtime error: " + message);
        throw new Exception();
    }



    private static class ReturnValueException extends RuntimeException{
        private Object value;

        public ReturnValueException(Object value){
            this.value = value;
        }
    }






}
