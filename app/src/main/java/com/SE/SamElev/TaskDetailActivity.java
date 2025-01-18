package com.SE.SamElev;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
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

    private Button taskComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // Initialize views
        taskNameTextView = findViewById(R.id.taskNameTextView);
        taskDescriptionTextView = findViewById(R.id.taskDescriptionTextView);
        dueDateTextView = findViewById(R.id.dueDateTextView);
        assignedEmailTextView = findViewById(R.id.assignedEmailTextView);
        documentUriTextView = findViewById(R.id.documentUriTextView);

//        checkPermissions();

        // Get extras from the intent
        String taskId = getIntent().getStringExtra("TASK_ID");
//        String documentUri = getIntent().getStringExtra("DOCUMENT_URI"); // Retrieve document URI
//
//        if (documentUri != null && !documentUri.isEmpty()) {
//            loadPdfFromUri(documentUri); // Load the PDF directly
//            documentUriTextView.setText(documentUri);
//        } else {
//            Toast.makeText(this, "No document attached to this task", Toast.LENGTH_SHORT).show();
//        }

        // Fetch task details for other data
        fetchTaskDetailsFromFirestore(taskId);
        taskComplete = findViewById(R.id.taskComplete);

        taskComplete.setOnClickListener(v -> {
            Toast.makeText(this, "Task Complete", Toast.LENGTH_SHORT).show();

        });
    }

//    private void checkPermissions() {
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) !=
//                android.content.pm.PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        }
//    }

    private void fetchTaskDetailsFromFirestore(String taskId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tasks")
                .document(taskId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String taskName = documentSnapshot.getString("taskName");
                        String taskDescription = documentSnapshot.getString("taskDescription");
                        String dueDate = documentSnapshot.getString("dueDate");
                        String assignedEmail = documentSnapshot.getString("assignedEmail");

                        taskNameTextView.setText(taskName);
                        taskDescriptionTextView.setText(taskDescription);
                        dueDateTextView.setText(dueDate);
                        assignedEmailTextView.setText(assignedEmail);
                    } else {
                        Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch task details", Toast.LENGTH_SHORT).show());
    }


//    private void loadPdfFromUri(String documentUri) {
//        if (documentUri.startsWith("gs://")) {
//            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(documentUri);
//            File localFile = new File(getCacheDir(), "downloaded_pdf.pdf");
//
//            storageReference.getFile(localFile)
//                    .addOnSuccessListener(taskSnapshot -> loadPdfFromFile(localFile))
//                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to download PDF", Toast.LENGTH_SHORT).show());
//        } else {
//            File file = new File(documentUri);
//            loadPdfFromFile(file);
//        }
//
//    }
//
//    private void loadPdfFromFile(File file) {
//        try {
//            if (!file.exists()) {
//                Toast.makeText(this, "PDF file does not exist", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Open the PDF file using PdfRenderer
//            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
//            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
//
//            if (pdfRenderer.getPageCount() > 0) {
//                // Show the first page of the PDF
//                showPage(0);
//            } else {
//                Toast.makeText(this, "No pages found in PDF", Toast.LENGTH_SHORT).show();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to load PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void showPage(int index) {
//        if (pdfRenderer != null && pdfRenderer.getPageCount() > index) {
//            if (currentPage != null) {
//                currentPage.close();
//            }
//
//            currentPage = pdfRenderer.openPage(index);
//
//            Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
//            currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
//
//            SurfaceHolder surfaceHolder = pdfRendererSurfaceView.getHolder();
//            Surface surface = surfaceHolder.getSurface();
//
//            if (surface.isValid()) {
//                Canvas canvas = surfaceHolder.lockCanvas();
//                canvas.drawBitmap(bitmap, 0, 0, null);
//                surfaceHolder.unlockCanvasAndPost(canvas);
//            }
//        } else {
//            Toast.makeText(this, "Invalid PDF page index", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
    }
}