package com.example.project;

public class Place {

    private String title;
    private String latitude;
    private String longitude;
    private String description;
    private String email;
    private String stars;
    private String zoom;
    private String currentPhotoPath;
    private long id;

    /**Constructor:*/
    public Place(String atitle, String alatitude, String alongitude, String adescription, String aemail, String astars, String azoom, String aCurrentPhotoPath, long i)
    {
        title = atitle;
        latitude = alatitude;
        longitude = alongitude;
        description = adescription;
        email = aemail;
        stars = astars;
        zoom = azoom;
        currentPhotoPath = aCurrentPhotoPath;
        id = i;
    }

    public String getTitle() {
        return title;
    }
    public String getLatitude() {
        return latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public String getDescription() {
        return description;
    }
    public String getEmail() {
        return email;
    }
    public String getStars() {
        return stars;
    }
    public String getZoom() {
        return zoom;
    }
    public String getCurrentPhotoPath() { return currentPhotoPath; }
    public long getId() {
        return id;
    }

}