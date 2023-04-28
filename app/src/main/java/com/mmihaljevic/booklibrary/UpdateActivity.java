package com.mmihaljevic.booklibrary;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateActivity extends AppCompatActivity {

    EditText title_input, author_input, pages_input;
    Button update_button, delete_button;
    String id, title, author, pages;

    String restapiLink = "https://knjige-api.herokuapp.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        title_input = findViewById(R.id.title_input2);
        author_input = findViewById(R.id.author_input2);
        pages_input = findViewById(R.id.pages_input2);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);

        //First we call this
        getAndSetIntentData();

        //Set actionbar title after getAndSetIntentData method
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get updated book data from input fields
                System.out.println("Clicked book " + id);
                title = title_input.getText().toString().trim();
                author = author_input.getText().toString().trim();
                pages = pages_input.getText().toString().trim();

                // Create JSON object with updated book data
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("title", title);
                    jsonObject.put("author", author);
                    jsonObject.put("pages", pages);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Make PUT request to API with updated book data
                String url = restapiLink + "book/" + id;
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
                Request request = new Request.Builder().url(url).put(body).build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Toast.makeText(UpdateActivity.this, "Book updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(UpdateActivity.this, "Failed to update book", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });

    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("id")){
            id = getIntent().getStringExtra("id");
            String url = restapiLink + "book/" + id;

            try {
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(new Request.Builder().url(url).build()).execute();

                // Check if request was successful
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    // Set book data to respective variables
                    title = jsonObject.getString("title");
                    author = jsonObject.getString("author");
                    pages = jsonObject.getString("pages");

                    // Set text fields with book data
                    title_input.setText(title);
                    author_input.setText(author);
                    pages_input.setText(pages);
                } else {
                    // Handle unsuccessful request
                    Toast.makeText(this, "Failed to retrieve book data.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to connect to server.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No book selected.", Toast.LENGTH_SHORT).show();
        }
    }


    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + title + " ?");
        builder.setMessage("Are you sure you want to delete " + title + " ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    String url = restapiLink + "book/" + id;

                    Request request = new Request.Builder()
                            .url(url)
                            .delete()
                            .build();

                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        Toast.makeText(UpdateActivity.this, "Book deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(UpdateActivity.this, "Failed to delete book", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(UpdateActivity.this, "Error deleting book", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}
