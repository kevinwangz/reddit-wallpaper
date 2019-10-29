package com.kevinwang.redditwallpaper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.Observable;
import java.util.Observer;

public class ViewImageActivity extends AppCompatActivity implements Observer {
    // Private Vars
    ImageModel imageModel;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // referred to class lecture slides for how to do this
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        Integer position;

        if (extras != null) {
            position = Integer.valueOf(extras.getString("position"));

            setContentView(R.layout.full_screen_image);

            imageModel = ImageModel.getInstance();
            imageModel.addObserver(this);

            imageView = (ImageView) findViewById(R.id.full_image_view);
            imageView.setImageBitmap(imageModel.getImageWithDetails(position).getImage());
            imageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    finish();
                }
            });

            imageModel.initObservers();
        }

    }

    @Override
    public void update(Observable o, Object arg)
    {
        // pass
    }
}