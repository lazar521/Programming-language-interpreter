package lexer;

class CharacterIterator {
    private String text;
    private int pos;


    CharacterIterator(String text){
        this.text = text;
        pos = 0;
    }

    boolean hasCharacters(){
        return pos + 1 <= text.length();
    }


    void advance(){
        pos++;
    }

    char getChar(){
        return text.charAt(pos);
    }

    char getPrevChar() throws Exception {
        if(pos>0) return text.charAt(pos-1);
        else throw new UnexpectedCharacterException("Peeking back at nonexisting character");
    }
}
