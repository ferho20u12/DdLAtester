package com.example.proyectoservicio;

import android.content.Context;
import android.widget.Toast;

public class Message {
    private Toast toast;
    private final Context context;
    public Message(Context context){
        this.context = context;
        toast = new Toast(context);
    }
    public void ShowNewMessage(String str){
        toast.cancel();
        toast = Toast.makeText(this.context,str,Toast.LENGTH_SHORT);
        toast.show();
    }
}
