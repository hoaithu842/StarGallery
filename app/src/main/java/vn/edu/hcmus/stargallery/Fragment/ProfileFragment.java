package vn.edu.hcmus.stargallery.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import vn.edu.hcmus.stargallery.R;

public class ProfileFragment extends Fragment {
    GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    LinearLayout layout;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleSignInClient = GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_profile, container, false);
        ImageButton logBtn = layout.findViewById(R.id.log_button);
        logBtn.setTag(R.drawable.icon_login);

        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer currentResource = (Integer) logBtn.getTag();

                if (currentResource != null) {
                    if (currentResource.intValue() == R.drawable.icon_login) {
                        Log.d("Log in neh", "Log in neh");
                        Intent signInIntent = googleSignInClient.getSignInIntent();
                        startActivityForResult(signInIntent, RC_SIGN_IN);

                        logBtn.setImageResource(R.drawable.icon_logout);
                        // Update the tag to reflect the new resource
                        logBtn.setTag(R.drawable.icon_logout);
                    } else if (currentResource.intValue() == R.drawable.icon_logout) {
                        Log.d("Log out neh", "Log out neh");
                        logBtn.setImageResource(R.drawable.icon_login);
                        // Update the tag to reflect the new resource

                        logBtn.setTag(R.drawable.icon_login);
                    }
                } else {
                    // Handle case where tag is null
                    Log.e("Tag Error", "Tag is null");
                }
            }
        });

//        RelativeLayout duplicate_button = layout.findViewById(R.id.show_duplicated);
//        duplicate_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ImageDuplicateActivity.class);
//                intent.putExtra("album_name", "Duplicated Images");
//                intent.putStringArrayListExtra("images_list", images);
//
//                startActivity(intent);
//            }
//        });
//
//        ArrayList<String> album = loadAlbums();
//        RelativeLayout familiar_pp_button = layout.findViewById(R.id.show_faces_detected);
//        familiar_pp_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), FaceDetectActivity.class);
//                intent.putExtra("album_name", "Faces Detected");
//                intent.putStringArrayListExtra("images_list", album);
//
//                startActivity(intent);
//            }
//        });
//
//        RelativeLayout location_button = layout.findViewById(R.id.show_location);
//        location_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), LocationActivity.class);
////                intent.putExtra("album_name", "Faces Detected");
//                intent.putStringArrayListExtra("images_list", images);
//
//                startActivity(intent);
//            }
//        });
//
//        loadImages();
////        loadSelfies();
        return layout;
    }
    // Handle Google Sign-In result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    // Handle the sign-in result
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, obtain an access token
            String accessToken = account.getIdToken();

            // Call a method to sync photos from Google Photos API
            syncPhotosFromGooglePhotosAPI(accessToken);
        } catch (ApiException e) {
            Log.e("Google Sign-In Error", "Sign-in failed", e);
        }
    }

    // Sync photos from Google Photos API
    private void syncPhotosFromGooglePhotosAPI(String accessToken) {
        // Use the access token to make requests to Google Photos API
        // Fetch user's photos and store them in images_list array
        // Example:
        // GooglePhotosApiService.getPhotos(accessToken, new GooglePhotosApiService.PhotosCallback() {
        //     @Override
        //     public void onPhotosFetched(List<Photo> photos) {
        //         images_list.addAll(photos);
        //     }
        // });
    }
}
