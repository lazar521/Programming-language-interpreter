package interpreter;

import ast.*;
import ast.Expr.Call;
import ast.Expr.Variable;


public class astPrinter implements ASTVisitor<Void>{
    private static int indent = 0;
    private static boolean printExpressions = true;
    
    
    private static String makeIndent(){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<indent;i++){
            sb.append("    ");
        }
        return sb.toString();
    }

    private static void print(String s){
        System.out.println(makeIndent() + s);
    }




    //==================== EXPRESSIONS ========================
    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        if(!printExpressions) return null;

        print("binary "+expr.operation.getType());
        indent++;
        expr.left.accept(this);
        expr.right.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        if(!printExpressions) return null;

        print("unary "+expr.operation.getType());
        indent++;
        expr.expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        if(!printExpressions) return null;

        print("literal "+expr.value.getValue());
        return null;
    }

    @Override
    public Void visitEnclosedExpr(Expr.Enclosed expr) {
        if(!printExpressions) return null;

        print("ENCLOSING");
        indent++;
        expr.expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visitCallExpr(Call expr) {
        if(!printExpressions) return null;

        print("Function call");
        indent++;
        
        for(Expr e:expr.arguments){
            e.accept(this);
        }

        indent--;
        return null;
    }


    @Override
    public Void visitVariableExpr(Variable expr) {
        print("variable");
        return null;
    }


    //================== STATEMENTS ===================

    @Override
    public Void visitExprStmt(Stmt.ExprStmt stmt) {
        print("ExprStmt");
        indent++;
        stmt.expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visitDeclStmt(Stmt.DeclStmt stmt) {
        stmt.decl.accept(this);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        print("whileStmt");
        indent++;
        stmt.condition.accept(this);
        
        for(Stmt s:stmt.statemets){
            s.accept(this);
        }

        indent--;
        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) {
        print("forStmt");
        indent++;

        if(stmt.varDeclaration != null) stmt.varDeclaration.accept(this);
        if(stmt.condition != null) stmt.condition.accept(this);
        if(stmt.update != null) stmt.update.accept(this);
    
        for(Stmt s:stmt.statements){
            s.accept(this);
        }

        indent--;
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        print("ifStmt");
        indent++;
        stmt.condition.accept(this);

        for(Stmt s: stmt.statements){
            s.accept(this);
        }
        
        indent--;
        return null;
    }

    @Override
    public Void visitRetStmt(Stmt.Ret stmt) {
        print("returnStmt");
        indent++;
        stmt.expr.accept(this);
        indent--;
        return null;
    }




    //===================== DECLARATIONS ====================
    
    @Override
    public Void visitVarDecl(Decl.Var decl) {
        print("varDecl");
        indent++;
        if(decl.expr != null) decl.expr.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visitFuncDecl(Decl.Func decl) {
        print("funcDecl");
        indent++;

        for(Stmt s:decl.statements){
            s.accept(this);
        }

        indent--;
        return null;
    }

    @Override
    public Void visitParamDecl(Decl.Param decl) {
        print("paramDecl");
        return null;
    }




}
