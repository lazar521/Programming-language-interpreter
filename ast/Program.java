package ast;

import java.util.List;

public class Program implements ASTNode {
    public List<Stmt.DeclStmt> funcDeclStatements;
    public List<Stmt.DeclStmt> varDeclStatements;

    public Program(List<Stmt.DeclStmt> funcDeclarations,List<Stmt.DeclStmt> varDeclarations){
        this.funcDeclStatements = funcDeclarations;
        this.varDeclStatements = varDeclarations;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) throws Exception {
        return visitor.visitProgram(this);
    }

    
}
