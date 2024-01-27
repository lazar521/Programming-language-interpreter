package token;


public class TokenList {
    ListNode head = null;
    ListNode tail = null;

    public TokenList(){}

    public ListNode getHead(){
        return head;
    }

    public void add(Token token){
        ListNode newNode = new ListNode(token);

        if(head == null){
            head = tail = newNode;
        }
        else{
            tail.setNext(newNode);
            tail = newNode;
        }
    }

    public TokenListIterator getIterator(){
        return new TokenListIterator(this);
    }



    class ListNode{
        private Token token;
        private ListNode next;
    
        ListNode(Token token) {
            this.token = token;
            this.next = null;
        }
    
        void setNext(ListNode next){
            this.next = next;
        }
    
        ListNode getNext(){
            return next;
        }
    
        Token getToken(){
            return token;
        }
    }

}
