package com.example.imagetranslate;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentification;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;


public class TranslateActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    TextView translatedView;
    Context context;
    String source_languageCode = "en";
    String source_Text;
    String native_ln;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        context = this;

        if(getIntent().getStringExtra("native_ln") != null){
            native_ln = getIntent().getStringExtra("native_ln");
        }

        //find imageview
        //imageView = findViewById(R.id.imageId);
        //find textview
        textView = findViewById(R.id.text);
        textView.setMovementMethod(new ScrollingMovementMethod());

        translatedView = findViewById(R.id.translated);
        translatedView.setMovementMethod(new ScrollingMovementMethod());
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.button);

        //check app level permission is granted for Camera
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //grant the permission
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doProcess(v);
            }
        });
    }

    public void doProcess(View view) {
        //open the camera => create an Intent object
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();
        //from bundle, extract the image
        Bitmap bitmap = (Bitmap) bundle.get("data");
        //set image in imageview
        //imageView.setImageBitmap(bitmap);
        //process the image
        //1. create a FirebaseVisionImage object from a Bitmap object
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        //2. Get an instance of FirebaseVision
        FirebaseApp.initializeApp(context);
        FirebaseVision firebaseVision = FirebaseVision.getInstance();
        //3. Create an instance of FirebaseVisionTextRecognizer
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
        //4. Create a task to process the image
        Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);
        //5. if task is success
        task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                String s = firebaseVisionText.getText();
                textView.setText(s);
                translatedView.setText("Translating...");
                translateText(s);

            }
        });
        //6. if task is failure
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void translateText(String s){

        this.source_Text = s;



        IdentifyLanguage identifyLanguage = new IdentifyLanguage();
        identifyLanguage.execute(s);

        TranslateLanguage translateLanguage = new TranslateLanguage();
        translateLanguage.execute();




    }

    class IdentifyLanguage extends AsyncTask<String,Integer,Integer>{
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        AlertDialog alertDialog;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPreExecute() {

//        alertDialogBuilder.setView(R.layout.identify_popup);
//        alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
        }

        @Override
        protected Integer doInBackground(String... string) {

            String s = string[0];
            final FirebaseLanguageIdentification languageIdentification = FirebaseNaturalLanguage.getInstance().getLanguageIdentification();
            languageIdentification.identifyLanguage(s)
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String language_string) {
                            if(language_string == "und"){
                                source_languageCode = "en";
                            }
                            else {
                                source_languageCode = language_string;
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            source_languageCode = "en";

                        }
                    });

            return 0;
        }

        protected void onPostExecute(Long result) {
//        alertDialog.dismiss();
        }

    }

    class TranslateLanguage extends AsyncTask<String,Integer,Integer>{

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        AlertDialog alertDialog;


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected void onPreExecute() {
            alertDialogBuilder.setView(R.layout.translating_popup);
            alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }

        @Override
        protected Integer doInBackground(String... string) {

            FirebaseTranslatorOptions options =
                    new FirebaseTranslatorOptions.Builder()
                            .setSourceLanguage(FirebaseTranslateLanguage.languageForLanguageCode(source_languageCode))
                            .setTargetLanguage(FirebaseTranslateLanguage.languageForLanguageCode(native_ln))
                            .build();
            final FirebaseTranslator languageTranslator =
                    FirebaseNaturalLanguage.getInstance().getTranslator(options);

            languageTranslator.downloadModelIfNeeded()
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void v) {
                                    // Model downloaded successfully. Okay to start translating.
                                    // (Set a flag, unhide the translation UI, etc.)
                                    languageTranslator.translate(source_Text)
                                            .addOnSuccessListener(
                                                    new OnSuccessListener<String>() {
                                                        @Override
                                                        public void onSuccess(@NonNull String translatedText) {
                                                            // Translation successful.
                                                            translatedView.setText(translatedText);


                                                        }
                                                    })
                                            .addOnFailureListener(
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Error.
                                                            // ...

                                                        }
                                                    });

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Model couldn’t be downloaded or other internal error.
                                    // ...
                                }
                            });
            return 0;
        }

        protected void onPostExecute(Long result) {
            alertDialog.dismiss();
        }

    }




}