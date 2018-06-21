package com.coexistech.jgallery;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.List;

class CustomPagerAdapter extends PagerAdapter {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private Activity activity;
    private List<ImageInfo> imageInfoList;
    Toolbar toolBar;
    Data data;

    public CustomPagerAdapter(Activity activity, List<ImageInfo> imageInfoList, Toolbar toolBar, Data data) {
        mContext = activity;
        this.activity = activity;
        this.imageInfoList = imageInfoList;
        this.toolBar = toolBar;
        this.data = data;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageInfoList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((RelativeLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.image_fragment, container, false);

        ImageView imageView = itemView.findViewById(R.id.imageViewFragment);
        ImageButton cropImageButton = itemView.findViewById(R.id.cropImageButton);
        final TextView textViewTitle = itemView.findViewById(R.id.textViewTitle);
        LinearLayout titleLinearLayout = itemView.findViewById(R.id.titleLinearLayout);

        Picasso.with(mContext).load("file://"+imageInfoList.get(position).getImagePathString()).into(imageView);
        cropImageButton.setId(position);
        cropImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.setImagePosition(position);
                CropImage.activity(Uri.parse("file://"+imageInfoList.get(position).getImagePathString())).start(activity);
            }
        });

        titleLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.image_dialog);
                dialog.setTitle("Title...");
                final EditText editText = dialog.findViewById(R.id.editText);
                editText.setText(imageInfoList.get(position).getImageTitleString());
                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!editText.getText().toString().trim().isEmpty()) {

                            imageInfoList.add(position, new ImageInfo(imageInfoList.get(position).getImagePathString(), editText.getText().toString()));
                            imageInfoList.remove(position+1);
                            textViewTitle.setText(imageInfoList.get(position).getImageTitleString());
                            dialog.dismiss();
                        }
                    }
                });

                dialog.show();
            }
        });

        toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_crop:
                        data.setImagePosition(position);
                        CropImage.activity(Uri.parse("file://"+imageInfoList.get(position).getImagePathString())).start(activity);
                        return true;
                }
                return true;
            }
        });

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
