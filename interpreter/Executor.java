package interpreter;

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

public class Executor implements ASTVisitor<Object>{
    private Environment environment;



    public void executeProgram(Program program){
        program.accept(this);
    }


    @Override
    public Object visitProgram(Program prog) {
        environment = new Environment();

        for(Stmt varDeclStmt: prog.varDeclarations){
            varDeclStmt.accept(this);
        }

        for(Stmt.DeclStmt funcDeclStmt: prog.funcDeclarations){
            Decl.Func decl = (Decl.Func) funcDeclStmt.declaration;

            environment.declareFunction(decl.identifier, decl, decl.type);
        }

        // TODO: make integrated functions
        
        environment.enterFunction("main");
        Decl.Func mainFunc = environment.fetchFunc("main");

        return mainFunc.accept(this);
    }


    @Override
    public Object visitExprStmt(Stmt.ExprStmt exprStmt) {
        exprStmt.expr.accept(this);
        return null;
    }


    @Override
    public Object visitDeclStmt(Stmt.DeclStmt declStmt) {
        declStmt.declaration.accept(this);
        return null;
    }



    @Override
    public Object visitWhileStmt(While whileStmt) {
        environment.enterCodeBlock();
        
        while( (int) whileStmt.condition.accept(this) != 0){
            for(Stmt stmt: whileStmt.body){
                stmt.accept(this);
            }
        }

        environment.exitCodeBlock();

        return null;
    }


    // TODO: For statement written like this expects all of the fields to be non null
    @Override
    public Object visitForStmt(For forStmt) {
        environment.enterCodeBlock();

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

        environment.exitCodeBlock();

        return null;
    }

    @Override
    public Object visitIfStmt(If ifStmt) {
        int res = (int) ifStmt.condition.accept(this);

        environment.enterCodeBlock();
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

        environment.exitCodeBlock();
 
        return null; 
    }

    @Override
    public Object visitRetStmt(Ret retStmt) throws ReturnValueException{
        Object retVal = null;
        
        if(retStmt.expr != null){
            retVal = retStmt.expr.accept(this);
        }
        
        throw new ReturnValueException(retVal);
    }


    @Override
    public Object visitVarDecl(Var decl) {
        environment.declareVar(decl.identifier, decl.type);
        
        if(decl.expr != null){
            environment.assignVar(decl.identifier, decl.expr.accept(this));
        }

        return null;
    }


    @Override
    public Object visitFuncDecl(Func funcDeclaration) {
        
        // First we execute the funciton body
        // When we encounter a return statement we throw an exception that holds the return value.
        // We catch it here and extract the return value. If exception is not thrown, 
        // then the function's return type is void so it doesn't matter what we return 
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
        environment.declareVar(decl.identifier, decl.type);
        return null;
    }



    @Override
    public Object visitBinaryExpr(Binary expr) {        
        Object left = expr.left.accept(this);
        Object right = expr.right.accept(this);

        // This should not ever happen here if the code passed the syntax check, but we check just in case for debug purposes if something goes wrong
        if(expr.left.type != expr.right.type){
            error("Executor.visitBinaryExpr: Binary expression non-matching types");
        }

        switch(expr.operator){
            case MULTIPLY:
                return (int)left * (int)right;
            case DIVIDE:
                return (int)left / (int)right;
            case MINUS:
                return (int)left - (int)right;
           
            case PLUS:
                if(expr.type == ASTEnums.STRING) return (String)left + (String)right;
                else return (int)left + (int)right;
            
            case EQUAL:
                if(expr.type == ASTEnums.STRING) return boolToInt( ((String)left).equals((String)right) );
                else return boolToInt( (int)left == (int) right );
            
            case NOT_EQUAL:
                if(expr.type == ASTEnums.STRING) return boolToInt( !((String)left).equals((String)right) );
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
                error("Executor.visitBinaryExpr: Unrecognized operator " + expr.operator);
        }
        return null;
    }

    @Override
    public Object visitUnaryExpr(Unary unary) {
        int operand = (int) unary.expr.accept(this);

        switch(unary.operator){
            case MINUS:
                return -operand;
            case NOT:
                if(operand == 0) return 1;
                else return 0;
            default:
                error("Executor.visitUnaryExpr: Unrecognized operator " + unary.operator);
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
                error("Executor.visitLiteralExpr: Invalid literal type " + expr.type);
        }

        return null;
    }



    @Override
    public Object visitAssignExpr(Assign assignment) {
        Object exprValue = assignment.expr.accept(this);
        
        environment.assignVar(assignment.identifier, exprValue);

        return exprValue;
    }



    @Override
    public Object visitCallExpr(Call call) {
        Decl.Func funcNode = environment.fetchFunc(call.funcIdentifier);

        // Calculating argument expressions before entering new function scope
        ArrayList<Object> argValues = new ArrayList<>();
        for(Expr arg:call.arguments){
            argValues.add(arg.accept(this));
        }

        // Entering new scope and assigning parameters their respective values
        environment.enterFunction(call.funcIdentifier);
        
        for(int i=0; i< funcNode.params.size();i++){
            Object argVal = argValues.get(i);    
            Param param = funcNode.params.get(i);
            
            environment.declareVar(param.identifier, param.type);
            environment.assignVar(param.identifier, argVal);
        }
        
        // Calling function body
        Object retVal = funcNode.accept(this);
        
        environment.exitFunction();
        
        return retVal;
    }




    @Override
    public Object visitVariableExpr(Variable variable) {
        return environment.fetchVar(variable.identifier);
    }




    private static class ReturnValueException extends RuntimeException{
        private Object value;

        public ReturnValueException(Object value){
            this.value = value;
        }
    }




    private static void error(String s){
        System.out.println("Internal error: " + s);
        System.exit(0);
    }


    private int boolToInt(boolean bool){
        if(bool) return 1;
        return 0;
    }
}
