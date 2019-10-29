package com.kevinwang.redditwallpaper;

import android.graphics.Bitmap;
import android.net.Uri;

public class ImageDetails {
    private Bitmap image;
    private Uri imageUri;
    private String href;

    ImageDetails(Bitmap image, Uri imageUri, String href) {
        this.image = image;
        this.imageUri = imageUri;
        this.href = href;
    }

    public Bitmap getImage() {
        return image;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public String getHref() {
        return href;
    }
}
