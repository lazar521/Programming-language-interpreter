package ast;

import java.util.ArrayList;

public abstract class Decl  implements ASTNode {  
    public int lineNumber;

    public static class Var extends Decl {
        public ASTEnums type;
        public String identifier;
        public Expr expr;

        public Var(ASTEnums type,String identifier,Expr expr,int lineNumber){
            this.type = type;
            this.identifier = identifier;
            this.expr = expr;
            this.lineNumber = lineNumber;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitVarDecl(this);
        }
    }


    public static class Func extends Decl{
        public ASTEnums type;
        public String identifier;
        public ArrayList<Param> params;
        public ArrayList<Stmt> body;

        public Func(ASTEnums type,String identifier,ArrayList<Param> parameters,ArrayList<Stmt> statements,int lineNumber){
            this.type = type;
            this.identifier = identifier;
            this.params = parameters;
            this.body = statements;
            this.lineNumber = lineNumber;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitFuncDecl(this);
        }
    }


    public static class Param extends Decl{
        public ASTEnums type;
        public String identifier;

        public Param(ASTEnums type,String identifier,int lineNumber){
            this.type = type;
            this.identifier = identifier;
            this.lineNumber = lineNumber;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitParamDecl(this);
        }
    }

}
