package com.SE.SamElev;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class updateUser extends AppCompatActivity {
    Button updateuser;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        updateuser = findViewById(R.id.btnupdateuser);
        listView = findViewById(R.id.listV);

        updateuser.setOnClickListener(v -> startActivity(new Intent(updateUser.this, addEmployee.class)));

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Fetch data from "Users" collection
        firestore.collection("Users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, String>> allUsersList = new ArrayList<>();

                    // Fetch users from the "Users" collection
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Map<String, String> userData = new HashMap<>();
                        userData.put("userId", document.getId());
                        userData.put("email", document.getString("email"));
                        userData.put("userType", document.getString("userType"));
                        allUsersList.add(userData);
                    }

                    // Fetch data from "Employees" collection
                    firestore.collection("Employees")
                            .get()
                            .addOnSuccessListener(employeeSnapshots -> {
                                // Fetch employees from the "Employees" collection
                                for (DocumentSnapshot employeeDoc : employeeSnapshots.getDocuments()) {
                                    Map<String, String> employeeData = new HashMap<>();
                                    employeeData.put("userId", employeeDoc.getId());
                                    employeeData.put("email", employeeDoc.getString("email"));
                                    employeeData.put("userType", "employee"); // Set a fixed userType for employees
                                    allUsersList.add(employeeData);
                                }

                                // Set up the samataAdapter to display the combined data
                                samataAdapter adapter = new samataAdapter(
                                        updateUser.this,
                                        R.layout.samata_adapter,  // Use your layout for list items
                                        allUsersList  // Pass the list of user data (Map)
                                ) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        // Use the getView method from samataAdapter
                                        View view = super.getView(position, convertView, parent);
                                        Map<String, String> userData = getItem(position);

                                        if (userData != null) {
                                            convertView = LayoutInflater.from(getContext()).inflate(R.layout.samata_adapter, parent, false);
                                            TextView text1 = convertView.findViewById(R.id.userMail);
                                            TextView text2 = convertView.findViewById(R.id.userlistType);
                                            text1.setText(userData.get("email"));
                                            text2.setText(userData.get("userType"));
                                        }
                                        Button delete = convertView.findViewById(R.id.btnDelete);
                                        delete.setOnClickListener(v -> {
                                            assert userData != null;
                                            deleteUser(userData.get("userId"), position);
                                        });
                                        return convertView;
                                    }
                                };

                                listView.setAdapter(adapter);
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error fetching employees: " + e.getMessage()));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching users: " + e.getMessage()));
    }

    // Method to delete a user from Firestore
    private void deleteUser(String userId, int position) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Delete the user from the "Users" collection
        firestore.collection("Users").document(userId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Notify the user that the user was deleted from Users collection
                    Toast.makeText(updateUser.this, "User deleted successfully from Users collection", Toast.LENGTH_SHORT).show();

                    // Attempt to delete the user from the "Employees" collection if the user exists there as well
                    firestore.collection("Employees").document(userId) // Assuming the userId is the same in the Employees collection
                            .delete()
                            .addOnSuccessListener(aVoid1 -> {
                                // Notify the user that the user was deleted from Employees collection
                                Toast.makeText(updateUser.this, "User deleted successfully from Employees collection", Toast.LENGTH_SHORT).show();

                                // Remove the user from the list and update the list view
                                samataAdapter adapter = (samataAdapter) listView.getAdapter();
                                adapter.remove(adapter.getItem(position)); // Remove the item at the given position
                                adapter.notifyDataSetChanged(); // Update the ListView
                            })
                            .addOnFailureListener(e -> {
                                // If the deletion fails in Employees collection
                                Toast.makeText(updateUser.this, "Error deleting user from Employees collection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // If the deletion fails in Users collection
                    Toast.makeText(updateUser.this, "Error deleting user from Users collection: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}