package vn.edu.hcmus.stargallery.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vn.edu.hcmus.stargallery.Adapter.AlbumDetailAdapter;
import vn.edu.hcmus.stargallery.Adapter.PersonAvatarAdapter;
import vn.edu.hcmus.stargallery.R;

public class FaceDetectActivity extends AppCompatActivity {
    String album_name;
//    private static final int REQUEST_DELETE_ITEM = 100;
    ArrayList<String> images;
    ArrayList<String> duplicated;
    HashMap<String, ArrayList<String>> personImageMap = new HashMap<>();
    ArrayList<Bitmap> personAvatar = new ArrayList<>();
    ImageButton backBtn;
    PersonAvatarAdapter adapter;
    GridLayoutManager manager;
    RecyclerView imagesView;
    private FaceDetector faceDetector;

    public void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .enableTracking()
                        .build();

        faceDetector = FaceDetection.getClient(highAccuracyOpts);

        images = new ArrayList<>();
        duplicated = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            album_name = intent.getStringExtra("album_name");
            duplicated = intent.getStringArrayListExtra("images_list");

            //Load 50 anh thui
            if (duplicated != null && !duplicated.isEmpty()) {
                for (int i = 0; i < Math.min(duplicated.size(), 50); i++) {
                    images.add(duplicated.get(i));
                }
            }

            duplicated = new ArrayList<>();
//            adapter = new AlbumDetailAdapter(this, duplicated);
            adapter = new PersonAvatarAdapter(this, personAvatar);
            manager = new GridLayoutManager(this, 4);

            imagesView = findViewById(R.id.album_images_view);
            imagesView.setAdapter(adapter);
            imagesView.setLayoutManager(manager);

            TextView total = findViewById(R.id.totalAlbumImage);
            SpannableStringBuilder s = new SpannableStringBuilder();
            s.append(album_name);
            s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, album_name.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            total.setText(s);
        }

        backBtn = findViewById(R.id.album_detail_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadFaces();
    }

    private void loadFaces() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (String imagePath : images) {
            executor.execute(new FaceDetectionTask(imagePath));
        }

        executor.shutdown();
    }

    private class FaceDetectionTask implements Runnable {
        private String imagePath;

        public FaceDetectionTask(String imagePath) {
            this.imagePath = imagePath;
        }

        @Override
        public void run() {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            InputImage inputImage = InputImage.fromBitmap(bitmap, 0);

            faceDetector.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                        @Override
                        public void onSuccess(List<Face> faces) {
                            for (Face face : faces) {
                                String faceId = String.valueOf(face.getTrackingId());
                                if (!personImageMap.containsKey(faceId)) {
                                    personImageMap.put(faceId, new ArrayList<>());
                                    Bitmap croppedFaceBitmap = getCroppedFaceBitmap(bitmap, face);
                                    personAvatar.add(croppedFaceBitmap);
                                }
                                personImageMap.get(faceId).add(imagePath);
                            }
                            displayResults();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("FaceDetection", "Face detection failed for image: " + imagePath);
                        }
                    });
        }
    }

    private Bitmap getCroppedFaceBitmap(Bitmap bitmap, Face face) {
//        int left = (int) face.getBoundingBox().left;
//        int top = (int) face.getBoundingBox().top;
//        int width = (int) face.getBoundingBox().width();
//        int height = (int) face.getBoundingBox().height();
//
//        // Ensure that the cropped region doesn't exceed the bounds of the original bitmap
//        if (left < 0) left = 0;
//        if (top < 0) top = 0;
//        if (left + width > bitmap.getWidth()) width = bitmap.getWidth() - left;
//        if (top + height > bitmap.getHeight()) height = bitmap.getHeight() - top;
//
//        return Bitmap.createBitmap(bitmap, left, top, width, height);
        // Get the bounding box of the face
        float left = face.getBoundingBox().left;
        float top = face.getBoundingBox().top;
        float width = face.getBoundingBox().width();
        float height = face.getBoundingBox().height();

        // Adjust the bounding box to include more of the face
        float expansionFactor = 0.2f; // Adjust this factor as needed
        float expandedLeft = Math.max(0, left - expansionFactor * width);
        float expandedTop = Math.max(0, top - expansionFactor * height);
        float expandedWidth = Math.min(bitmap.getWidth() - expandedLeft, (1 + 2 * expansionFactor) * width);
        float expandedHeight = Math.min(bitmap.getHeight() - expandedTop, (1 + 2 * expansionFactor) * height);

        // Crop the bitmap using the adjusted bounding box
        return Bitmap.createBitmap(bitmap, (int) expandedLeft, (int) expandedTop, (int) expandedWidth, (int) expandedHeight);
    }

    private void displayResults() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                duplicated.clear();
//                for (Map.Entry<String, ArrayList<String>> entry : personImageMap.entrySet()) {
//                    ArrayList<String> images = entry.getValue();
//                    duplicated.add(images.get(0));
//                }
                Objects.requireNonNull(imagesView.getAdapter()).notifyDataSetChanged();
            }
        });
    }
}
