package my.google.drive;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.tensorflow.demo.ClassifierActivity;
import org.tensorflow.demo.env.Logger;

/**
 * Created by garyhsu on 2018/8/2.
 */

public class DriveService {

    private static DriveService sDriveService = null;

    /**
     * Handles high-level drive functions like sync
     */
    private DriveClient mDriveClient;

    /**
     * Handle access to Drive resources/files.
     */
    private DriveResourceClient mDriveResourceClient;

    private Activity mActivity;

    public static DriveService getInstance(Activity activity) {
        if (sDriveService == null) {
            synchronized (DriveService.class) {
                if (sDriveService == null) {
                    sDriveService = new DriveService(activity);
                }
            }
        }
        return sDriveService;
    }

    private DriveService(Activity activity){
        mActivity = activity;
    }

    /**
     * Starts the sign-in process and initializes the Drive client.
     */
    public void signIn() {
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(mActivity);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        } else {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(mActivity, signInOptions);
            mActivity.startActivityForResult(googleSignInClient.getSignInIntent(), ClassifierActivity.REQUEST_CODE_SIGN_IN);
        }
    }

    /**
     * Continues the sign-in process, initializing the Drive clients with the current
     * user's account.
     */
    public void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(mActivity.getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(mActivity.getApplicationContext(), signInAccount);
        Toast.makeText(mActivity,"Successfully sign in google drive",Toast.LENGTH_SHORT).show();
    }

    public void uploadFile(String fileName) {
        final Task<DriveFolder> rootFolderTask = mDriveResourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
        Tasks.whenAll(rootFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = rootFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();
                    File imgFile = new  File("/mnt/sdcard/tensorflow/"+fileName);
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                    myBitmap.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                    try {
                        outputStream.write(bitmapStream.toByteArray());
                    } catch (IOException e) {
                        Logger.e("Unable to write file contents " + e);
                    }
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(fileName)
                            .setMimeType("image/png")
                            .setStarred(true)
                            .build();
                    return mDriveResourceClient.createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(mActivity,
                        driveFile -> {
                            Logger.d("upload to google drive sucessfully");
                            Toast.makeText(mActivity,"upload to google drive sucessfully",Toast.LENGTH_SHORT).show();
                        })
                .addOnFailureListener(mActivity, e -> {
                    Logger.e("Unable to upload image", e);
                    Toast.makeText(mActivity,"unable to upload image to google drive",Toast.LENGTH_SHORT).show();
                });
    }

}
