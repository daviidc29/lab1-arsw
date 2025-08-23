/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;

import java.sql.SQLOutput;

/**
 *
 * @author David Castro
 */
public class CountThread extends Thread {
    private final int a, b;

    CountThread(int a, int b){
        this.a = a;
        this.b = b;
    }

    @Override
    public void run() {
        for (int i = a+1; i < b; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println();
    }
}
