package interpreter.environment;

import java.util.HashMap;
import java.util.Stack;
import java.util.LinkedList;

import ast.ASTEnums;



// This class only provides basic functionalities and doesn't do any error checking or recovery
// All of that will be for the user to handle


public class DeclarationtTable {
    private HashMap< String, Stack<Variable> > nameTable; 
    private int nestingLevel;
 
    // Block scopes can be nested inside each other. We use the stack to keep track of those scopes.
    // Each value in the stack is a list of names declared in that particular scope.
    private Stack< LinkedList<String> > cleanupTracker;

    
    public DeclarationtTable(){
        this.nameTable = new HashMap<>();
        this.nestingLevel = 0;

        this.cleanupTracker = new Stack<>();
        this.cleanupTracker.push(new LinkedList<>());
    }


    public void declare(String name,ASTEnums type){
        Stack<Variable> stack;
        
        if(nameTable.containsKey(name)){
            stack = nameTable.get(name);
        }
        else{
            stack = new Stack<Variable>();
            nameTable.put(name, stack);
        }

        stack.push(new Variable(type,nestingLevel));

        // We keep track of variables declared in the current block scope so we can remove later when exiting the block
        cleanupTracker.peek().add(name);
    }


    // Checking if a variable with that name has been declared at all 
    public boolean isDeclared(String name){
        return nameTable.containsKey(name);
    }

    // Checking if a variable with the same name has alredy been declared in the current block scope
    public boolean isDeclaredInInnerScope(String name){
        return (nameTable.containsKey(name) && nameTable.get(name).peek().level == nestingLevel);
    }


    // Calling isInitialized() on an undeclared variable will cause a runtime error
    public boolean isInitialized(String name){
        return nameTable.get(name).peek().initalized;
    }

    // Calling fetch() on an uninitialized variable will cause a runtime error
    public Object fetch(String name){
        return nameTable.get(name).peek().value;
    }


    // Calling assign() on an undeclared variable will cause a runtime error
    public void assign(String name,Object value){
        Variable var = nameTable.get(name).peek();
        var.initalized = true;
        var.value = value;
    }


    // Calling getType() on an undeclared variable will cause a runtime error
    public ASTEnums getType(String name){
        return nameTable.get(name).peek().type;
    }


    // If we enter new block scope, we can redeclare a variable. That way we shadow the outer variable with the same name
    public void enterBlockScope(){
        nestingLevel++;
        cleanupTracker.push(new LinkedList<>());
    }


    // When exiting a block scope we throw away all the variables that were declared in that scope
    public void exitBlockScope(){
        nestingLevel--;
        
        LinkedList<String> latestDeclarations = cleanupTracker.pop();

        for(String name: latestDeclarations){
            Stack<Variable> stack = nameTable.get(name);
            
            stack.pop();
            if(stack.empty()){
                nameTable.remove(name);
            }
        }
    }


    // Wrapper class to keep info about variables in the declaration table
    private static class Variable{
        public boolean initalized;
        public ASTEnums type;
        public Object value;
        public int level;

        public Variable(ASTEnums type,int nestingLevel){
            this.type = type;
            this.initalized = false;
            this.value = null;
            this.level = nestingLevel;
        }
    }
}
