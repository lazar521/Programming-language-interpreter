


int counter = 0;

fn string main(){
    string x = "Hello";
    return strReturner(x + str2());
}

fn string strReturner(string y){
    counter = counter + 1;
    if(counter == 50){
        return y;
    }
    return strReturner(y + " Hi");
    
}


fn string str2(){
    return " making";
}