package net.iGap.module.api.beepTunes;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Album {

    @Expose
    private List<Artist> artists;
    @Expose
    private String contentType;
    @Expose
    private Long created;
    @Expose
    private String englishName;
    @Expose
    private Long finalPrice;
    @Expose
    private Genre genre;
    @Expose
    private Long id;
    @Expose
    private String image;
    @Expose
    private Long likes;
    @Expose
    private String name;
    @Expose
    private Long preSalePrice;
    @Expose
    private Long specialOfferPrice;
    @Expose
    private String status;
    @Expose
    private String type;

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public Long getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(Long finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPreSalePrice() {
        return preSalePrice;
    }

    public void setPreSalePrice(Long preSalePrice) {
        this.preSalePrice = preSalePrice;
    }

    public Long getSpecialOfferPrice() {
        return specialOfferPrice;
    }

    public void setSpecialOfferPrice(Long specialOfferPrice) {
        this.specialOfferPrice = specialOfferPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
