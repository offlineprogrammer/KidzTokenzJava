package com.offlineprogrammer.KidzTokenz;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.offlineprogrammer.KidzTokenz.kid.Kid;
import com.offlineprogrammer.KidzTokenz.task.KidTask;

import java.util.List;

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
                // Redeem.this.lambda$initViews$0$Redeem(view);
            }
        });
        this.cameraIntentText.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                //   Redeem.this.lambda$initViews$1$Redeem(view);
            }
        });
        this.editButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                //   Redeem.this.lambda$initViews$2$Redeem(view);
            }
        });

        if (this.m_selectedKid != null) {
            //  this.pointText.setText(String.valueOf(m_selectedKid.getTokenNumber()));
            //  EditText customEditText = this.noteText;
            //  customEditText.setText("Redeemed wish list item \"" + m_selectedTask.getTaskName() + "\"");
            //  this.noteText.setVisibility(View.GONE);
            //  this.pointText.setEnabled(false);
            //  this.pointText.setBackgroundColor(0);
            //  this.noteText.setBackgroundColor(0);
            //  ((TextView) view.findViewById(C1616R.C1619id.wish_item_name)).setText(this.wish.getName());
            //  view.findViewById(C1616R.C1619id.wish_item_1).setVisibility(0);
            //  view.findViewById(C1616R.C1619id.wish_item_2).setVisibility(0);
            //  ((TextView) view.findViewById(C1616R.C1619id.subtitle_redeem)).setText(C1616R.string.redeeming_wish_list_item);
            //  view.findViewById(C1616R.C1619id.some_title).setVisibility(8);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}