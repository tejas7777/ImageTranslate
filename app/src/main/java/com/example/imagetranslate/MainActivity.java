package com.example.imagetranslate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.LanguageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String[] languages = {"English", "French", "Italian", "German","Swedish","Danish","Spanish","Portugese","Dutch","Afrikaans","Polish","Russian","Ukranian","Norwegian","Finnish"};
        String[] BCP_CODES = {"en","fr","it","de","sv","da","es","pt","nl","nl","pl","ru","ru","no","fn"};
        recyclerView.setAdapter(new LanguageAdapter(languages,BCP_CODES,this));
    }
}