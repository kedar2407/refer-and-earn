package com.example.mydream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText profileFullName,profileEmail,profilePhone;
    ImageView profileImageView;
    Button resendCode,intent;
    TextView verifyMsg;
    FirebaseAuth fAuth;
    String userId,count;
    FirebaseUser user;
    Uri shortLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        resendCode = findViewById(R.id.resendCode);
        verifyMsg = findViewById(R.id.verifyMsg);

        fAuth = FirebaseAuth.getInstance();
        count=String.valueOf(Register.count);
        user = fAuth.getCurrentUser();
        userId=user.getEmail().toString();
        if (!user.isEmailVerified()) {
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);

            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag", "onFailure: Email not sent " + e.getMessage());
                        }
                    });
                }
            });
        }

        TextView tv = findViewById(R.id.tv1);
        TextView tv2 = findViewById(R.id.tvReferelCount);
        TextView user = findViewById(R.id.userId);
        user.setText(fAuth.getCurrentUser().getEmail());
        TextView tv3 = findViewById(R.id.tvusersfs);
        tv3.setText(fAuth.getCurrentUser().getEmail().toString());
        tv2.setText(count);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    createlink();

            }
        });

    }



    public void createlink(){
        Log.e("main", "create link ");

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.blueappsoftware.com/"))
                .setDynamicLinkDomain("examplevr.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                //.setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildDynamicLink();
//click -- link -- google play store -- inistalled/ or not  ----
        Uri dynamicLinkUri = dynamicLink.getUri();
        Log.e("main", "  Long refer "+ dynamicLink.getUri());

        createReferlink(userId);

    }

    public void createReferlink(String custid){
        // manuall link
        String sharelinktext  = "https://examplevr.page.link/?" +
                "link=http://https://examplevr.page.link/?"+custid +"-"+
                "&apn="+ getPackageName()+
                "&st="+"My Refer Link"+
                "&sd="+"Reward Coins 20"+
                "&si="+"https://www.blueappsoftware.com/logo-1.png";

        Log.e("mainactivity", "sharelink - "+sharelinktext);
        // shorten the link
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                //.setLongLink(dynamicLink.getUri())    // enable it if using firebase method dynamicLink
                .setLongLink(Uri.parse(sharelinktext))  // manually
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();
                            Log.e("main ", "short link "+ shortLink.toString());
                            // share app dialog
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT,  shortLink.toString());
                            intent.setType("text/plain");
                            startActivity(intent);


                        } else {
                            // Error
                            // ...
                            Log.e("main", " error "+task.getException() );

                        }
                    }
                });


    }
}

