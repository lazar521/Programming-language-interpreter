package interpreter.environment;


import java.util.Stack;

import ast.Decl;
import ast.ASTEnums;



// The error messages from Environment module are intended for the programmer, not the user.
// It's SemanticChecker's job to inform the user 

public class Environment{ 
    DeclarationtTable functionTable;
    DeclarationtTable globalScope;
    DeclarationtTable localScope;
    Stack<FunctionCall> callStack;


    public Environment(){
        this.functionTable = new DeclarationtTable();
        this.globalScope = new DeclarationtTable();
        this.localScope = null;
        this.callStack = new Stack<>();
    }

    
    //  OPERATIONS REGARDING FUNCTIONS 

    public void declareFunction(String name,Decl.Func funcNode, ASTEnums returnType){
        if(functionTable.isDeclared(name)){
            error("Environment.declareFunction: Declaring a function with the same name twice '" + name + "'");
            return;
        }

        functionTable.declare(name, returnType);
        functionTable.assign(name, funcNode);
    }


    public Decl.Func fetchFunc(String name){
        if(!functionTable.isDeclared(name)){
            error("Environment.fetchFunc: Fetching an undeclared function '" + name + "'");
            return null;
        }

        return (Decl.Func) functionTable.fetch(name);
    }


    public ASTEnums getFuncReturnType(String name){
        if(!functionTable.isDeclared(name)){
            error("Environment.getFuncReturnType: Checking the type of an undeclared function");
            return ASTEnums.UNDEFINED;

        }

        return functionTable.getType(name);
    }


    public Decl.Func fetchCurrentFunction(){
        if(callStack.empty()){
            error("Environment.fetchCurrentFunction: Fetching current function, but the call stack is empty");
            return null;
        }

        return fetchFunc(callStack.peek().name);
    }


    public boolean isFuncDeclared(String name){
        return functionTable.isDeclared(name);
    }





    // OPERATIONS REGARDING VARIABLES

    public void declareVar(String name,ASTEnums type){
        if(callStack.empty()){
            if(globalScope.isDeclared(name)){
                error("Environment.declareVar: Declaring a variable with the same name twice in global scope twice '" + name + "'");
                return;
            }

            globalScope.declare(name, type);
        }
        else{
            if(localScope.isDeclaredInInnerScope(name)){
                error("Environment.declareVar: Declaring a variable with the same name in the same code block '" + name + "'");
                return;
            }

            localScope.declare(name, type);
        }
        
    }


    public void assignVar(String name,Object value){
        if(localScope != null && localScope.isDeclared(name)){
            localScope.assign(name,value);
        }
        else if(globalScope.isDeclared(name)){
            globalScope.assign(name, value);
        }
        else{
            error("Environment.assignVar: Assigning to an undeclared variable '" + name + "'");
        }
    }


    public ASTEnums fetchVarType(String name){
        if(localScope!= null && localScope.isDeclared(name)){
            return localScope.getType(name);
        }
        
        if(globalScope.isDeclared(name)){
            return globalScope.getType(name);
        }

        error("Environment.fetchVarType: Fetching data type of an undeclared variable '" + name + "'");
        return ASTEnums.UNDEFINED;
    }



    public Object fetchVar(String name){
        if(localScope != null && localScope.isDeclared(name)){
            if(localScope.isInitialized(name)){
                return localScope.fetch(name);
            }
            else{
                error("Environment.fetchVar: Fetching an uninitialized variable '" + name + "'");
            }
        }
        
        if(globalScope.isDeclared(name)){
            if(globalScope.isInitialized(name)){
                return globalScope.fetch(name);
            }
            else{
                error("Environment.fetchVar: Fetching an uninitialized variable '" + name + "'");
            }
        }

        error("Environment.fetchVar: Fetching an undeclared variable '" + name + "'");
        return null; 
    }


    public boolean isVarDeclared(String name){
        if(localScope != null && localScope.isDeclared(name)){
            return true;
        }
        else if(globalScope.isDeclared(name)){
            return true;
        }

        return false;
    }


    public boolean isVarInitialized(String name){
        if(localScope != null && localScope.isDeclared(name)){
            return localScope.isInitialized(name);
        }
        else if(globalScope.isDeclared(name)){
            return globalScope.isInitialized(name);
        }
        
        error("Environment.isVarInitialized: Checking initialization status of an undeclared variable");
        return false;
    }


    public boolean isVarDeclaredLocally(String name){
        if(callStack.empty()){
            // Global scope doesn't have nested block scopes inside it. 
            return globalScope.isDeclared(name);
        }
        else{
            return localScope.isDeclaredInInnerScope(name);
        }
    }


    // These functions are used for manipulating scopes. 

    public void enterCodeBlock(){
        if(localScope == null){
            error("Environment.enterCodeBlock: No active local scope");
        }
        localScope.enterBlockScope();
    }

    public void exitCodeBlock(){
        if(localScope == null){
            error("Environment.exitCodeBlock: No active local scope");
        }
        localScope.exitBlockScope();
    }


    public void enterFunction(String name){
        if(callStack.size() == 100){
            error("Maximum function call stack size of 100 reached. Exiting.");
        }

        localScope = new DeclarationtTable();
        callStack.push(new FunctionCall(name,localScope));
    }


    public void exitFunction(){
        callStack.pop();

        if(callStack.empty()){
            localScope = null;
        }
        else{
            localScope = callStack.peek().scope;
        }
    }




    public void error(String s){
        System.out.println("Internal error: " + s);
        System.exit(0);
    }



    private static class FunctionCall{
        public DeclarationtTable scope;
        public String name;

        public FunctionCall(String name,DeclarationtTable scope){
            this.name = name;
            this.scope = scope;
        }
    }


}