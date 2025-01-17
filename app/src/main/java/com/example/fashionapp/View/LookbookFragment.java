package com.example.fashionapp.View;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.fashionapp.R;
import com.example.fashionapp.ViewModel.SharedViewModel;
import com.example.fashionapp.databinding.FragmentLookbookBinding;

import java.util.ArrayList;
import java.util.List;

public class LookbookFragment extends Fragment {

    private FragmentLookbookBinding binding;
    ImageView box_image;
    private SharedViewModel sharedViewModel;
    private AlertDialog dialog;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentLookbookBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    private int getImageById(String category, String num) {
        int id = getResources().getIdentifier(
                category + num, "raw", getActivity().getPackageName());
        return id;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        box_image = getView().findViewById(R.id.box_image);

        View.OnClickListener onClickListener = new Button.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                binding.guest.setSelected(false);
                binding.casual.setSelected(false);
                binding.date.setSelected(false);
                binding.sports.setSelected(false);
                binding.work.setSelected(false);

                String btnId = getResources().getResourceName(v.getId()).split("/")[1];

                String btnTxt = ((Button) v).getText().toString();

                for (int i = 1; i < 10; i++) {
                    int id = getResources().getIdentifier("date_"+i, "id", getActivity().getPackageName());
                    ImageButton imgbtn = view.findViewById(id);
                    int imgid = getImageById(btnId, String.valueOf(i));
                    imgbtn.setImageResource(imgid);
                    imgbtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View imgv) {
                            popUpImg(imgid, btnTxt);
                        }
                    });
                }
                v.setSelected(true);
            }
        };

        binding.guest.setOnClickListener(onClickListener);
        binding.casual.setOnClickListener(onClickListener);
        binding.date.setOnClickListener(onClickListener);
        binding.sports.setOnClickListener(onClickListener);
        binding.work.setOnClickListener(onClickListener);

        binding.homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(LookbookFragment.this)
                        .navigate(R.id.action_global_toHome);
            }
        });
    }

    private void popUpImg(int imgId, String lookname){
        ImageView imgview=new ImageView(getContext());
        imgview.setImageResource(imgId);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(lookname+" 코디 추천");
        builder.setView(imgview);

        builder.setNegativeButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog!= null) dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        binding = null;
    }

}