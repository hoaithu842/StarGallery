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
import vn.edu.hcmus.stargallery.Adapter.FaceDetectAdapter;
import vn.edu.hcmus.stargallery.R;

public class FaceDetectActivity extends AppCompatActivity {
    String album_name;
    private static final int REQUEST_DELETE_ITEM = 100;
    ArrayList<String> images;
    ArrayList<String> duplicated;
    HashMap<String, ArrayList<String>> personImageMap = new HashMap<>();
//    ArrayList<Bitmap> personAvatar = new ArrayList<>();
    ImageButton backBtn;
//    FaceDetectAdapter adapter;
    AlbumDetailAdapter adapter;
    GridLayoutManager manager;
    RecyclerView imagesView;


    private FaceDetector faceDetector;

    public void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        // High-accuracy landmark detection and face classification
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
//                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
//                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .enableTracking()
                        .build();

        // Real-time contour detection
        FaceDetectorOptions realTimeOpts =
                new FaceDetectorOptions.Builder()
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .build();

        faceDetector = FaceDetection.getClient(highAccuracyOpts);

        images = new ArrayList<>();
        duplicated = new ArrayList<>();
        personImageMap = new HashMap<>();
//        personAvatar = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            album_name = intent.getStringExtra("album_name");
            duplicated = intent.getStringArrayListExtra("images_list");

            if (duplicated != null && !duplicated.isEmpty()) {
                // Add up to 50 images to the duplicated list
                for (int i = 0; i < Math.min(duplicated.size(), 50); i++) {
                    images.add(duplicated.get(i));
                }
            }

            duplicated = new ArrayList<>();
            adapter = new AlbumDetailAdapter(this, duplicated);
//            adapter = new FaceDetectAdapter(this, personAvatar);
            manager = new GridLayoutManager(this,4);

            imagesView = findViewById(R.id.album_images_view);
            imagesView.setAdapter(adapter);
            imagesView.setLayoutManager(manager);

            TextView total = findViewById(R.id.totalAlbumImage);
            SpannableStringBuilder s = new SpannableStringBuilder();
            s.append(album_name);
            s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, album_name.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            total.setText(s);
//            TextView txt = findViewById(R.id.totalImage);
//            txt.setText(Integer.toString(images.size()) + " photos");
        }

        backBtn = findViewById(R.id.album_detail_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter.setOnClickListener(new AlbumDetailAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                List<String> faceIds = new ArrayList<>(personImageMap.keySet());
                if (position >= 0 && position < faceIds.size()) {
                    String faceId = faceIds.get(position);

                    // Get the list of images associated with the faceId
                    ArrayList<String> images = personImageMap.get(faceId);

                    // Start AlbumDetailActivity and pass the list of images
                    Intent intent = new Intent(FaceDetectActivity.this, AlbumDetailActivity.class);
                    intent.putExtra("album_name", "Faces for ID: " + faceId);
                    intent.putStringArrayListExtra("images_list", images);
                    startActivity(intent);
                }
            }
        });

        ImageButton showBtn = findViewById(R.id.add_album_btn);
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayResults();
            }
        });
        loadFaces();
    }
//    private void loadFaces() {
//        int NUMBER_OF_THREADS = images.size() / 10;
//        // Define a thread pool executor with a fixed number of threads
//        ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
//
//        for (String imagePath : images) {
//            executor.execute(new FaceDetectionTask(imagePath));
//        }
//
//        // Shut down the executor after all tasks are complete
//        executor.shutdown();
//    }
//
//    // Runnable task for face detection
//    private class FaceDetectionTask implements Runnable {
//        private String imagePath;
//
//        public FaceDetectionTask(String imagePath) {
//            this.imagePath = imagePath;
//        }
//
//        @Override
//        public void run() {
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//            InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
//
//            faceDetector.process(inputImage)
//                    .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
//                        @Override
//                        public void onSuccess(List<Face> faces) {
//                            // Process the detected faces
//                            for (Face face : faces) {
//                                String faceId = String.valueOf(face.getTrackingId());
//                                if (!personImageMap.containsKey(faceId)) {
//                                    personImageMap.put(faceId, new ArrayList<>());
//
////                                    Bitmap croppedFaceBitmap = Bitmap.createBitmap(bitmap,
////                                            (int) face.getBoundingBox().left,
////                                            (int) face.getBoundingBox().top,
////                                            (int) face.getBoundingBox().width(),
////                                            (int) face.getBoundingBox().height());
////
////                                    personAvatar.add(croppedFaceBitmap);
////                                    imagesView.getAdapter().notifyDataSetChanged();
//                                }
//                                personImageMap.get(faceId).add(imagePath);
//                            }
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.e("FaceDetection", "Face detection failed for image: " + imagePath);
//                            // Handle any errors
//                        }
//                    });
//        }
//    }

    private void loadFaces() {
        int i = 0;
        for (String imagePath : images) {
            Log.d("fhsjklhgewkr", Integer.toString(i));
            i = i + 1;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            InputImage image = InputImage.fromBitmap(bitmap, 0);

            faceDetector.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                        @Override
                        public void onSuccess(List<Face> faces) {
                            if (faces.isEmpty()) {
//                                Log.d("Empty", "Empty");
                            } else {
                                for (Face face : faces) {
                                String faceId = String.valueOf(face.getTrackingId()); // Get the unique identifier for the face

                                if (!personImageMap.containsKey(faceId)) {
                                    personImageMap.put(faceId, new ArrayList<>());
                                }

                                personImageMap.get(faceId).add(imagePath);
//                                    Log.d("FaceDetection", "Face detected with ID: " + face.getTrackingId());
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            Log.e("FaceDetection", "Face detection failed: " + e.getMessage());
                            // Handle any errors
                        }
                    });
        }
    }

    private void displayResults() {
        duplicated.clear();
        for (Map.Entry<String, ArrayList<String>> entry : personImageMap.entrySet()) {
            String faceId = entry.getKey();
            ArrayList<String> images = entry.getValue();
//            for (String imagePath : images) {
//                // Display or process the image
//                 Log.d(faceId, imagePath);
//            }
            duplicated.add(images.get(0));
        }
        Objects.requireNonNull(imagesView.getAdapter()).notifyDataSetChanged();
    }
}
