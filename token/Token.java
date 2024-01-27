package token;


public class Token {
    private String value;
    private TType TType;
    private int lineNumber;

    public Token(String value, TType TType, int lineNumber){
        this.value = value;
        this.TType = TType;
        this.lineNumber = lineNumber;
    }

    public TType getType(){
        return TType;
    }

    public String getValue(){
        return value;
    }

    public int getLineNumber(){
        return lineNumber;
    }

    @Override
    public String toString() {
        if(value == null){
            return TType.name();
        }
        else{
            return value + "  " + TType.name();
        }
    }
}
