package lexer;

import token.TType;
import java.util.HashMap;

class Keywords {
    private static final HashMap<String, TType> keywords;

    static {
        keywords = new HashMap<String, TType>();
        keywords.put("if",       TType.IF);
        keywords.put("else",     TType.ELSE);
        keywords.put("while",    TType.WHILE);
        keywords.put("for",      TType.FOR);
        keywords.put("class",    TType.CLASS);
        keywords.put("def",      TType.DEF);
        keywords.put("null",     TType.NULL);
        keywords.put("or",       TType.OR);
        keywords.put("and",      TType.AND);
        keywords.put("this",     TType.THIS);
        keywords.put("super",    TType.SUPER );
        keywords.put("return",   TType.RETURN);
        keywords.put("true",     TType.TRUE);
        keywords.put("false",    TType.FALSE);
    }


    static boolean isKeyword(String word){
        return keywords.containsKey(word);
    }

    static TType getType(String word){
        return keywords.get(word);
    }
}
