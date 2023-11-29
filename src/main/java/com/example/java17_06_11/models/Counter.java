package com.example.java17_06_11.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



public class Counter {
    public  int count = 0;
    public int add(){
        int buf = count;
        count++;
        return buf;
    }
    public int clear(){
        count=0;

        return count;
    }
}
