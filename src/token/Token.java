package token;

public class Token {
    
    // Only tokens of type IDENTIFIER, STRING_LITERAL and NUM_LITERAL will have the 'string' field set. Other tokens don't need it
    private String string;
    private TType TType;
    private int lineNumber;

    public Token(String value, TType TType, int lineNumber){
        this.string = value;
        this.TType = TType;
        this.lineNumber = lineNumber;
    }

    public TType getType(){
        return TType;
    }

    public String getString(){
        return string;
    }

    public int getLineNumber(){
        return lineNumber;
    }

    @Override
    public String toString() {
        if(string == null){
            return TType.name();
        }
        else{
            return string + "  " + TType.name();
        }
    }
}
