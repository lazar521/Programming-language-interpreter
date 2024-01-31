package ast;

import token.*;
import java.util.ArrayList;

import interpreter.NodeExecutionVisitor;


public abstract class Decl  implements ASTNode {  
 

    public static class Var extends Decl {
        public Token type;
        public Token identifier;
        public Expr expr;

        public Var(Token type,Token ident,Expr expr){
            this.type = type;
            this.identifier = ident;
            this.expr = expr;
        }

        @Override
        public <T> T execute(NodeExecutionVisitor<T> visitor) {
            return visitor.executeVarDecl(this);
        }
    }


    public static class Func extends Decl{
        public Token type;
        public Token funcName;
        public ArrayList<Param> params;
        public ArrayList<Stmt> statements;

        public Func(Token type,Token ident,ArrayList<Param> parameters,ArrayList<Stmt> stmts){
            this.type = type;
            this.funcName = ident;
            this.params = parameters;
            this.statements = stmts;
        }

        @Override
        public <T> T execute(NodeExecutionVisitor<T> visitor) {
            return visitor.executeFuncDecl(this);
        }
    }

    public static class Param extends Decl{
        public Token type;
        public Token identifier;

        public Param(Token type,Token ident){
            this.type = type;
            this.identifier = ident;
        }

        @Override
        public <T> T execute(NodeExecutionVisitor<T> visitor) {
            return visitor.executeParamDecl(this);
        }
    }

}
