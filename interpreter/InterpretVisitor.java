package interpreter;

import ast.Expr;
import token.*;

public interface InterpretVisitor <T> {
    public T executeExprBinary(Expr.Binary expr) ;
    public T executeExprUnary(Expr.Unary expr) ;
    public T executeExprLiteral(Expr.Literal expr) ;
    public T executeExprEnclosed(Expr.Enclosed expr) ;
} 