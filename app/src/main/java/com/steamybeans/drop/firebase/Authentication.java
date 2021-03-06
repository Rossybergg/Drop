package com.steamybeans.drop.firebase;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.steamybeans.drop.views.HomeActivity;
import com.steamybeans.drop.views.LoginPage;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import
//end


public class Authentication extends AppCompatActivity {
    private final Context context;

    public Authentication(Context context) {
        this.context = context;
    }

    private FirebaseAuth firebaseAuth;

    public void login(String email, String password) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // sign in success
                            context.startActivity(new Intent(context, HomeActivity.class));
                        } else {
                            // Sign in fails
                            Toast.makeText(context, "Email or password is incorrect", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void checkUsernameIsUnique(final String username, final String password, final String email) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList usernames = new ArrayList();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    usernames.add(snapshot.child("username").getValue().toString());
                }

                if (usernames.contains(username)) {
                    Toast.makeText(context, "Username already exists", Toast.LENGTH_LONG).show();
                } else {
                    signUp(email, password, username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void signUp(String email, String password, final String username) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User signedUpUser= new User();
                            String uid = signedUpUser.getUid();
                            addUserToDatabase(uid, username);
                            // Sign up success
                            context.startActivity(new Intent(context, LoginPage.class));
                        } else {
                            // Sign up fails
                            Toast.makeText(context, "Email address is not valid", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void alreadySignedIn() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {context.startActivity(new Intent(context, HomeActivity.class));}
    }

    public void checkAccountIsActive() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof FirebaseAuthInvalidUserException) {
                        context.startActivity(new Intent(context, LoginPage.class));
                    }
                }
            });
        }
    }


    private void addUserToDatabase(final String uid, final String username) {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                databaseReference.child(uid).child("username").setValue(username);
                databaseReference.child(uid).child("profileimage").setValue("default");
                databaseReference.child(uid).child("notifications").child("default").setValue("null");
                databaseReference.child(uid).child("achievementdata").child("downvotesgiven").setValue(0);
                databaseReference.child(uid).child("achievementdata").child("downvotesreceived").setValue(0);
                databaseReference.child(uid).child("achievementdata").child("dropsposted").setValue(0);
                databaseReference.child(uid).child("achievementdata").child("dropsviewed").setValue(0);
                databaseReference.child(uid).child("achievementdata").child("upvotesgiven").setValue(0);
                databaseReference.child(uid).child("achievementdata").child("upvotesreceived").setValue(0);
                databaseReference.child(uid).child("achievementdata").child("xp").setValue(0);
                databaseReference.child(uid).child("achievementdata").child("profilepicture").setValue(0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
