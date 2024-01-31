package interpreter;

import ast.ASTVisitor;
import ast.Decl.*;
import ast.Expr.*;
import ast.Stmt.*;

public class Interpreter implements ASTVisitor<Object>{

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
        
        
        switch(expr.operation.getType()){
            case PLUS:
        }
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeUnaryExpr'");
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeLiteralExpr'");
    }

    @Override
    public Object visitEnclosedExpr(Enclosed expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeEnclosedExpr'");
    }

    @Override
    public Object visitCallExpr(Call expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeCallExpr'");
    }

 
}
