package lexer;

public class UnexpectedCharacterException extends Exception {
    public UnexpectedCharacterException(String message){
        super(message);
    }
}
