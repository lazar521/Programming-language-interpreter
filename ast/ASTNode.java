package ast;

public interface ASTNode {
    public <T> T accept(ASTVisitor<T> visitor) throws Exception;
}
