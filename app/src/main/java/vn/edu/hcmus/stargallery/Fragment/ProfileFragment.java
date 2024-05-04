package vn.edu.hcmus.stargallery.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import vn.edu.hcmus.stargallery.R;

public class ProfileFragment extends Fragment {
    CircleImageView accountAvatar;
    TextView accountName;
    TextView accountEmail;
    GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    LinearLayout layout;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.web_client_id))
//                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .requestScopes(new Scope("https://www.googleapis.com/auth/photoslibrary.readonly"))
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
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
//        logBtn.setTag(R.drawable.icon_login);

        accountAvatar = layout.findViewById(R.id.avatar_iv);
        accountEmail = layout.findViewById(R.id.account_email);
        accountName = layout.findViewById(R.id.account_name);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
//                Integer currentResource = (Integer) logBtn.getTag();
//
//                if (currentResource != null) {
//                    if (currentResource.intValue() == R.drawable.icon_login) {
//                        Log.d("Log in neh", "Log in neh");
//                        Intent signInIntent = googleSignInClient.getSignInIntent();
//                        startActivityForResult(signInIntent, RC_SIGN_IN);
//
//                        logBtn.setImageResource(R.drawable.icon_logout);
//                        // Update the tag to reflect the new resource
//                        logBtn.setTag(R.drawable.icon_logout);
//                    } else if (currentResource.intValue() == R.drawable.icon_logout) {
//                        Log.d("Log out neh", "Log out neh");
//                        logBtn.setImageResource(R.drawable.icon_login);
//                        // Update the tag to reflect the new resource
//
//                        logBtn.setTag(R.drawable.icon_login);
//                    }
//                } else {
//                    // Handle case where tag is null
//                    Log.e("Tag Error", "Tag is null");
//                }
            }
        });
        return layout;
    }

    // Start Google Sign-In flow
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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


            if (account != null && account.getGrantedScopes().contains(new Scope("https://www.googleapis.com/auth/photoslibrary.readonly"))) {
                // Permissions granted, proceed with accessing Google Photos
                //            String accessToken = account.getIdToken();
                String serverAuthCode = account.getServerAuthCode();

                String photoUrl = null;
                if (account.getPhotoUrl() != null) {
                    photoUrl = account.getPhotoUrl().toString();
                }
                String email = account.getEmail();
                String name = account.getDisplayName();
                if (photoUrl != null) {
                    Glide.with(requireContext()).load(photoUrl).into(accountAvatar);
                }
                if (email != null) {
                    accountEmail.setText(email);
                }
                if (name != null) {
                    accountName.setText(name);
                }
                // Set button thanh log out

                // Call a method to sync photos from Google Photos API
                syncPhotosFromGooglePhotosAPI(serverAuthCode);
            } else {
                // Permissions not granted, handle accordingly
            }
        } catch (ApiException e) {
            Log.e("Google Sign-In Error", "Sign-in failed", e);
        }
    }

    // Sync photos from Google Photos API
    private void syncPhotosFromGooglePhotosAPI(String accessTokenString) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = calendar.getTime();

            AccessToken accessToken = new AccessToken(accessTokenString, tomorrow);
            PhotosLibrarySettings settings =
                    PhotosLibrarySettings.newBuilder().setCredentialsProvider(
                                    FixedCredentialsProvider.create(UserCredentials.newBuilder()
                                            .setClientId("220480641841-pap2lcm4g323i0sbj6jndcm90el5uvpp.apps.googleusercontent.com")
                                            .setClientSecret("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")
                                            .setAccessToken(accessToken)
                                            .build()))
                            .build();

            PhotosLibraryClient photosLibraryClient = PhotosLibraryClient.initialize(settings);
//            Album createdAlbum = photosLibraryClient.createAlbum("Star Galleryyyyy");

//            InternalPhotosLibraryClient.ListMediaItemsPagedResponse response = photosLibraryClient.listMediaItems();
//            for (MediaItem item : response.iterateAll()) {
//                // Get some properties of a media item
//                String id = item.getId();
//                String description = item.getDescription();
//                String mimeType = item.getMimeType();
//                String productUrl = item.getProductUrl();
//                String filename = item.getFilename();
//            }
        } catch (IOException e) {
            Log.e("Sync Error", "Error syncing photos from Google Photos API", e);
        }
    }
}
