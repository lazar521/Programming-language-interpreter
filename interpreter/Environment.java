package interpreter;


import java.util.Enumeration;
import java.util.HashMap;

import ast.Decl;
import ast.Stmt;
import ast.DataType;

public abstract class Environment{ 


    public static class FunctionsTable extends Environment{
        private HashMap<String,Function> functions;

        public FunctionsTable(){
            this.functions = new HashMap<>();
        }

        public void add(String name,Decl.Func fnNode){
            if(functions.containsKey(name)){
                System.out.println("FunctionTable::add: Adding function "+name+" to function table twice!");
                System.exit(0);
            }
            // In interpreter package we try not to look at TType enum since now we're operating 
            // with AST nodes, which are on a higher level of abstraction. So here we just extract
            // the DataType from TType and only use DataType enum from now on
            DataType type = DataType.UNDEFINED;
            switch (fnNode.type.getType()) {
                case TYPE_INT:
                    type = DataType.INT;
                    break;
                case TYPE_STR:
                    type = DataType.STRING;
                case TYPE_VOID:
                    type = DataType.VOID;
                default:
                    System.out.println("FunctionTable::add: DataType " + fnNode.type + " is not allowed for a funciton");
                    break;
            }

            functions.put(name,new Function(fnNode,type));
        }

        public Decl.Func getFunc(String name){
            return functions.get(name).funcNode;
        }

        public boolean isDeclared(String name){
            return functions.containsKey(name);
        }

        public DataType getFuncReturnType(String name){
            return functions.get(name).returnType;
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


        public void set(String name,Object value){
            if(!variables.containsKey(name)){
                System.out.println("GlobalsTable::set: Modifying an uninitialized variable");
                System.exit(0);
            }

            Variable var = variables.get(name);

            var.initalized = true;
            var.value = value;
        }

        public boolean isInitialized(String name){
            return variables.containsKey(name);
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





    private static class Function{
        public DataType returnType;
        public Decl.Func funcNode;

        public Function(Decl.Func func, DataType type){
            this.returnType = type;
            this.funcNode = func;
        }
    }

}