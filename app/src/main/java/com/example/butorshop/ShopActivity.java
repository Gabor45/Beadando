package com.example.butorshop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=1;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;
    private int kosaritems = 0;
    private ArrayList<ShopList> mShoplist;

    private NotificationHandler mNoti;

    private ShoplistAdapter mAdapter;

    private FrameLayout redCircle;
    private TextView contentTextView;

    private int gridNumber = 1;
    private RecyclerView mRecycle;

    private boolean viewRow = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        mNoti=new NotificationHandler(this);

        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

        } else {
            Toast.makeText(ShopActivity.this, "Unauthenticated user", Toast.LENGTH_LONG).show();
            finish();
        }

        mRecycle = findViewById(R.id.recycleview);
        mRecycle.setLayoutManager(new GridLayoutManager(this, gridNumber));

        mShoplist = new ArrayList<>();

        mAdapter = new ShoplistAdapter(this, mShoplist);

        mRecycle.setAdapter(mAdapter);

        mFirestore=FirebaseFirestore.getInstance();
        mItems=mFirestore.collection("Items");


        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){

        }else {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        queryData();
    }
    private void queryData(){
        mShoplist.clear();

        mItems.orderBy("name").limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document: queryDocumentSnapshots)
            {
                ShopList item=document.toObject(ShopList.class);
                mShoplist.add(item);
            }
            if(mShoplist.size()==0)
            {
                initializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initializeData() {
        String[] itemList = getResources().getStringArray(R.array.shoplist_name);
        String[] itemInfo = getResources().getStringArray(R.array.shoplist_info);
        String[] itemPrice = getResources().getStringArray(R.array.shoplist_price);
        TypedArray itemImageRes = getResources().obtainTypedArray(R.array.shoplist_img);

        for (int i = 0; i < itemList.length; i++) {
            mItems.add(new ShopList(itemList[i], itemInfo[i], itemPrice[i], itemImageRes.getResourceId(i, 0)));
            Log.i(ShopActivity.class.getName(), itemList[i]);
        }
        itemImageRes.recycle();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "The application is unable to use location services.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.shop_list_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search_bar);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.setting:
                return true;
            case R.id.kosar:
                return true;
            case R.id.viewalertcount:
                if (viewRow) {
                    changeSpanCount(item, R.drawable.baseline_view_module_grid, 1);
                } else {
                    changeSpanCount(item, R.drawable.baseline_view_stream_row, 2);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawAbleId, int i) {
        viewRow = !viewRow;
        item.setIcon(drawAbleId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecycle.getLayoutManager();
        layoutManager.setSpanCount(i);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        final MenuItem alertMenuItem = menu.findItem(R.id.kosar);

        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.redcircle);
        contentTextView = (TextView) rootView.findViewById(R.id.viewalertcount);


        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon() {
        Button mybutton=findViewById(R.id.kosarba);

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_up);
        mybutton.startAnimation(anim);

        kosaritems = (kosaritems + 1);
        contentTextView.setText(String.valueOf(kosaritems));

        try {
            mNoti.send("You just bought an item");
        }catch(Exception e)
        {
            Toast.makeText(ShopActivity.this, "Couldnt send notification", Toast.LENGTH_LONG).show();
        }
    }

}