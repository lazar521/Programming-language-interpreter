package interpreter.environment;

import java.util.ArrayList;
import java.util.Scanner;

import ast.ASTEnums;
import ast.Decl;


// If we want to add a new built-in function we just need to modify this file, no need to touch anything else

public class BuiltIns {
    

    // We want to create fake nodes for every built-in funciton to trick the SemanticChecker and the Executor into working with these
    // nodes like they would with any other. The only place where we handle these fake nodes differently is when executing the Expr.Call node in the Executor class 

    public static void declareBuiltIns(Environment env) throws Exception{
        if(env == null){
            internalError("declareBuiltIns: Environment is null");
        }

        ArrayList<Decl.Param> parameters;
                
        // Making fake print(string message) function node
        parameters = new ArrayList<>();
        parameters.add(new Decl.Param(ASTEnums.STRING, "s" , 0));
        Decl.Func printFunc = new Decl.Func(ASTEnums.VOID, "print", parameters, null, 0);
        
        // Making fake readStr() function node
        parameters = new ArrayList<>();
        Decl.Func readStrFunc = new Decl.Func(ASTEnums.STRING, "readStr", parameters, null, 0);


        // Making fake readInt() function node
        parameters = new ArrayList<>();
        Decl.Func readIntFunc = new Decl.Func(ASTEnums.INT, "readInt", parameters, null, 0);

        
        // Making fake intToStr(int x) function node
        parameters = new ArrayList<>();
        parameters.add(new Decl.Param(ASTEnums.INT, "x", 0));
        Decl.Func intToStr = new Decl.Func(ASTEnums.STRING, "intToStr", parameters, null, 0);

        // Declring the fake nodes
        env.declareFunction(printFunc.identifier, printFunc, printFunc.type);
        env.declareFunction(readStrFunc.identifier, readStrFunc, readStrFunc.type);
        env.declareFunction(readIntFunc.identifier, readIntFunc, readIntFunc.type);
        env.declareFunction(intToStr.identifier, intToStr, intToStr.type);
    }



    // Checks if the given function is a built-in

    public static boolean isFuncBuiltIn(String funcName){
        switch (funcName) {
            case "print":
            case "readStr":
            case "readInt":
            case "intToStr":
                return true;
            
            default:
                return false;
        }
    }


    // An interface used to execute all of the built-in functions

    public static Object executeFunction(String funcIdentifier, ArrayList<Object> arguments){
        
        switch (funcIdentifier) {
            
            case "print":
                if(arguments.size() != 1) internalError("executeFunction: Calling 'print' with wrong number of arguments");
                print((String) arguments.get(0));
                return null;

            case "readStr":
                if(arguments.size() != 0) internalError("executeFunction: Calling 'readStr' with wrong number of arguments");
                return readStr();

            case "readInt":
                if(arguments.size() != 0) internalError("executeFunction: Calling 'readInt' with wrong number of arguments");
                return readInt();
            
            case "intToStr":
                if(arguments.size() != 1) internalError("executeFunction: Calling 'intToStr' with wrong number of arguments");
                return intToStr((int) arguments.get(0));

            default:
                internalError("executeFunction: Calling a function that is not built-in");
                return null;
            }

    }




    // Implementations for built-in functions 

    private static void print(String s){
        System.out.println(s);
    }

    // Here the Java compiler is complaining that we should call sc.close() at the end of the function 
    // But when we do that it throws some runtime errors and crashes the program for some reason
    private static String readStr(){
        Scanner sc = new Scanner(System.in);
        String str =  sc.nextLine();
        return str;
    }

    private static int readInt(){
        Scanner sc = new Scanner( System.in );
        int x = sc.nextInt();
        return x;
    }


    private static String intToStr(int x){
        return Integer.toString(x);
    }




    private static void internalError(String message){
        System.out.println("Internal error: BuiltIns." + message);
        System.exit(0);
    }
    
}
