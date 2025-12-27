package com.example.bustracking;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FeedbackListActivity extends AppCompatActivity {

    private RecyclerView rv;
    private FeedbackAdapter adapter;
    private List<FeedbackModel> feedbackList = new ArrayList<>();
    private DatabaseReference dbRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_list);

        rv = findViewById(R.id.rvFeedback);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FeedbackAdapter(feedbackList, model -> showFeedbackDetailDialog(model));
        rv.setAdapter(adapter);

        dbRoot = FirebaseDatabase.getInstance().getReference();

        loadFeedbacks();
    }

    /** LOAD ALL FEEDBACK + REAL STUDENT NAMES */
    private void loadFeedbacks() {
        dbRoot.child("feedback").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                feedbackList.clear();

                for (DataSnapshot studentNode : snapshot.getChildren()) {

                    String studentId = studentNode.getKey();   // must match Students UID

                    for (DataSnapshot fbNode : studentNode.getChildren()) {

                        FeedbackModel model = fbNode.getValue(FeedbackModel.class);

                        if (model == null) continue;

                        model.id = fbNode.getKey();
                        model.userId = studentId;

                        // 🔥 Now fetch REAL name from Users/Students
                        dbRoot.child("Users").child("Students").child(studentId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot stuSnap) {

                                        if (stuSnap.exists()) {
                                            model.studentName = stuSnap.child("username").getValue(String.class);
                                            model.studentBranch = stuSnap.child("branch").getValue(String.class);
                                        } else {
                                            model.studentName = "Unknown Student";
                                            model.studentBranch = "";
                                        }

                                        feedbackList.add(model);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override public void onCancelled(@NonNull DatabaseError error) { }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    /** SHOW FEEDBACK POPUP */
    private void showFeedbackDetailDialog(FeedbackModel f) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        String timeStr = (f.timestamp > 0) ? sdf.format(new Date(f.timestamp)) : "";

        String msg = "Message: " + (f.message != null ? f.message : "") +
                "\n\nBy: " + (f.studentName != null ? f.studentName : "Unknown") +
                "\nBranch: " + (f.studentBranch != null ? f.studentBranch : "—") +
                "\n\nAt: " + timeStr;

        new AlertDialog.Builder(FeedbackListActivity.this)
                .setTitle("Feedback Detail")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    /** MODEL **/
    public static class FeedbackModel {
        public String id;
        public String userId;
        public String message;
        public long timestamp;

        // Added new fields
        public String studentName;
        public String studentBranch;

        public FeedbackModel() {}
    }

    /** ADAPTER **/
    static class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.VH> {

        interface OnFeedbackClick { void onClick(FeedbackModel model); }

        private final List<FeedbackModel> list;
        private final OnFeedbackClick listener;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());

        FeedbackAdapter(List<FeedbackModel> list, OnFeedbackClick listener) {
            this.list = list;
            this.listener = listener;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_feedbacks, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            FeedbackModel f = list.get(position);

            holder.tvMessage.setText(f.message != null ? f.message : "(no message)");
            holder.tvUser.setText("By: " + (f.studentName != null ? f.studentName : "Unknown"));
            holder.tvTime.setText(f.timestamp > 0 ? sdf.format(new Date(f.timestamp)) : "");

            holder.itemView.setOnClickListener(v -> listener.onClick(f));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvMessage, tvUser, tvTime;

            VH(@NonNull View itemView) {
                super(itemView);
                tvMessage = itemView.findViewById(R.id.tvFeedbackMessage);
                tvUser = itemView.findViewById(R.id.tvFeedbackUser);
                tvTime = itemView.findViewById(R.id.tvFeedbackTime);
            }
        }
    }
}
