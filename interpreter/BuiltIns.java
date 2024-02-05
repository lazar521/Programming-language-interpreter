package interpreter;

import java.util.Scanner;

import ast.ASTEnums;
import interpreter.environment.Environment;

class BuiltIns {
    
    public static void declareBuiltIns(Environment env) throws Exception{
        if(env == null){
            throw new Exception();
        }

        env.declareFunction("print", null, ASTEnums.VOID);
        env.declareFunction("readStr", null, ASTEnums.STRING);
        env.declareFunction("readInt", null, ASTEnums.INT);
    }


    public void print(String s){
        System.out.println(s);
    }

    public String readStr(){
        Scanner sc = new Scanner(System.in); 
        String str =  sc.nextLine(); 
        sc.close();
        return str;
    }

    public int readInt(){
        Scanner sc = new Scanner( System.in );
        int x = sc.nextInt();
        sc.close();
        return x;
    }


    public boolean isFuncBuiltIn(String funcName){
        switch (funcName) {
            case "print":
            case "readStr":
            case "readInt":
                return true;
            
            default:
                return false;
        }
    }
    
}
