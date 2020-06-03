package com.example.criminalintent;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        // find recyclerview, create a recyclerview parameter
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);

        // set layout manager
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return view;
    }

    private void updateUI(){
        // get the prepared data
        CrimeLab crimeLab = CrimeLab.get(getActivity());

        // the data list
        List<Crime> crimes = crimeLab.getmCrimes();

        // create recycler adapter
        mAdapter = new CrimeAdapter(crimes);

        // connect recycler view and this adapter
        mCrimeRecyclerView.setAdapter(mAdapter);
    }

    // Recylerview viewholder
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDataTextView;
        private Crime mCrime;

        // constructor function, the list item crime is one item layout
        public CrimeHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView  = (TextView) itemView.findViewById(R.id.crime_title);
            mDataTextView = (TextView) itemView.findViewById(R.id.crime_date);
        }

        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getmTile());
            mDataTextView.setText(mCrime.getmDate().toString());
        }

        @Override
        public void onClick(View v) {
            Log.d("ViewHolder", mCrime.getmTile());
            Toast.makeText(getActivity(), mCrime.getmTile() + " clicked!", Toast.LENGTH_SHORT).show();
        }
    }

    // Adapter is to store data
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{

        // parameter data list
        private List<Crime> mCrimes;

        // constructor, the parameter is the data list.
        public CrimeAdapter(List<Crime> crimes){
            mCrimes = crimes;
        }

        // create view holder
        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}
