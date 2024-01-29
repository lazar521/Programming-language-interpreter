package ast;

import token.*;
import java.util.ArrayList;

public abstract class Decl {
    

    public class Var extends Decl {
        public Token identifier;
        public Expr expr;

        public Var(Token ident,Expr expr){
            this.identifier = ident;
            this.expr = expr;
        }

    }


    public class Func extends Decl{
        public Token type;
        public Token funcName;
        public ArrayList<Param> params;

        public Func(Token type,Token ident,ArrayList<Param> parameters){
            this.type = type;
            this.funcName = ident;
            this.params = parameters;
        }
    }

    public class Param extends Decl{
        public Token type;
        public Token identifier;

        public Param(Token type,Token ident){
            this.type = type;
            this.identifier = ident;
        }
    }

}
