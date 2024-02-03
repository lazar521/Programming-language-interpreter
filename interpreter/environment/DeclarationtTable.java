package interpreter.environment;

import java.util.HashMap;
import java.util.Stack;
import java.util.LinkedList;

import ast.ASTEnums.DataType;


public class DeclarationtTable {
    private HashMap< String, Stack<Variable> > nameTable; 
    private Stack< LinkedList<String> > cleanupTracker;
    private String tableName;
    private int nestingLevel;

    
    public DeclarationtTable(String name){
        this.nameTable = new HashMap<>();
        this.tableName = name;
        this.nestingLevel = 0;

        this.cleanupTracker = new Stack<>();
        this.cleanupTracker.push(new LinkedList<>());
    }


    public void declare(String name,DataType type){
        Stack<Variable> stack;
        
        if(!nameTable.containsKey(name)){
            stack = new Stack<Variable>();
            nameTable.put(name, stack);
        }
        else{
            stack = nameTable.get(name);
            if(stack.peek().level == nestingLevel){
                Environment.environmentError(tableName+".declare: declaring a variable twice in the same scope " + name);
            }
        }

        stack.push(new Variable(type,nestingLevel));
        cleanupTracker.peek().add(name);
    }



    public boolean isDeclared(String name){
        return nameTable.containsKey(name);
    }


    public boolean isInitialized(String name){
        if(!isDeclared(name)){
            Environment.environmentError(tableName+".isInitialized: accessing an undeclared variale " + name);
        }

        return nameTable.get(name).peek().initalized;
    }


    public Object fetch(String name){
        if(!isDeclared(name)){
            Environment.environmentError(tableName+".fetch: accessing an undeclared variable " + name);
        }

        if(!isInitialized(name)){
            Environment.environmentError(tableName+".fetch: accessing an uninitialized variable " + name);
        }

        return nameTable.get(name).peek().value;
    }


    // TODO: Later we can probably remove this type parameter 
    public void assign(String name,Object value,DataType type){
        if(!isDeclared(name)){
            Environment.environmentError(tableName+".assign: assigning to an undeclared variable " + name);
        }

        Variable var = nameTable.get(name).peek();

        if(var.type != type){
            Environment.environmentError(tableName+".assign: Assigning type " + type + " to variable " + name + " of type " + var.type);
        }

        var.initalized = true;
        var.value = value;
    }


    public DataType getType(String name){
        if(isDeclared(name)){
            return nameTable.get(name).peek().type;
        }

        Environment.environmentError(tableName+".getType: Accessing an undeclared variable " + name);
        return DataType.UNDEFINED;
    }


    public void enterBlockScope(){
        nestingLevel++;
        cleanupTracker.push(new LinkedList<>());
    }


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



    private static class Variable{
        public boolean initalized;
        public DataType type;
        public Object value;
        public int level;

        public Variable(DataType type,int nestingLevel){
            this.type = type;
            this.initalized = false;
            this.value = null;
            this.level = nestingLevel;
        }
    }
}
