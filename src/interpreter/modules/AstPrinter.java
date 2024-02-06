package interpreter.modules;

import ast.*;
import ast.ASTEnums;
import ast.Expr.Assign;
import ast.Expr.Call;
import ast.Expr.Variable;


// Here we perform recursive inorder print of every tree node.
// Every incrementation of the 'indent' variable will have it's corresponding decrementation in the same function


public class AstPrinter implements ASTVisitor<Void>{
    private static String horizontalLine = "_____________________________________________________________________";

    private int indent = 0;
    private boolean printExpressions = true;

  
    // We print the given string indented by some amount
    private void print(String s){
        StringBuilder sb = new StringBuilder();
        
        for(int i=0;i<indent;i++){
            sb.append("     ");
        }

        String indentation = sb.toString();

        System.out.println(indentation + s);
    }


    public  void printAST(Program program) throws Exception{
        program.accept(this);
    }



    @Override
    public Void visitProgram(Program prog) throws Exception {
        System.out.println(horizontalLine);
        System.out.println("                  PRINTING ABSTRACT SYNTAX TREE");
        System.out.println(horizontalLine);
        System.out.println("\n                      GLOBAL VARIABLES \n");

        // First, we print all the global variable declarations
        for(Stmt s:prog.varDeclStatements){
            s.accept(this);
            System.out.println(horizontalLine);
        }

        System.out.println(horizontalLine);
       
        System.out.println("\n                         FUNCTIONS\n");
       
        // Here we're printing the function declarations 
        for(Stmt s:prog.funcDeclStatements){
            s.accept(this);
            System.out.println(horizontalLine);

        }

        return null;
    }




    //================== STATEMENTS ===================

    @Override
    public Void visitExprStmt(Stmt.ExprStmt stmt) throws Exception {
        print("Stmt.Expr");

        indent++;
        stmt.expr.accept(this);
        indent--;

        return null;
    }


    @Override
    public Void visitDeclStmt(Stmt.DeclStmt stmt) throws Exception {
        stmt.declaration.accept(this);
        return null;
    }


    @Override
    public Void visitWhileStmt(Stmt.While stmt) throws Exception {
        print("Stmt.While");

        indent++;

        print("CONDITION:");

        indent++;
        stmt.condition.accept(this);
        indent--;

        print("BODY:");

        indent++;
        for(Stmt s:stmt.body){
            s.accept(this);
        }
        indent--;
        
        indent--;

        return null;
    }

    @Override
    public Void visitForStmt(Stmt.For stmt) throws Exception {
        print("Stmt.For");
        indent++;

        print("DECLARATION:");

        indent++;
        if(stmt.varDeclaration != null) stmt.varDeclaration.accept(this);
        indent--;

        print("CONDITION:");

        indent++;
        if(stmt.condition != null) stmt.condition.accept(this);
        indent--;

        print("UPDATER:");

        indent++;
        if(stmt.update != null) stmt.update.accept(this);
        indent--;


        print("BODY:");

        indent++;
        for(Stmt s:stmt.body){
            s.accept(this);
        }
        indent--;

        indent--;
        return null;
    }



    @Override
    public Void visitIfStmt(Stmt.If stmt) throws Exception {
        print("Stmt.If");

        indent++;

        print("CONDITION:");

        indent++;
        stmt.condition.accept(this);
        indent--;

        print("BODY:");

        indent++;
        for(Stmt s: stmt.body){
            s.accept(this);
        }
        indent--;
        

        if(stmt.elseBody != null){
            print("ELSE:");
            indent++;
            for(Stmt s: stmt.elseBody){
                s.accept(this);
            }
            indent--;
        }

        indent--;

        return null;
    }

    @Override
    public Void visitRetStmt(Stmt.Ret stmt) throws Exception {
        print("Stmt.Ret");

        indent++;
        stmt.expr.accept(this);
        indent--;

        return null;
    }




    //===================== DECLARATIONS ====================
    
    @Override
    public Void visitVarDecl(Decl.Var decl) throws Exception {
        print("Decl.Var '" + decl.identifier + "'");

        indent++;
        
        print("ASSIGN:");

        if(decl.expr != null) {
            indent++;
            decl.expr.accept(this);
            indent--;
        }

        indent--;
        return null;
    }



    @Override
    public Void visitFuncDecl(Decl.Func decl) throws Exception {
        print("Decl.Func '" + decl.identifier + "'");

        indent++;
        
        print("PARAMETERS:");

        indent++;
        for(Decl.Param param:decl.params){
            param.accept(this);
        }
        indent--;

        print("BODY:");
        
        indent++;
        for(Stmt s:decl.body){
            s.accept(this);
        }
        indent--;
        
        indent--;

        return null;
    }

    @Override
    public Void visitParamDecl(Decl.Param decl) {
        print("Decl.Param '" + decl.identifier +"'");
        return null;
    }




    //==================== EXPRESSIONS ========================

    @Override
    public Void visitBinaryExpr(Expr.Binary expr) throws Exception {
        if(!printExpressions) return null;

        print(expr.operator+".binary");
        
        indent++;
        expr.left.accept(this);
        expr.right.accept(this);
        indent--;

        return null;
    }

    @Override
    public Void visitUnaryExpr(Expr.Unary expr) throws Exception {
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
            print("Expr.Lit \"" + expr.value +"\"");
        }
        else{
            print("Expr.Lit " + expr.value);
        }
        return null;
    }


    @Override
    public Void visitAssignExpr(Assign assignment) throws Exception {
        if(!printExpressions) return null;

        print("Expr.Assign '" + assignment.identifier +"'");
        
        indent++;
        assignment.expr.accept(this);
        indent--;

        return null;
    }


    @Override
    public Void visitCallExpr(Call expr) throws Exception {
        if(!printExpressions) return null;

        print("Expr.Call '" + expr.funcIdentifier + "'");

        indent++;

        print("ARGS:");

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
        print("Expr.Var '" + expr.identifier + "'");
        return null;
    }



}
