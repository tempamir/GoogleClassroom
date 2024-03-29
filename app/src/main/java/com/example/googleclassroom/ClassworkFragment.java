package com.example.googleclassroom;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;
import java.net.Socket;

public class ClassworkFragment extends Fragment
{
    User thisUser;
    Class thisClass;
    FloatingActionButton fab;
    RecyclerView topics_list ;
    Classes fragment;
    ClassworkFragment(Classes fragment)
    {
        this.fragment = fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classwork,container,false);
        thisUser = (User) getArguments().getSerializable("user");
        thisClass = (Class) getArguments().getSerializable("aClass") ;
        topics_list = view.findViewById(R.id.topic_rec_parent);
        boolean isTeacher = thisClass.findTeacher(thisUser);
        LinearLayoutManager llm1 = new LinearLayoutManager(getContext());
        topics_list.setLayoutManager(llm1);
        Create_adptors();
        fab = view.findViewById(R.id.fab_classwork);
        if (!isTeacher)
            fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        topics_list = view.findViewById(R.id.topic_rec_parent);
        return view;
    }
    public void openDialog()
    {
        DialogFab dialog = new DialogFab(this);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user",thisUser);
        bundle.putSerializable("aClass",thisClass);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(),"tag");
    }
    void Create_adptors()
    {
        int temp = thisUser.findClass(thisClass);
        TopicAss_Adaptor adapter = new TopicAss_Adaptor(thisClass,thisUser,thisUser.classes.get(temp).topics,this) ;
        topics_list.setAdapter(adapter);
    }
}
class Refresh_classwork extends AsyncTask<String , Void , String> {

    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    DataInputStream dataInputStream;
    boolean result;
    WeakReference<ClassworkFragment> activityRefrence;
    byte[] pic;
    Class aClass;
    User user;
    Refresh_classwork(ClassworkFragment context){
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

           user = (User) in.readObject();
            aClass= (Class) in.readObject();

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
        super.onPostExecute(s);
        ClassworkFragment activity = activityRefrence.get();
        activity.thisClass = aClass;
        activity.thisUser = user;
        activity.Create_adptors();
        Refresh_classes rc = new Refresh_classes(activity.fragment);
        rc.execute("refresh_classes" , activity.thisUser.username , activity.thisClass.name);
    }
}
