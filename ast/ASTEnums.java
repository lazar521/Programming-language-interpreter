package ast;

import token.*;

public class ASTEnums{
    public enum DataType {
        STRING,
        INT,
        VOID,
        UNDEFINED
    }

    public enum Operations{
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        EQUAL,
        NOT_EQUAL,
        LESS,
        LESS_EQ,
        GREATER,
        GREATER_EQ,
        NOT,
        UNDEFINED
    }
 
    public static DataType toAstType(TType type){
        switch (type) {
            case TYPE_STR:
            case STRING_LITERAL:
                return DataType.STRING;

            case NUM_LITERAL:
            case TYPE_INT:
                return DataType.INT;

            case TYPE_VOID:
                return DataType.VOID;

            default:
                return DataType.UNDEFINED;
        }
    }

    public static Operations toAstOperation(TType type){
        switch(type){
            case PLUS:
                return Operations.PLUS;
            case MINUS:
                return Operations.MINUS;
            case STAR:
                return Operations.MULTIPLY;
            case SLASH:
                return Operations.DIVIDE;
            case EQUAL_EQUAL:
                return Operations.EQUAL;
            case BANG_EQUAL:
                return Operations.NOT_EQUAL;
            case BANG:
                return Operations.NOT;
            case GREATER:
                return Operations.GREATER;
            case GREATER_EQUAL:
                return Operations.GREATER_EQ;
            case LESS:
                return Operations.LESS;
            case LESS_EQUAL:
                return Operations.LESS_EQ;
            default:
                return Operations.UNDEFINED;
        }
    }

}


