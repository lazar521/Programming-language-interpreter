package ast;

import token.*;
import java.util.ArrayList;
import ast.ASTEnums.*;

public abstract class Decl  implements ASTNode {  
 

    public static class Var extends Decl {
        public DataType type;
        public String identifier;
        public Expr expr;

        public Var(Token type,Token ident,Expr expr){
            this.type = ASTEnums.toAstType(type.getType());
            this.identifier = ident.getValue();
            this.expr = expr;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitVarDecl(this);
        }
    }


    public static class Func extends Decl{
        public DataType type;
        public String funcName;
        public ArrayList<Param> params;
        public ArrayList<Stmt> statements;

        public Func(Token type,Token ident,ArrayList<Param> parameters,ArrayList<Stmt> stmts){
            this.type = ASTEnums.toAstType(type.getType());
            this.funcName = ident.getValue();
            this.params = parameters;
            this.statements = stmts;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitFuncDecl(this);
        }
    }


    public static class Param extends Decl{
        public DataType type;
        public String identifier;

        public Param(Token type,Token ident){
            this.type = ASTEnums.toAstType(type.getType());
            this.identifier = ident.getValue();
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitParamDecl(this);
        }
    }

}
