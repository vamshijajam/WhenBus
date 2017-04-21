package com.example.vamshijajam.whenbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class thanks extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks);
        Thread thread = new Thread(){
            @Override
            public void run() {
                try{
                        sleep(5000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    Intent intent = new Intent(thanks.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        };thread.start();
    }
}
