package com.example.googleclassroom;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;

public class Create_class extends AppCompatActivity {
    Toolbar toolbar;

    EditText className;
    EditText classDescription;
    EditText roomNumber;
    User thisUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);
        toolbar = findViewById(R.id.toolbar_create_class);
        thisUser = (User) getIntent().getSerializableExtra("user");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create Class");
        className = findViewById(R.id.classname);
        classDescription = findViewById(R.id.class_description);
        roomNumber = findViewById(R.id.class_door);
        className.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {



            }
        });
        roomNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create,menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancle_join_class)
        {

        }
        if (item.getItemId() == R.id.create_create_class)
        {
//            Intent classInt = new Intent(getApplicationContext(),Classes.class);
//            startActivity(classInt);

            CreateClass createClass = new CreateClass(Create_class.this);

            System.out.println(thisUser.username);

            createClass.execute("create_class" , className.getText().toString() , classDescription.getText().toString() , roomNumber.getText().toString() , thisUser.username);


        }
        return super.onOptionsItemSelected(item);
    }
}


class CreateClass extends AsyncTask<String , Void , String> {

    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    DataInputStream dataInputStream;
    boolean result;
    WeakReference<Create_class> activityRefrence;
    byte[] pic;
    Class aClass;

    CreateClass(Create_class context){
        activityRefrence = new WeakReference<>(context);
    }


    @Override
    protected String doInBackground(String... strings) {

        try {
//            Toast.makeText(activityRefrence.get(), "pressed in 1", Toast.LENGTH_SHORT).show();
            socket = new Socket("10.0.2.2" , 6666);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

//            Toast.makeText(activityRefrence.get(), "pressed in 2", Toast.LENGTH_SHORT).show();

            out.writeObject(strings);
            out.flush();

            result = in.readBoolean();

            if (result) {
                aClass = (Class) in.readObject();
            }

            out.close();
            in.close();
            socket.close();


        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Create_class activity = activityRefrence.get();

        if (activity == null || activity.isFinishing()){
            return;
        }

        if (result){
            Toast.makeText(activity, "Class Created", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(activity, main_page.class);
            intent.putExtra("aClass" , aClass);
            activity.startActivity(intent);
        }
        else {
            Toast.makeText(activity, "ah", Toast.LENGTH_LONG).show();
        }

    }
}