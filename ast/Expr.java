package ast;

import token.*;

import java.util.List;

public abstract class Expr implements ASTNode{
    public DataType type = DataType.UNDEFINED;


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
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitBinaryExpr(this);
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
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }



    public static class Literal extends Expr{
        public Token value;

        public Literal(Token value){
            this.value = value;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }


    public static class Call extends Expr{
        public Token identifier;
        public List<Expr> arguments;


        public Call(Token ident,List<Expr> args){
            this.identifier = ident;
            this.arguments = args;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor){
            return visitor.visitCallExpr(this);
        }

    }


    public static class Enclosed extends Expr{
        public Expr expr;

        public  Enclosed(Expr expr){
            this.expr = expr;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitEnclosedExpr(this);
        }
    }


    public static class Variable extends Expr{
        public Token identifier;

        public Variable(Token ident){
            this.identifier = ident;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitVariableExpr(this);
        }   
    }

}
