package com.kevinwang.redditwallpaper;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

class ImageModel extends Observable
{
    // Create static instance of this mModel
    private static final ImageModel instance = new ImageModel();
    static ImageModel getInstance()
    {
        return instance;
    }

    // Private Variables
    private ArrayList<ImageDetails> imageDetails;

    ImageModel() {
        this.imageDetails = new ArrayList(10);
    }

    public ImageDetails getImageWithDetails(int index) {
        return imageDetails.get(index);
    }

    public void setImages(ArrayList imagesWithDetails) {
        this.imageDetails = imagesWithDetails;
        notifyObservers();
    }

    public void clearImages() {
        imageDetails.clear();
        notifyObservers();
    }

    public int getNumImages() {
        return imageDetails.size();
    }

    /**
     * Helper method to make it easier to initialize all observers
     */
    public void initObservers()
    {
        setChanged();
        notifyObservers();
    }

    /**
     * Deletes an observer from the set of observers of this object.
     * Passing <CODE>null</CODE> to this method will have no effect.
     *
     * @param o the observer to be deleted.
     */
    @Override
    public synchronized void deleteObserver(Observer o)
    {
        super.deleteObserver(o);
    }

    /**
     * Adds an observer to the set of observers for this object, provided
     * that it is not the same as some observer already in the set.
     * The order in which notifications will be delivered to multiple
     * observers is not specified. See the class comment.
     *
     * @param o an observer to be added.
     * @throws NullPointerException if the parameter o is null.
     */
    @Override
    public synchronized void addObserver(Observer o)
    {
        super.addObserver(o);
    }

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    @Override
    public synchronized void deleteObservers()
    {
        super.deleteObservers();
    }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to
     * indicate that this object has no longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and <code>null</code>. In other
     * words, this method is equivalent to:
     * <blockquote><tt>
     * notifyObservers(null)</tt></blockquote>
     *
     * @see Observable#clearChanged()
     * @see Observable#hasChanged()
     * @see Observer#update(Observable, Object)
     */
    @Override
    public void notifyObservers()
    {
        super.notifyObservers();
    }
}
