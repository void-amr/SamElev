package com.SE.SamElev;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TaskListData extends AppCompatActivity {
    private TextView taskNameTextView, taskDescriptionTextView, dueDateTextView, assignedEmailTextView;
    private Button deleteTaskButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list_data); // Ensure the layout ID matches

        // Initialize views with updated IDs
        taskNameTextView = findViewById(R.id.task_name_text_view);
        taskDescriptionTextView = findViewById(R.id.task_description_text_view);
        dueDateTextView = findViewById(R.id.due_date_text_view);
        assignedEmailTextView = findViewById(R.id.assigned_email_text_view);
        deleteTaskButton = findViewById(R.id.delete);
        // Get task ID from the intent
        String taskId = getIntent().getStringExtra("TASK_ID");

        // Ensure that task ID is not null or empty
        if (taskId != null && !taskId.isEmpty()) {
            // Fetch task details from Firestore
            fetchTaskDetailsFromFirestore(taskId);
        } else {
            Toast.makeText(this, "Invalid task ID", Toast.LENGTH_SHORT).show();
        }
        deleteTaskButton.setOnClickListener(view -> {
            if (taskId != null && !taskId.isEmpty()) {
                deleteTaskFromFirestore(taskId);
            } else {
                Toast.makeText(this, "Invalid task ID", Toast.LENGTH_SHORT).show();
            }
        });    }

    private void fetchTaskDetailsFromFirestore(String taskId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tasks")
                .document(taskId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fetch task details safely
                        String taskName = documentSnapshot.getString("taskName");
                        String taskDescription = documentSnapshot.getString("taskDescription");
                        String dueDate = documentSnapshot.getString("dueDate");
                        String assignedEmail = documentSnapshot.getString("assignedEmail");

                        // Set data to the views
                        taskNameTextView.setText(taskName != null ? taskName : "N/A");
                        taskDescriptionTextView.setText(taskDescription != null ? taskDescription : "N/A");
                        dueDateTextView.setText(dueDate != null ? dueDate : "N/A");
                        assignedEmailTextView.setText(assignedEmail != null ? assignedEmail : "N/A");
                    } else {
                        Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Show error message with exception
                    Toast.makeText(this, "Failed to fetch task details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void deleteTaskFromFirestore(String taskId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tasks")
                .document(taskId)
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show();
                    sendNotificationToAdmin("Task Deleted", "A task has been deleted successfully.");
                    finish(); // Closes the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sendNotificationToAdmin(String title, String message) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch the admin's FCM token from Firestore
        db.collection("users")
                .document("admin123@gmail.com") // Replace with admin's user document ID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Get the admin's FCM token
                        String fcmToken = documentSnapshot.getString("fcmToken");
                        if (fcmToken != null) {
                            sendPushNotification(fcmToken, title, message);
                        } else {
                            Log.e("FCM", "Admin's FCM token is null");
                        }
                    } else {
                        Log.e("FCM", "Admin not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FCM", "Failed to fetch admin FCM token: " + e.getMessage());
                });
    }
    private void sendPushNotification(String fcmToken, String title, String message) {
        // Prepare the notification payload
        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "key=" + "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCy5Ke+cTzrNcBg\\njwLfdyQJuBCRjafCDuC60lBVUPpbf9J0iNL6gM279a22SgXrp8jXGmg6Tm2sq1+o\\nAL8yZFyekFX8kt86pw7svdlI5d1HorWdO7lxqq12rReoFaeNm8Nqlxmp55+hVjgt");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            JSONObject notification = new JSONObject();
            notification.put("to", fcmToken); // Admin's FCM token
            notification.put("title", title);
            notification.put("body", message);
            JSONObject request = new JSONObject();
            request.put("notification", notification);
            OutputStream os = connection.getOutputStream();
            os.write(request.toString().getBytes());
            os.flush();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d("FCM", "Notification sent successfully to admin");
            } else {
                Log.e("FCM", "Failed to send notification to admin. Response code: " + responseCode);
            }
            connection.disconnect();
        } catch (Exception e) {
            Log.e("FCM", "Error sending notification: " + e.getMessage());
        }
    }

}
