package interpreter.environment;


import java.util.Stack;

import ast.Decl;
import ast.ASTEnums;



// The error messages from Environment module are intended for the programmer, not the user.
// It's SemanticChecker's job to inform the user 

public class Environment{ 
    private DeclarationtTable functionTable;
    private DeclarationtTable globalScope;
    private DeclarationtTable localScope;
    private Stack<FunctionCall> callStack;

    private int MAX_CALLSTACK_SIZE = 100;

    public Environment(){
        this.functionTable = new DeclarationtTable();
        this.globalScope = new DeclarationtTable();
        this.localScope = null;
        this.callStack = new Stack<>();
    }

    
    //  OPERATIONS REGARDING FUNCTIONS 

    public void declareFunction(String name,Decl.Func funcNode, ASTEnums returnType){
        if(functionTable.isDeclared(name)){
            internalError("declareFunction: Declaring a function with the same name twice '" + name + "'");
            return;
        }

        functionTable.declare(name, returnType);
        functionTable.assign(name, funcNode);
    }


    public Decl.Func fetchFunc(String name){
        if(!functionTable.isDeclared(name)){
            internalError("fetchFunc: Fetching an undeclared function '" + name + "'");
            return null;
        }

        return (Decl.Func) functionTable.fetch(name);
    }


    public ASTEnums getFuncReturnType(String name){
        if(!functionTable.isDeclared(name)){
            internalError("getFuncReturnType: Checking the type of an undeclared function");
            return ASTEnums.UNDEFINED;

        }

        return functionTable.getType(name);
    }


    public Decl.Func fetchCurrentFunction(){
        if(callStack.empty()){
            internalError("fetchCurrentFunction: Fetching current function, but the call stack is empty");
            return null;
        }

        return callStack.peek().funcNode;
    }


    public boolean isFuncDeclared(String name){
        return functionTable.isDeclared(name);
    }





    // OPERATIONS REGARDING VARIABLES

    public void declareVar(String name,ASTEnums type){
        if(callStack.empty()){
            if(globalScope.isDeclared(name)){
                internalError("declareVar: Declaring a variable with the same name twice in global scope twice '" + name + "'");
                return;
            }

            globalScope.declare(name, type);
        }
        else{
            if(localScope.isDeclaredInInnerScope(name)){
                internalError("declareVar: Declaring a variable with the same name in the same code block '" + name + "'");
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
            internalError("assignVar: Assigning to an undeclared variable '" + name + "'");
        }
    }


    public ASTEnums fetchVarType(String name){
        if(localScope!= null && localScope.isDeclared(name)){
            return localScope.getType(name);
        }
        
        if(globalScope.isDeclared(name)){
            return globalScope.getType(name);
        }

        internalError("fetchVarType: Fetching data type of an undeclared variable '" + name + "'");
        return ASTEnums.UNDEFINED;
    }



    public Object fetchVar(String name){
        if(localScope != null && localScope.isDeclared(name)){
            if(localScope.isInitialized(name)){
                return localScope.fetch(name);
            }
            else{
                internalError("fetchVar: Fetching an uninitialized variable '" + name + "'");
            }
        }
        
        if(globalScope.isDeclared(name)){
            if(globalScope.isInitialized(name)){
                return globalScope.fetch(name);
            }
            else{
                internalError("fetchVar: Fetching an uninitialized variable '" + name + "'");
            }
        }

        internalError("fetchVar: Fetching an undeclared variable '" + name + "'");
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
        
        internalError("isVarInitialized: Checking initialization status of an undeclared variable");
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
            internalError("enterCodeBlock: No active local scope");
        }
        localScope.enterBlockScope();
    }

    public void exitCodeBlock(){
        if(localScope == null){
            internalError("exitCodeBlock: No active local scope");
        }
        localScope.exitBlockScope();
    }


    public void enterFunction(Decl.Func funcNode){
        if(callStack.size() == MAX_CALLSTACK_SIZE){
            internalError("enterFunction: Maximum function call stack size of 100 reached. Exiting.");
        }

        localScope = new DeclarationtTable();
        callStack.push(new FunctionCall(funcNode,localScope));
    }


    public void exitFunction(){
        if(callStack.empty()){
            internalError("exitFunction: Exiting a function but call stack is empty");
        }

        callStack.pop();

        if(callStack.empty()){
            localScope = null;
        }
        else{
            localScope = callStack.peek().scope;
        }
    }


    public boolean isMaxCallstackReached(){
        return (callStack.size() == MAX_CALLSTACK_SIZE); 
    }


    public void internalError(String message){
        System.out.println("Internal error: Environment." + message);
        System.exit(0);
    }




    private static class FunctionCall{
        public DeclarationtTable scope;
        public Decl.Func funcNode;

        public FunctionCall(Decl.Func funcNode,DeclarationtTable scope){
            this.funcNode = funcNode;
            this.scope = scope;
        }
    }


}