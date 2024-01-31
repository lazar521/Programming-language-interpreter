package interpreter;

import ast.*;

public interface NodeExecutionVisitor <T> {
    // Statement nodes
    public T executeExprStmt(Stmt.ExprStmt stmt);
    public T executeDeclStmt(Stmt.DeclStmt stmt);
    public T executeWhileStmt(Stmt.While stmt);
    public T executeForStmt(Stmt.For stmt);
    public T executeIfStmt(Stmt.If stmt);
    public T executeRetStmt(Stmt.Ret stmt);

    // Declaration nodes
    public T executeVarDecl(Decl.Var decl);
    public T executeFuncDecl(Decl.Func decl);
    public T executeParamDecl(Decl.Param decl);

    // Expression nodes
    public T executeExprBinary(Expr.Binary expr) ;
    public T executeExprUnary(Expr.Unary expr) ;
    public T executeExprLiteral(Expr.Literal expr) ;
    public T executeExprEnclosed(Expr.Enclosed expr) ;
} 