package vn.edu.hcmus.stargallery.Fragment;

import static android.os.Environment.MEDIA_MOUNTED;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import vn.edu.hcmus.stargallery.Activity.MultiSelectImageActivity;
import vn.edu.hcmus.stargallery.Adapter.ImagesViewAdapter;
import vn.edu.hcmus.stargallery.Activity.ImageDetailActivity;
import vn.edu.hcmus.stargallery.R;

public class ImagesFragment extends Fragment {
    private static final int REQUEST_DELETE_ITEM = 100;
    RecyclerView imagesView;
    LinearLayout layout;
    static int PERMISSION_REQUEST_CODE = 100;
    ArrayList<String> images;
    ArrayList<String> filteredImages = null;
    ImagesViewAdapter adapter;
    GridLayoutManager manager;

    void updateLabel(ArrayList<String> list) {
        TextView txt = layout.findViewById(R.id.totalImage);
        if (list.size()==0) {
            txt.setText("Empty");
        } else if (list.size()==1) {
            txt.setText(Integer.toString(list.size()) + " photo");
        } else {
            txt.setText(Integer.toString(list.size()) + " photos");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        images = new ArrayList<>();
        adapter = new ImagesViewAdapter(getContext(),images);
        manager = new GridLayoutManager(getContext(),4);

        adapter.setOnClickListener(new ImagesViewAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
                if (filteredImages == null) {
                    intent.putExtra("image_path", images.get(position));
                    intent.putStringArrayListExtra("images_list", images);
                } else {
                    intent.putExtra("image_path", filteredImages.get(position));
                    intent.putStringArrayListExtra("images_list", filteredImages);
                }

                startActivityForResult(intent, REQUEST_DELETE_ITEM);
            }
        });
        adapter.setOnLongClickListener(new ImagesViewAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(int position) {
                Intent intent = new Intent(getActivity(), MultiSelectImageActivity.class);
                intent.putExtra("image_path", images.get(position));
                intent.putStringArrayListExtra("images_list", images);
                startActivity(intent);
                Toast.makeText(getContext(), "LONGGGG", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterImages(String query) {
        filteredImages = new ArrayList<>();
        for (String imagePath : images) {
            // Perform a case-insensitive search on the image file path
            if (imagePath.toLowerCase().contains(query.toLowerCase())) {
                filteredImages.add(imagePath);
            }
        }
        updateLabel(filteredImages);
        adapter.setImages(filteredImages);
        imagesView.getAdapter().notifyDataSetChanged();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DELETE_ITEM && resultCode == Activity.RESULT_OK) {

            Integer itemDeleted = data.getIntExtra("itemDeleted", 0);
            if (itemDeleted >= 0 && itemDeleted < images.size()) {
                String imagePath = images.get(itemDeleted);
                images.remove(images.get(itemDeleted));
                imagesView.getAdapter().notifyItemRemoved(itemDeleted);
                // Handle item deletion
                // Check if the image path exists before deletion (avoid potential errors)
                if (new File(imagePath).exists()) {
                    int deleted = getContext().getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.Images.Media.DATA + " = ?", new String[]{imagePath});
                    if (deleted == 1) {
                        // Image deleted successfully
                        Log.i("ImageDelete", "Image deleted: " + imagePath);
                    } else {
                        Log.w("ImageDelete", "Failed to delete image: " + imagePath);
                    }
                } else {
                    Log.w("ImageDelete", "Image path not found: " + imagePath);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        images.clear();
        loadImages();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_images, container, false);
        imagesView = layout.findViewById(R.id.images_view);
        imagesView.setAdapter(adapter);
        imagesView.setLayoutManager(manager);
        loadImages();
        updateLabel(images);

        EditText searchEditText = layout.findViewById(R.id.search_et);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used, but required for TextWatcher interface
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used, but required for TextWatcher interface
                if (s.toString().length()==0) {
                    adapter.setImages(images);
                    imagesView.getAdapter().notifyDataSetChanged();
                    filteredImages = null;
                    updateLabel(images);
                } else {
                    filterImages(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Filter the images based on the search query

            }
        });

        ImageButton calendarButton = layout.findViewById(R.id.calendar_button);
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(requireContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.calendar_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.date_option) {
                            performSearchByDay();
                            return true;
                        } else if (item.getItemId() == R.id.month_option) {
                            performSearchByMonth();
                            return true;
                        } else if (item.getItemId() == R.id.year_option) {
                            performSearchByYear();
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        return layout;
    }
    private void performSearchByDay() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth);
                filterImagesByDay(selectedDate);
            }
        }, year, month, dayOfMonth);

        datePickerDialog.show();
    }
    private void filterImagesByDay(String selectedDate) {
        ArrayList<String> filteredImages = new ArrayList<>();

        for (String imagePath : images) {
            ExifInterface exifInterface = null;
            try {
                exifInterface = new ExifInterface(new File(imagePath).getAbsolutePath());
                String imageDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                if (imageDateTime != null) {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
                    Date inputDate = inputFormat.parse(imageDateTime);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String creationDate = sdf.format(inputDate);
                    Date creationDateParsed = sdf.parse(creationDate);
                    Date selectedDateParsed = sdf.parse(selectedDate);

                    if (creationDateParsed != null && selectedDateParsed != null) {
                        if (creationDateParsed.equals(selectedDateParsed)) {
                            filteredImages.add(imagePath);
                        } else if (creationDateParsed.before(selectedDateParsed)) {
                            break;
                        }
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            } catch (IOException | ParseException e) {
                Log.e("ExifInterface", "Error reading image metadata", e);
            } finally {
                if (exifInterface != null) {
//                    exifInterface.close();
                }
            }
        }

        this.filteredImages = filteredImages;

        updateLabel(filteredImages);
        adapter.setImages(filteredImages);
        imagesView.getAdapter().notifyDataSetChanged();
    }

    private void performSearchByMonth() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        String selectedDate = String.format(Locale.getDefault(), "%04d-%02d", selectedYear, selectedMonth + 1);
                        filterImagesByMonth(selectedDate);
                    }
                },
                year,  // Set initial year
                month, // Set initial month (0-based index)
                1      // Set initial day of month (1-based)
        );
        datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);

        datePickerDialog.show();
    }
    private void filterImagesByMonth(String selectedMonth) {
        ArrayList<String> filteredImages = new ArrayList<>();
        for (String imagePath : images) {
            ExifInterface exifInterface = null;
            try {
                exifInterface = new ExifInterface(new File(imagePath).getAbsolutePath());
                String imageDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                if (imageDateTime != null) {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
                    Date inputDate = inputFormat.parse(imageDateTime);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                    String creationDate = sdf.format(inputDate);
                    Date creationDateParsed = sdf.parse(creationDate);
                    Date selectedDateParsed = sdf.parse(selectedMonth);

                    if (creationDateParsed != null && selectedDateParsed != null) {
                        if (creationDateParsed.equals(selectedDateParsed)) {
                            filteredImages.add(imagePath);
                        } else if (creationDateParsed.before(selectedDateParsed)) {
                            break;
                        }
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            } catch (IOException | ParseException e) {
                Log.e("ExifInterface", "Error reading image metadata", e);
            } finally {
                if (exifInterface != null) {
//                exifInterface.close();
                }
            }
        }

        this.filteredImages = filteredImages;

        updateLabel(filteredImages);
        adapter.setImages(filteredImages);
        imagesView.getAdapter().notifyDataSetChanged();
    }

    private void performSearchByYear() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        String selectedDate = String.format(Locale.getDefault(), "%04d", selectedYear);
                        filterImagesByYear(selectedDate);
                    }
                },
                year,  // Set initial year
                0,     // Set initial month (0-based index)
                1      // Set initial day of month (1-based)
        );
        datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        datePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("month", "id", "android")).setVisibility(View.GONE);

        // Show the date picker dialog
        datePickerDialog.show();
    }
    private void filterImagesByYear(String selectedYear) {
        ArrayList<String> filteredImages = new ArrayList<>();

        for (String imagePath : images) {
            ExifInterface exifInterface = null;
            try {
                exifInterface = new ExifInterface(new File(imagePath).getAbsolutePath());
                String imageDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                if (imageDateTime != null) {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
                    Date inputDate = inputFormat.parse(imageDateTime);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
                    String creationYear = sdf.format(inputDate);

                    if (creationYear.equals(selectedYear)) {
                        filteredImages.add(imagePath);
                    } else if (Integer.valueOf(creationYear) < Integer.valueOf(selectedYear)) {
                        break;
                    }
                } else {
                    break;
                }
            } catch (IOException | ParseException e) {
                Log.e("ExifInterface", "Error reading image metadata", e);
            } finally {
                if (exifInterface != null) {
//                exifInterface.close();
                }
            }
        }

        this.filteredImages = filteredImages;

        updateLabel(filteredImages);
        adapter.setImages(filteredImages);
        imagesView.getAdapter().notifyDataSetChanged();
    }
    private void loadImages() {
        boolean SDCard = Environment.getExternalStorageState().equals(MEDIA_MOUNTED);
        if (SDCard) {
            final String[] colums = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String order = MediaStore.Images.Media.DATE_TAKEN + " DESC";
            Cursor cursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, colums, null, null, order);
            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                int colunmindex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                images.add(cursor.getString(colunmindex));
            }
            imagesView.getAdapter().notifyDataSetChanged();
        }
    }

    public Boolean deleteFile(Context context, Uri uri) {
        File file = new File(uri.getPath());
        String [] selectionArgs = {(file.getAbsolutePath())};
        ContentResolver contentResolver = context.getContentResolver();
        String where = null;
        Uri filesUri = null;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            filesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            where = MediaStore.Images.Media._ID + "=?";
            selectionArgs = new String[] {(file.getName())};
        } else {
            where = MediaStore.MediaColumns.DATA + "=?";
            filesUri = MediaStore.Files.getContentUri("external");
        }
        int result =  contentResolver.delete(filesUri, where, selectionArgs);
        return file.exists();
    }
}