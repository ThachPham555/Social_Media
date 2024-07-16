package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.project.model.Notification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageReference mStorageRef;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private EditText editTextDescription;
    private ImageView imageView, userImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        db = FirebaseFirestore.getInstance();

        editTextDescription = findViewById(R.id.edit_text_description);
        imageView = findViewById(R.id.image_view);

        Button buttonChooseImage = findViewById(R.id.button_choose_image);
        buttonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        Button buttonUpload = findViewById(R.id.button_upload);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
            }
        });

        Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadFile() {
        if (imageUri != null) {
            final String des = editTextDescription.getText().toString().trim();
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();

                                    String gio = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
                                    String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

                                    auth = FirebaseAuth.getInstance();

                                    FirebaseUser currentUser = auth.getCurrentUser();
                                    if (currentUser != null) {
                                        String userId = currentUser.getUid();

                                        db.collection("users").document(userId).get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        if (documentSnapshot.exists()) {
                                                            String userName = documentSnapshot.getString("username");
                                                            String userImage = documentSnapshot.getString("image");
                                                            Map<String, Object> upload = new HashMap<>();
                                                            upload.put("name", userName);
                                                            upload.put("des", des);
                                                            upload.put("image", downloadUrl);
                                                            upload.put("gio", gio);
                                                            upload.put("date", date);
                                                            upload.put("userImage", userImage);

                                                            db.collection("posts")
                                                                    .add(upload)
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                            Toast.makeText(AddActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                                                            Log.d("Firestore", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                                            Notification noti = new Notification();
                                                                            noti.setUserId(userId);
                                                                            noti.setPostId(documentReference.getId());
                                                                            noti.setImage(downloadUrl);
                                                                            noti.setMessage( userName + " đã đăng một bài viết vào lúc " + gio + " ngày " + date);
                                                                            db.collection("notifications")
                                                                                    .add(noti)
                                                                                    .addOnSuccessListener(documentReference1 -> {
                                                                                        Log.d("Notification", "Notification saved successfully");
                                                                                    });
                                                                            Intent i = new Intent(AddActivity.this, HomeActivity.class);
                                                                            startActivity(i);
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(AddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            Log.d("Firestore", "Error adding document", e);
                                                                        }
                                                                    });
                                                        } else {
                                                            Toast.makeText(AddActivity.this, "User details not found in Firestore", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                })
                                                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(AddActivity.this, "Failed to get user details", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(AddActivity.this, "No user is currently signed in", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("Firestore", "Error adding document", e);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();  // Kết thúc AddActivity và quay lại HomeActivity
    }
}