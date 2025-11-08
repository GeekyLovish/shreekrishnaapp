package com.raman.kumar.shrikrishan.adminPanel.addPosts;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.raman.kumar.customClasses.Extensions;
import com.raman.kumar.modals.getaModal.getGeetaModal.Datum;
import com.raman.kumar.modals.getaModal.DeleteGetaModal;
import com.raman.kumar.shrikrishan.R;
import com.raman.kumar.shrikrishan.apiNetworking.RetrofitClient;
import com.raman.kumar.shrikrishan.model.CommonResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mann on 20/2/18.
 */

public class EditGitaAdapter extends RecyclerView.Adapter<EditGitaAdapter.MyViewHolder> {
    FragmentTransaction ft;
    private List<Datum> geetaList;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    String from;

    Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textview1;
        LinearLayout mainLay;

        ImageView editButton;
        private ItemClickListener clickListener;

        public MyViewHolder(View view) {
            super(view);
            textview1 = view.findViewById(R.id.textview1);
            mainLay = view.findViewById(R.id.mainLay);
            editButton = view.findViewById(R.id.editButton);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }


    }


    public EditGitaAdapter(List<Datum> arrayList, Context context, FragmentTransaction ft, String from) {
        this.geetaList = arrayList;
        this.mContext = context;
        this.ft = ft;
        this.from = from;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.text_row_edit, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final String description = geetaList.get(position).getContent();
        String title = geetaList.get(position).getTitle();

        holder.textview1.setText(title);
//        holder.mainLay.setOnClickListener(view -> {
//            Intent i = new Intent(mContext, FullScreenTextActivity.class);
//            i.putExtra("title", geetaList.get(position).getTitle());
//            i.putExtra("content", geetaList.get(position).getContent());
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext.startActivity(i);
//        });

        holder.editButton.setOnClickListener(v -> {
            openDialogForOptions(position, holder.editButton);
        });
    }

    private void openDialogForOptions(int position, ImageView editButton) {
        PopupMenu popup = new PopupMenu(mContext, editButton);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());


        // Set up individual item click listeners
        popup.getMenu().findItem(R.id.edit).setOnMenuItemClickListener(item -> {
            // Start VideoUploadActivity with the necessary data
            Intent intent = new Intent(mContext, AddGitaActivity.class);
            intent.putExtra("title", geetaList.get(position).getTitle());
            intent.putExtra("content", geetaList.get(position).getContent());
            intent.putExtra("id", geetaList.get(position).getId().toString());
            intent.putExtra("position", String.valueOf(position));
            intent.putExtra("from", from);
            mContext.startActivity(intent);
            return true; // Indicate that the event was handled
        });
        // Set up individual item click listeners
        popup.getMenu().findItem(R.id.delete).setOnMenuItemClickListener(item -> {
            if (from.equals("gita")){

                Call<DeleteGetaModal> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .deleteGeeta("application/json", Extensions.getBearerToken(),geetaList.get(position).getId().toString());
                call.enqueue(new Callback<DeleteGetaModal>() {
                    @Override
                    public void onResponse(Call<DeleteGetaModal> call, Response<DeleteGetaModal> response) {
                        DeleteGetaModal deleteGeeta = response.body();
                        if (response.isSuccessful()) {
                            if (deleteGeeta.getStatus()) {
                                geetaList.remove(position);
                                notifyDataSetChanged();
                                notifyItemRemoved(position);
//                                            ((EditGitaActivity)mContext).getGeetaList();
                                Toast.makeText(mContext, "Deleted Successfully", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(mContext, deleteGeeta.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                        Log.d("yyyyy",t.getMessage());
                        Toast.makeText(mContext, "Failed to delete !", Toast.LENGTH_LONG).show();
                    }
                });



//                            myRef.child("Geeta_Parts").child(geetaList.get(position).getId()).removeValue();
//                            geetaList.remove(position);
//                            notifyDataSetChanged();
//                            notifyItemRemoved(position);
//                            ((EditGitaActivity)mContext).getGeetaList();
            }else {
//                            Toast.makeText(mContext, "ID: "+geetaList.get(position).getId(), Toast.LENGTH_SHORT).show();
                Call<DeleteGetaModal> call = RetrofitClient
                        .getInstance()
                        .getApi()
                        .deleteAarti("application/json", Extensions.getBearerToken(),geetaList.get(position).getId().toString());
                call.enqueue(new Callback<DeleteGetaModal>() {
                    @Override
                    public void onResponse(Call<DeleteGetaModal> call, Response<DeleteGetaModal> response) {
                        DeleteGetaModal deleteAarti = response.body();

                        System.out.println("sdfjsdkljfks" + response.body());
                        if (response.isSuccessful()) {
                            if (deleteAarti.getStatus()) {
                                geetaList.remove(position);
                                notifyDataSetChanged();
                                notifyItemRemoved(position);
//                                            ((EditGitaActivity)mContext).getArtiList();
                                Toast.makeText(mContext, "Deleted Successfully", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(mContext, deleteAarti.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DeleteGetaModal> call, Throwable t) {
                        Log.d("yyyyy",t.getMessage());
                        Toast.makeText(mContext, "Failed to delete !", Toast.LENGTH_LONG).show();
                    }
                });


//                            myRef.child("Arti_Parts").child(geetaList.get(position).getId()).removeValue();
//                            geetaList.remove(position);
//                            notifyDataSetChanged();
//                            notifyItemRemoved(position);
//                            ((EditGitaActivity)mContext).getArtiList();
            }
            return true; // Indicate that the event was handled
        });

        popup.show();//showing popup menu
    }

    @Override
    public int getItemCount() {
        return geetaList.size();
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }


}