package ast;

import java.util.ArrayList;

import interpreter.NodeExecutionVisitor;
import token.*;


public abstract class Stmt implements ASTNode{
    

    public static class ExprStmt extends Stmt{
        public Expr expr;
        public Token identifier;

        public ExprStmt (Token ident,Expr expr){
            this.expr = expr;
            this.identifier = ident;
        }

                @Override
        public <T> T execute(NodeExecutionVisitor<T> visitor) {
            return visitor.executeExprStmt(this);
        }
    }


    public static class DeclStmt extends Stmt{
        public Decl decl;
    
        public DeclStmt (Decl decl){
            this.decl = decl;
        }

        @Override
        public <T> T execute(NodeExecutionVisitor<T> visitor) {
            return visitor.executeDeclStmt(this);
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
        public <T> T execute(NodeExecutionVisitor<T> visitor) {
            return visitor.executeWhileStmt(this);
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
        public <T> T execute(NodeExecutionVisitor<T> visitor) {
            return visitor.executeForStmt(this);
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
        public <T> T execute(NodeExecutionVisitor<T> visitor) {
            return visitor.executeIfStmt(this);
        }
    }



    public static class Ret extends Stmt{
        public Expr expr;

        public Ret(Expr expr){
            this.expr = expr;
        }

        @Override
        public <T> T execute(NodeExecutionVisitor<T> visitor) {
            return visitor.executeRetStmt(this);
        }
    }

}
