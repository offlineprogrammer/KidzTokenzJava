package com.offlineprogrammer.KidzTokenz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.offlineprogrammer.KidzTokenz.kid.Kid;
import com.offlineprogrammer.KidzTokenz.task.KidTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CelebrateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CelebrateFragment extends Fragment implements EasyPermissions.PermissionCallbacks {


    private static final String ARG_PARAM1 = "selectedKid";
    private static final String ARG_PARAM2 = "selectedTask";
    private static final int CAMERA_REQUEST = 1888;
    public Context context;
    ImageView kidImageView;
    private Kid m_selectedKid;
    private KidTask m_selectedTask;
    private TextView cameraIntentText;
    private ImageView editButton;
    private Bitmap image;
    private String imagePath;
    private ImageView celebrate_image_view;
    private EditText celebrate_note_text;
    private TextView warnText;


    public CelebrateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param selectedKid  Parameter 1.
     * @param selectedTask Parameter 2.
     * @return A new instance of fragment CelebrateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CelebrateFragment newInstance(Kid selectedKid, KidTask selectedTask) {
        CelebrateFragment fragment = new CelebrateFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, selectedKid);
        args.putParcelable(ARG_PARAM2, selectedTask);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            m_selectedKid = getArguments().getParcelable(ARG_PARAM1);
            m_selectedTask = getArguments().getParcelable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_celebrate, container, false);
    }

    public void onAttach(Context context2) {
        super.onAttach(context2);
        this.context = context2;
    }

    public void onResume() {
        super.onResume();

    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (this.m_selectedKid == null) {
            ((TaskActivity) this.context).setOkAndFinish();
        } else {
            initViews(view);
        }
    }


    private void initViews(View view) {


        this.celebrate_image_view = view.findViewById(R.id.celebrate_image_view);

        this.cameraIntentText = view.findViewById(R.id.camera_button);
        this.warnText = view.findViewById(R.id.warn_text);
        this.celebrate_note_text = view.findViewById(R.id.celebrate_note_text);

        this.editButton = view.findViewById(R.id.celebrate_edit_image);
        kidImageView = view.findViewById(R.id.kidMonsterImage);

        kidImageView.setImageResource(context.getResources().getIdentifier(m_selectedKid.getMonsterImageResourceName(), "drawable",
                context.getPackageName()));


        view.findViewById(R.id.celebrate_share_button).setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                validate();
            }
        });
        this.cameraIntentText.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                pickImage();
            }
        });
        this.editButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                pickImage();
            }
        });


    }

    private void pickImage() {

        ImagePicker.create(this).returnMode(ReturnMode.ALL).folderMode(true).includeVideo(false).limit(1).theme(R.style.AppTheme_NoActionBar).single().start();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, CAMERA_REQUEST);

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).setTitle("Permissions Required").setPositiveButton("Settings").setNegativeButton("Cancel").setRequestCode(5).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        EasyPermissions.onRequestPermissionsResult(i, strArr, iArr, this);
    }


    public void onCropFinish(Intent intent) {
        Uri output = UCrop.getOutput(intent);
        this.imagePath = output.getPath();
        GlideApp.with(this).load(output.getPath()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).centerCrop().into(this.celebrate_image_view);
        this.cameraIntentText.setVisibility(View.GONE);
        this.editButton.setVisibility(View.VISIBLE);
        this.celebrate_image_view.setVisibility(View.VISIBLE);
        this.image = BitmapFactory.decodeFile(this.imagePath);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        Image firstImageOrNull;
        if (ImagePicker.shouldHandle(i, i2, intent) && (firstImageOrNull = ImagePicker.getFirstImageOrNull(intent)) != null) {
            UCrop.of(Uri.fromFile(new File(firstImageOrNull.getPath())), Uri.fromFile(new File(this.context.getCacheDir(), "cropped"))).withAspectRatio(1.0f, 1.0f).start((TaskActivity) this.context);

        }
    }


    private void validate() {
        String trim = this.celebrate_note_text.getText().toString().trim();

        if (trim.isEmpty()) {
            this.warnText.setText(R.string.share_note_are_empty);
            this.warnText.setVisibility(View.VISIBLE);
            return;
        }
        this.warnText.setVisibility(View.INVISIBLE);


        ((TaskActivity) this.context).gotoSharePage(this.image, trim);
    }


}