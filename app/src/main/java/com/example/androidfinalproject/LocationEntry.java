package com.example.androidfinalproject;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStreamWriter;

public class LocationEntry extends AppCompatActivity {

    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_entry);
        title = getIntent().getStringExtra("Title"); //set title
        EditText editText = (EditText)findViewById(R.id.note_title);
        editText.setText(title, TextView.BufferType.EDITABLE);

        String word = getIntent().getStringExtra("Words");//set text if already exists
        if (word != null) {
            EditText note = (EditText) findViewById(R.id.note_text);
            note.setText(word, TextView.BufferType.EDITABLE);
        }

        EditText note_word = (EditText)findViewById(R.id.note_text);
        note_word.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            Intent data = new Intent();
            EditText ttl = (EditText)findViewById(R.id.note_title);
            EditText note = (EditText)findViewById(R.id.note_text);
            data.putExtra("Title", ttl.getText().toString());
            data.putExtra("Rem", title);
            setResult(RESULT_OK, data);
            save(ttl.getText().toString(), note.getText().toString());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

}
