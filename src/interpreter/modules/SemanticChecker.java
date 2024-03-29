package interpreter.modules;

import java.util.ArrayList;
import java.util.Collections;

import ast.*;
import ast.Decl.*;
import ast.Stmt.*;
import interpreter.environment.Environment;
import interpreter.environment.BuiltIns;
import ast.Expr.*;



// We visit every node in the AST to check if it is semantically correct. When we pass our AST to the Executor class it can just execute 
// every node without worrying if, for example, we're trying to multiply an int with a string.

// All Expr nodes have a'type' field. Some other node classes also have the 'type' field but they set that field in their constructors
// while only some Expr nodes don't. In Expr nodes the 'type' field tells us which type of data that node will return when executed

// If a node has an Expr node as its child, it must first visit the child node before checking the child's 'type' field.
// Expr nodes deduce and set their own 'type' field, so we must visit them first before checking their type


public class SemanticChecker implements ASTVisitor<Object>{
    private boolean ERROR_OCCURED;
    private Environment env;
    private int loopNesting;
    private ArrayList<Error> errorList;

    public void checkSemantics(Program program) throws Exception{
        env = new Environment();
        errorList = new ArrayList<>();
        ERROR_OCCURED = false;
        loopNesting = 0;

        program.accept(this);

        
        if(ERROR_OCCURED) {
            Collections.sort(errorList);
            
            for(Error error: errorList){
                System.out.println(error.message);
            }
            throw new Exception();
        }
    }


    @Override
    public Object visitProgram(Program prog) throws Exception {

        // First we visit all of global variable declarations
        // We check their semantics first so that we can declare them rightaway and make them visible to every function in the program
        for(Stmt stmt:prog.varDeclStatements){
            stmt.accept(this);
        }

        // Now we need to declare all the functions to make them visible to the whole program
        // For now we're just checking their parameter's semantics and declaring them. We're not checking function bodies here
        
        // We start with built-in funcitons
        BuiltIns.declareBuiltIns(env);

        // Then we declare the rest of the functions in the program 
        for(Stmt.DeclStmt declarationStatement : prog.funcDeclStatements){
            Decl.Func funcDeclaration = (Decl.Func) declarationStatement.declaration;

            // If a function with the same name has alredy been declared we need to report that. If not then we just declare our function.
            if(env.isFuncDeclared(funcDeclaration.identifier)){
                report(funcDeclaration.lineNumber,"Declaring a function with the same name twice '" + funcDeclaration.identifier + "' , ignoring the second one");
            }
            else{
                env.declareFunction(funcDeclaration.identifier, funcDeclaration, funcDeclaration.type);
            }
        }

        // Check if main() function is present
        if(!env.isFuncDeclared("main")){
            report(0, "Cannot execute without main() function");
        }
        else{
            Decl.Func mainFunc = env.fetchFunc("main");
            if(mainFunc.params.size() != 0){
                report(mainFunc.lineNumber, "The main() function cannot take any parameters");
            }
        }


        // Now we're analyzing the body of each function
        for(Stmt funcDeclStmt:prog.funcDeclStatements){
            funcDeclStmt.accept(this);
        }
        
        return null;
    }



    @Override
    public Object visitExprStmt(ExprStmt exprStmt) throws Exception {
        exprStmt.expr.accept(this);
        return null;
    }



    @Override
    public Object visitDeclStmt(DeclStmt declStmt) throws Exception {
        declStmt.declaration.accept(this);
        return null;
    }



    @Override
    public Object visitWhileStmt(While whileStmt) throws Exception {
        whileStmt.condition.accept(this);

        if(whileStmt.condition.type != ASTEnums.INT){
            report(whileStmt.lineNumber,"While statement condition expects type INT, but got " + whileStmt.condition.type);
        }

        env.enterCodeBlock();

        loopNesting++;
        for(Stmt stmt: whileStmt.body){
            stmt.accept(this);
        }
        loopNesting--;

        env.exitCodeBlock();

        return null;
    }



    @Override
    public Object visitForStmt(For forStmt) throws Exception {
        env.enterCodeBlock();

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

        loopNesting++;
        for(Stmt stmt: forStmt.body){
            stmt.accept(this);
        }
        loopNesting--;

        env.exitCodeBlock();
        return null;
    }



    @Override
    public Object visitIfStmt(If ifStmt) throws Exception {
        ifStmt.condition.accept(this);

        if(ifStmt.condition.type != ASTEnums.INT){
            report(ifStmt.lineNumber,"If statement condition of wrong type. Expected INT, but got " + ifStmt.condition.type);
        }

        env.enterCodeBlock();
        for(Stmt stmt: ifStmt.body){
            stmt.accept(this);
        }
        env.exitCodeBlock();


        if(ifStmt.elseBody != null){

            env.enterCodeBlock();
            for(Stmt stmt: ifStmt.elseBody){
                stmt.accept(this);
            }
            env.exitCodeBlock();

        }

        return null;
    }



    @Override
    public Object visitRetStmt(Ret retStmt) throws Exception {
        Decl.Func function = env.fetchCurrentFunction();

        if(retStmt.expr == null){
            if(function.type != ASTEnums.VOID){
                report(retStmt.lineNumber,"Invalid return type. Expected VOID ,but got " + retStmt.expr.type);
           }
        }
        else {
            retStmt.expr.accept(this);
            if(function.type != retStmt.expr.type){
                report(retStmt.lineNumber,"Invalid return type. Expected " +  function.type +" ,but got " + retStmt.expr.type);
            }
        }
        return null;
    }



    @Override
    public Object visitVarDecl(Decl.Var varDecl) throws Exception { 
        if(loopNesting > 0){
            report(varDecl.lineNumber, "Cannot declare variables inside loops '" + varDecl.identifier + "'");
        }
        
        if(varDecl.type == ASTEnums.VOID){
            report(varDecl.lineNumber,"Declaring a variable with type VOID '" + varDecl.identifier + "'");
            varDecl.type = ASTEnums.UNDEFINED;
        }

        // We can declare variable multiple times BUT NOT in the same block scope
        if(env.isVarDeclaredInCurrentBlock(varDecl.identifier)){
            report(varDecl.lineNumber,"Declaring a variable with the same name twice in same code block '" + varDecl.identifier +"' , ignoring the second one");
        }
        else{
            env.declareVar(varDecl.identifier,varDecl.type);
        }


        if(varDecl.expr != null){
            varDecl.expr.accept(this);

            // Check if the expression returns the data type that is expected
            if(varDecl.expr.type != varDecl.type && varDecl.type != ASTEnums.UNDEFINED){
                report(varDecl.lineNumber,"Assigning a wrong type to variable '"+varDecl.identifier+"' . Expected "+varDecl.type +" ,but got " + varDecl.expr.type);
            }
   
            // This way we just tell the environment that a varible has been initialized. 
            // It doesn't matter which value we pass since we won't be using it. We are only performing semantic checks now.
            env.assignVar(varDecl.identifier, null);

        }

        return null;
    }



    @Override
    public Object visitFuncDecl(Func funcNode) throws Exception {
        
        // We've alredy declared all the global functions. So if it turns out that now we're visiting a function  
        // that hasn't been been declared, it means that it is nested inside another function and we don't allow that
        // In fact, we won't even analyze the nested function's body
        if(!env.isFuncDeclared(funcNode.identifier)){
            report(funcNode.lineNumber, "Nesting functions is not allowed '" + funcNode.identifier + "'");
            return null;
        }

        env.enterFunction(funcNode);
        
        // Note that we have alredy checked param semantics of every function at the start of the semantic analisys process
        // So we don't need to do that here
        for(Param param:funcNode.params){

            // checking parameter semantics
            param.accept(this);

            if(env.isVarDeclared(param.identifier)){
                report(funcNode.lineNumber,"Declaring two or more parameters with the same name '" + param.identifier +"' , ignoring the second one");
            }
            else{
                // Here we're telling the environment that the parameters are declared and initialized inside the funciton body
                env.declareVar(param.identifier, param.type);
                env.assignVar(param.identifier, null);
            }

        }

        for(Stmt stmt: funcNode.body){
            stmt.accept(this);
        }

        // If the function's return type is not VOID, then it must end with a return statement
        if(funcNode.type != ASTEnums.VOID){
            Stmt lastStatement = funcNode.body.get(funcNode.body.size()-1);
            if( !(lastStatement instanceof Stmt.Ret) ){
                report(funcNode.lineNumber, "Function '" + funcNode.identifier +"' of type " + funcNode.type + " should have a return statement at the end");
            }
        }

        env.exitFunction();

        return null;
    }


    // We have alredy know param's type. It was given in it's constructor, there's nothing to do here
    @Override
    public Object visitParamDecl(Param param) {
        return null;
    }



    @Override
    public Object visitBinaryExpr(Expr.Binary binary) throws Exception {
        binary.left.accept(this);
        binary.right.accept(this);

        if(binary.left.type != binary.right.type){
            report(binary.lineNumber,"Binary expression: Unmatching types " + binary.left.type +" " + binary.operator +" " +binary.right.type);
            binary.type = ASTEnums.UNDEFINED;
            return null;
        }
        
        // If both operands are of type STRING, we need to check if we're using 
        // permitted operations for the STRING data type
        
        if(binary.left.type == ASTEnums.STRING){
           
            switch (binary.operator) {
                case PLUS:
                    binary.type = ASTEnums.STRING;
                    break;

                // Comparison operations return INT ( 1 or 0 )
                case EQUAL:
                case NOT_EQUAL:
                    binary.type = ASTEnums.INT;
                    break;  

                default:
                    report(binary.lineNumber,"Binary expression: Applying invalid operation " + binary.operator + " to STRING data type");
                    binary.type = ASTEnums.UNDEFINED;
            }
        }
        else{
            binary.type = binary.left.type;
        }

        return null;
    }



    @Override
    public Object visitUnaryExpr(Unary unary) throws Exception {
        unary.expr.accept(this);

        if(unary.expr.type != ASTEnums.INT){
            report(unary.lineNumber,"Unary expression: Applying operator " + unary.operator + " to a " + unary.expr.type + " data type");
            unary.type = ASTEnums.UNDEFINED;
        }
        else{
            unary.type = unary.expr.type;
        }
        

        return null;

    }


    // We have alredy know literal's type. It was given in it's constructor
    @Override
    public Object visitLiteralExpr(Literal literal){
        return null;
    }


    @Override
    public Object visitAssignExpr(Assign assignment) throws Exception {
        ASTEnums varType;

        if(env.isVarDeclared(assignment.identifier)){
            varType = env.fetchVarType(assignment.identifier);
            // This way we just tell the environment that here the variable has been initialized (or assigned to)
            env.assignVar(assignment.identifier, null);
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
    public Object visitCallExpr(Call call) throws Exception {
        if(!env.isFuncDeclared(call.funcIdentifier)){
            report(call.lineNumber,"Calling an undeclared function '" + call.funcIdentifier + "'");
            call.type = ASTEnums.UNDEFINED;
            return null;
        }


        Decl.Func function = env.fetchFunc(call.funcIdentifier);
        
        call.type = function.type;

        int argCnt = call.arguments.size();
        int paramCnt = function.params.size();

        if(argCnt != paramCnt){
            report(call.lineNumber,"Call to '"+ call.funcIdentifier + "' ==> Wrong number of arguments. Expected " + paramCnt +" ,but got "+ argCnt);
        }

        // Check if the given arguments match the expected parameter types 
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
        if(env.isVarDeclared(variable.identifier)){
            variable.type = env.fetchVarType(variable.identifier);
            
            if(!env.isVarInitialized(variable.identifier)){
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
        errorList.add( new Error(lineNumber, "Line " + lineNumber +": " + s) );
    }


    // Error message wrapper 
    private static class Error implements Comparable<Error>{
        public int line;
        public String message;

        public Error(int line,String msg){
            this.line = line;
            this.message = msg;
        }


        @Override
        public int compareTo(Error error) {
            if (this.line < error.line) return -1;
            if (this.line > error.line) return 1;
            return 0;
        }
    }

}