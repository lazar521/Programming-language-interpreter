package interpreter;

import java.util.List;

import javax.management.relation.RelationSupport;

import ast.*;
import ast.ASTEnums.DataType;
import ast.ASTEnums.Operations;
import ast.Decl.*;
import ast.Stmt.*;
import interpreter.environment.Environment;
import ast.Expr.*;

import java.util.LinkedList;

public class SemanticChecker implements ASTVisitor<Object>{
    private boolean DECLARING_GLOBAL_FUNCTIONS;
    private boolean ERROR_OCCURED;
    private Environment environment;
    private LinkedList<Decl.Func> globalFunctions;



    public void checkSemantics(Program program){
        environment = new Environment();
        globalFunctions = new LinkedList<>();
        ERROR_OCCURED = false;

        // First we find all global functions and note them in our environment
        DECLARING_GLOBAL_FUNCTIONS = true;
        program.accept(this);

        // Now we're actually doing the analysis and type checking
        // Since we've alredy performed type checking for the 
        DECLARING_GLOBAL_FUNCTIONS = false;
        program.accept(this);
    }



    @Override
    public Object visitProgram(Program prog) {
        for(Stmt s: prog.statements){
            s.accept(this);
        }
        
        return null;
    }


    @Override
    public Object visitExprStmt(ExprStmt exprStmt) {
        exprStmt.expr.accept(this);

        // TODO: this part should go into visitBinary that will handle assignment operation
        if(exprStmt.identifier != null){
            if(exprStmt.expr.type != environment.getVarType(exprStmt.identifier)){
                report("Variable " + exprStmt.identifier +" is of type " + environment.getVarType(exprStmt.identifier) +" type " + exprStmt.expr.type +" was assigned");
            }

            // This way we just tell the environment that the varible has been initialized here. It doesn't matter which value we pass since we won't be using it
            // We are only performing semantic checks now.
            environment.assignVar(exprStmt.identifier, null, environment.getVarType(exprStmt.identifier));
        }

        return null;
    }


    @Override
    public Object visitDeclStmt(DeclStmt declStmt) {
        declStmt.decl.accept(this);
        return null;
    }


    @Override
    public Object visitWhileStmt(While whileStmt) {
        whileStmt.condition.accept(this);

        if(whileStmt.condition.type != DataType.INT){
            report("While statement condition expects type INT, but got " + whileStmt.condition.type);
        }

        environment.enterCodeBlock();

        for(Stmt stmt: whileStmt.statemets){
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
            if(forStmt.condition.type != DataType.INT){
                report("For statement condition expects type INT, but got " + forStmt.condition.type);
            }
        }

        if(forStmt.update != null){
            forStmt.update.accept(this);
        }

        for(Stmt stmt: forStmt.statements){
            stmt.accept(this);
        }

        environment.exitCodeBlock();
        return null;
    }



    @Override
    public Object visitIfStmt(If ifStmt) {
        ifStmt.condition.accept(this);

        if(ifStmt.condition.type != DataType.INT){
            report("If statement condition of wrong type. Expected INT, but got " + ifStmt.condition.type);
        }

        environment.enterCodeBlock();

        for(Stmt stmt: ifStmt.statements){
            stmt.accept(this);
        }

        environment.exitCodeBlock();

        return null;
    }



    @Override
    public Object visitRetStmt(Ret retStmt) {
        retStmt.expr.accept(this);

        Decl.Func currentFunc = environment.getCurrentFunction();

        if(retStmt.expr == null){
            if(currentFunc.type != DataType.VOID){
                report("Invalid return type. Expected VOID ,but got " + retStmt.expr.type);
           }
        }
        else{
            if(currentFunc.type != retStmt.expr.type){
                report("Invalid return type. Expected " +  currentFunc.type +" ,but got " + retStmt.expr.type);
            }
        }

        return null;
    }



    @Override
    public Object visitVarDecl(Var varDecl) {
        if(DECLARING_GLOBAL_FUNCTIONS){
            return null;
        }
        
        environment.declareVar(varDecl.identifier,varDecl.type);
        
        // TODO: We will move this in visitBinaryExpression later
        if(varDecl.expr != null){
            varDecl.expr.accept(this);

            if(varDecl.expr.type != varDecl.type){
                report("Assigning a wrong type to variable "+varDecl.identifier+". Expected "+varDecl.type +" ,but got " + varDecl.expr.type);
            }
   
            // This way we just tell the environment that the varible has been initialized here. It doesn't matter which value we pass since we won't be using it
            // We are only performing semantic checks now.
            environment.assignVar(varDecl.identifier, null, varDecl.type);

        }

        return null;
    }



    @Override
    public Object visitFuncDecl(Func funcNode) {
        if(DECLARING_GLOBAL_FUNCTIONS){
            globalFunctions.add(funcNode);
            environment.declareFunction(funcNode.funcName, funcNode, funcNode.type);
            return null;
        }

        environment.enterFunction(funcNode.funcName);
        
        for(Param param:funcNode.params){
            environment.declareVar(param.identifier, param.type);
        }

        for(Stmt stmt: funcNode.statements){
            stmt.accept(this);
        }

        environment.exitFunction();

        return null;
    }


    // We have alredy declared param type in it's constructor
    @Override
    public Object visitParamDecl(Param param) {
        if(param.type == DataType.UNDEFINED || param.type == DataType.VOID){
            report("Invalid parameter data type "+ param.type);
        }

        return null;
    }



    @Override
    public Object visitBinaryExpr(Binary binary) {
        binary.left.accept(this);
        binary.right.accept(this);

        if(binary.left.type != binary.right.type){
            report("Binary expression: Unmatching types " + binary.left.type +" " + binary.operation +" " +binary.right.type);
            binary.type = DataType.UNDEFINED;
            return null;
        }
        
        // If both operands are of type STRING, we need to check if we're applying 
        // permitted operations for the STRING DataType
        binary.type = binary.left.type;
        if(binary.type == DataType.STRING){
           
            switch (binary.operation) {
                case PLUS:
                case EQUAL:
                case NOT_EQUAL:
                    break;  // We're OK

                default:
                    report("Binary expression: Applying invalid operation " + binary.operation + " to STRING data type");
                    binary.type = DataType.UNDEFINED;
            }
        }

        return null;
    }



    @Override
    public Object visitUnaryExpr(Unary unary) {
        unary.expr.accept(this);

        if(unary.expr.type != DataType.INT){
            report("Unary expression: Applying operator " + unary.operation + " to an invalid type");
            unary.type = DataType.UNDEFINED;
        }
        else{
            unary.type = unary.expr.type;
        }

        return null;

    }


    // We had alredy deducted Literal's type in it's constructor 
    @Override
    public Object visitLiteralExpr(Literal expr){
        return null;
    }


    @Override
    public Object visitEnclosedExpr(Enclosed enclosed) {
        enclosed.expr.accept(this);
        enclosed.type = enclosed.expr.type;
        return null;
    }


    @Override
    public Object visitCallExpr(Call call) {
        Decl.Func function = environment.fetchFunc(call.identifier);
        call.type = function.type;

        int argCnt = call.arguments.size();
        int paramCnt = function.params.size();

        if(argCnt != paramCnt){
            report("Call to "+ call.identifier + ". Wrong number of arguments. Expected " + paramCnt +" ,but got "+ argCnt);
        }

        int argsToCheck = Math.min(argCnt,paramCnt);
        for(int i=0;i<argsToCheck;i++){
            if(call.arguments.get(i).type != function.params.get(i).type){
                report("Call to "+call.identifier+" .Argument number " + Integer.toString(i+1) + "is of wrong type. Expected " + function.params.get(i).type +" ,but got" + call.arguments.get(i).type);
            }
        }

        return null;
    }


    @Override
    public Object visitVariableExpr(Variable variable) {
        variable.type = environment.getVarType(variable.identifier);
        
        // We can't use uninitialized variables. This checks if we've intialized the variable before using it.
        // If not it throws an error
        environment.fetchVar(variable.identifier);

        return null;
    }



    private void report(String s){
        ERROR_OCCURED = true;
        System.out.println("Error: " + s);
    }
}