package ast;


public interface ASTVisitor <T> {
    // Statement nodes
    public T visitProgram(Program prog) throws Exception;
    public T visitExprStmt(Stmt.ExprStmt stmt) throws Exception;
    public T visitDeclStmt(Stmt.DeclStmt stmt) throws Exception;
    public T visitWhileStmt(Stmt.While stmt) throws Exception;
    public T visitForStmt(Stmt.For stmt) throws Exception;
    public T visitIfStmt(Stmt.If stmt) throws Exception;
    public T visitRetStmt(Stmt.Ret stmt) throws Exception;

    // Declaration nodes
    public T visitVarDecl(Decl.Var decl) throws Exception;
    public T visitFuncDecl(Decl.Func decl) throws Exception;
    public T visitParamDecl(Decl.Param decl) throws Exception;

    // Expression nodes
    public T visitBinaryExpr(Expr.Binary expr) throws Exception;
    public T visitUnaryExpr(Expr.Unary expr) throws Exception;
    public T visitAssignExpr(Expr.Assign expr) throws Exception;
    public T visitLiteralExpr(Expr.Literal expr) throws Exception;
    public T visitCallExpr(Expr.Call expr) throws Exception;
    public T visitVariableExpr(Expr.Variable expr) throws Exception;
} 