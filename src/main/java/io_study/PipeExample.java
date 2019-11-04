package main.java.io_study;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class PipeExample {

    public static void main(String[] args) throws IOException {
        final PipedInputStream input = new PipedInputStream();
        final PipedOutputStream output = new PipedOutputStream(input);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    int data;
                    while ((data = input.read())!=-1){
                        System.out.print((char)data);
                    }
                }catch (IOException e){}
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    output.write("Hello world!".getBytes());
                }catch (IOException e){}
            }
        });

        t1.start();
        t2.start();
    }
}
