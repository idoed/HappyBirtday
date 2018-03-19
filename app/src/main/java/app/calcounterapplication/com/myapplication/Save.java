package app.calcounterapplication.com.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by idoed on 15/03/2018.
 */

public class Save {
    private Context theThis;
    private String NameOfFolder = "/HappyBirthday";
    private String NameOfFile = "HappyBirtdayImage";
    public File file;

    public File getFile() {
        return file;
    }

    public void saveImage(Context context, Bitmap ImageToSave) {
        theThis = context;
        String mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + NameOfFolder;
        String CurrectDateAndTime = getCurrectDateAndTime();
        File dir = new File(mFilePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
         file = new File(dir, NameOfFile + CurrectDateAndTime + ".jpg");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ImageToSave.compress(Bitmap.CompressFormat.JPEG,85,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            MakeSureFileWasCreated(file);
            AbleToSave();
        } catch (FileNotFoundException e) {
            UnableToSave();
        } catch (IOException e) {
            UnableToSave();
        }
    }

    private void MakeSureFileWasCreated(File file) {
        MediaScannerConnection.scanFile(theThis, new String[]{file.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String s, Uri uri) {
                Log.e("External Storage"," Scanned "+s+" :");
                Log.e("External Storage","---> uri= "+uri);

            }
        });

    }

    private void AbleToSave() {
        Toast.makeText(theThis, "Picture saved", Toast.LENGTH_SHORT).show();
    }

    private String getCurrectDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-mm-ss");
        String mFormatDate = df.format(c.getTime());
        return mFormatDate;
    }


    private void UnableToSave() {
        Toast.makeText(theThis, "Picture Cannot be Saved on Gallery", Toast.LENGTH_SHORT).show();
    }
}