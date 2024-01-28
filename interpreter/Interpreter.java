package interpreter;

import parser.Expr.Binary;
import parser.Expr.Enclosed;
import parser.Expr.Literal;
import parser.Expr.Unary;
import token.*;

public class Interpreter implements InterpretVisitor{

    @Override
    public Token executeExprBinary(Binary expr) {
        // Token left = expr.left.execute(this);
        // Token right = expr.right.execute(this);

        // if(left.getType() != right.getType()) throw new Exception("Different type operands");

        // if(left.getType() == TType.STRING_LITERAL){
        //     if(expr.operation.getType() == TType.PLUS) {
        //         return new Token(left.getValue() + right.getValue(), TType.STRING_LITERAL, left.getLineNumber());
        //     }
        //     else throw new Exception("Invalid string operation: Line " + left.getLineNumber());
        // }


        // switch(left.getType()){
        //     case PLUS:

        // }

        throw new UnsupportedOperationException("Unimplemented method 'executeExprUnary'");

    }

    @Override
    public Token executeExprUnary(Unary expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeExprUnary'");
    }

    @Override
    public Token executeExprLiteral(Literal expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeExprLiteral'");
    }

    @Override
    public Token executeExprEnclosed(Enclosed expr) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeExprEnclosed'");
    }
    
}
