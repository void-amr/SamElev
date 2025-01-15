package com.SE.SamElev;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class samataAdapter extends ArrayAdapter<Map<String, String>> {

    private Context context;
    private List<Map<String, String>> users;  // List of maps to hold user data

    public samataAdapter(@NonNull Context context, int resource, @NonNull List<Map<String, String>> users) {
        super(context, resource, users);
        this.context = context;
        this.users = users;  // Initialize the users list
    }

    @Nullable
    @Override
    public Map<String, String> getItem(int position) {
        if (position >= 0 && position < users.size()) {
            return users.get(position);
        } else {
            return null;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate the layout for the custom list item
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.samata_adapter, parent, false);

        // Get the user data at the current position
        Map<String, String> user = getItem(position);

        // Set the user email and user type into the respective TextViews
        TextView userEmailTextView = convertView.findViewById(R.id.userMail);
        TextView userTypeTextView = convertView.findViewById(R.id.userlistType);
        Button deleteButton = convertView.findViewById(R.id.btnDelete);

        if (user != null) {
            userEmailTextView.setText(user.get("email"));
            userTypeTextView.setText(user.get("userType"));
        }

        // Handle the delete button click
        deleteButton.setOnClickListener(v -> {
            if (user != null) {
                deleteUser(user.get("userId"), position);
            }
        });

        return convertView;
    }

    // Method to delete the user from Firestore
    private void deleteUser(String userId, int position) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Delete the user from both the "Users" and "Employees" collections
        firestore.collection("Users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Notify the user that the user was deleted from Users collection
                    Toast.makeText(context, "User deleted successfully from Users collection", Toast.LENGTH_SHORT).show();

                    // Attempt to delete the user from Employees collection if the user exists there as well
                    firestore.collection("Employees").document(userId) // Assuming the userId is the same in the Employees collection
                            .delete()
                            .addOnSuccessListener(aVoid1 -> {
                                // Notify the user that the user was deleted from Employees collection
                                Toast.makeText(context, "User deleted successfully from Employees collection", Toast.LENGTH_SHORT).show();

                                // Remove the user from the list and update the list view
                                users.remove(position);
                                notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                // If the deletion fails in Employees collection
                                Toast.makeText(context, "Error deleting user from Employees collection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // If the deletion fails in Users collection
                    Toast.makeText(context, "Error deleting user from Users collection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
