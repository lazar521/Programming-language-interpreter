package ast;

import interpreter.NodeExecutionVisitor;


public interface ASTNode {
    public <T> T execute(NodeExecutionVisitor<T> visitor);  
}
