package token;

public class TokenListIterator {
    TokenList.ListNode current;

    public TokenListIterator(TokenList list){
        this.current =  list.getHead();
    }


    public Token getCurrent() {
        return current.getToken();
    }

    public void advance(){
        current = current.getNext();
    }

    public boolean hasTokens() {
        return current != null;
    }
}
