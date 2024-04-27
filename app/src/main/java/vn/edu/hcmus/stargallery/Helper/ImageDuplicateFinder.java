package vn.edu.hcmus.stargallery.Helper;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageDuplicateFinder {
    ArrayList<String> images;
    private OnDuplicateImageFoundListener listener;
    public ImageDuplicateFinder(ArrayList<String> images, OnDuplicateImageFoundListener listener) {
        this.images = images;
        this.listener = listener;
    }
    public void findDuplicateImagesAsync() {
        new DuplicateImageFinderTask().execute(images.toArray(new String[0]));
    }
    private class DuplicateImageFinderTask extends AsyncTask<String, Void, List<List<String>>> {

        @Override
        protected List<List<String>> doInBackground(String... imagePaths) {
            return findDuplicateImages(imagePaths);
        }

        @Override
        protected void onPostExecute(List<List<String>> duplicateGroups) {
            if (listener != null) {
                listener.onDuplicateImageFound(duplicateGroups);
            }
        }
    }
    private List<List<String>> findDuplicateImages(String[] imagePaths) {
        Map<String, List<String>> hashToImagesMap = new HashMap<>();

        for (String imagePath : imagePaths) {
            try {
                File file = new File(imagePath);
                byte[] fileBytes = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream(file);
                fis.read(fileBytes);
                fis.close();

                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] hashBytes = md.digest(fileBytes);

                // Convert byte array to hexadecimal string
                StringBuilder sb = new StringBuilder();
                for (byte b : hashBytes) {
                    sb.append(String.format("%02x", b));
                }
                String hash = sb.toString();

                // Check if this hash already exists in the map
                if (!hashToImagesMap.containsKey(hash)) {
                    hashToImagesMap.put(hash, new ArrayList<>());
                }
                hashToImagesMap.get(hash).add(imagePath);
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        // Filter out groups with only one image
        List<List<String>> duplicateGroups = new ArrayList<>();
        for (List<String> group : hashToImagesMap.values()) {
            if (group.size() > 1) {
                duplicateGroups.add(group);
            }
        }
        return duplicateGroups;
    }

    public interface OnDuplicateImageFoundListener {
        void onDuplicateImageFound(List<List<String>> duplicateGroups);
    }
//    public List<List<String>> findDuplicateImages() {
//        Map<String, List<String>> hashToImagesMap = new HashMap<>();
//
//        for (String imagePath : images) {
//            try {
//                File file = new File(imagePath);
//                byte[] fileBytes = new byte[(int) file.length()];
//                FileInputStream fis = new FileInputStream(file);
//                fis.read(fileBytes);
//                fis.close();
//
//                MessageDigest md = MessageDigest.getInstance("MD5");
//                byte[] hashBytes = md.digest(fileBytes);
//
//                // Convert byte array to hexadecimal string
//                StringBuilder sb = new StringBuilder();
//                for (byte b : hashBytes) {
//                    sb.append(String.format("%02x", b));
//                }
//                String hash = sb.toString();
//
//                // Check if this hash already exists in the map
//                if (!hashToImagesMap.containsKey(hash)) {
//                    hashToImagesMap.put(hash, new ArrayList<>());
//                }
//                hashToImagesMap.get(hash).add(imagePath);
//            } catch (IOException | NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }
//        }
//
//        // Filter out groups with only one image
//        List<List<String>> duplicateGroups = new ArrayList<>();
//        for (List<String> group : hashToImagesMap.values()) {
//            if (group.size() > 1) {
//                duplicateGroups.add(group);
//            }
//        }
//        return duplicateGroups;
//    }
}