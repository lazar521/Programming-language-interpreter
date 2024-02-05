package ast;

import java.util.ArrayList;



public abstract class Stmt implements ASTNode{
    public int lineNumber;

    public static class ExprStmt extends Stmt{
        public Expr expr;

        public ExprStmt (Expr expr,int lineNumber){
            this.expr = expr;
            this.lineNumber = lineNumber;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitExprStmt(this);
        }
    }


    public static class DeclStmt extends Stmt{
        public Decl declaration;
    
        public DeclStmt (Decl decl,int lineNumber){
            this.declaration = decl;
            this.lineNumber = lineNumber;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitDeclStmt(this);
        }
    }


    public static class While extends Stmt{
        public Expr condition;
        public ArrayList<Stmt> body;

        public While(Expr condition,ArrayList<Stmt> statements,int lineNumber){
            this.condition = condition;
            this.body = statements;
            this.lineNumber = lineNumber;
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
        public ArrayList<Stmt> body;

        public For(Decl.Var varDeclaration,Expr condition,Expr update,ArrayList<Stmt> statements,int lineNumber){
            this.varDeclaration = varDeclaration;
            this.condition = condition;
            this.update = update;
            this.body = statements;
            this.lineNumber = lineNumber;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitForStmt(this);
        }
    }



    public static class If extends Stmt{
        public Expr condition;
        public ArrayList<Stmt> body;
        public ArrayList<Stmt> elseBody;

        public If(Expr condition,ArrayList<Stmt> statements,ArrayList<Stmt> elseStatements,int lineNumber){
            this.condition = condition;
            this.body = statements;
            this.elseBody = elseStatements;
            this.lineNumber = lineNumber;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitIfStmt(this);
        }
    }



    public static class Ret extends Stmt{
        public Expr expr;

        public Ret(Expr expr,int lineNumber){
            this.expr = expr;
            this.lineNumber = lineNumber;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitRetStmt(this);
        }
    }

}
