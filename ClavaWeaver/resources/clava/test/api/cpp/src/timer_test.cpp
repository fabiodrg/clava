double bar() {
    return 1.0;
}

double bar2() {
    return 1.0;
}

double bar3() {
    return 1.0;
}

double foo() {
    double a = 0;
    
    for(int i=0; i<1000; i++) {
        a += bar();
    }
    
    return a;
}

int main() {
    
    foo();
	bar2();
	bar3();
}