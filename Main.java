package com.company;

public class Main {

    public static void main(String[] args) {
	// write your code here
        Preprocessor p;
        QueryExecutor q;
        if(args.length > 0)
        {
            if(args[0].equals("preprocess"))
                 p = new Preprocessor();
            else if(args[0].equals("query"))
                q = new QueryExecutor();
        }


    }
}
