package com.kevinwang.redditwallpaper;

import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity implements Observer, RecyclerViewAdapter.ClickListener {

    ImageModel imageModel;
    ImageView loadImages;
    ImageView clearImages;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerViewAdapter adapter;
    LoadImagesFromReddit loadImagesFromRedditTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageModel = ImageModel.getInstance();
        imageModel.addObserver(this);

        loadImages = (ImageView) findViewById(R.id.toolbar_load);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new GridLayoutManager(this, 2);
        }
        else{
            layoutManager = new LinearLayoutManager(this);
        }
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerViewAdapter();
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        loadImages.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose a Subreddit");

                final EditText input = new EditText(MainActivity.this);

                builder.setView(input);
                builder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                String subreddit = input.getText().toString();
                                if (loadImagesFromRedditTask != null) {
                                    System.out.println(loadImagesFromRedditTask.getStatus());
                                    if (loadImagesFromRedditTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
                                        System.out.println(loadImagesFromRedditTask.getStatus());
                                        loadImagesFromRedditTask = new LoadImagesFromReddit(subreddit, "top");
                                    }
                                    if (loadImagesFromRedditTask.getStatus().equals(AsyncTask.Status.PENDING)) {
                                        loadImagesFromRedditTask.execute();
                                    }
                                } else {
                                    loadImagesFromRedditTask = new LoadImagesFromReddit(subreddit, "top");
                                    loadImagesFromRedditTask.execute();
                                }
                            }
                        });

                builder.show();
            }
        });

        clearImages = (ImageView) findViewById(R.id.toolbar_remove);

        clearImages.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("clearing images");
                imageModel.clearImages();
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });

        // Init observers
        imageModel.initObservers();
    }

    @Override
    public void onImageClick(View view, int position) {
        // refered to class lecture slides for how to do this
        // transition to next screen
        Intent intent = new Intent(this, ViewImageActivity.class);
        intent.putExtra("position", String.valueOf(position));

        // Start activity
        startActivity(intent);
    }

    @Override
    public void onHrefClick(View view, int position) {
        String href = imageModel.getImageWithDetails(position).getHref();
        Uri webpage = Uri.parse(href);
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
        startActivity(webIntent);
    }

    private String downloadImage(Bitmap imageBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, "title", null);
    }

    @Override
    public void onDownloadClick(View view, int position) {
        Bitmap imageBitmap = imageModel.getImageWithDetails(position).getImage();
        String path = downloadImage(imageBitmap);
        System.out.println(path);
    }

    @Override
    public void onSetWallpaper(View view, int position) {
        Bitmap imageBitmap = imageModel.getImageWithDetails(position).getImage();
        WallpaperManager wm = WallpaperManager.getInstance(this);
        Uri uri = null;
        try {
            String path = downloadImage(imageBitmap);
            uri = Uri.parse(path);
            System.out.println(getContentResolver().getType(uri));
            Intent intent = wm.getCropAndSetWallpaperIntent(uri);

            startActivity(intent);
            getContentResolver().delete(uri, null,null);
            return;
        } catch (IllegalArgumentException e) {
            if (uri != null) {
                getContentResolver().delete(uri, null, null);
            }
            // pass
        }
        try {
            wm.setBitmap(imageBitmap);
        } catch (IOException e) {
            System.out.println("Cannot set image as wallpaper");
        }
    }

    @Override
    public void update(Observable o, Object arg)
    {
        recyclerView.post(new Runnable()
        {
            @Override
            public void run() {
                recyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    private class LoadImagesFromReddit extends AsyncTask<Void, Void, Void> {

        private String redditUrl = "https://www.reddit.com";
        private String path;
        private String modifier;

        protected LoadImagesFromReddit(String subreddit, String modifier) {
            this.path = "/r/" + subreddit;
            this.modifier = "/" + modifier;
        }

        @Override
        protected Void doInBackground(Void... x) {
            ArrayList<ImageDetails> imagesWithDetails = new ArrayList<>(10);

            //Connect to website
            List<String> imageUrls = new ArrayList<>();
            List<String> hrefs = new ArrayList<>();

            try {
                Document document = Jsoup.connect(redditUrl + path + modifier).get();
                System.out.println(redditUrl + path + modifier);
                Elements imageElements = document.getElementsByTag("img");
                for (Element element : imageElements) {
                    String src = element.attr("src");
                    if (src.contains("preview.redd.it") || src.contains("external-preview")) {
                        Element parent = element.parent().parent().parent();
                        String href = parent.attr("href");
                        if (href.toLowerCase().startsWith(path.toLowerCase())) {
                            System.out.println("image source: " + src);
                            System.out.println(href);
                            imageUrls.add(src);
                            hrefs.add(href);
                        }
                    }
                }
            } catch (IOException e) {
                // do nothing
            }

            // download the image
            for (int i = 0; i < imageUrls.size(); i++) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new URL(imageUrls.get(i)).openStream());
                    imagesWithDetails.add(new ImageDetails(bitmap, Uri.parse(imageUrls.get(i)), redditUrl + hrefs.get(i)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            imageModel.setImages(imagesWithDetails);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            recyclerView.post(new Runnable()
            {
                @Override
                public void run() {
                    recyclerView.getAdapter().notifyDataSetChanged();
                }
            });
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }
    }
}

