package edu.ucsd.neurores;

/**
 * Created by tbpetersen on 9/8/2017.
 */

public class HTTPRequestException  extends Exception{
    HTTPRequestException(){

    }

    HTTPRequestException(String s ){
        super(s);
    }
}
