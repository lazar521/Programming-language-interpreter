


int counter = 0;

fn string main(){
    string x  = "Hello";
    return strReturner(x);
}

fn string strReturner(string y){
    counter = counter + 1;
    if(counter == 90){
        return y;
    }
    return strReturner(y+" Hi");
}


