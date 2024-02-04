package token;

// token types
public enum TType {

    // basic symbols
    RIGHT_PAREN,
    LEFT_PAREN,
    RIGHT_BRACE,
    LEFT_BRACE,
    RIGHT_BRACKET,
    LEFT_BRACKET,
    SEMICOLON,
    DOT,
    COMMA,
    MINUS,
    PLUS,
    STAR,
    SLASH,
    EQUAL,
    BANG,
    BANG_EQUAL,
    EQUAL_EQUAL,
    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL,
    IDENTIFIER,
    NUM_LITERAL,
    STRING_LITERAL,

    // keywords
    IF,
    ELSE,
    WHILE,
    FOR,
    CLASS,
    fn,
    NULL,
    OR,
    AND,
    THIS,
    SUPER,
    RETURN,
    TRUE,
    FALSE,
    FN,


    // type specifiers
    TYPE_VOID,
    TYPE_STR,
    TYPE_INT,

    EOF
}
