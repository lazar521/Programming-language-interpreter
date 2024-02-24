package interpreter.environment;

import java.util.HashMap;
import java.util.Stack;
import java.util.LinkedList;

import ast.ASTEnums;



// This class only provides basic functionalities and doesn't perform any error checking 
// All of that will be for the user of the class to handle


public class DeclarationtTable {

    // We nest block scopes. Every entry in the nameTable is a stack of variables that have that name.
    // Variable at the top of the stack shadows the ones below it. That way we allow the redeclaration of
    // a variable but in different level of block nesting 
    private HashMap< String, Stack<Variable> > nameTable; 
    private int nestingLevel;
    
    // Block scopes can be nested inside each other. We use the stack to keep track of those scopes.
    // Each value in the stack is a list of names declared in that particular scope
    // When we leave a particular block scope we "undeclare" the variables declared there
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

        // We keep track of variables declared in the current block scope so we can remove their declaration later when exiting the block
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
        int initLevel = nameTable.get(name).peek().initLevel;
        // A variable should be initialized in the current or any outside code block (if it is initialized at all)
        return (initLevel <= nestingLevel );
    }

    // Calling fetch() on an uninitialized variable will cause a runtime error
    public Object fetch(String name){
        return nameTable.get(name).peek().value;
    }


    // Calling assign() on an undeclared variable will cause a runtime error
    public void assign(String name,Object value){
        Variable var = nameTable.get(name).peek();
        var.value = value; 
        var.initLevel = (nestingLevel < var.initLevel ? nestingLevel : var.initLevel);
    }


    // Calling getType() on an undeclared variable will cause a runtime error
    public ASTEnums getType(String name){
        return nameTable.get(name).peek().type;
    }


    // If we enter new block scope, we can redeclare a variable. That way we shadow the outer variable with the same name
    // 'nestingLevel' variable keeps track of the number of block scopes we're currently inside of, since block scopes can be nested
    public void enterBlockScope(){
        nestingLevel++;
        cleanupTracker.push(new LinkedList<>());
    }


    // When exiting a block scope we pop all the variables that were declared in that block  
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
        public int initLevel;       // Indicates the lowest level of nesting here a variable had been initialized
        public ASTEnums type;
        public Object value;
        public int level;

        public Variable(ASTEnums type,int nestingLevel){
            this.type = type;
            this.initLevel = UNINITIALIZED;  
            this.value = null;
            this.level = nestingLevel;
        }
    }

    private static int UNINITIALIZED = 9999;
}
