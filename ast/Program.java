package ast;

import java.util.List;

public class Program implements ASTNode {
    public List<Stmt.DeclStmt> funcDeclarations;
    public List<Stmt.DeclStmt> varDeclarations;

    public Program(List<Stmt.DeclStmt> funcDeclarations,List<Stmt.DeclStmt> varDeclarations){
        this.funcDeclarations = funcDeclarations;
        this.varDeclarations = varDeclarations;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) throws Exception {
        return visitor.visitProgram(this);
    }

    
}
