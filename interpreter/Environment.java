package interpreter;


import java.util.HashMap;

import ast.Decl;
import ast.ASTEnums.*;

public abstract class Environment{ 


    public static class FunctionsTable extends Environment{
        private HashMap<String,Decl.Func> functions;

        public FunctionsTable(){
            this.functions = new HashMap<>();
        }

        public void add(String name,Decl.Func fnNode){
            if(functions.containsKey(name)){
                System.out.println("FunctionTable::add: Adding function "+name+" to function table twice!");
                System.exit(0);
            }

            functions.put(name,fnNode);
        }

        public Decl.Func getFunc(String name){
            return functions.get(name);
        }

        public boolean isDeclared(String name){
            return functions.containsKey(name);
        }
    }




    public static class GlobalsTable extends Environment{
        private HashMap<String,Variable> variables;
        
        public GlobalsTable(){
            this.variables = new HashMap<>();
        }

        public void add(String name,DataType type){
            if(variables.containsKey(name)){
                System.out.println("GlobalsTable::set: Adding variable "+name+" to the global table twice!");
            }

            variables.put(name, new Variable(type));
        }

        // We overload set() function for 2 types of data
        // For String type 
        public void setString(String name,String value){
            Variable var = getVariable(name, DataType.STRING);
            var.initalized = true;
            var.value = value;
        }

        // For int type 
        public void setInt(String name,int value){
            Variable var = getVariable(name, DataType.INT);
            var.initalized = true;
            var.value = value;
        }

        public String getString(String name){
            return (String) getVariable(name, DataType.STRING).value;
        }

        public int getInt(String name){
            return (int) getVariable(name, DataType.INT).value;
        }

        public boolean isInitialized(String name){
            return variables.get(name).initalized;
        }

        public boolean isDeclared(String name){
            return variables.containsKey(name);
        }

        private Variable getVariable(String name,DataType type){
            if(!variables.containsKey(name)){
                System.out.println("GlobalsTable: Fetching an uninitialized variable");
                System.exit(0);
            }
            Variable var = variables.get(name);
            if(var.type != DataType.STRING){
                System.out.println("GlobalsTable: Fetching variable of invalid data type");
                System.exit(0);
            }
            return var;
        }
    }




    // TODO: Implement locals table
    public static class LocalsTable extends Environment{

    }




    // TODO: Unfinished Variable class
    private static class Variable{
        public boolean initalized;
        public DataType type;
        public Object value;

        public Variable(DataType dType){
            this.type = dType;
            this.initalized = false;
            this.value = null;
        }
    }


}