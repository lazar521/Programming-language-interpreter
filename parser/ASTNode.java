package parser;

import interpreter.InterpretVisitor;
import token.Token;

public interface ASTNode {
    public Token execute(InterpretVisitor visitor);
} 
