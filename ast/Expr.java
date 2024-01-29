package ast;

import token.*;
import interpreter.InterpretVisitor;


public abstract class Expr{

    public abstract <T> T execute(InterpretVisitor<T> visitor);


    public static class Binary extends Expr{
        public Expr left;
        public Expr right;
        public Token operation;

        public Binary(Expr left,Token operation,Expr right){
            this.left = left;
            this.operation = operation;
            this.right = right;
        }

        @Override
        public <T> T execute(InterpretVisitor<T> visitor) {
            return visitor.executeExprBinary(this);
        }
    
    }



    public static class Unary extends Expr{
        public Expr expr;
        public Token operation;

        public Unary(Token operation,Expr expr){
            this.expr = expr;
            this.operation = operation;
        }

        @Override
        public <T> T execute(InterpretVisitor<T> visitor) {
            return visitor.executeExprUnary(this);
        }
    }



    public static class Literal extends Expr{
        public Token value;

        public Literal(Token value){
            this.value = value;
        }

        @Override
        public <T> T execute(InterpretVisitor<T> visitor) {
            return visitor.executeExprLiteral(this);
        }
    }



    public static class Enclosed extends Expr{
        public Expr expr;

        public  Enclosed(Expr expr){
            this.expr = expr;
        }

        @Override
        public <T> T execute(InterpretVisitor<T> visitor) {
            return visitor.executeExprEnclosed(this);
        }
    }


}
