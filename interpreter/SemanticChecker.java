package interpreter;



import ast.*;
import ast.Decl.*;
import ast.Stmt.*;
import interpreter.environment.Environment;
import ast.Expr.*;


public class SemanticChecker implements ASTVisitor<Object>{
    private boolean ERROR_OCCURED;
    private Environment environment;


    public boolean checkSemantics(Program program){
        environment = new Environment();
        ERROR_OCCURED = false;
        program.accept(this);

        return !ERROR_OCCURED;
    }



    @Override
    public Object visitProgram(Program prog) {
        for(Stmt stmt:prog.varDeclarations){
            stmt.accept(this);
        }

        // First we declare all the functions before checking their semantics
        // We do this to make all functions visible to the whole program
        for(Stmt.DeclStmt declarationStatement : prog.funcDeclarations){
            Decl.Func funcDeclaration = (Decl.Func) declarationStatement.declaration;
            
            // We also need to check function parameter semantics 
            for(Decl.Param param : funcDeclaration.params){
                param.accept(this);
            }

            if(environment.isFuncDeclared(funcDeclaration.identifier)){
                report(funcDeclaration.lineNumber,"Declaring a function with the same name twice " + funcDeclaration.identifier + "'");
            }
            else{
                environment.declareFunction(funcDeclaration.identifier, funcDeclaration, funcDeclaration.type);
            }
        }

        if(!environment.isFuncDeclared("main")){
            report(0, "Cannot execute without main() functions");
        }

        // Here we're checking function bodies
        for(Stmt stmt:prog.funcDeclarations){
            stmt.accept(this);
        }
        
        return null;
    }



    @Override
    public Object visitExprStmt(ExprStmt exprStmt) {
        exprStmt.expr.accept(this);
        return null;
    }



    @Override
    public Object visitDeclStmt(DeclStmt declStmt) {
        declStmt.declaration.accept(this);
        return null;
    }



    @Override
    public Object visitWhileStmt(While whileStmt) {
        whileStmt.condition.accept(this);

        if(whileStmt.condition.type != ASTEnums.INT){
            report(whileStmt.lineNumber,"While statement condition expects type INT, but got " + whileStmt.condition.type);
        }

        environment.enterCodeBlock();

        for(Stmt stmt: whileStmt.body){
            stmt.accept(this);
        }

        environment.exitCodeBlock();

        return null;
    }



    @Override
    public Object visitForStmt(For forStmt) {
        environment.enterCodeBlock();

        if(forStmt.varDeclaration != null){
            forStmt.varDeclaration.accept(this);
        }

        if(forStmt.condition != null){
            forStmt.condition.accept(this);
            if(forStmt.condition.type != ASTEnums.INT){
                report(forStmt.lineNumber,"For statement condition expects type INT, but got " + forStmt.condition.type);
            }
        }

        if(forStmt.update != null){
            forStmt.update.accept(this);
        }

        for(Stmt stmt: forStmt.body){
            stmt.accept(this);
        }

        environment.exitCodeBlock();
        return null;
    }



    @Override
    public Object visitIfStmt(If ifStmt) {
        ifStmt.condition.accept(this);

        if(ifStmt.condition.type != ASTEnums.INT){
            report(ifStmt.lineNumber,"If statement condition of wrong type. Expected INT, but got " + ifStmt.condition.type);
        }

        environment.enterCodeBlock();
        for(Stmt stmt: ifStmt.body){
            stmt.accept(this);
        }
        environment.exitCodeBlock();


        if(ifStmt.elseBody != null){

            environment.enterCodeBlock();
            for(Stmt stmt: ifStmt.elseBody){
                stmt.accept(this);
            }
            environment.exitCodeBlock();

        }

        return null;
    }



    @Override
    public Object visitRetStmt(Ret retStmt) {
        retStmt.expr.accept(this);

        Decl.Func function = environment.fetchCurrentFunction();

        if(retStmt.expr == null){
            if(function.type != ASTEnums.VOID){
                report(retStmt.lineNumber,"Invalid return type. Expected VOID ,but got " + retStmt.expr.type);
           }
        }
        else{
            if(function.type != retStmt.expr.type){
                report(retStmt.lineNumber,"Invalid return type. Expected " +  function.type +" ,but got " + retStmt.expr.type);
            }
        }

        return null;
    }



    @Override
    public Object visitVarDecl(Decl.Var varDecl) { 
        if(varDecl.type == ASTEnums.VOID){
            report(varDecl.lineNumber,"Declaring a variable with type void '" + varDecl.identifier + "'");
            varDecl.type = ASTEnums.UNDEFINED;
        }

        if(environment.isVarDeclaredLocally(varDecl.identifier)){
            report(varDecl.lineNumber,"Declaring a variable with the same name in same code block '" + varDecl.identifier +"'");
        }
        else{
            environment.declareVar(varDecl.identifier,varDecl.type);
        }


        if(varDecl.expr != null){
            varDecl.expr.accept(this);

            if(varDecl.expr.type != varDecl.type && varDecl.type != ASTEnums.UNDEFINED){
                report(varDecl.lineNumber,"Assigning a wrong type to variable "+varDecl.identifier+". Expected "+varDecl.type +" ,but got " + varDecl.expr.type);
            }
   
            // This way we just tell the environment that a varible has been initialized. It doesn't matter which value we pass since we won't be using it
            // We are only performing semantic checks now.
            environment.assignVar(varDecl.identifier, null);

        }

        return null;
    }



    @Override
    public Object visitFuncDecl(Func funcNode) {

        environment.enterFunction(funcNode.identifier);
        
        // Note that we have alredy checked param semantics of every function at the start of the semantic analisys process
        // So we don't need to do that here
        for(Param param:funcNode.params){

            if(environment.isVarDeclared(param.identifier)){
                report(funcNode.lineNumber,"Declaring two or more parameters with the same name '" + param.identifier +"'");
            }
            else{
                // Here we're telling the environment that the parameters are declared and initialized inside the funciton body
                environment.declareVar(param.identifier, param.type);
                environment.assignVar(param.identifier, null);
            }

        }

        for(Stmt stmt: funcNode.body){
            stmt.accept(this);
        }

        environment.exitFunction();

        return null;
    }


    // We have alredy know param's type. It was given in it's constructor
    @Override
    public Object visitParamDecl(Param param) {
        if(param.type == ASTEnums.UNDEFINED || param.type == ASTEnums.VOID){
            report(param.lineNumber,"Invalid parameter data type "+ param.type);
        }

        return null;
    }



    @Override
    public Object visitBinaryExpr(Expr.Binary binary) {
        binary.left.accept(this);
        binary.right.accept(this);

        if(binary.left.type != binary.right.type){
            report(binary.lineNumber,"Binary expression: Unmatching types " + binary.left.type +" " + binary.operator +" " +binary.right.type);
            binary.type = ASTEnums.UNDEFINED;
            return null;
        }
        
        // If both operands are of type STRING, we need to check if we're applying 
        // permitted operations for the STRING ASTEnums
        binary.type = binary.left.type;
        if(binary.type == ASTEnums.STRING){
           
            switch (binary.operator) {
                case PLUS:
                case EQUAL:
                case NOT_EQUAL:
                    break;  // We're OK

                default:
                    report(binary.lineNumber,"Binary expression: Applying invalid operation " + binary.operator + " to STRING data type");
                    binary.type = ASTEnums.UNDEFINED;
            }
        }

        return null;
    }



    @Override
    public Object visitUnaryExpr(Unary unary) {
        unary.expr.accept(this);
        unary.type = unary.expr.type;

        if(unary.type != ASTEnums.INT){
            report(unary.lineNumber,"Unary expression: Applying operator " + unary.operator + " to an invalid type");
            unary.type = ASTEnums.UNDEFINED;
        }
        

        return null;

    }


    // We have alredy know literal's type. It was given in it's constructor
    @Override
    public Object visitLiteralExpr(Literal literal){
        return null;
    }


    @Override
    public Object visitAssignExpr(Assign assignment) {
        ASTEnums varType;

        if(environment.isVarDeclared(assignment.identifier)){
            varType = environment.fetchVarType(assignment.identifier);
            // This way we just tell the environment that here the variable has been initialized or assigned to
            environment.assignVar(assignment.identifier, null);
        }
        else{
            report(assignment.lineNumber,"Assigning to an undeclared variable '" + assignment.identifier + "'");
            varType = ASTEnums.UNDEFINED;
        }
        
        assignment.expr.accept(this);

        if(assignment.expr.type != varType && varType != ASTEnums.UNDEFINED){
            report(assignment.lineNumber,"Wrong type being assigned to '"+assignment.identifier+"' . Expected " + varType + " , but got " + assignment.expr.type);
        }


        return null;
    }


    @Override
    public Object visitCallExpr(Call call) {
        if(!environment.isFuncDeclared(call.funcIdentifier)){
            report(call.lineNumber,"Calling an undeclared function '" + call.funcIdentifier + "'");
            call.type = ASTEnums.UNDEFINED;
            return null;
        }


        Decl.Func function = environment.fetchFunc(call.funcIdentifier);
        
        call.type = function.type;

        int argCnt = call.arguments.size();
        int paramCnt = function.params.size();

        if(argCnt != paramCnt){
            report(call.lineNumber,"Call to '"+ call.funcIdentifier + "' ==> Wrong number of arguments. Expected " + paramCnt +" ,but got "+ argCnt);
        }

        int argsToCheck = Math.min(argCnt,paramCnt);
        for(int i = 0; i < argsToCheck; i++){
            call.arguments.get(i).accept(this);

            if(call.arguments.get(i).type != function.params.get(i).type){
                report(call.lineNumber,"Call to '"+call.funcIdentifier+"'' ==> Argument number " + Integer.toString(i+1) + " is of wrong type. Expected " + function.params.get(i).type +" ,but got " + call.arguments.get(i).type);
            }
        }

        return null;
    }


    @Override
    public Object visitVariableExpr(Variable variable) {
        if(environment.isVarDeclared(variable.identifier)){
            variable.type = environment.fetchVarType(variable.identifier);
            
            if(!environment.isVarInitialized(variable.identifier)){
                report(variable.lineNumber, "Using an uninitialized variable '" + variable.identifier + "'");
            }

        }
        else{
            report(variable.lineNumber,"Using an undeclared variable " + variable.identifier + "'");
            variable.type = ASTEnums.UNDEFINED;
        }

        return null;
    }



    private void report(int lineNumber,String s){
        ERROR_OCCURED = true;
        System.out.println("Line " + lineNumber +": " + s);
    }
}