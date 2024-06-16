package com.smart.mynote;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.smart.mynote.googleAuth.GoogleAuthActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{
    FloatingActionButton fab, fabLogout;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceUpdate;
    StorageReference storageReference;
    ValueEventListener eventListener;
    RecyclerView recyclerView;
    List<NoteLists> noteLists;
    MainAdapter adapter;
    SearchView searchView;
    Uri uri;
    String imageUrl = "", uID;
    AlertDialog dialog;
    Dialog alertDialog;
    private ImageView dialogImageView;
    TextView updateTitle, updateDesc, headingTV;
    Button updateButton, addButton;
    private ProgressDialog progressDialog;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);
        fabLogout = findViewById(R.id.fabLogout);
        searchView = findViewById(R.id.search);

        Log.d("sanju", "onCreate: "+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        searchView.clearFocus();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        dialog = builder.create();
        dialog.show();

        noteLists = new ArrayList<>();

        uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("sanju", "onCreate: "+ uID);

        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Android Tutorials");

        adapter = new MainAdapter(getApplicationContext(), noteLists, new MainAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(List<NoteLists> dataList, int position) {
                databaseReferenceUpdate = FirebaseDatabase.getInstance()
                        .getReference()
                        .child("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("Android Tutorials").child(dataList.get(position).getKey());

                showDialog(dataList, position, "update");
            }

            @Override
            public void onItemDeleted(List<NoteLists> dataList, int position) {
                showAlertDialog(dataList, position);
            }

            @Override
            public void onTvShowAction(Boolean isSelected) {
//                if (isSelected){
//                    buttonAdd.setVisibility(View.VISIBLE);
//                }else{
//                    buttonAdd.setVisibility(View.GONE);
//                }
            }
        });

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noteLists.clear();
                for (DataSnapshot itemSnapshot: snapshot.getChildren()){
                    NoteLists allNotes = itemSnapshot.getValue(NoteLists.class);
                    allNotes.setKey(itemSnapshot.getKey());
                    noteLists.add(allNotes);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }
        });

        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return true;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(noteLists, 0,"add");
            }
        });

        fabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), GoogleAuthActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void showAlertDialog(List<NoteLists> dataList, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Are you sure! you want to delete the Note?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // User clicked Yes button
                    showProgressDialog();
                    // Handle delete action
                    final DatabaseReference reference = FirebaseDatabase.getInstance()
                            .getReference()
                            .child("users")
                            .child(uID)
                            .child("Android Tutorials");
                    if (noteLists.get(position).getDataImage().isEmpty()){
                        reference.child(MainActivity.this.noteLists.get(position).getKey()).removeValue();
                        Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                reference.child(MainActivity.this.noteLists.get(position).getKey()).removeValue();
                                Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    adapter.notifyDataSetChanged();
                    dismissProgressDialog();
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // Handle the No button click
                    dialog.dismiss();
                })
                .show();
    }

    private void showDialog(List<NoteLists> dataList, int position, String check) {
        alertDialog = new Dialog(this);
        alertDialog.setContentView(R.layout.update_dialog);

        // Adjust dialog window properties
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.gravity = Gravity.CENTER;
            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Optional, for rounded corners
        }

        dialogImageView = alertDialog.findViewById(R.id.updateImage);
        updateTitle = alertDialog.findViewById(R.id.updateTitle);
        updateDesc = alertDialog.findViewById(R.id.updateDesc);
        updateButton = alertDialog.findViewById(R.id.updateButton);
        addButton = alertDialog.findViewById(R.id.addButton);
        headingTV = alertDialog.findViewById(R.id.headingTV);
        dialogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });

        if (check.equals("add")){
            addButton.setVisibility(View.VISIBLE);
            updateButton.setVisibility(View.GONE);
            headingTV.setText("Add Note");

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = "", desc = "";
                    if (!updateTitle.getText().toString().isEmpty())
                        title = updateTitle.getText().toString().trim();

                    if (!updateTitle.getText().toString().isEmpty())
                        desc = updateDesc.getText().toString().trim();

                    if (uri != null){
                        imageUrl = uri.toString();
                    }

                    addData(title, desc, imageUrl);
                    adapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            });

        }
        else {
            addButton.setVisibility(View.GONE);
            updateButton.setVisibility(View.VISIBLE);
            headingTV.setText("Update & Delete Note");

            if (dataList.get(position).getDataImage().isEmpty())
                dialogImageView.setBackground(getApplicationContext().getResources().getDrawable(R.drawable.uploadimg));
            else
                Glide.with(getApplicationContext()).load(dataList.get(position).getDataImage()).into(dialogImageView);

            updateTitle.setText(dataList.get(position).getDataTitle());
            updateDesc.setText(dataList.get(position).getDataDesc());

            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle edit action
                    saveData(updateTitle.getText().toString().trim(), updateDesc.getText().toString().trim(), dataList.get(position).getDataImage(), dataList.get(position).getKey());
                    adapter.notifyDataSetChanged();
                    alertDialog.dismiss();
                }
            });
        }

        alertDialog.show();
    }

    private void addData(String Title, String Desc, String dataImage) {
        showProgressDialog();
        if (uri == null){
            uploadAddData(Title, Desc, dataImage, "");
        }
        else {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Note Images")
                    .child(uri.getLastPathSegment());

            storageReference.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isComplete());
                            Uri urlImage = uriTask.getResult();
                            imageUrl = urlImage.toString();
                            uploadAddData(Title, Desc, dataImage, imageUrl);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dismissProgressDialog();
                            Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void uploadAddData(String dataTitle, String dataDesc, String dataImage, String imageUrl) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String randomId = database.getReference().push().getKey();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Set the desired time zone
        String dateTime = dateFormat.format(new Date());

        NoteLists noteLists;

        if (Objects.equals(imageUrl, ""))
            noteLists = new NoteLists(dataTitle, dataDesc, dateTime, dataImage, randomId);
        else
            noteLists = new NoteLists(dataTitle, dataDesc, dateTime, imageUrl, randomId);

        //We are changing the child from title to currentDate,
        // because we will be updating title as well and it may affect child value.
        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(uID)
                .child("Android Tutorials")
                .child(randomId)
                .setValue(noteLists)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dismissProgressDialog();
                            Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismissProgressDialog();
                        Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

        imageUrl = "";
        uri = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri dataUri = data.getData();
                    if (dataUri != null) {
                        uri = dataUri;
                        dialogImageView.setImageURI(uri);
                        Log.d("sanju", "Image URI: " + uri.toString());
                    } else {
                        Log.d("sanju", "Data URI is null");
                    }
                } else {
                    Log.d("sanju", "Data is null");
                }
            } else {
                Log.d("sanju", "Result code is not OK");
            }
        } else {
            Log.d("sanju", "Request code is not PICK_IMAGE_REQUEST");
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Processing...");
            progressDialog.setCancelable(true); // Allows the back button to dismiss the dialog
            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                }
            });
        }
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void saveData(String dataTitle, String dataDesc, String dataImage, String key){
        showProgressDialog();

        if (uri == null){
            updateData(dataTitle, dataDesc, dataImage, "", key);
        }
        else {
            storageReference = FirebaseStorage.getInstance().getReference().child("Note Images").child(uri.getLastPathSegment());

            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete());
                    Uri urlImage = uriTask.getResult();
                    imageUrl = urlImage.toString();
                    updateData(dataTitle, dataDesc, dataImage, imageUrl, key);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dismissProgressDialog();
                    Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void updateData(String dataTitle, String dataDesc, String dataImage, String imageUrl, String key){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Set the desired time zone
        String dateTime = dateFormat.format(new Date());
        NoteLists noteLists;

        if (Objects.equals(imageUrl, ""))
            noteLists = new NoteLists(dataTitle, dataDesc, dateTime, dataImage, key);
        else
            noteLists = new NoteLists(dataTitle, dataDesc, dateTime, imageUrl, key);

        databaseReferenceUpdate.setValue(noteLists).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    dismissProgressDialog();
                    Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                    if (Objects.equals(imageUrl, ""))
                        return;

                    StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(dataImage);
                    reference.delete();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dismissProgressDialog();
                Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void searchList(String text){
        ArrayList<NoteLists> searchList = new ArrayList<>();
        for (NoteLists noteLists : noteLists){
            if (noteLists.getDataTitle().toLowerCase().contains(text.toLowerCase())){
                searchList.add(noteLists);
            }
        }
        adapter.searchDataList(searchList);
    }
}