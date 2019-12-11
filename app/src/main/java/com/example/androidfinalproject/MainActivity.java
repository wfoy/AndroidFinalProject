package com.example.androidfinalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String mUsername;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public static final String ANONYMOUS = "anonymous";
    private String mUseremail;

    private final int REQUEST_CODE = 20;
    private final ArrayList<String> notes = new ArrayList<String>();
    ArrayAdapter<String> itemsAdapter;
    String last;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, signinActivity.class));
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            TextView userid = (TextView)findViewById(R.id.textView);
            userid.setText("Logged in as " + mUsername);
            mUseremail = mFirebaseUser.getEmail();
            TextView userem = (TextView)findViewById(R.id.textView2);
            userem.setText(mUseremail);
        }

        //if notes already exist
        if(fileExists("__LATS__")) {
            String[] entries = open("__LATS__").split(" ", 0);
            for(int j = 0; j < entries.length; j++)
            {
                if(entries[j] != null && !entries[j].isEmpty() && !entries[j].equals("") && !entries[j].equals(" ")) {
                    notes.add(entries[j]);
                }
            }
            //deal with extra entry appearing
            if(notes.size() > 0) {
                if (!notes.get(notes.size() - 1).equals(last)) {
                    notes.remove(notes.size() - 1);
                }
            }
        }

        itemsAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,notes);

        final ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(itemsAdapter);

        FloatingActionButton button = findViewById(R.id.button); //add note
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocationEntry.class);
                intent.putExtra("Title", "Note" + Integer.toString(notes.size()));
                MainActivity.this.startActivityForResult(intent, REQUEST_CODE);
            }
        });

        listView.setClickable(true);//click on note
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                String name = listView.getItemAtPosition(position).toString();
                if(fileExists(name)) {
                    Intent intent = new Intent(MainActivity.this, LocationEntry.class);
                    intent.putExtra("Title", name);
                    intent.putExtra("Words", open(name));
                    MainActivity.this.startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String name = data.getExtras().getString("Title");
            String prev = data.getExtras().getString("Rem");

            if(!name.isEmpty() && name != null && !notes.contains(name) && !name.equals("") && !name.equals(" ")) {
                int i = notes.indexOf(prev);
                if(i != -1)
                    notes.set(i, name);//update note title
                else
                    notes.add(name);//add note title if new
            }
            itemsAdapter.notifyDataSetChanged();
        }
    }

    /** Checks if the file denoted by fileName exists. **/
    private boolean fileExists(String fileName) {
        File file = getBaseContext().getFileStreamPath(fileName);
        return file.exists();
    }

    /** Opens the file denoted by fileName and returns the contents of the file. **/
    private String open(String fileName) {
        String content = "";
        if (fileExists(fileName)) {
            try {
                InputStream in = openFileInput(fileName);
                if ( in != null) {
                    InputStreamReader tmp = new InputStreamReader( in );
                    BufferedReader reader = new BufferedReader(tmp);
                    String str;
                    StringBuilder buf = new StringBuilder();
                    while ((str = reader.readLine()) != null) {
                        buf.append(str + "\n");
                    } in .close();
                    content = buf.toString();
                }
            } catch (java.io.FileNotFoundException e) {} catch (Throwable t) {
                Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        }
        return content;
    }

    @Override
    public void onResume() {
        super.onResume();
        mUsername = mFirebaseUser.getDisplayName();
        Toast.makeText(getApplicationContext(),"USER SIGNED IN 2 as " + mUsername,Toast.LENGTH_SHORT).show();
        TextView userid = (TextView)findViewById(R.id.textView);
        userid.setText("Logged in as " + mUsername);
        mUseremail = mFirebaseUser.getEmail();
        TextView userem = (TextView)findViewById(R.id.textView2);
        userem.setText(mUseremail);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        startActivity(new Intent(this, signinActivity.class));
    }

    /** Writes textToSave to the file denoted by fileName. **/
    private void save(String fileName, String textToSave) {
        try {
            OutputStreamWriter out =
                    new OutputStreamWriter(openFileOutput(fileName, 0));
            out.write(textToSave);
            out.close();
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        } catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //create string of titles of notes
        String toSave = "";
        for(int i = 0; i < notes.size(); i++)
        {
            toSave += notes.get(i) + " ";
        }

        save("__LATS__", toSave);//save string of titles to __NOTES__ file
        if(notes.size() > 0)
            last = notes.get(notes.size()-1);
    }
}
