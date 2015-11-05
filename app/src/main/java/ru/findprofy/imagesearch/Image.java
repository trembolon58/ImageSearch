package ru.findprofy.imagesearch;

/**
 * класс хранит в себе информацию о картинке
 * width height не используются
 * так же можно расширить класс для хранения большего количества информации,
 * которая приходит с ответом сервера
 */
public class Image {

    private String thumbnail;
    private int width;
    private int height;
    private String src;

    public Image(int width, int height, String thumbnail, String src) {
        this.thumbnail = thumbnail;
        this.height = height;
        this.width = width;
        this.src = src;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getSrc() {
        return src;
    }
}
