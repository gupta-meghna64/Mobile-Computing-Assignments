package com.example.assignment2;

import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private DatabaseHelper myDb;
    private CrimeAdapter mAdapter;
    String URL = "content://com.example.assignment2.StudentsProvider";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view
                .findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        myDb = new DatabaseHelper(getContext());
        insertToDatabase(myDb);
        Cursor res = myDb.getAllData();
        ShoppingLab shoppingLab = ShoppingLab.get(getActivity(), res);
        List<ShoppingItem> crimes = shoppingLab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void insertToDatabase(DatabaseHelper db){
        if(myDb.getAllData().getCount() == 0) {
            for (int i = 1; i < 16; i++) {
                myDb.insertData(i);
            }
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ShoppingItem mCrime;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private View mView;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
            mView = (View) itemView.findViewById(R.id.seperator);
        }

        public void bind(final ShoppingItem crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getmDescription().toString());

            mTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = ShoppingItemPagerActivity.newIntent(getActivity(), mCrime.getId());
                    startActivity(intent);
                }
            });

            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
            mSolvedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    crime.setSolved(false);
                    ContentValues values = new ContentValues();
                    values.put(ItemProvider._ID, mCrime.getmId().toString());
                    myDb.deleteData(Integer.parseInt(mCrime.getTitle().split(" ")[1]));
                    mSolvedImageView.setVisibility((View.GONE));

                }
            });
            mView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onClick(View view) {
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<ShoppingItem> mCrimes;

        public CrimeAdapter(List<ShoppingItem> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            ShoppingItem crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}