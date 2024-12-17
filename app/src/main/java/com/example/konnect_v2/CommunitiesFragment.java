package com.example.konnect_v2;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CommunitiesFragment extends Fragment implements Support {
    private ListView subKonnectList;
    private List<SubKonnect> subKonnects;
    private SubKonnectListAdapter subKonnectListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_communities, container, false);
        subKonnectList = view.findViewById(R.id.all_sub_konnects);

        subKonnects = new ArrayList<>();
        subKonnectListAdapter = new SubKonnectListAdapter(requireActivity(), subKonnects);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        subKonnectsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subKonnects.clear();

                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        SubKonnect subKonnect = dataSnapshot.getValue(SubKonnect.class);

                        if (subKonnect != null) {
                            subKonnects.add(subKonnect);
                            subKonnectList.setAdapter(subKonnectListAdapter);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}