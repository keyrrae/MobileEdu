/*
 *  Copyright (c) 2017 - present, Xuan Wang
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree.
 *
 */

package edu.ucsb.cs.cs185.foliostation.collectiondetails;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.util.ArrayList;

import edu.ucsb.cs.cs185.foliostation.ItemCards;
import edu.ucsb.cs.cs185.foliostation.R;
import edu.ucsb.cs.cs185.foliostation.editentry.EditTabsActivity;
import edu.ucsb.cs.cs185.foliostation.mycollections.CardsFragment;
import edu.ucsb.cs.cs185.foliostation.mycollections.DetailBlurDialog;
import edu.ucsb.cs.cs185.foliostation.mycollections.GridCardAdapter;

public class CollectionDetailsActivity extends AppCompatActivity {

    private int mCardIndex;
    private RecyclerView mRecyclerView;
    private CollectionDetailsAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    private static int IMAGE_PICKER = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_details);

        Intent intent= getIntent();
        mCardIndex = intent.getIntExtra("CARD_INDEX", 0);

        mRecyclerView = (RecyclerView) findViewById(R.id.detail_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        final ItemCards.Card card = ItemCards.getInstance(getApplicationContext()).cards.get(mCardIndex);

        mAdapter = new CollectionDetailsAdapter(getApplicationContext(), card.getImages());
        mAdapter.setHasStableIds(true);

        mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        mLayoutManager.setItemPrefetchEnabled(true);

        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter.setOnItemClickListener(new CollectionDetailsAdapter.OnRecyclerViewItemClickListener(){
            @Override
            public void onItemClick(View view , int position){
                startDetailDialog(position);
            }
        });

        /*
        SnapHelper helper = new LinearSnapHelper();
        helper.attachToRecyclerView(mRecyclerView);*/
        ItemCards itemCards = ItemCards.getInstance(getApplicationContext());
        itemCards.setAdapter(mAdapter);

        if(itemCards.cards.size() == 0){
            itemCards.inflateDummyContent();
        }

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        ImageButton addPic = (ImageButton) findViewById(R.id.add_image_to_collection);
        addPic.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                ImagePicker.getInstance().setSelectLimit(24 - card.getImages().size());
                Intent intent = new Intent(CollectionDetailsActivity.this, ImageGridActivity.class);
                startActivityForResult(intent, IMAGE_PICKER);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == IMAGE_PICKER) {
                /*
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                ItemCards itemCards = ItemCards.getInstance(getApplicationContext());
                itemCards.addNewCardFromImages(images);
                Intent intent = new Intent(getApplicationContext(), EditTabsActivity.class);
                intent.putExtra("CARD_INDEX", 0);
                startActivity(intent);
                */
                Toast.makeText(this, "Got data", Toast.LENGTH_SHORT).show();


            } else {
                Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
            }
        }
    }


    protected void startDetailDialog(int position){
        Bundle arguments = new Bundle();
        arguments.putInt("CARD_INDEX", mCardIndex);
        arguments.putInt("IMAGE_INDEX", position);

        arguments.putString("FROM", "DETAILS");
        DetailBlurDialog fragment = new DetailBlurDialog();

        fragment.setArguments(arguments);
        FragmentManager ft = this.getSupportFragmentManager();

        fragment.show(ft, "dialog");
        //TODO: move takeScreenShot  to somewhere else

        Bitmap map = CardsFragment.takeScreenShot(this);
        Bitmap fast = CardsFragment.BlurBuilder.blur(getApplicationContext(), map);
        final Drawable draw = new BitmapDrawable(getResources(), fast);

        ImageView background = (ImageView) findViewById(R.id.activity_background);
        background.bringToFront();
        background.setScaleType(ImageView.ScaleType.FIT_XY);
        background.setImageDrawable(draw);
    }
}
