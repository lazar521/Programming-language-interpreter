package ast;

import java.util.List;

public class Program implements ASTNode {
    public List<Stmt> statements;

    public Program(List<Stmt> declStmts){
        this.statements = declStmts;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitProgram(this);
    }

    
}
