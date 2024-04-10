package vn.edu.hcmus.stargallery.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.icu.text.SimpleDateFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.net.ParseException;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import ly.img.android.pesdk.PhotoEditorSettingsList;
import ly.img.android.pesdk.assets.filter.basic.FilterPackBasic;
import ly.img.android.pesdk.assets.font.basic.FontPackBasic;
import ly.img.android.pesdk.assets.frame.basic.FramePackBasic;
import ly.img.android.pesdk.assets.overlay.basic.OverlayPackBasic;
import ly.img.android.pesdk.assets.sticker.emoticons.StickerPackEmoticons;
import ly.img.android.pesdk.assets.sticker.shapes.StickerPackShapes;
import ly.img.android.pesdk.backend.model.state.LoadSettings;
import ly.img.android.pesdk.backend.model.state.PhotoEditorSaveSettings;
import ly.img.android.pesdk.backend.model.state.manager.SettingsList;
import ly.img.android.pesdk.ui.activity.PhotoEditorBuilder;
import ly.img.android.pesdk.ui.model.state.UiConfigFilter;
import ly.img.android.pesdk.ui.model.state.UiConfigFrame;
import ly.img.android.pesdk.ui.model.state.UiConfigOverlay;
import ly.img.android.pesdk.ui.model.state.UiConfigSticker;
import ly.img.android.pesdk.ui.model.state.UiConfigText;
import vn.edu.hcmus.stargallery.Listener.OnSwipeTouchListener;
import vn.edu.hcmus.stargallery.R;

public class ImageDetailActivity extends AppCompatActivity {

    ImageView imageView;
    private int currentIndex;
    private ArrayList<String> images_list;
    float rotateDegree = 0;

    ArrayList<String> delete_images;
    String image_path = "";
    Bitmap myImg;
    boolean image_changed = false;
    Matrix matrix = new Matrix();
    PopupWindow popupWindow;
    BottomNavigationView nav_bot;
    BottomNavigationView editor_nav_bot;
    BottomNavigationView nav_top;
    float x1, x2;
    private float scaleFactor = 1.0f;
    public static int PESDK_RESULT = 1;
    public static int GALLERY_RESULT = 2;

    private SettingsList createPesdkSettingsList() {

        // Create a empty new SettingsList and apply the changes on this reference.
        PhotoEditorSettingsList settingsList = new PhotoEditorSettingsList(true);

        // If you include our asset Packs and you use our UI you also need to add them to the UI Config,
        // otherwise they are only available for the backend
        // See the specific feature sections of our guides if you want to know how to add your own Assets.

        settingsList.getSettingsModel(UiConfigFilter.class).setFilterList(
                FilterPackBasic.getFilterPack()
        );

        settingsList.getSettingsModel(UiConfigText.class).setFontList(
                FontPackBasic.getFontPack()
        );

        settingsList.getSettingsModel(UiConfigFrame.class).setFrameList(
                FramePackBasic.getFramePack()
        );

        settingsList.getSettingsModel(UiConfigOverlay.class).setOverlayList(
                OverlayPackBasic.getOverlayPack()
        );

        settingsList.getSettingsModel(UiConfigSticker.class).setStickerLists(
                StickerPackEmoticons.getStickerCategory(),
                StickerPackShapes.getStickerCategory()
        );

        return settingsList;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

//        editor_nav_bot = findViewById(R.id.editor_nav_bot);
//        editor_nav_bot.setVisibility(View.INVISIBLE);

        imageView = findViewById(R.id.imageView);

        nav_bot = findViewById(R.id.detail_nav_bot);
        nav_bot.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.shareBtn) {
                    onShareBtnClick();
                } else if (item.getItemId() == R.id.editBtn) {
                    onEditBtnClick();
                } else if (item.getItemId() == R.id.favoriteBtn) {
                    item.setIcon(R.drawable.b_heart_btn_filled);
//                    onFavoriteBtnClick();
                } else if (item.getItemId() == R.id.deleteBtn) {
                    onDeleteBtnClick();
                } else if (item.getItemId() == R.id.rotateBtn) {
                    onRotateBtnClick();
                }
                return false;
            }
        });

        nav_top = findViewById(R.id.detail_nav_top);
        Menu menu = nav_top.getMenu();
        for (int i = 1; i < menu.size()-1; i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setEnabled(false);
        }
        nav_top.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.backBtn) {
                    onBackBtnClick();
                }
                else if(item.getItemId() == R.id.infoBtn) {
                    try {
                        onInfoBtnClick();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                return false;
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            image_path = intent.getStringExtra("image_path");
            images_list = intent.getStringArrayListExtra("images_list");

            if (images_list == null || images_list.isEmpty()) {
                // Handle case where no image paths are provided
                // return;
            }

            currentIndex = images_list.indexOf(image_path);
            if (image_path != null) {
                loadImage(image_path);
                image_changed = false;
                myImg = BitmapFactory.decodeFile(image_path);
            }
        }
        // Swipe gestures

        imageView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                image_changed = true;
                showNextImage();
            }
            @Override
            public void onSwipeTop() throws IOException {
                showInfo();
            }
            @Override
            public void onSwipeRight() {
                image_changed = true;
                showPreviousImage();
            }
            public void onSwipeBottom() {
                finish();
            }
        });
    }
//    @Override
//    public void onBackPressed() {
//        // Call your custom method to handle the back button press
//        super.onBackPressed();
//        if(editor_nav_bot.getVisibility() == View.VISIBLE){
//            editor_nav_bot.setVisibility(View.INVISIBLE);
//        } else finish();
//
//    }


    private void loadImage(String image_path) {
        Glide.with(this).load(image_path).into(imageView);
        this.image_path = image_path;
    }
    private void showNextImage() {
        if (currentIndex < images_list.size() - 1) {
            currentIndex++;
            loadImage(images_list.get(currentIndex));
        } else {
            Toast.makeText(ImageDetailActivity.this, "No more images", Toast.LENGTH_SHORT).show();
        }
    }
    private void showPreviousImage() {
        if (currentIndex > 0) {
            currentIndex--;
            loadImage(images_list.get(currentIndex));
        } else {
            Toast.makeText(ImageDetailActivity.this, "No previous images", Toast.LENGTH_SHORT).show();
        }
    }
    public void showInfo() throws IOException {
//        Uri uri = ImageView.setImageURI(new File(image_path)));
//        gfgIn = getContentResolver().openInputStream(uri);
//        ExifInterface exifInterface = new ExifInterface(new File(image_path));
        ExifInterface exifInterface = new ExifInterface(new File(image_path).getAbsolutePath());
        String imageDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        String imageMake = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
        String imageModel = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
        String imageFlash = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
        String imageFocalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
        String imageLength = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
        String imageWidth = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
        String imageISO = exifInterface.getAttribute(ExifInterface.TAG_ISO);
        String imageResolution = exifInterface.getAttribute(ExifInterface.TAG_RESOLUTION_UNIT);
//
//        Log.d("exif", imageDateTime);
//        Log.d("exif", imageMake);
//        Log.d("exif", imageModel);
//        Log.d("exif", imageFlash);
//        Log.d("exif", imageFocalLength);
//        Log.d("exif", imageLength);
//        Log.d("exif", imageWidth);
//        Log.d("exif", imageISO);
//        Log.d("exif", imageResolution);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View bottomSheet = getLayoutInflater().inflate(R.layout.info_bottom_sheet, null);
        dialog.setContentView(bottomSheet);
        if (!dialog.isShowing()) {
            TextView txt;
            LinearLayout l;
            if(imageDateTime != null){
                try {
                    // Create a SimpleDateFormat object with the desired format
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM dd, yyyy HH:mm");
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                    Date inputDate = inputFormat.parse(imageDateTime);
                    txt = bottomSheet.findViewById(R.id.image_date_taken);
                    txt.setText(sdf.format(inputDate));
                } catch (java.text.ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            if(imageMake != null && imageModel != null && imageFocalLength != null && imageISO != null){
                String[] iFL = imageFocalLength.split("/") ;
                double ifl = (Double.parseDouble(iFL[0]) / Double.parseDouble(iFL[1]));
                Log.d("exif", imageMake + " " + imageModel + " · " + ifl + "mm · " + imageISO + "ISO");
                txt = bottomSheet.findViewById(R.id.phone_describe);
                txt.setText(imageMake + " " + imageModel + " · " + ifl + "mm · " + imageISO + "ISO");
            }
            if(image_path != null){
//                String[] img = image_path.split());
                if(imageLength != null && imageWidth != null ){
                    Log.d("exif", image_path.substring(0, image_path.lastIndexOf("/")) + "\n" + imageLength + " x " + imageWidth + " pixels");
                    txt = bottomSheet.findViewById(R.id.img_resolution);
                    txt.setText(image_path.substring(image_path.lastIndexOf("/") + 1) + "\n" + imageLength + " x " + imageWidth + " pixels" );
                }
                Log.d("exif", image_path);
                txt = bottomSheet.findViewById(R.id.img_storage);
                txt.setText(image_path.substring(0, image_path.lastIndexOf("/")));
            }
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }
    public void onBackBtnClick(){
//        if(editor_nav_bot.getVisibility() == View.VISIBLE){
//            editor_nav_bot.setVisibility(View.INVISIBLE);
//        } else
        finish();
    }
    public void onInfoBtnClick() throws IOException {
        showInfo();
    }
    public void onShareBtnClick() {
        if (images_list != null && currentIndex >= 0 && currentIndex < images_list.size()) {
            String filePath = images_list.get(currentIndex);
            File file = new File(filePath);
            if (file.exists()) {
                Uri fileUri = FileProvider.getUriForFile(ImageDetailActivity.this, getPackageName() + ".provider", file);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, fileUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(intent, "Share to:"));
            } else {
                Toast.makeText(ImageDetailActivity.this, "File not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ImageDetailActivity.this, "No image to share", Toast.LENGTH_SHORT).show();
        }
    }
    public void onEditBtnClick() {
//        Intent intent = new Intent(this, ImageEditActivity.class);
//        intent.putExtra("image_path", image_path);
//        startActivity(intent);


        SettingsList settingsList = createPesdkSettingsList();

        // Set input image
        settingsList.getSettingsModel(LoadSettings.class).setSource(Uri.fromFile(new File(image_path)));

        settingsList.getSettingsModel(PhotoEditorSaveSettings.class).setOutputToGallery(Environment.DIRECTORY_DCIM);

        new PhotoEditorBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, PESDK_RESULT);
    }
    public void onFavoriteBtnClick() {
        finish();
    }
    public void onDeleteBtnClick() {
        final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(ImageDetailActivity.this);
        confirmDialog.setTitle("Delete photo");
        confirmDialog.setMessage("Do you want to delete it?");
        confirmDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

//                Intent resultIntent = new Intent();
//                resultIntent.putExtra("itemDeleted", currentIndex);
//                setResult(Activity.RESULT_OK, resultIntent);
//                finish();Intent resultIntent = new Intent();
//                resultIntent.putExtra("itemDeleted", currentIndex);
//                setResult(Activity.RESULT_OK, resultIntent);
//                finish();
//                Toast.makeText(getApplicationContext(), "H notify ne", Toast.LENGTH_SHORT).show();
//                notifyImageDeleted(currentIndex); // Notify the fragment about image deletion
//                new File(images_list.get(currentIndex)).delete();
//                images_list.remove(currentIndex);
//                if (images_list.size() > currentIndex) {
//                    loadImage(images_list.get(currentIndex));
//                } else if (images_list.size() > currentIndex - 1) {
//                    currentIndex--;
//                    loadImage(images_list.get(currentIndex-1));
//                }
            }
        });
        confirmDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
                dialogInterface.dismiss();
            }
        });
        confirmDialog.show();
    }
    public void onRotateBtnClick() {
        if (imageView != null) {
            if(image_changed){
                myImg = BitmapFactory.decodeFile(image_path);
            }
            matrix.postRotate(90);
            Bitmap rotated = Bitmap.createBitmap(myImg, 0, 0, myImg.getWidth(), myImg.getHeight(), matrix, true);
            imageView.setImageBitmap(rotated);
        }
    }
}