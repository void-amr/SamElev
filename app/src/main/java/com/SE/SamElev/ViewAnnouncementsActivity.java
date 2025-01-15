package com.SE.SamElev;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ViewAnnouncementsActivity extends AppCompatActivity {

    private ListView announcementsListView;
    private FirebaseFirestore firestore;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> announcementsList;
    private ArrayList<String> documentIds; // Store document IDs for deletion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_announcements);

        firestore = FirebaseFirestore.getInstance();
        announcementsListView = findViewById(R.id.announcementsListView);
        announcementsList = new ArrayList<>();
        documentIds = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, announcementsList);
        announcementsListView.setAdapter(adapter);

        fetchAnnouncements();

        // Set a click listener for the ListView items
        announcementsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedAnnouncement = announcementsList.get(position);
            String documentId = documentIds.get(position); // Get the corresponding document ID
            showDeleteDialog(selectedAnnouncement, documentId, position);
        });
    }

    private void fetchAnnouncements() {
        firestore.collection("Announcements").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        announcementsList.clear(); // Clear the list to avoid duplicates
                        documentIds.clear(); // Clear the document IDs list
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String message = document.getString("message");
                            if (message != null) {
                                announcementsList.add(message);
                                documentIds.add(document.getId()); // Store the document ID
                            }
                        }
                        adapter.notifyDataSetChanged(); // Notify adapter about data changes
                    } else {
                        Toast.makeText(this, "Failed to load announcements", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDeleteDialog(String selectedAnnouncement, String documentId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Announcement")
                .setMessage("Are you sure you want to delete this announcement?\n\n" + selectedAnnouncement)
                .setPositiveButton("Delete", (dialog, which) -> deleteAnnouncement(documentId, position))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteAnnouncement(String documentId, int position) {
        firestore.collection("Announcements").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove the item from the list and update the UI
                    announcementsList.remove(position);
                    documentIds.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Announcement deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete announcement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
