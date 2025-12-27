package com.example.bustracking;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class StudentsListActivity extends AppCompatActivity {

    private RecyclerView rv;
    private StudentsAdapter adapter;
    private List<StudentModel> students = new ArrayList<>();
    private List<StudentModel> filteredList = new ArrayList<>();
    private DatabaseReference dbRoot;
    private EditText etSearchBranch;
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_list);

        rv = findViewById(R.id.rvStudents);
        etSearchBranch = findViewById(R.id.etSearchBranch);
        btnSearch = findViewById(R.id.btnSearch);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentsAdapter(filteredList);
        rv.setAdapter(adapter);

        dbRoot = FirebaseDatabase.getInstance().getReference();
        loadStudents();

        // Filter using button click
        btnSearch.setOnClickListener(v -> filterByBranch());

        // Optional: live filter while typing
        etSearchBranch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterByBranch();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadStudents() {
        dbRoot.child("Users").child("Students").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                students.clear();
                for (DataSnapshot s : snapshot.getChildren()) {
                    StudentModel m = s.getValue(StudentModel.class);
                    if (m != null) {
                        students.add(m);
                    }
                }
                filteredList.clear();
                filteredList.addAll(students);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // Filter students by branch
    private void filterByBranch() {
        String query = etSearchBranch.getText().toString().trim().toLowerCase();
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(students);
        } else {
            for (StudentModel s : students) {
                if (s.branch != null && s.branch.toLowerCase().contains(query)) {
                    filteredList.add(s);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    // Model class
    public static class StudentModel {
        public String branch;
        public String email;
        public String password;
        public String semester;
        public String username;
        public String usn;

        public StudentModel() { }
    }

    // Adapter
    static class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.VH> {
        List<StudentModel> list;
        DatabaseReference dbRoot;

        StudentsAdapter(List<StudentModel> list) {
            this.list = list;
            this.dbRoot = FirebaseDatabase.getInstance().getReference();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_student, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            StudentModel s = list.get(position);

            holder.tvName.setText(s.username != null ? s.username : "—");
            holder.tvUsn.setText("USN: " + (s.usn != null ? s.usn : "—"));
            holder.tvEmail.setText("Email: " + (s.email != null ? s.email : "—"));
            holder.tvBranch.setText("Branch: " + (s.branch != null ? s.branch : "—"));
            holder.tvSemester.setText("Semester: " + (s.semester != null ? s.semester : "—"));

            holder.btnDelete.setOnClickListener(v -> {
                int currentPos = holder.getAdapterPosition();
                if (currentPos == RecyclerView.NO_POSITION) return;

                StudentModel currentStudent = list.get(currentPos);
                if (currentStudent.email == null) return;

                new android.app.AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Student")
                        .setMessage("Are you sure you want to delete " + currentStudent.username + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            dbRoot.child("Users").child("Students")
                                    .orderByChild("email").equalTo(currentStudent.email)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                ds.getRef().removeValue();
                                            }
                                            int adapterPos = holder.getAdapterPosition();
                                            if (adapterPos != RecyclerView.NO_POSITION) {
                                                list.remove(adapterPos);
                                                notifyItemRemoved(adapterPos);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) { }
                                    });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvUsn, tvEmail, tvBranch, tvSemester, btnDelete;

            VH(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvStudentName);
                tvUsn = itemView.findViewById(R.id.tvStudentUSN);
                tvEmail = itemView.findViewById(R.id.tvStudentEmail);
                tvBranch = itemView.findViewById(R.id.tvStudentBranch);
                tvSemester = itemView.findViewById(R.id.tvStudentSemester);
                btnDelete = itemView.findViewById(R.id.btnDeleteStudent);
            }
        }
    }
}
