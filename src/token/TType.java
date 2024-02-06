package token;

// token types
public enum TType {

    // basic symbols
    RIGHT_PAREN,
    LEFT_PAREN,
    RIGHT_BRACE,
    LEFT_BRACE,
    SEMICOLON,
    COMMA,

    // operators
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

    // literals and identifiers
    IDENTIFIER,
    NUM_LITERAL,
    STRING_LITERAL,

    // keywords
    IF,
    ELSE,
    WHILE,
    FOR,
    RETURN,
    FN,
    VOID,
    STRING,
    INT,

    EOF
}
