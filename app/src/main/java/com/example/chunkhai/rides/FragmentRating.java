package com.example.chunkhai.rides;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.chunkhai.rides.Object.ParcelUser;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.yugansh.tyagi.smileyrating.SmileyRatingView;

public class FragmentRating extends Fragment{
    private static final String TAG = "FragmentRating";

    //widgets
    RelativeLayout rlRating;
    RelativeLayout rlFace;
    TextView tvRatingCaption;
    ImageView ivProfileImg;
    SmileyRatingView smileyRatingView;
    SimpleRatingBar ratingBar;
    EditText etReview;
    Button btnSubmit;
    TextView tvSkip;

    //vars
    private OnGetFromUserClickListener mListener;
    ParcelUser user;
    int position;
    int size;

    public static FragmentRating newInstance(ParcelUser user, int position, int size) {
        FragmentRating fragment = new FragmentRating();
        Bundle args = new Bundle();
        args.putParcelable("mUser", user);
        args.putInt("position", position);
        args.putInt("size", size);
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnGetFromUserClickListener {
        void clickSubmit(float rating, String review, int position);
        void clickSkip();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            user = getArguments().getParcelable("mUser");
            position = getArguments().getInt("position");
            size = getArguments().getInt("size");
        }
        try {
            mListener = (OnGetFromUserClickListener ) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " must implement OnGetFromUserClickListener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rating, container, false);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        tvRatingCaption = view.findViewById(R.id.tvRatingCaption);
        rlRating = view.findViewById(R.id.rlRating);
        rlFace = view.findViewById(R.id.rlFace);
        ivProfileImg = view.findViewById(R.id.ivProfileImg);
        smileyRatingView =  view.findViewById(R.id.smiley_view);
        ratingBar =  view.findViewById(R.id.rating_bar);
        etReview = view.findViewById(R.id.etReview);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        tvSkip = view.findViewById(R.id.tvSkip);
        initFragmentWidgets();

        return view;
    }

    private void initFragmentWidgets(){
        String caption = "Do you happy with <b>" + user.getUser_profileName() +"</b> ?";
        tvRatingCaption.setText(Html.fromHtml(caption));
        if(user.getUser_profilePicSrc() == null || user.getUser_profilePicSrc().equals("")){
            ivProfileImg.setImageDrawable(getContext().getResources().getDrawable(R.mipmap.img_default_profile_pic));
        }
        else {
            Glide.with(getContext())
                    .load(user.getUser_profilePicSrc())
                    .apply(new RequestOptions().optionalCenterCrop())
                    .into(ivProfileImg);
        }

        ratingBar.setOnRatingBarChangeListener(new SimpleRatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(SimpleRatingBar simpleRatingBar, float rating, boolean fromUser) {
                float emotion = rating - 1;
                smileyRatingView.setSmiley(emotion);

                if(rating == 1){
                    rlRating.setBackground(getResources().getDrawable(R.drawable.gradient_red));
                }
                else if(rating == 2){
                    rlRating.setBackground(getResources().getDrawable(R.drawable.gradient_orange));
                }
                else if (rating == 3){
                    rlRating.setBackground(getResources().getDrawable(R.drawable.gradient_yellow));
                }
                else if(rating == 4){
                    rlRating.setBackground(getResources().getDrawable(R.drawable.gradient_light_green));
                }
                else if(rating == 5){
                    rlRating.setBackground(getResources().getDrawable(R.drawable.gradient_dark_green));
                }
                else {
                    rlRating.setBackgroundColor(getResources().getColor(R.color.colorLightGreenGrey));
                    smileyRatingView.setSmiley(1);

                }
            }
        });

        if(position == size - 1) {
            btnSubmit.setText("Complete");
        }
        else if(position < size - 1){
            btnSubmit.setText("Next");
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float rating = ratingBar.getRating();
                String review = etReview.getText().toString();

                if(rating != 0.0) {
                    btnSubmit.setEnabled(false);
                    btnSubmit.setText(Html.fromHtml("\u2713 Submitted"));

                    Toast.makeText(getContext(), "Submitted", Toast.LENGTH_SHORT).show();

                    clickSubmit(view);
                }
                else {
                    Toast.makeText(getContext(), "Please enter rating" + review, Toast.LENGTH_SHORT).show();
                }

            }
        });

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSkip(view);
            }
        });
    }

    public void clickSubmit(View v) {
        if (mListener != null) {
            mListener.clickSubmit(ratingBar.getRating(), etReview.getText().toString(), position);
        }
    }

    public void clickSkip(View v) {
        if (mListener != null) {
            mListener.clickSkip();
        }
    }

}