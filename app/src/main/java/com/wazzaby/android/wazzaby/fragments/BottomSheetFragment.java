package com.wazzaby.android.wazzaby.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.wazzaby.android.wazzaby.R;
import com.wazzaby.android.wazzaby.connInscript.ImagePickerActivity;
import com.wazzaby.android.wazzaby.model.Database.SessionManager;
import com.wazzaby.android.wazzaby.model.dao.DatabaseHandler;

import java.io.IOException;

import static com.wazzaby.android.wazzaby.connInscript.UploadImage.REQUEST_IMAGE;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private LinearLayout galerie;
    private LinearLayout camera;
    private LinearLayout bottomsheetblock;

    private String dark_mode_item = null;
    private DatabaseHandler database;
    private SessionManager session;

    private ImageView gallery_icon;
    private ImageView camera_icon;
    private TextView gallery_text;
    private TextView camera_text;
    private Resources res;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View bossmaleo = inflater.inflate(R.layout.bottom_sheet_picture,container,false);

        database = new DatabaseHandler(getActivity());
        session = new SessionManager(getActivity());
        res = getResources();

        camera = bossmaleo.findViewById(R.id.camera);
        galerie = bossmaleo.findViewById(R.id.galerie);
        bottomsheetblock = bossmaleo.findViewById(R.id.bottom_sheet);

        gallery_icon = bossmaleo.findViewById(R.id.gallery_icon);
        camera_icon = bossmaleo.findViewById(R.id.camera_icon);
        gallery_text = bossmaleo.findViewById(R.id.gallery_text);
        camera_text = bossmaleo.findViewById(R.id.camera_text);

        dark_mode_item = database.getDARKMODE();
        //si le dark mode est activé
        if (dark_mode_item.equals("1"))
        {
            bottomsheetblock.setBackgroundColor(getResources().getColor(R.color.darkprimarydark));
            Drawable gallery_drawable_icon = res.getDrawable(R.drawable.baseline_collections_black_24);
            gallery_drawable_icon.mutate().setColorFilter(getResources().getColor(R.color.graycolor), PorterDuff.Mode.SRC_IN);
            gallery_icon.setImageDrawable(gallery_drawable_icon);
            gallery_text.setTextColor(res.getColor(R.color.graycolor));

            Drawable camera_drawable_icon = res.getDrawable(R.drawable.baseline_camera_alt_black_24);
            camera_drawable_icon.mutate().setColorFilter(getResources().getColor(R.color.graycolor),PorterDuff.Mode.SRC_IN);
            camera_icon.setImageDrawable(camera_drawable_icon);
            camera_text.setTextColor(res.getColor(R.color.graycolor));
            //setTheme(R.style.AppDarkTheme);
            //edit_modenuit.setChecked(true);
        } else if (dark_mode_item.equals("0")) {
            //setTheme(R.style.AppTheme);
            //edit_modenuit.setChecked(false);
        }

        //On capture l'évenement sur la camera
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(),"Click sur la camera !!",Toast.LENGTH_LONG).show();
                launchCameraIntent();
            }
        });

        galerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(),"Click sur la galerie !!",Toast.LENGTH_LONG).show();
                launchGalleryIntent();
            }
        });

        // Inflate the layout for this fragment
        return bossmaleo;
    }


    private void launchCameraIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                    // loading profile image from local cache
                    //loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}