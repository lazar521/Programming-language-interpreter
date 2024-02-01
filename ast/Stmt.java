package ast;

import java.util.ArrayList;

import token.*;


public abstract class Stmt implements ASTNode{


    public static class ExprStmt extends Stmt{
        public Expr expr;
        public String identifier;

        public ExprStmt (Token ident,Expr expr){
            this.expr = expr;
            this.identifier = ident.getValue();
        }

                @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitExprStmt(this);
        }
    }


    public static class DeclStmt extends Stmt{
        public Decl decl;
    
        public DeclStmt (Decl decl){
            this.decl = decl;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitDeclStmt(this);
        }
    }


    public static class While extends Stmt{
        public Expr condition;
        public ArrayList<Stmt> statemets;

        public While(Expr cond,ArrayList<Stmt> stmts){
            this.condition = cond;
            this.statemets = stmts;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }


    public static class For extends Stmt{
        public Decl.Var varDeclaration;
        public Expr condition;
        public Expr update;
        public ArrayList<Stmt> statements;

        public For(Decl.Var decl,Expr cond,Expr upd,ArrayList<Stmt> stmts){
            this.varDeclaration = decl;
            this.condition = cond;
            this.update = upd;
            this.statements = stmts;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitForStmt(this);
        }
    }


    public static class If extends Stmt{
        public Expr condition;
        public ArrayList<Stmt> statements;

        public If(Expr cond,ArrayList<Stmt> stmts){
            this.condition = cond;
            this.statements = stmts;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitIfStmt(this);
        }
    }



    public static class Ret extends Stmt{
        public Expr expr;

        public Ret(Expr expr){
            this.expr = expr;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitRetStmt(this);
        }
    }

}
