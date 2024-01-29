package interpreter;

import ast.Expr;
import ast.Expr.Binary;
import ast.Expr.Enclosed;
import ast.Expr.Literal;
import ast.Expr.Unary;
import token.TType;
import token.Token;

public class astPrinter implements InterpretVisitor<Void>{
    private static int indent = 0;
    
    
    private static String printIndent(){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<indent;i++){
            sb.append("    ");
        }
        return sb.toString();
    }

    @Override
    public Void executeExprBinary(Expr.Binary expr) {
        System.out.println(printIndent()+"binary "+expr.operation.getType());
        indent++;
        expr.left.execute(this);
        expr.right.execute(this);
        indent--;
        return null;
    }

    @Override
    public Void executeExprUnary(Expr.Unary expr) {
        System.out.println(printIndent()+"unary "+expr.operation.getType());
        indent++;
        expr.expr.execute(this);
        indent--;
        return null;
    }

    @Override
    public Void executeExprLiteral(Expr.Literal expr) {
        System.out.println(printIndent()+"literal "+expr.value.getValue());
        return null;
    }

    @Override
    public Void executeExprEnclosed(Expr.Enclosed expr) {
        System.out.println(printIndent()+"ENCLOSING");
        indent++;
        expr.expr.execute(this);
        indent--;
        return null;
    }

    public static void main(String[] args){
        Expr expr = new Expr.Binary(
            new Expr.Unary(new Token(null,TType.MINUS,1), new Expr.Literal(new Token("123", TType.NUM_LITERAL,1 ))),
            new Token(null, TType.STAR, 1),
            new Expr.Enclosed(new Expr.Literal(new Token("45,24", TType.NUM_LITERAL, 1)))
            );
        astPrinter ap = new astPrinter();

        expr.execute(ap);
    }
}
