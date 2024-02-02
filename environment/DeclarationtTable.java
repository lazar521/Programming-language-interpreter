package environment;

import java.util.HashMap;
import java.util.Stack;

import ast.ASTEnums.DataType;

public class DeclarationtTable {
    private HashMap< String, Stack<Variable> > variableTable; 
    private int nestingLevel; 
    private String tableName;

    public DeclarationtTable(String name){
        this.variableTable = new HashMap<>();
        this.nestingLevel = 0;
        this.tableName = name;
    }


    public void declare(String name,DataType type){
        Stack<Variable> stack;
        
        if(!variableTable.containsKey(name)){
            stack = new Stack<Variable>();
            variableTable.put(name, stack);
        }
        else{
            stack = variableTable.get(name);
            if(stack.peek().level == nestingLevel){
                Environment.environmentError(tableName+".declare: declaring a variable twice in the same scope");
            }
        }

        stack.push(new Variable(type,nestingLevel));
    }



    public boolean isDeclared(String name){
        return variableTable.containsKey(name);
    }


    public boolean isInitialized(String name){
        if(!isDeclared(name)){
            Environment.environmentError(tableName+".initialize: accessing an undeclared variale");
        }

        return variableTable.get(name).peek().initalized;
    }


    public Object load(String name){
        if(!isDeclared(name)){
            Environment.environmentError(tableName+".load: accessing an undeclared variable");
        }

        if(!isInitialized(name)){
            Environment.environmentError(tableName+".load: accessing an uninitialized variable");
        }

        return variableTable.get(name).peek().value;
    }


    // TODO: Later we can probably remove this type parameter 
    public void assign(String name,Object value,DataType type){
        if(!isDeclared(name)){
            Environment.environmentError(tableName+".assign: assigning to an undeclared variable");
        }

        Variable var = variableTable.get(name).peek();

        if(var.type != type){
            Environment.environmentError(tableName+".assign: Assigning type " + type + " to a variable of type " + var.type);
        }

        var.initalized = true;
        var.value = value;
    }


    public void increaseNesting(){
        nestingLevel++;
    }

    public void decreaseNesting(){
        nestingLevel--;
        
        for(String name: variableTable.keySet()){
            Stack<Variable> stack = variableTable.get(name);
            
            if(stack.peek().level > nestingLevel){
                stack.pop();
                
                if(stack.empty()){
                    variableTable.remove(name);
                }
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
