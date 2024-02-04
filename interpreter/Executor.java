package interpreter;

import ast.ASTVisitor;
import ast.Decl;
import ast.Decl.*;
import ast.Expr.*;
import ast.Program;
import ast.Stmt;
import ast.Stmt.*;
import interpreter.environment.Environment;
import ast.ASTEnums.*;

public class Executor implements ASTVisitor<Object>{
    private Environment environment;

    @Override
    public Object visitProgram(Program prog) {
        throw new UnsupportedOperationException("Unimplemented method 'executeDeclStmt'");

    }


    @Override
    public Object visitExprStmt(ExprStmt stmt) {
        // TODO: Add assign operation
        return stmt.expr.accept(this);
    }

    @Override
    public Object visitDeclStmt(DeclStmt stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeDeclStmt'");
    }

    @Override
    public Object visitWhileStmt(While stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeWhileStmt'");
    }

    @Override
    public Object visitForStmt(For stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeForStmt'");
    }

    @Override
    public Object visitIfStmt(If stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeIfStmt'");
    }

    @Override
    public Object visitRetStmt(Ret stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeRetStmt'");
    }

    @Override
    public Object visitVarDecl(Var decl) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeVarDecl'");
    }

    @Override
    public Object visitFuncDecl(Func decl) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeFuncDecl'");
    }

    @Override
    public Object visitParamDecl(Param decl) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeParamDecl'");
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        // TODO: Add type checking
        
        Object left = expr.left.accept(this);
        Object right = expr.right.accept(this);

        switch(expr.operator){
            case MULTIPLY:
                return (int)left * (int)right;
            case DIVIDE:
                return (int)left / (int)right;
            case MINUS:
                return (int)left - (int)right;
            case PLUS:
                // TODO: Add string concatenation
                return (int)left + (int)right;
            default:
                System.out.println("An invalid operation in Interpreter::visitBinaryExpr");
                System.exit(0);    
        }
        return null;
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        // TODO: Add type checking

        Object operand = expr.accept(this);

        switch(expr.operator){
            case MINUS:
                return -(int)operand;
            case NOT:
                return !(boolean)operand;
            default:
                System.out.println("An invalid operation in Interpreter::visitUnaryExpr");
                System.exit(0);
        }

        return null;
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        // Todo: Add type checking

        String value = expr.value; 

        switch(expr.type){
            case INT:
                return Integer.parseInt(value);
            case STRING:
                return value;
            default:
                System.out.println("An invalid type in Interpreter::visitLiteralExpr");
                System.exit(0);
        }

        return null;
    }

    @Override
    public Object visitAssignExpr(Assign expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitAssignExpr'");
    }


    


    @Override
    public Object visitCallExpr(Call call) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeCallExpr'");
    }

    @Override
    public Object visitVariableExpr(Variable variable) {
        //variable.type = environment.fetchVarType(variable.identifier);
        return environment.fetchVar(variable.identifier);
    }



}
