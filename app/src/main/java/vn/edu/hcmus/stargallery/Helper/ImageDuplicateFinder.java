package vn.edu.hcmus.stargallery.Helper;

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
    public ImageDuplicateFinder(ArrayList<String> images) {
        this.images = images;
    }

    public List<List<String>> findDuplicateImages() {
        Map<String, List<String>> hashToImagesMap = new HashMap<>();

        for (String imagePath : images) {
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
}