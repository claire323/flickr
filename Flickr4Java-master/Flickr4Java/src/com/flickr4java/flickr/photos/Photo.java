/*
 * Copyright (c) 2005 Aetrion LLC.
 */
package com.flickr4java.flickr.photos;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import javax.imageio.ImageIO;

import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.stats.Stats;
import com.flickr4java.flickr.stats.StatsInterface;
import com.flickr4java.flickr.tags.Tag;
import com.flickr4java.flickr.util.IOUtilities;
import com.flickr4java.flickr.util.StringUtilities;

/**
 * Class representing metadata about a Flickr photo. Instances do not actually contain the photo data, however you can obtain the photo data by calling
 * {@link PhotosInterface#getImage(Photo, int)} or {@link PhotosInterface#getImageAsStream(Photo, int)}.
 * 
 * @author Anthony Eden
 * @version $Id: Photo.java,v 1.28 2009/07/23 21:49:35 x-mago Exp $
 */
public class Photo {

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATS = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected synchronized SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private static final String DEFAULT_ORIGINAL_IMAGE_SUFFIX = "_o.jpg";

    private static final String SMALL_SQUARE_IMAGE_SUFFIX = "_s.jpg";

    private static final String SMALL_IMAGE_SUFFIX = "_m.jpg";

    private static final String THUMBNAIL_IMAGE_SUFFIX = "_t.jpg";

    private static final String MEDIUM_IMAGE_SUFFIX = ".jpg";

    private static final String LARGE_IMAGE_SUFFIX = "_b.jpg";

    private static final String SQUARE_LARGE_IMAGE_SUFFIX = "_q.jpg";

    private static final String SQUARE_320_IMAGE_SUFFIX = "_n.jpg";

    private static final String MEDIUM_640_IMAGE_SUFFIX = "_z.jpg";

    private static final String MEDIUM_800_IMAGE_SUFFIX = "_c.jpg";

    private Size squareSize;

    private Size smallSize;

    private Size thumbnailSize;

    private Size mediumSize;

    private Size largeSize;

    private Size originalSize;

    private Size squareLargeSize;

    private Size small320Size;

    private Size medium640Size;

    private Size medium800Size;

    private String id;

    private User owner;

    private String secret;

    private String farm;

    private String server;

    private boolean favorite;

    private String license;

    private boolean primary;

    private String title;

    private String description;

    private boolean publicFlag;

    private boolean friendFlag;

    private boolean familyFlag;

    private Date dateAdded;

    private Date datePosted;

    private Date dateTaken;

    private Date lastUpdate;

    private String takenGranularity;

    private Permissions permissions;

    private Editability editability;

    private int comments;

    private int views = -1;

    private int rotation;

    private Collection<Note> notes;

    private Collection<Tag> tags;

    private Collection<String> urls;

    private String iconServer;

    private String iconFarm;

    private String url;

    private GeoData geoData;

    private String originalFormat;

    private String originalSecret;

    private String placeId;

    private String media;

    private String mediaStatus;

    private String pathAlias;

    private int originalWidth;

    private int originalHeight;

    /**
     * Stats on views, comments and favorites. Only set on {@link StatsInterface#getPopularPhotos} call.
     */
    private Stats stats;

    public Photo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getFarm() {
        return farm;
    }

    public void setFarm(String farm) {
        this.farm = farm;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public void setPrimary(String primary) {
        setPrimary("1".equals(primary));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPublicFlag() {
        return publicFlag;
    }

    public void setPublicFlag(boolean publicFlag) {
        this.publicFlag = publicFlag;
    }

    public boolean isFriendFlag() {
        return friendFlag;
    }

    public void setFriendFlag(boolean friendFlag) {
        this.friendFlag = friendFlag;
    }

    public boolean isFamilyFlag() {
        return familyFlag;
    }

    public void setFamilyFlag(boolean familyFlag) {
        this.familyFlag = familyFlag;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        setDateAdded(new Date(dateAdded));
    }

    public void setDateAdded(String dateAdded) {
        if (dateAdded == null || "".equals(dateAdded)) {
            return;
        }
        setDateAdded(Long.parseLong(dateAdded) * 1000);
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public void setDatePosted(long datePosted) {
        setDatePosted(new Date(datePosted));
    }

    public void setDatePosted(String datePosted) {
        if (datePosted == null || "".equals(datePosted)) {
            return;
        }
        setDatePosted(Long.parseLong(datePosted) * 1000);
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        if (dateTaken == null || "".equals(dateTaken)) {
            return;
        }
        try {
            setDateTaken(((DateFormat) DATE_FORMATS.get()).parse(dateTaken));
        } catch (ParseException e) {
            // TODO: figure out what to do with this error
            e.printStackTrace();
        }
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setLastUpdate(String lastUpdateStr) {
        if (lastUpdateStr == null || "".equals(lastUpdateStr)) {
            return;
        }
        long unixTime = Long.parseLong(lastUpdateStr);
        setLastUpdate(new Date(unixTime * 1000L));
    }

    public String getTakenGranularity() {
        return takenGranularity;
    }

    public void setTakenGranularity(String takenGranularity) {
        this.takenGranularity = takenGranularity;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    public Editability getEditability() {
        return editability;
    }

    public void setEditability(Editability editability) {
        this.editability = editability;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public void setComments(String comments) {
        if (comments != null) {
            setComments(Integer.parseInt(comments));
        }
    }

    public Collection<Note> getNotes() {
        return notes;
    }

    public void setNotes(Collection<Note> notes) {
        this.notes = notes;
    }

    public Collection<Tag> getTags() {
        return tags;
    }

    public void setTags(Collection<Tag> tags) {
        this.tags = tags;
    }

    /**
     * 
     * @return List of {@link PhotoUrl}
     */
    public Collection<String> getUrls() {
        return urls;
    }

    /**
     * 
     * @param urls
     *            List of {@link PhotoUrl}
     */
    public void setUrls(Collection<String> urls) {
        this.urls = urls;
    }

    /**
     * Sets the number of views for this Photo. For un-authenticated calls this value is not available and will be set to -1.
     * 
     * @param views
     * @deprecated attribute no longer available
     */
    @Deprecated
    public void setViews(String views) {
        if (views != null) {
            try {
                setViews(Integer.parseInt(views));
            } catch (NumberFormatException e) {
                setViews(-1);
            }
        }
    }

    /**
     * 
     * @param views
     * @deprecated attribute no longer available
     */
    @Deprecated
    public void setViews(int views) {
        this.views = views;
    }

    /**
     * Number of views. Set to -1 if the value is not available.
     * 
     * @return Number of views
     * @deprecated attribute no longer available
     */
    @Deprecated
    public int getViews() {
        return views;
    }

    /**
     * Set the degrees of rotation. Value will be set to -1, if not available.
     * 
     * @param rotation
     */
    public void setRotation(String rotation) {
        if (rotation != null) {
            try {
                setRotation(Integer.parseInt(rotation));
            } catch (NumberFormatException e) {
                setRotation(-1);
            }
        }
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public int getRotation() {
        return rotation;
    }

    public String getIconServer() {
        return iconServer;
    }

    public void setIconServer(String iconServer) {
        this.iconServer = iconServer;
    }

    public String getIconFarm() {
        return iconFarm;
    }

    public void setIconFarm(String iconFarm) {
        this.iconFarm = iconFarm;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public GeoData getGeoData() {
        return geoData;
    }

    public void setGeoData(GeoData geoData) {
        this.geoData = geoData;
    }

    public boolean hasGeoData() {
        return geoData != null;
    }

    public String getOriginalFormat() {
        return originalFormat;
    }

    public void setOriginalFormat(String originalFormat) {
        this.originalFormat = originalFormat;
    }

    public String getOriginalSecret() {
        return originalSecret;
    }

    public void setOriginalSecret(String originalSecret) {
        this.originalSecret = originalSecret;
    }

    /**
     * @deprecated
     * @see PhotosInterface#getImage(Photo, int)
     * @return An Image
     * @throws IOException
     * @throws FlickrException
     */
    @Deprecated
    public BufferedImage getOriginalImage() throws IOException, FlickrException {
        if (originalFormat != null) {
            return getOriginalImage("_o." + originalFormat);
        }
        return getOriginalImage(DEFAULT_ORIGINAL_IMAGE_SUFFIX);
    }

    /**
     * Get an InputStream for the original image. Callers must close the stream upon completion.
     * 
     * @deprecated
     * @see PhotosInterface#getImageAsStream(Photo, int)
     * @return The InputStream
     * @throws IOException
     */
    @Deprecated
    public InputStream getOriginalAsStream() throws IOException, FlickrException {
        if (originalFormat != null) {
            return getOriginalImageAsStream("_o." + originalFormat);
        }
        return getOriginalImageAsStream(DEFAULT_ORIGINAL_IMAGE_SUFFIX);
    }

    /**
     * Get the original image URL.
     * 
     * @return The original image URL
     */
    public String getOriginalUrl() throws FlickrException {
        if (originalSize == null) {
            if (originalFormat != null) {
                return getOriginalBaseImageUrl() + "_o." + originalFormat;
            }
            return getOriginalBaseImageUrl() + DEFAULT_ORIGINAL_IMAGE_SUFFIX;
        } else {
            return originalSize.getSource();
        }
    }

    /**
     * Get an Image object which is a 75x75 pixel square.
     * 
     * @deprecated
     * @see PhotosInterface#getImage(Photo, int)
     * @return An Image
     * @throws IOException
     */
    @Deprecated
    public BufferedImage getSmallSquareImage() throws IOException {
        return getImage(SMALL_SQUARE_IMAGE_SUFFIX);
    }

    /**
     * @deprecated
     * @see PhotosInterface#getImageAsStream(Photo, int)
     * @return The InputStream
     * @throws IOException
     */
    @Deprecated
    public InputStream getSmallSquareAsInputStream() throws IOException {
        return getImageAsStream(SMALL_SQUARE_IMAGE_SUFFIX);
    }

    public String getSmallSquareUrl() {
        if (squareSize == null) {
            return getBaseImageUrl() + SMALL_SQUARE_IMAGE_SUFFIX;
        } else {
            return squareSize.getSource();
        }
    }

    /**
     * @deprecated
     * @see PhotosInterface#getImage(Photo, int)
     * @return An Image
     * @throws IOException
     */
    @Deprecated
    public BufferedImage getThumbnailImage() throws IOException {
        return getImage(THUMBNAIL_IMAGE_SUFFIX);
    }

    /**
     * @deprecated
     * @see PhotosInterface#getImageAsStream(Photo, int)
     * @return The InputStream
     * @throws IOException
     */
    @Deprecated
    public InputStream getThumbnailAsInputStream() throws IOException {
        return getImageAsStream(THUMBNAIL_IMAGE_SUFFIX);
    }

    public String getThumbnailUrl() {
        if (thumbnailSize == null) {
            return getBaseImageUrl() + THUMBNAIL_IMAGE_SUFFIX;
        } else {
            return thumbnailSize.getSource();
        }
    }

    /**
     * @deprecated
     * @see PhotosInterface#getImage(Photo, int)
     * @return An Image
     * @throws IOException
     */
    @Deprecated
    public BufferedImage getSmallImage() throws IOException {
        return getImage(SMALL_IMAGE_SUFFIX);
    }

    /**
     * @deprecated
     * @see PhotosInterface#getImageAsStream(Photo, int)
     * @return The InputStream
     * @throws IOException
     */
    @Deprecated
    public InputStream getSmallAsInputStream() throws IOException {
        return getImageAsStream(SMALL_IMAGE_SUFFIX);
    }

    public String getSmallUrl() {
        if (smallSize == null) {
            return getBaseImageUrl() + SMALL_IMAGE_SUFFIX;
        } else {
            return smallSize.getSource();
        }
    }

    /**
     * @deprecated
     * @see PhotosInterface#getImage(Photo, int)
     * @return An Image
     * @throws IOException
     */
    @Deprecated
    public BufferedImage getMediumImage() throws IOException {
        return getImage(MEDIUM_IMAGE_SUFFIX);
    }

    /**
     * @deprecated
     * @see PhotosInterface#getImageAsStream(Photo, int)
     * @return The InputStream
     * @throws IOException
     */
    @Deprecated
    public InputStream getMediumAsStream() throws IOException {
        return getImageAsStream(MEDIUM_IMAGE_SUFFIX);
    }

    public String getMediumUrl() {
        if (mediumSize == null) {
            return getBaseImageUrl() + MEDIUM_IMAGE_SUFFIX;
        } else {
            return mediumSize.getSource();
        }
    }

    /**
     * @deprecated
     * @see PhotosInterface#getImage(Photo, int)
     * @return An Image
     * @throws IOException
     */
    @Deprecated
    public BufferedImage getLargeImage() throws IOException {
        return getImage(LARGE_IMAGE_SUFFIX);
    }

    /**
     * @deprecated
     * @see PhotosInterface#getImageAsStream(Photo, int)
     * @return The InputStream
     * @throws IOException
     */
    @Deprecated
    public InputStream getLargeAsStream() throws IOException {
        return getImageAsStream(LARGE_IMAGE_SUFFIX);
    }

    public String getLargeUrl() {
        if (largeSize == null) {
            return getBaseImageUrl() + LARGE_IMAGE_SUFFIX;
        } else {
            return largeSize.getSource();
        }
    }

    public String getSquareLargeUrl() {
        if (squareLargeSize == null) {
            return getBaseImageUrl() + SQUARE_LARGE_IMAGE_SUFFIX;
        } else {
            return squareLargeSize.getSource();
        }
    }


    public String getSmall320Url() {
        if (small320Size == null) {
            return getBaseImageUrl() + SQUARE_320_IMAGE_SUFFIX;
        } else {
            return small320Size.getSource();
        }
    }


    public String getMedium640Url() {
        if (medium640Size == null) {
            return getBaseImageUrl() + MEDIUM_640_IMAGE_SUFFIX;
        } else {
            return medium640Size.getSource();
        }
    }


    public String getMedium800Url() {
        if (medium800Size == null) {
            return getBaseImageUrl() + MEDIUM_800_IMAGE_SUFFIX;
        } else {
            return medium800Size.getSource();
        }
    }

    /**
     * Get an image using the specified URL suffix.
     * 
     * @deprecated
     * @param suffix
     *            The URL suffix, including the .extension
     * @return The BufferedImage object
     * @throws IOException
     */
    @Deprecated
    private BufferedImage getImage(String suffix) throws IOException {
        StringBuffer buffer = getBaseImageUrl();
        buffer.append(suffix);
        return _getImage(buffer.toString());
    }

    /**
     * Get the original-image using the specified URL suffix.
     * 
     * @deprecated
     * @see PhotosInterface#getImage(Photo, int)
     * @param suffix
     *            The URL suffix, including the .extension
     * @return The BufferedImage object
     * @throws IOException
     * @throws FlickrException
     */
    @Deprecated
    private BufferedImage getOriginalImage(String suffix) throws IOException, FlickrException {
        StringBuffer buffer = getOriginalBaseImageUrl();
        buffer.append(suffix);
        return _getImage(buffer.toString());
    }

    /**
     * @deprecated
     * @param urlStr
     * @return BufferedImage
     * @throws IOException
     */
    @Deprecated
    private BufferedImage _getImage(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        InputStream in = null;
        try {
            in = conn.getInputStream();
            return ImageIO.read(in);
        } finally {
            IOUtilities.close(in);
        }
    }

    /**
     * Get an image as a stream. Callers must be sure to close the stream when they are done with it.
     * 
     * @deprecated
     * @see PhotosInterface#getImageAsStream(Photo, int)
     * @param suffix
     *            The suffix
     * @return The InputStream
     * @throws IOException
     */
    @Deprecated
    private InputStream getImageAsStream(String suffix) throws IOException {
        StringBuffer buffer = getBaseImageUrl();
        buffer.append(suffix);
        return _getImageAsStream(buffer.toString());
    }

    /**
     * 
     * @deprecated
     * @see PhotosInterface#getImageAsStream(Photo, int)
     * @param suffix
     * @return InoutStream
     * @throws IOException
     * @throws FlickrException
     */
    @Deprecated
    private InputStream getOriginalImageAsStream(String suffix) throws IOException, FlickrException {
        StringBuffer buffer = getOriginalBaseImageUrl();
        buffer.append(suffix);
        return _getImageAsStream(buffer.toString());
    }

    private InputStream _getImageAsStream(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();
        return conn.getInputStream();
    }

    private StringBuffer getBaseImageUrl() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(_getBaseImageUrl());
        buffer.append(getSecret());
        return buffer;
    }

    private StringBuffer getOriginalBaseImageUrl() throws FlickrException, NullPointerException {
        StringBuffer buffer = new StringBuffer();
        buffer.append(_getBaseImageUrl());
        if (getOriginalSecret().length() > 8) {
            buffer.append(getOriginalSecret());
        } else {
            throw new FlickrException("0", "OriginalUrl not available because of missing originalsecret.");
        }
        return buffer;
    }

    private StringBuffer _getBaseImageUrl() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("http://farm");
        buffer.append(getFarm());
        buffer.append(".static.flickr.com/");
        buffer.append(getServer());
        buffer.append("/");
        buffer.append(getId());
        buffer.append("_");
        return buffer;
    }

    /**
     * @return A placeId
     * @see com.flickr4java.flickr.places.PlacesInterface#resolvePlaceId(String)
     */
    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMediaStatus() {
        return mediaStatus;
    }

    public void setMediaStatus(String mediaStatus) {
        this.mediaStatus = mediaStatus;
    }

    public int getOriginalWidth() {
        return originalWidth;
    }

    public void setOriginalWidth(String originalWidth) {
        try {
            setOriginalWidth(Integer.parseInt(originalWidth));
        } catch (NumberFormatException ex) {
        }
    }

    public void setOriginalWidth(int originalWidth) {
        this.originalWidth = originalWidth;
    }

    public int getOriginalHeight() {
        return originalHeight;
    }

    public void setOriginalHeight(String originalHeight) {
        try {
            setOriginalHeight(Integer.parseInt(originalHeight));
        } catch (NumberFormatException ex) {
        }
    }

    public void setOriginalHeight(int originalHeight) {
        this.originalHeight = originalHeight;
    }

    /**
     * Set sizes to override the generated URLs of the different sizes.
     * 
     * @param sizes
     * @see com.flickr4java.flickr.photos.PhotosInterface#getSizes(String)
     */
    public void setSizes(Collection<Size> sizes) {
        for (Size size : sizes) {
            if (size.getLabel() == Size.SMALL) {
                smallSize = size;
            } else if (size.getLabel() == Size.SQUARE) {
                squareSize = size;
            } else if (size.getLabel() == Size.THUMB) {
                thumbnailSize = size;
            } else if (size.getLabel() == Size.MEDIUM) {
                mediumSize = size;
            } else if (size.getLabel() == Size.LARGE) {
                largeSize = size;
            } else if (size.getLabel() == Size.ORIGINAL) {
                originalSize = size;
            } else if (size.getLabel() == Size.SQUARE_LARGE) {
                squareLargeSize = size;
            } else if (size.getLabel() == Size.SMALL_320) {
                small320Size = size;
            } else if (size.getLabel() == Size.MEDIUM_640) {
                medium640Size = size;
            } else if (size.getLabel() == Size.MEDIUM_800) {
                medium800Size = size;
            }
        }
    }

    public Size getSquareSize() {
        return squareSize;
    }

    public Size getSmallSize() {
        return smallSize;
    }

    public Size getThumbnailSize() {
        return thumbnailSize;
    }

    public Size getMediumSize() {
        return mediumSize;
    }

    public Size getLargeSize() {
        return largeSize;
    }
    
    public Size getOriginalSize() {
        return originalSize;
    }

    public Size getSquareLargeSize() {
        return squareLargeSize;
    }

    public Size getSmall320Size() {
        return small320Size;
    }

    public Size getMedium640Size() {
        return medium640Size;
    }

    public Size getMedium800Size() {
        return medium800Size;
    }

    /**
     * @return the pathAlias
     */
    public String getPathAlias() {
        return pathAlias;
    }

    /**
     * @param pathAlias
     *            the pathAlias to set
     */
    public void setPathAlias(String pathAlias) {
        this.pathAlias = pathAlias;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     * @see <a href="http://www.ibm.com/developerworks/library/j-dyn0603/">http://www.ibm.com/developerworks/library/j-dyn0603/</a>
     */
    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        // object must be Photo at this point
        Photo test = (Photo) obj;
        Class cl = this.getClass();
        Method[] method = cl.getMethods();
        for (int i = 0; i < method.length; i++) {
            Matcher m = StringUtilities.getterPattern.matcher(method[i].getName());
            if (m.find() && !method[i].getName().equals("getClass")) {
                try {
                    Object res = method[i].invoke(this);
                    Object resTest = method[i].invoke(test);
                    String retType = method[i].getReturnType().toString();
                    if (retType.indexOf("class") == 0) {
                        if (res != null && resTest != null) {
                            // System.out.println("class: " + method[i].getName());
                            if (!res.equals(resTest)) {
                                return false;
                            }
                        } else {
                            if (res == null && resTest == null) {
                                // nop
                            } else if (res == null || resTest == null) {
                                // one is set and one is null
                                return false;
                            }
                        }
                    } else if (retType.equals("int")) {
                        if (!((Integer) res).equals(resTest)) {
                            return false;
                        }
                    } else if (retType.equals("boolean")) {
                        if (!((Boolean) res).equals(resTest)) {
                            return false;
                        }
                    } else if (retType.equals("interface java.util.Collection")) {
                        if (res != null && resTest != null) {
                            List col = (List) res;
                            List colTest = (List) resTest;
                            if (col.size() != colTest.size()) {
                                return false;
                            }
                            for (int j = 0; j < col.size(); j++) {
                                Object tobj1 = col.get(j);
                                Object tobj2 = colTest.get(j);
                                if (tobj1 instanceof Tag) {
                                    Tag tag1 = (Tag) tobj1;
                                    Tag tag2 = (Tag) tobj2;
                                    if (!tag1.equals(tag2)) {
                                        return false;
                                    }
                                } else if (tobj1 instanceof PhotoUrl) {
                                    PhotoUrl url1 = (PhotoUrl) tobj1;
                                    PhotoUrl url2 = (PhotoUrl) tobj2;
                                    if (!url1.equals(url2)) {
                                        return false;
                                    }
                                } else if (tobj1 instanceof Note) {
                                    Note note1 = (Note) tobj1;
                                    Note note2 = (Note) tobj2;
                                    if (!note1.equals(note2)) {
                                        return false;
                                    }
                                } else {
                                    System.out.println("Photo unhandled object: " + tobj1.getClass().getName());
                                }
                            }
                        } else {
                            if (res == null && resTest != null) {
                                return false;
                            }
                            if (res != null && resTest == null) {
                                return false;
                            }
                        }
                    } else {
                        System.out.println("Photo#equals() missing type " + method[i].getName() + "|" + method[i].getReturnType().toString());
                    }
                } catch (IllegalAccessException ex) {
                    System.out.println("equals " + method[i].getName() + " " + ex);
                } catch (InvocationTargetException ex) {
                    // System.out.println("equals " + method[i].getName() + " " + ex);
                } catch (Exception ex) {
                    System.out.println("equals " + method[i].getName() + " " + ex);
                }
            }
        }
        return true;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 1;
        Class cl = this.getClass();
        Method[] method = cl.getMethods();
        for (int i = 0; i < method.length; i++) {
            Matcher m = StringUtilities.getterPattern.matcher(method[i].getName());
            if (m.find() && !method[i].getName().equals("getClass")) {
                Object res = null;
                try {
                    res = method[i].invoke(this);
                } catch (IllegalAccessException ex) {
                    System.out.println("Photo hashCode " + method[i].getName() + " " + ex);
                } catch (InvocationTargetException ex) {
                    // System.out.println("hashCode " + method[i].getName() + " " + ex);
                }
                if (res != null) {
                    if (res instanceof Boolean) {
                        Boolean bool = (Boolean) res;
                        hash += bool.hashCode();
                    } else if (res instanceof Integer) {
                        Integer inte = (Integer) res;
                        hash += inte.hashCode();
                    } else if (res instanceof String) {
                        String str = (String) res;
                        hash += str.hashCode();
                    } else if (res instanceof Editability) {
                        Editability edit = (Editability) res;
                        hash += edit.hashCode();
                    } else if (res instanceof GeoData) {
                        GeoData edit = (GeoData) res;
                        hash += edit.hashCode();
                    } else if (res instanceof Size) {
                        Size size = (Size) res;
                        hash += size.hashCode();
                    } else if (res instanceof Permissions) {
                        Permissions perm = (Permissions) res;
                        hash += perm.hashCode();
                    } else if (res instanceof User) {
                        User user = (User) res;
                        hash += user.hashCode();
                    } else if (res instanceof ArrayList) {
                        ArrayList list = (ArrayList) res;
                        hash += list.hashCode();
                    } else {
                        System.out.println("Photo hashCode unrecognised object: " + res.getClass().getName());
                    }
                }
            }
        }
        return hash;
    }

}
