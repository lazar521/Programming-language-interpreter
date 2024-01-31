package interpreter;

import ast.*;


public class astPrinter implements NodeExecutionVisitor<Void>{
    private static int indent = 0;
    private static boolean printExpressions = false;
    
    
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
    public Void executeExprBinary(Expr.Binary expr) {
        if(!printExpressions) return null;

        print("binary "+expr.operation.getType());
        indent++;
        expr.left.execute(this);
        expr.right.execute(this);
        indent--;
        return null;
    }

    @Override
    public Void executeExprUnary(Expr.Unary expr) {
        if(!printExpressions) return null;

        print("unary "+expr.operation.getType());
        indent++;
        expr.expr.execute(this);
        indent--;
        return null;
    }

    @Override
    public Void executeExprLiteral(Expr.Literal expr) {
        if(!printExpressions) return null;

        print("literal "+expr.value.getValue());
        return null;
    }

    @Override
    public Void executeExprEnclosed(Expr.Enclosed expr) {
        if(!printExpressions) return null;

        print("ENCLOSING");
        indent++;
        expr.expr.execute(this);
        indent--;
        return null;
    }





    //================== STATEMENTS ===================

    @Override
    public Void executeExprStmt(Stmt.ExprStmt stmt) {
        print("ExprStmt");
        indent++;
        stmt.expr.execute(this);
        indent--;
        return null;
    }

    @Override
    public Void executeDeclStmt(Stmt.DeclStmt stmt) {
        stmt.decl.execute(this);
        return null;
    }

    @Override
    public Void executeWhileStmt(Stmt.While stmt) {
        print("whileStmt");
        indent++;
        stmt.condition.execute(this);
        
        for(Stmt s:stmt.statemets){
            s.execute(this);
        }

        indent--;
        return null;
    }

    @Override
    public Void executeForStmt(Stmt.For stmt) {
        print("forStmt");
        indent++;

        if(stmt.varDeclaration != null) stmt.varDeclaration.execute(this);
        if(stmt.condition != null) stmt.condition.execute(this);
        if(stmt.update != null) stmt.update.execute(this);
    
        for(Stmt s:stmt.statements){
            s.execute(this);
        }

        indent--;
        return null;
    }

    @Override
    public Void executeIfStmt(Stmt.If stmt) {
        print("ifStmt");
        indent++;
        stmt.condition.execute(this);

        for(Stmt s: stmt.statements){
            s.execute(this);
        }
        
        indent--;
        return null;
    }

    @Override
    public Void executeRetStmt(Stmt.Ret stmt) {
        print("returnStmt");
        indent++;
        stmt.expr.execute(this);
        indent--;
        return null;
    }




    //===================== DECLARATIONS ====================
    
    @Override
    public Void executeVarDecl(Decl.Var decl) {
        print("varDecl");
        indent++;
        if(decl.expr != null) decl.expr.execute(this);
        indent--;
        return null;
    }

    @Override
    public Void executeFuncDecl(Decl.Func decl) {
        print("funcDecl");
        indent++;

        for(Stmt s:decl.statements){
            s.execute(this);
        }

        indent--;
        return null;
    }

    @Override
    public Void executeParamDecl(Decl.Param decl) {
        print("paramDecl");
        return null;
    }
}
