package ast;

public interface ASTVisitor <T> {
    // Statement nodes
    public T visitProgram(Program prog);
    public T visitExprStmt(Stmt.ExprStmt stmt);
    public T visitDeclStmt(Stmt.DeclStmt stmt);
    public T visitWhileStmt(Stmt.While stmt);
    public T visitForStmt(Stmt.For stmt);
    public T visitIfStmt(Stmt.If stmt);
    public T visitRetStmt(Stmt.Ret stmt);

    // Declaration nodes
    public T visitVarDecl(Decl.Var decl);
    public T visitFuncDecl(Decl.Func decl);
    public T visitParamDecl(Decl.Param decl);

    // Expression nodes
    public T visitBinaryExpr(Expr.Binary expr) ;
    public T visitUnaryExpr(Expr.Unary expr) ;
    public T visitLiteralExpr(Expr.Literal expr) ;
    public T visitEnclosedExpr(Expr.Enclosed expr) ;
    public T visitCallExpr(Expr.Call expr);
    public T visitVariableExpr(Expr.Variable expr);
} 