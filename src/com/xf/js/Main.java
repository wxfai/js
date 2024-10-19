package com.xf.js;

public class Main {
    public static void main(String[] args) {
        String outputFile = "out/hello";
    	macho m = new macho(outputFile);
    	m.writeFile();
    }

}
