package ast;

import java.util.List;

public abstract class Expr implements ASTNode{
    // Purpose of the 'type' field is to help us during semantic analysis
    // Not every node will have that field set in their constructors (meaning some will have the UNDEFINED as the default type)
    // But every Expr node will have to have it set by the end of the semantic analisys
    public ASTEnums type = ASTEnums.UNDEFINED;
    public int lineNumber;

    public static class Binary extends Expr{
        public Expr left;
        public Expr right;
        public ASTEnums operator;

        public Binary(Expr left,ASTEnums operator,Expr right,int lineNumber){
            this.left = left;
            this.operator = operator;
            this.right = right;
            this.lineNumber = lineNumber;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) throws Exception {
            return visitor.visitBinaryExpr(this);
        }
    
    }



    public static class Unary extends Expr{
        public Expr expr;
        public ASTEnums operator;

        public Unary(ASTEnums operator,Expr expr,int lineNumber){
            this.expr = expr;
            this.operator = operator;
            this.lineNumber = lineNumber;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) throws Exception {
            return visitor.visitUnaryExpr(this);
        }
    }



    public static class Assign extends Expr{
        public String identifier;
        public Expr expr;

        public Assign(String identifier,Expr expression,int lineNumber){
            this.identifier = identifier;
            this.expr = expression;
            this.lineNumber = lineNumber;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) throws Exception {
            return visitor.visitAssignExpr(this);
        }
    }


    public static class Literal extends Expr{
        public String value;

        public Literal(String value,ASTEnums type,int lineNumber){
            this.value = value;
            this.type = type;
            this.lineNumber = lineNumber;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) throws Exception {
            return visitor.visitLiteralExpr(this);
        }
    }


    public static class Call extends Expr{
        public String funcIdentifier;
        public List<Expr> arguments;


        public Call(String identifier,List<Expr> arguments,int lineNumber){
            this.funcIdentifier = identifier;
            this.arguments = arguments;
            this.lineNumber = lineNumber;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) throws Exception{
            return visitor.visitCallExpr(this);
        }

    }


 

    public static class Variable extends Expr{
        public String identifier;

        public Variable(String identifier,int lineNumber){
            this.identifier = identifier;
            this.lineNumber = lineNumber;
        }

        @Override
        public <T> T accept(ASTVisitor<T> visitor) throws Exception {
            return visitor.visitVariableExpr(this);
        }   
    }

}
