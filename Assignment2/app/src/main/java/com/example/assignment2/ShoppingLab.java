package com.example.assignment2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ShoppingLab {
    private static ShoppingLab sShoppingLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    private List<ShoppingItem> mCrimes;

    public static ShoppingLab get(Context context, Cursor cursor) {
        if (sShoppingLab == null) {
            sShoppingLab = new ShoppingLab(context, cursor);
        }

        return sShoppingLab;
    }

    private ShoppingLab(Context context, Cursor cursor) {
        mContext = context.getApplicationContext();

        mCrimes = new ArrayList<>();
        while(cursor.moveToNext()){
            int name = Integer.parseInt(cursor.getString(1));
            ShoppingItem crime = new ShoppingItem();
            crime.setTitle("Item " + name);
            crime.setmDescription("Description " + name);
            crime.setSolved(true);
            mCrimes.add(crime);
        }

//        for (int i = 1; i < 16; i++) {
//            ShoppingItem crime = new ShoppingItem();
//            crime.setTitle("Item " + i);
//            crime.setmDescription("Description" + i);
//            crime.setSolved(true);
//            mCrimes.add(crime);
//        }

    }


    public List<ShoppingItem> getCrimes() {
        return mCrimes;
    }

    public ShoppingItem getCrime(UUID id) {
        for (ShoppingItem crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }

        return null;
    }


}