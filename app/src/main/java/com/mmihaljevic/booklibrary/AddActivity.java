package com.mmihaljevic.booklibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddActivity extends AppCompatActivity {

    EditText title_input, author_input, pages_input;
    Button add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        title_input = findViewById(R.id.title_input);
        author_input = findViewById(R.id.author_input);
        pages_input = findViewById(R.id.pages_input);
        add_button = findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = title_input.getText().toString().trim();
                String author = author_input.getText().toString().trim();

                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(author)) {
                    Toast.makeText(AddActivity.this, "Please enter a title and author", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create an OkHttpClient instance to make HTTP requests
                OkHttpClient client = new OkHttpClient();

                // Create a new book object with the provided data
                JSONObject bookJson = new JSONObject();
                try {
                    bookJson.put("title", title);
                    bookJson.put("author", author);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AddActivity.this, "Error creating book object", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new HTTP request with the book data
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(JSON, bookJson.toString());
                Request request = new Request.Builder()
                        .url("http://10.0.2.2:8080/book") //route
                        .post(requestBody)
                        .build();

                // Send the HTTP request and handle the response
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddActivity.this, "Error adding book", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseBody = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject responseJson = new JSONObject(responseBody);
                                    String message = responseJson.getString("message");
                                    Toast.makeText(AddActivity.this, message, Toast.LENGTH_SHORT).show();
                                    finish(); // return to previous activity after adding book
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(AddActivity.this, "Error parsing server response", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
