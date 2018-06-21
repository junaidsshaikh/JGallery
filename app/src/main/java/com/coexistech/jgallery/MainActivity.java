package com.coexistech.jgallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    GridView imageGridView;
    Toolbar toolBar;

    List<ImageInfo> imageInfoList;
    List<String> imageStringList;
    List<Bitmap> imageBitmapList;
    boolean[] imageBooleanList;
    ImageAdapter imageAdapter;
    ActionMode actionMode;

    List<ImageInfo> imageDataInfoList;
    Cursor imageCursor;
    int cnt;
    private final int PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);
        toolBar.inflateMenu(R.menu.menu_crop);

        imageGridView = findViewById(R.id.imageGridView);
        imageInfoList = new ArrayList<>();
        imageDataInfoList = new ArrayList<>();
        imageStringList = new ArrayList<>();
        imageBitmapList = new ArrayList<>();

        imageAdapter = new ImageAdapter(imageInfoList);
        imageGridView.setAdapter(imageAdapter);

        int result = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {

            new ImageSync().execute();

        } else {
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            ActivityCompat.requestPermissions(MainActivity.this, permissions, PERMISSION_CODE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                new ImageSync().execute();

            } else {
                Toast.makeText(this, "Storage permission needs to be granted to access Images", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class ImageSync extends AsyncTask<String, String, List<ImageInfo>> {

        String[] columns;
        String orderBy;
        int image_column_index;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            columns = new String[]{MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            orderBy = MediaStore.Images.Media._ID+" DESC";
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please wait a moment");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(true);
        }

        @Override
        protected List<ImageInfo> doInBackground(String... strings) {
            imageCursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
            assert imageCursor != null;
            image_column_index = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            imageBooleanList = new boolean[imageCursor.getCount()];
            if(imageCursor != null) {
                for(int i=0; i<imageCursor.getCount(); i++) {
                    imageCursor.moveToPosition(i);
                    int id = imageCursor.getInt(image_column_index);
                    int dataColumnIndex = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    String imagePath = imageCursor.getString(dataColumnIndex);
                    imageInfoList.add(new ImageInfo(id, imagePath));
                }
            }
            return imageInfoList;
        }

        @Override
        protected void onPostExecute(List<ImageInfo> imageInfos) {
            super.onPostExecute(imageInfos);
            progressDialog.dismiss();
            imageAdapter.notifyDataSetChanged();
        }
    }


    public class ImageAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        List<ImageInfo> imageInfoList;

        public ImageAdapter(List<ImageInfo> imageInfoList) {
            this.imageInfoList = imageInfoList;
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return imageInfoList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = mInflater.inflate(R.layout.layout_image_gallery, null);

            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.imageView = view.findViewById(R.id.thumbImage);
            viewHolder.checkBox = view.findViewById(R.id.checkBox);

            viewHolder.imageView.setId(position);
            viewHolder.checkBox.setId(position);

            Picasso.with(MainActivity.this).load("file://"+imageInfoList.get(position).getImagePathString()).into(viewHolder.imageView);

            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;
                    int id = cb.getId();
                    if(actionMode == null) {
                        actionMode = startActionMode(new MenuCallback());
                    }
                    if (imageBooleanList[id]){
                        cb.setChecked(false);
                        imageBooleanList[id] = false;
                    } else {
                        cb.setChecked(true);
                        imageBooleanList[id] = true;
                    }

                    getBooleanList(imageBooleanList);

                }
            });

            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView cb = (ImageView) view;
                    int id = cb.getId();
                    if(actionMode == null) {
                        actionMode = startActionMode(new MenuCallback());
                    }
                    if (imageBooleanList[id]){
                        viewHolder.checkBox.setChecked(false);
                        imageBooleanList[id] = false;
                    } else {
                        viewHolder.checkBox.setChecked(true);
                        imageBooleanList[id] = true;
                    }

                    getBooleanList(imageBooleanList);

                }
            });


            viewHolder.checkBox.setChecked(imageBooleanList[position]);
            viewHolder.id = position;

            return view;
        }

        private void getBooleanList(boolean[] imageBooleanList) {

            cnt=0;
            for (boolean anImageBooleanList : imageBooleanList) {
                if (anImageBooleanList) {
                    cnt = cnt + 1;
                }
            }

            if(cnt==0) {
                actionMode.finish();
            } else {
                actionMode.setTitle(cnt+" Selected");
            }

        }

    }

    private class ViewHolder {
        ImageView imageView;
        CheckBox checkBox;
        int id;
    }

    private class MenuCallback implements ActionMode.Callback {

        public MenuCallback() {
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.menu_gallery, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if(menuItem.getItemId() == R.id.action_done) {
                cnt=0;
                imageStringList = new ArrayList<>();
                for (int i =0; i<imageBooleanList.length; i++){
                    if (imageBooleanList[i]){
                        cnt = cnt+1;
                        imageStringList.add(imageInfoList.get(i).getImagePathString());
                    }
                }
                if (cnt == 0){
                    Toast.makeText(getApplicationContext(),
                            "Please select at least one image",
                            Toast.LENGTH_LONG).show();
                } else {

                    Intent intent = new Intent(MainActivity.this, ImageSlideActivity.class);
                    intent.putStringArrayListExtra("IMAGE_LIST", (ArrayList<String>) imageStringList);
                    startActivity(intent);
                }
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            actionMode.finish();
            imageBooleanList = new boolean[imageCursor.getCount()];
        }
    }
}
