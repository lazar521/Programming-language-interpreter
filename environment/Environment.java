package environment;


import java.util.Stack;

import ast.ASTEnums.DataType;

public class Environment{ 
    DeclarationtTable functionTable;
    DeclarationtTable globalVariables;
    DeclarationtTable localVariables;
    Stack<DeclarationtTable> funcitonStack;
    

    public Environment(){
        functionTable = new DeclarationtTable("FunctionTable");
        globalVariables = new DeclarationtTable("GlobalVariables");
        localVariables = null;
        funcitonStack = new Stack<DeclarationtTable>();
    }

    
    public DataType getType(){
        return null;
    }

    public Object fetchFunction(){
        
        return null;
    }

    public void declareFunction(){
        // TODO: if argument not instanceof Decl.Stmt throw error
    }

    public void declareVariable(){

    }

    public void assign(){

    }


    public Object load(){
        return null;
    }


    public void enterCodeBlock(){
        localVariables.increaseNesting();
    }

    public void exitCodeBlock(){
        localVariables.increaseNesting();
    }


    public void enterFunction(){
        localVariables = new DeclarationtTable("FunctionVariables");
        funcitonStack.push(localVariables);
    }


    public void exitFunction(){
        funcitonStack.pop();
        localVariables = funcitonStack.peek();
    }



    public static void environmentError(String s){
        System.out.println(s);
        System.exit(0);
    }
}