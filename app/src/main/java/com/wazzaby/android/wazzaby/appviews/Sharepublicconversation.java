package com.wazzaby.android.wazzaby.appviews;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.model.Const;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;
import com.wazzaby.android.wazzaby.model.data.Profil;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import developer.semojis.Helper.EmojiconEditText;
import developer.semojis.actions.EmojIconActions;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.wazzaby.android.wazzaby.appviews.Home.titlehome;

public class Sharepublicconversation extends AppCompatActivity {

    //private Toolbar toolbar;
    private Resources res;
    private ProgressDialog pDialog;
    private DatabaseHandler database;
    private SessionManager session;
    private String LIBELLE;
    private EmojiconEditText editText;
    private ImageView image_cancel;
    private ImageView image_post;

    public static final int REQUEST_GALLERY_IMAGE = 1;
    public static final int REQUEST_IMAGE_CAPTURE = 0;
    public static String fileName;
    private static final String TAG = Sharepublicconversation.class.getSimpleName();

    private View rootView;
    private ImageView image_view;
    private File sourceFile;
    private String name_file;
    private String extension;
    private String id_messagepublic;
    private String ID_photo;
    private JSONObject reponse;
    //private JSONObject data;
    private String etat;
    private boolean status = false;
    private Profil user;
    private com.google.android.material.textfield.TextInputLayout block;
    private ImageView close_activity;
    private ImageView send_messagepublic;
    private ImageView add_picture;
    private  ImageView insert_picture;
    private Context context;
    //private EmojiEditText emojiEditText;
    private ImageView add_emoji;
    private RelativeLayout rootviewemoji;
    private boolean visibility_emoji = true;
    public Uri globale_uri;
    private EmojIconActions emojIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sharepublicconversation);
        res = getResources();
        //toolbar =  findViewById(R.id.toolbar);
        editText = findViewById(R.id.textArea_information);
        image_cancel = findViewById(R.id.image_cancel);
        image_post = findViewById(R.id.image_view);
       // imageblock = findViewById(R.id.imageblock);
        block = findViewById(R.id.block_edittext);
        this.etat = "1";
        this.status = false;
        //the emoji showing condition
        visibility_emoji = true;
        add_emoji = findViewById(R.id.add_emoji);
        rootviewemoji = findViewById(R.id.myemojiview);


        database = new DatabaseHandler(this);
        session = new SessionManager(getApplicationContext());
        user = database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID)));

        /*if(Integer.valueOf(user.getETAT()) == 1) {
            Toast.makeText(this,user.getETAT(),Toast.LENGTH_LONG).show();
        }*/

        rootView = findViewById(R.id.root_view);
        //emojiconEditText =  findViewById(R.id.textArea_information);
        //icon_cancel_image_view = findViewById(R.id.image_cancel);
        image_view = findViewById(R.id.image_view);
        image_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_view.setVisibility(View.GONE);
                image_cancel.setVisibility(View.GONE);
                status = false;
            }
        });

        close_activity = findViewById(R.id.close_activity);
        send_messagepublic = findViewById(R.id.send);
        add_picture = findViewById(R.id.add_picture);
        insert_picture = findViewById(R.id.insert_picture);
        context = this;



        close_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                setResult(RESULT_OK, i);
                finish();
            }
        });

        send_messagepublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate() == true) {
                    LIBELLE = editText.getText().toString();
                    pDialog = new ProgressDialog(Sharepublicconversation.this);
                    pDialog.setMessage(res.getString(R.string.chargement));
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                    if (status) {
                        Connexion();
                    } else {
                        RequeteFinale();
                    }
                }
            }
        });

        add_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(Sharepublicconversation.this)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    takeCameraImage();
                                    //GalleryTakeImagePickerOptions();
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        insert_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(Sharepublicconversation.this)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    chooseImageFromGallery();
                                    //GalleryTakeImagePickerOptions();
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });


        this.ConnexionSynchronizationProblematique();

        emojIcon= new EmojIconActions(this, rootView,  editText,
                add_emoji);
        emojIcon.ShowEmojIcon();

        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard","open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard","close");
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send, menu);
        MenuItem favoriteItem = menu.findItem(R.id.send);
        MenuItem favoriteItem1 = menu.findItem(R.id.add_picture);
        MenuItem favoriteItem2 = menu.findItem(R.id.insert_picture);
        Drawable newIcon = favoriteItem.getIcon();
        Drawable newIcon1 = favoriteItem1.getIcon();
        Drawable newIcon2 = favoriteItem2.getIcon();
        newIcon.mutate().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);
        newIcon1.mutate().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);
        newIcon2.mutate().setColorFilter(Color.rgb(255, 255, 255), PorterDuff.Mode.SRC_IN);
        favoriteItem.setIcon(newIcon);
        favoriteItem1.setIcon(newIcon1);
        favoriteItem2.setIcon(newIcon2);
        return true;
    }


    private void Connexion()
    {
        int anonymous;
        if(Integer.valueOf(user.getETAT())==1){
            anonymous = 1;
        }   else {
            anonymous = 0;
        }
        String urlrecuperefile = Const.dns.concat("/WazzabyApi/public/api/photomessagepublic?file_extension=").concat(extension)
                .concat("&id_user=")
                .concat(String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getID()))
                .concat("&id_problematique=")
                .concat(String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB()))
                .concat("&anonymous=").concat(String.valueOf(anonymous));
        //String  url = Const.dns.concat("/WazzabyApi/public/api/SaveMessagePublic?etat=").concat(this.etat).concat("&libelle=").concat(LIBELLE).concat("&id_problematique=").concat(String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB()).concat('&ID=').concat(String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getID())).concat("&id_message_public=").concat(String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlrecuperefile,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //pDialog.dismiss();
                        showJSON(response);
                        UploadMultipart();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        if(error instanceof ServerError)
                        {
                            Toast.makeText(Sharepublicconversation.this,res.getString(R.string.error_volley_servererror),Toast.LENGTH_LONG).show();

                        }else if(error instanceof NetworkError)
                        {
                            Toast.makeText(Sharepublicconversation.this,res.getString(R.string.error_volley_noconnectionerror),Toast.LENGTH_LONG).show();

                        }else if(error instanceof AuthFailureError)
                        {
                            Toast.makeText(Sharepublicconversation.this,res.getString(R.string.error_volley_noconnectionerror),Toast.LENGTH_LONG).show();

                        }else if(error instanceof ParseError)
                        {
                            Toast.makeText(Sharepublicconversation.this,res.getString(R.string.error_volley_noconnectionerror),Toast.LENGTH_LONG).show();

                        }else if(error instanceof NoConnectionError)
                        {
                            Toast.makeText(Sharepublicconversation.this,res.getString(R.string.error_volley_noconnectionerror),Toast.LENGTH_LONG).show();

                        }else if(error instanceof TimeoutError)
                        {
                            Toast.makeText(Sharepublicconversation.this,res.getString(R.string.error_volley_noconnectionerror),Toast.LENGTH_LONG).show();

                        }else
                        {

                            Toast.makeText(Sharepublicconversation.this,res.getString(R.string.error_volley_noconnectionerror),Toast.LENGTH_LONG).show();

                        }
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showSettingsDialog() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(Sharepublicconversation.this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(Sharepublicconversation.this);
        dialog.setCancelable(false);
        dialog.setTitle("Permissions");
        dialog.setMessage("Etes vous sure de supprimer cette permission ?" );
        dialog.setPositiveButton("Supprimer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                openSettings();
            }
        })
                .setNegativeButton("Annuler ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Action for "Cancel".
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void chooseImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        sourceFile = new File(path, fileName);
        return getUriForFile(Sharepublicconversation.this, getPackageName() + ".provider", image);
    }

    private void takeCameraImage() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            fileName = System.currentTimeMillis() + ".jpg";
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = getCacheImagePath(fileName);
                    globale_uri = getCacheImagePath(fileName);
                    status = true;
                    extension = "jpg";
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        image_post.setImageBitmap(bitmap);
                        image_cancel.setVisibility(View.VISIBLE);
                        image_view.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Sharepublicconversation.this, "Echec !!", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_GALLERY_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = data.getData();
                    globale_uri = data.getData();
                    extension = "jpg";
                    sourceFile = new File(getPathGallery(imageUri));
                    status = true;
                    //Toast.makeText(Sharepublicconversation.this,getPathGallery(imageUri),Toast.LENGTH_LONG).show();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        image_post.setImageBitmap(bitmap);
                        image_cancel.setVisibility(View.VISIBLE);
                        image_view.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Sharepublicconversation.this, "Echec !!", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }



    public void UploadMultipart() {

        String path = getPath(globale_uri);
        try {

            String uploadId = UUID.randomUUID().toString();

            new MultipartUploadRequest(this, uploadId, Const.dns.concat("/uploads/uploadScript.php"))
                    .addFileToUpload(path, "photostatus") //Adding file
                    .addParameter("name_file", name_file)
                    .setNotificationConfig(new UploadNotificationConfig())
                    .setMaxRetries(2)
                    .startUpload();
            etat = "0";
            RequeteFinale();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    public void showJSON(String response)
    {
        try {
            reponse = new JSONObject(response);
            name_file = reponse.getString("name_file");
            id_messagepublic = reponse.getString("id_messagepublic");
            ID_photo = reponse.getString("ID_photo");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void RequeteFinale()
    {
        int anonymous;
        if (Integer.valueOf(user.getETAT())==1) {
          anonymous = 1;
        } else {
          anonymous = 0;
        }
        String urlrequetefinale;
        if (status == true) {
            urlrequetefinale = Const.dns.concat("/WazzabyApi/public/api/SaveMessagePublic?etat=")
                    .concat(this.etat).concat("&libelle=")
                    .concat(LIBELLE)
                    .concat("&id_problematique=").concat(String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB()))
                    .concat("&ID=").concat(String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getID()))
                    .concat("&id_message_public=").concat(this.id_messagepublic)
                    .concat("&anonymous=").concat(String.valueOf(anonymous));
        }else {
            urlrequetefinale = Const.dns.concat("/WazzabyApi/public/api/SaveMessagePublic?etat=")
                    .concat(this.etat).concat("&libelle=")
                    .concat(LIBELLE).concat("&id_problematique=")
                    .concat(String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getIDPROB()))
                    .concat("&ID=").concat(String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getID()))
                    .concat("&anonymous=").concat(String.valueOf(anonymous));
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlrequetefinale,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        Toast.makeText(Sharepublicconversation.this,res.getString(R.string.comment_share),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Sharepublicconversation.this,Home.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        if(error instanceof ServerError)

                        {
                            Toast.makeText(Sharepublicconversation.this,res.getString(R.string.error_volley_servererror),Toast.LENGTH_LONG).show();

                        }else if(error instanceof NetworkError)
                        {
                            Toast.makeText(Sharepublicconversation.this,res.getString(R.string.error_volley_noconnectionerror),Toast.LENGTH_LONG).show();

                        }else if(error instanceof AuthFailureError)
                        {
                            Toast.makeText(Sharepublicconversation.this,res.getString(R.string.error_volley_noconnectionerror),Toast.LENGTH_LONG).show();

                        }else if(error instanceof ParseError)
                        {
                            Toast.makeText(Sharepublicconversation.this,res.getString(R.string.error_volley_noconnectionerror),Toast.LENGTH_LONG).show();

                        }else if(error instanceof NoConnectionError)
                        {
                            Toast.makeText(Sharepublicconversation.this,res.getString(R.string.error_volley_noconnectionerror),Toast.LENGTH_LONG).show();

                        }else if(error instanceof TimeoutError)
                        {
                            Toast.makeText(Sharepublicconversation.this,res.getString(R.string.error_volley_noconnectionerror),Toast.LENGTH_LONG).show();

                        }else
                        {

                            Toast.makeText(Sharepublicconversation.this,res.getString(R.string.error_volley_noconnectionerror),Toast.LENGTH_LONG).show();

                        }
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public String getPathGallery(Uri uri)
    {
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null,MediaStore.Images.Media._ID+" = ?",new String[]{document_id},null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    public boolean validate() {
        boolean valid = true;

        String _champ_libelle = editText.getText().toString();

        if(_champ_libelle.isEmpty()) {
            block.setError("Veuillez renseigner votre message public !");
            Toast.makeText(getApplicationContext(),"Veuillez renseigner votre message public !",Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            block.setError(null);
        }


        return valid;
    }


    //Cette methode assure la synchronization après une mise à jour de problématique
    public void ConnexionSynchronizationProblematique() {
        String url_sendkey = Const.dns.concat("/WazzabyApi/public/api/SynchronizationProblematique?user_id=").concat(String.valueOf(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getID()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_sendkey,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject problematique = new JSONObject(response);
                            String problematique_libelle = problematique.getString("problematique_libelle");
                            int id_prob = problematique.getInt("problematique_id");
                            user.setLibelle_prob(problematique_libelle);
                            user.setIDPROB(String.valueOf(id_prob));

                            database.UpdateIDPROB(database.getUSER(Integer.valueOf(session.getUserDetail().get(SessionManager.Key_ID))).getID(),Integer.valueOf(user.getIDPROB()),user.getLibelle_prob());
                            titlehome.setTitle(user.getLibelle_prob());

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
