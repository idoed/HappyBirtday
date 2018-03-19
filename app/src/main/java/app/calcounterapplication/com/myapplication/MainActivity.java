package app.calcounterapplication.com.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ScaleDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;

public class MainActivity extends AppCompatActivity {

    public static final int IMAGE_GALLARY_REQUEST = 20, REQUEST_CAMERA = 1, SAVE_PHOTO = 5;
    private ImageView mChangingImageBut;
    private TextView mHideText;
    private android.support.v7.app.AlertDialog.Builder dialog;
    public Bitmap bitmap;
    public File mFilePath;
    public RelativeLayout myview;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Bitmap bit = mChangingImageBut.getDrawingCache();
        outState.putParcelable("myImage", bit);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Bitmap bitmap = savedInstanceState.getParcelable("myImage");
            mChangingImageBut.setImageBitmap(bitmap);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myview=(RelativeLayout) findViewById(R.id.myscreen_view);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, SAVE_PHOTO);
            }
        });
//        final FloatingActionButton sharefab = (FloatingActionButton) findViewById(R.id.fabShare);
//        sharefab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mFilePath == null) {
//                    Toast.makeText(MainActivity.this, "Save the Image First", Toast.LENGTH_SHORT).show();
//                } else {
//                    Uri pictureUri = Uri.fromFile(mFilePath);
//                    Intent intent = new Intent(Intent.ACTION_SEND);
//                    intent.setType("image/jpeg");
//                    intent.putExtra(Intent.EXTRA_STREAM, pictureUri);
//                    startActivity(Intent.createChooser(intent, "Share picture with..."));
//                }
//            }
//        });
        mChangingImageBut = (ImageView) findViewById(R.id.changing_pic);
        mHideText = findViewById(R.id.disapear_text);

        mChangingImageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHideText.setVisibility(View.GONE);
                dialog = new android.support.v7.app.AlertDialog.Builder((Activity) view.getContext());
                dialog.setTitle("Upload Photo");
                dialog.setMessage("Choose Between 2 Options");
                dialog.setCancelable(true);

                //Setting the Image Caputre (New Photo)
                dialog.setPositiveButton("Take New Photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        askPermission(Manifest.permission.CAMERA, REQUEST_CAMERA);

                    }
                });
                //Set The Existing Photo Option
                dialog.setNegativeButton("Choose exist photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        askPermission(Manifest.permission.READ_EXTERNAL_STORAGE, IMAGE_GALLARY_REQUEST);
                    }
                });
                dialog.show();
            }
        });
    }

    public void SavingPhoto() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        FloatingActionButton fabShare = (FloatingActionButton) findViewById(R.id.fabShare);
        fab.setVisibility(View.INVISIBLE);
//        fab.setVisibility(View.INVISIBLE);
        View view = (View)findViewById(R.id.myscreen_view);
        view.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(view.getDrawingCache());
        Save saveImage = new Save();
        saveImage.saveImage(getApplicationContext(), bitmap);
//        try {
//            mFilePath = saveImage.getFile();
//        } catch (NullPointerException e) {
//            Toast.makeText(this, "Didn't receive the file", Toast.LENGTH_SHORT).show();
//        }
        fab.setVisibility(View.VISIBLE);
//        fabShare.setVisibility(View.VISIBLE);
    }

    public void CallGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        //*Where do we want to find the data?
        File pictureDerectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String path = pictureDerectory.getPath();
        //Finaly get URI representation=
        Uri data1 = Uri.parse(path);
        //set the Data and type
        intent.setDataAndType(data1, "image/*");
        //WE WILL invoke that activity and get somthing from it.
        startActivityForResult(intent, IMAGE_GALLARY_REQUEST);

    }

    public void CallCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case IMAGE_GALLARY_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CallGallery();

                }
            case REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CallCamera();

                }
            case SAVE_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SavingPhoto();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    public void askPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            //We Dont Have Permission
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            //we have permission
            switch (requestCode) {
                case IMAGE_GALLARY_REQUEST:
                    CallGallery();
                    break;
                case REQUEST_CAMERA:
                    CallCamera();
                    break;
                case SAVE_PHOTO:
                    SavingPhoto();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            //if we are here everything processed successfully;
            if (requestCode == IMAGE_GALLARY_REQUEST) {
                //if we are here we got the image

                Uri Imageuri = data.getData();
                //the address of the image on the sd
                InputStream inputStream;

                try {
                    inputStream = getContentResolver().openInputStream(Imageuri);
                    Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                    mChangingImageBut.setImageBitmap(myBitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    //getBitmap from string

                }
            } else if (requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                Bitmap image = (Bitmap) bundle.get("data");
                mChangingImageBut.setImageBitmap(image);
                mChangingImageBut.setRotation(90);

            }
        }
    }
}
