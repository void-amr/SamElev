package com.SE.SamElev;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView taskNameTextView, taskDescriptionTextView, dueDateTextView, assignedEmailTextView, documentUriTextView;
    private SurfaceView pdfRendererSurfaceView;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // Initialize TextViews
        taskNameTextView = findViewById(R.id.taskNameTextView);
        taskDescriptionTextView = findViewById(R.id.taskDescriptionTextView);
        dueDateTextView = findViewById(R.id.dueDateTextView);
        assignedEmailTextView = findViewById(R.id.assignedEmailTextView);
        documentUriTextView = findViewById(R.id.documentUriTextView);

        // Initialize SurfaceView for PDF rendering
        pdfRendererSurfaceView = findViewById(R.id.pdfRendererSurfaceView);

        // Check for storage permission
        checkPermissions();

        // Get the task ID passed from HomeFragment
        String taskId = getIntent().getStringExtra("TASK_ID");

        // Fetch task details from Firestore using the task ID
        fetchTaskDetailsFromFirestore(taskId);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    private void fetchTaskDetailsFromFirestore(String taskId) {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch task details by document ID
        db.collection("tasks")
                .document(taskId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retrieve the task details
                        String taskName = documentSnapshot.getString("taskName");
                        String taskDescription = documentSnapshot.getString("taskDescription");
                        String dueDate = documentSnapshot.getString("dueDate");
                        String assignedEmail = documentSnapshot.getString("assignedEmail");
                        String documentUri = documentSnapshot.getString("documentUri");

                        // Display the details in the TextViews
                        taskNameTextView.setText(taskName);
                        taskDescriptionTextView.setText(taskDescription);
                        dueDateTextView.setText(dueDate);
                        assignedEmailTextView.setText(assignedEmail);
                        documentUriTextView.setText(documentUri);

                        // Load the PDF if the document URI is valid
                        if (documentUri != null && !documentUri.isEmpty()) {
                            loadPdfFromUri(documentUri);
                        }
                    } else {
                        Toast.makeText(TaskDetailActivity.this, "Task not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TaskDetailActivity.this, "Failed to fetch task details", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadPdfFromUri(String documentUri) {
        if (documentUri.startsWith("gs://")) {
            // If it's a Firebase Storage URI, download the file first
            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(documentUri);
            File localFile = new File(getCacheDir(), "downloaded_pdf.pdf");

            storageReference.getFile(localFile)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Once the file is downloaded, load it
                        loadPdfFromFile(localFile);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("TaskDetailActivity", "Failed to download PDF", e);
                    });
        } else {
            // If it's a file path, load it directly
            File file = new File(documentUri);
            loadPdfFromFile(file);
        }
    }

    private void loadPdfFromFile(File file) {
        try {
            if (!file.exists()) {
                Toast.makeText(this, "PDF file does not exist", Toast.LENGTH_SHORT).show();
                return;
            }

            // Open the PDF file using PdfRenderer
            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);

            if (pdfRenderer.getPageCount() > 0) {
                // Show the first page of the PDF
                showPage(0);
            } else {
                Toast.makeText(this, "No pages found in PDF", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showPage(int index) {
        if (pdfRenderer != null && pdfRenderer.getPageCount() > 0) {
            // Close the previous page if any
            if (currentPage != null) {
                currentPage.close();
            }

            // Open the selected page
            currentPage = pdfRenderer.openPage(index);

            // Create a Bitmap to render the PDF page into
            Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);

            // Render the PDF page onto the Bitmap
            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            // Now display the Bitmap on the SurfaceView
            SurfaceHolder surfaceHolder = pdfRendererSurfaceView.getHolder();
            Surface surface = surfaceHolder.getSurface();

            // Lock the Canvas once and draw the Bitmap on the Surface
            if (surface != null) {
                surface.lockCanvas(null).drawBitmap(bitmap, 0, 0, null);
                surface.unlockCanvasAndPost(surface.lockCanvas(null));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
    }
}
