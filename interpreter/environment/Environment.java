package interpreter.environment;


import java.util.Stack;

import ast.Decl;
import ast.ASTEnums.DataType;

public class Environment{ 
    DeclarationtTable functionTable;
    DeclarationtTable globalScope;
    DeclarationtTable localScope;
    Stack<CalledFunction> functionStack;

    public Environment(){
        functionTable = new DeclarationtTable("FunctionTable");
        globalScope = new DeclarationtTable("GlobalVariables");
        localScope = null;
        functionStack = new Stack<>();
    }

    

    public void declareFunction(String name,Decl.Func funcNode, DataType returnType){
        functionTable.declare(name, returnType);
        functionTable.assign(name, funcNode, returnType);
    }


    public Decl.Func fetchFunc(String name){
        return (Decl.Func) functionTable.fetch(name);
    }


    public DataType getFuncReturnType(String name){
        if(functionTable.isDeclared(name)){
            return functionTable.getType(name);
        }

        environmentError("Environment.getFuncReturnType: Accessing an undeclared varible " + name);
        return null;
    }


    public Decl.Func getCurrentFunction(){
        return fetchFunc(functionStack.peek().name);
    }



    public void declareVar(String name,DataType type){
        if(functionStack.empty()){
            globalScope.declare(name, type);
        }
        else{
            localScope.declare(name, type);
        }
        
    }


    public void assignVar(String name,Object value,DataType type){
        if(localScope != null && localScope.isDeclared(name)){
            localScope.assign(name,value,type);
        }
        else if(globalScope.isDeclared(name)){
            globalScope.assign(name, value, type);
        }
        else{
            environmentError("Environment.assign: Assigning to an undeclared variable " + name);
        }

    }

    public DataType getVarType(String name){
        if(localScope!= null && localScope.isDeclared(name)){
            return localScope.getType(name);
        }
        
        if(globalScope.isDeclared(name)){
            return globalScope.getType(name);
        }

        environmentError("Environment.getVarType: Accessing an undeclared variable " + name);
        return DataType.UNDEFINED;
    }



    public Object fetchVar(String name){
        if(localScope != null && localScope.isInitialized(name)){
            return localScope.fetch(name);
        }
        
        if(globalScope.isInitialized(name)){
            return globalScope.fetch(name);
        }

        environmentError("Environment.fetch: fetching an uninitialized variable " + name);
        return null; 
    }



    public void enterCodeBlock(){
        if(localScope == null){
            environmentError("Environment.enterCodeBlock: No active local scope");
        }
        localScope.enterBlockScope();
    }

    public void exitCodeBlock(){
        if(localScope == null){
            environmentError("Environment.exitCodeBlock: No active local scope");
        }
        localScope.exitBlockScope();
    }


    public void enterFunction(String name){
        localScope = new DeclarationtTable(name +"::FunctionTable");
        functionStack.push(new CalledFunction(name,localScope));
    }


    public void exitFunction(){
        functionStack.pop();

        if(functionStack.empty()){
            localScope = null;
        }
        else{
            localScope = functionStack.peek().scope;
        }
    }




    public static void environmentError(String s){
        System.out.println(s);
        System.exit(0);
    }



    private static class CalledFunction{
        public DeclarationtTable scope;
        public String name;

        public CalledFunction(String name,DeclarationtTable scope){
            this.name = name;
            this.scope = scope;
        }
    }
}