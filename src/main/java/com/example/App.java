package com.example;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println("Hello World!");
        System.out.println("total: " + sumatoria(20));
    }

    public static int sumatoria(int n){
        int suma = 0;

        for (int i = 1; i <= n; ++i){
            
            suma += i;
            System.out.println(i);
            
        }
        return suma;

    }


}
