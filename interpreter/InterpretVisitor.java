package interpreter;

import parser.Expr;
import token.*;

public interface InterpretVisitor {
    public Token executeExprBinary(Expr.Binary expr) ;
    public Token executeExprUnary(Expr.Unary expr) ;
    public Token executeExprLiteral(Expr.Literal expr) ;
    public Token executeExprEnclosed(Expr.Enclosed expr) ;
} 