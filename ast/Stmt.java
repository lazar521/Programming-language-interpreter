package ast;

import java.util.ArrayList;

public class Stmt {
    

    public class ExprStmt extends Stmt{
        public Expr expr;

        public ExprStmt (Expr expr){
            this.expr = expr;
        }

    }


    public class DeclStmt extends Stmt{
        public Decl decl;
    
        public DeclStmt (Decl decl){
            this.decl = decl;
        }
    }


    public class While extends Stmt{
        public Expr condition;
        public ArrayList<Stmt> statemets;

        public While(Expr cond,ArrayList<Stmt> stmts){
            this.condition = cond;
            this.statemets = stmts;
        }
    }


    public class For extends Stmt{
        public Decl.Var varDeclaration;
        public Expr condition;
        public Expr update;

        public For(Decl.Var decl,Expr cond,Expr upd){
            this.varDeclaration = decl;
            this.condition = cond;
            this.update = upd;
        }
    }


    public class Ret extends Stmt{
        public Expr expr;

        public Ret(Expr expr){
            this.expr = expr;
        }
    }

}
