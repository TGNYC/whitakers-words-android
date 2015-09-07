package com.ids1024.whitakerswords;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.RuntimeException;
import java.lang.InterruptedException;
import android.app.Activity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.EditText;
import android.os.Bundle;

public class WhitakersWords extends Activity
{
    /** Called when the activity is first created. */

    public void copyFiles() throws IOException {
        String[] filenames = {"ADDONS.LAT", "DICTFILE.GEN", "EWDSFILE.GEN",
                "INDXFILE.GEN", "INFLECTS.SEC", "STEMFILE.GEN",
                "UNIQUES.LAT", "words"};
        for (String filename: filenames) {
            // Note: Android does not accept upper case in resource names, so it is converted
            // And file extensions are stripped, so remove that
            String resourcename = filename.toLowerCase();
            if (resourcename.contains(".")) { 
                resourcename = resourcename.substring(0, filename.lastIndexOf('.'));
            }

            int identifier = getResources().getIdentifier(resourcename, "raw", getPackageName());
            InputStream ins = getResources().openRawResource(identifier);
            byte[] buffer = new byte[ins.available()];
            ins.read(buffer);
            ins.close();
            FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);
            fos.write(buffer);
            fos.close();
        }
        File wordsbin = getFileStreamPath("words");
        wordsbin.setExecutable(true);
    }

    public String executeWords(String text, boolean fromenglish) {
        String wordspath = getFilesDir().getPath() + "/words";
        Process process;
        try {
            if (fromenglish) {
                String[] command = {wordspath, "~E", text};
                process = Runtime.getRuntime().exec(command, null, getFilesDir());
            } else {
                String[] command = {wordspath, text};
                process = Runtime.getRuntime().exec(command, null, getFilesDir());
            }

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            
            reader.close();
            process.waitFor();

            return output.toString();

        } catch(IOException e) {
            throw new RuntimeException(e.getMessage());
        } catch(InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void searchWord(View view) {
        TextView result_text = (TextView)findViewById(R.id.result_text);
        EditText search_term = (EditText)findViewById(R.id.search_term);
        String term = search_term.getText().toString();
        result_text.setText((CharSequence)executeWords(term, false));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            copyFiles();
        } catch(IOException e) {
                throw new RuntimeException(e.getMessage());
        }

        setContentView(R.layout.main);

        EditText search_term = (EditText)findViewById(R.id.search_term);
        search_term.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchWord((View)v);
                    handled = true;
                }
                return handled;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
		// TODO
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
