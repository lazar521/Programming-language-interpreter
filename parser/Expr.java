package parser;

import token.*;
import interpreter.InterpretVisitor;


public abstract class Expr implements ASTNode{


    public static class Binary extends Expr{
        public Expr left;
        public Expr right;
        public Token operation;

        public Binary(Expr left,Token operation,Expr right){
            this.left = left;
            this.operation = operation;
            this.right = right;
        }

        public Token execute(InterpretVisitor visitor) {
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

        public Token execute(InterpretVisitor visitor) {
            return visitor.executeExprUnary(this);
        }
    }



    public static class Literal extends Expr{
        Token value;

        public Literal(Token value){
            this.value = value;
        }

        public Token execute(InterpretVisitor visitor) {
            return visitor.executeExprLiteral(this);
        }
    }



    public static class Enclosed extends Expr{
        private Expr expr;

        Enclosed(Expr expr){
            this.expr = expr;
        }

        public Token execute(InterpretVisitor visitor) {
            return visitor.executeExprEnclosed(this);
        }
    }


}
