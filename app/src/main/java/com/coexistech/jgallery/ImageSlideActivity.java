package com.coexistech.jgallery;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.List;

public class ImageSlideActivity extends AppCompatActivity {

    ViewPager viewPager;
    CustomPagerAdapter customPagerAdapter;
    ArrayList<String> imageStringList;
    Toolbar toolBar;
    Uri resultUri;
    private List<ImageInfo> imageInfoList;
    ImageInfo imageInfo;
    Data dataInfo;

    FloatingActionButton sendFabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slide);

        imageStringList = new ArrayList<>();
        imageInfoList = new ArrayList<>();
        imageInfo = new ImageInfo();
        toolBar = findViewById(R.id.toolBar);
        toolBar.inflateMenu(R.menu.menu_crop);

        sendFabButton = findViewById(R.id.sendFabButton);

        viewPager = findViewById(R.id.viewPager);
        dataInfo = new Data();

        if(getIntent() != null) {
            imageStringList = getIntent().getStringArrayListExtra("IMAGE_LIST");
        }

        for(int i=0; i<imageStringList.size(); i++) {
            imageInfoList.add(i, new ImageInfo(imageStringList.get(i)));
        }

        customPagerAdapter = new CustomPagerAdapter(this, imageInfoList, toolBar, dataInfo);
        viewPager.setAdapter(customPagerAdapter);

        sendFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageInfoList.size() > 0) {
                    // send to server...
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                if(dataInfo.getImagePosition() != -1) {
                    imageInfoList.add(dataInfo.getImagePosition(), new ImageInfo(resultUri.toString(), imageInfoList. get(dataInfo.getImagePosition()).getImageTitleString()));
                    imageInfoList.remove(dataInfo.getImagePosition()+1);
                    customPagerAdapter.notifyDataSetChanged();
                    viewPager.setAdapter(null);
                    viewPager.setAdapter(customPagerAdapter);
                    viewPager.setCurrentItem(dataInfo.getImagePosition());
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
