package ast;

import token.*;
import ast.ASTEnums.*;
import java.util.List;

public abstract class Expr implements ASTNode{

    // This is a field that every Expr subtype will have.
    // Its purpose is to help us perform semantic analysis
    // We won't be assigning it rightaway, but in SemanticChecker class
    public DataType type = DataType.UNDEFINED;


    public static class Binary extends Expr{
        public Expr left;
        public Expr right;
        public Operations operation;

        public Binary(Expr left,Token operation,Expr right){
            this.left = left;
            this.operation = ASTEnums.toAstOperation(operation.getType());
            this.right = right;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    
    }



    public static class Unary extends Expr{
        public Expr expr;
        public Operations operation;

        public Unary(Token operation,Expr expr){
            this.expr = expr;
            this.operation = ASTEnums.toAstOperation(operation.getType());
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }



    public static class Literal extends Expr{
        public String value;

        public Literal(Token literal){
            this.value = literal.getValue();
            this.type = ASTEnums.toAstType(literal.getType());
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }


    public static class Call extends Expr{
        public String identifier;
        public List<Expr> arguments;


        public Call(Token ident,List<Expr> args){
            this.identifier = ident.getValue();
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
        public String identifier;

        public Variable(Token ident){
            this.identifier = ident.getValue();
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) {
            return visitor.visitVariableExpr(this);
        }   
    }

}
