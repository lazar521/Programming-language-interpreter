package interpreter;

import java.util.List;
import ast.*;
import ast.Decl.*;
import ast.Stmt.*;
import ast.Expr.*;



public class SemanticChecker implements ASTVisitor<Object>{
    private boolean CHECKING_GLOBALS;
    private Environment.FunctionsTable funcTable;
    private Environment.GlobalsTable globalVariables;

    public void checkSemantics(List<Stmt> statements){
        CHECKING_GLOBALS = true;

        funcTable = new Environment.FunctionsTable.FunctionsTable();
        globalVariables = new Environment.GlobalsTable.GlobalsTable();

        // first we note all the functions and global variables and return immediately from each declaration statement
        for(Stmt stmt:statements){
            stmt.accept(this);
        }

        CHECKING_GLOBALS = false;

        // TODO: check if the main() function is present

        for(Stmt stmt:statements){
            stmt.accept(this);
        }
    }

    @Override
    public Object visitProgram(Program prog) {
        for(Stmt s: prog.statements){
            s.accept(this);
        }
        
        return null;
    }

    @Override
    public Object visitExprStmt(ExprStmt stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitExprStmt'");
    }

    @Override
    public Object visitDeclStmt(DeclStmt stmt) {
        stmt.decl.accept(this);
        return null;
    }

    @Override
    public Object visitWhileStmt(While stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitWhileStmt'");
    }

    @Override
    public Object visitForStmt(For stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitForStmt'");
    }

    @Override
    public Object visitIfStmt(If stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitIfStmt'");
    }

    @Override
    public Object visitRetStmt(Ret stmt) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitRetStmt'");
    }

    @Override
    public Object visitVarDecl(Var decl) {
        if(CHECKING_GLOBALS){
            // TODO: Catch exceptions when declaring a global variable with an expression that cannot be calculated during semantic analyze
            globalVariables.add(decl.identifier,decl.type);
            if(decl.expr != null){
                // TODO: Assign
            }
            return null;
        }

        return null;
    }

    @Override
    public Object visitFuncDecl(Func decl) {
        if(CHECKING_GLOBALS){

            return null;
        }

        return null;
    }

    @Override
    public Object visitParamDecl(Param decl) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitParamDecl'");
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitBinaryExpr'");
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitUnaryExpr'");
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitLiteralExpr'");
    }

    @Override
    public Object visitEnclosedExpr(Enclosed expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitEnclosedExpr'");
    }

    @Override
    public Object visitCallExpr(Call expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitCallExpr'");
    }

    @Override
    public Object visitVariableExpr(Variable expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'visitVariableExpr'");
    }


}