package com.example.assignment2;

import androidx.fragment.app.Fragment;

public class ItemListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ItemListFragment();
    }
}