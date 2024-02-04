package interpreter;

import ast.*;
import ast.ASTEnums;
import ast.Expr.Assign;
import ast.Expr.Call;
import ast.Expr.Variable;


public class AstPrinter implements ASTVisitor<Void>{
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




    @Override
    public Void visitProgram(Program prog) {
        print("GLOBAL VARIABLES");
        
        indent++;
        for(Stmt s:prog.varDeclarations){
            s.accept(this);
        }
        indent--;

        print("FUNCTIONS");
        indent++;
        for(Stmt s:prog.funcDeclarations){
            s.accept(this);
        }
        indent--;

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
        stmt.declaration.accept(this);
        return null;
    }


    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        print("whileStmt");

        indent++;

        print("condition:");
        indent++;
        stmt.condition.accept(this);
        indent--;

        print("body:");
        indent++;
        for(Stmt s:stmt.body){
            s.accept(this);
        }
        indent--;
        
        indent--;

        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) {
        print("forStmt");
        indent++;

        print("declaration:");
        indent++;
        if(stmt.varDeclaration != null) stmt.varDeclaration.accept(this);
        indent--;

        print("condition:");
        indent++;
        if(stmt.condition != null) stmt.condition.accept(this);
        indent--;

        print("updater:");
        indent++;
        if(stmt.update != null) stmt.update.accept(this);
        indent--;


        print("body:");
        indent++;
        for(Stmt s:stmt.body){
            s.accept(this);
        }
        indent--;

        indent--;
        return null;
    }

    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        print("ifStmt");
        indent++;

        print("condition:");
        indent++;
        stmt.condition.accept(this);
        indent--;

        print("body:");
        indent++;
        for(Stmt s: stmt.body){
            s.accept(this);
        }
        indent--;
        
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
        print("varDecl '" + decl.identifier + "'");

        indent++;
        
        print("assign:");
        indent++;
        if(decl.expr != null) {
            decl.expr.accept(this);
        }
        indent--;

        indent--;
        return null;
    }



    @Override
    public Void visitFuncDecl(Decl.Func decl) {
        print("funcDecl '" + decl.identifier + "'");

        indent++;
        
        print("parameters:");
        indent++;
        for(Decl.Param param:decl.params){
            param.accept(this);
        }
        indent--;

        print("body:");
        
        indent++;
        for(Stmt s:decl.body){
            s.accept(this);
        }
        indent--;
        
        return null;
    }

    @Override
    public Void visitParamDecl(Decl.Param decl) {
        print("paramDecl '" + decl.identifier +"'");
        return null;
    }




    //==================== EXPRESSIONS ========================

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) {
        if(!printExpressions) return null;

        print(expr.operator+".binary");
        
        indent++;
        expr.left.accept(this);
        expr.right.accept(this);
        indent--;

        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) {
        if(!printExpressions) return null;

        print(expr.operator+".unary");
        indent++;
        expr.expr.accept(this);
        indent--;
        return null;
    }


    @Override
    public Void visitLiteralExpr(Expr.Literal expr) {
        if(!printExpressions) return null;

        if(expr.type == ASTEnums.STRING){
            print("lit '" + expr.value +"'");
        }
        else{
            print("lit " + expr.value);
        }
        return null;
    }

    @Override
    public Void visitAssignExpr(Assign assignment) {
        if(!printExpressions) return null;

        print("assigning to '" + assignment.identifier +"'");
        
        indent++;
        assignment.expr.accept(this);
        indent--;

        return null;
    }


    @Override
    public Void visitCallExpr(Call expr) {
        if(!printExpressions) return null;

        print("Call."+expr.funcIdentifier);

        indent++;
        print("args:");
        indent++;
        for(Expr e:expr.arguments){
            e.accept(this);
        }
        indent--;
        indent--;

        return null;
    }


    @Override
    public Void visitVariableExpr(Variable expr) {
        print("var " + expr.identifier);
        return null;
    }



}
