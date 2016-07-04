package com.example.hongyu.remote_music_player5.VO;

import java.io.Serializable;

/**
 * Created by hongyu on 16/6/8.
 */
public class MusicInfo implements Serializable {
    private String id;
    private String musicName;
    private String artist;
    private String albums;
    private String thumbnail;
    private String musicPath;

    public MusicInfo(String musicName, String artist, String albums, String thumbnail, String musicPath) {
        this.musicName = musicName;
        this.artist = artist;
        this.albums = albums;
        this.thumbnail = thumbnail;
        this.musicPath = musicPath;
    }

    public MusicInfo(String musicName, String musicPath) {
        this.musicPath = musicPath;
        this.musicName = musicName;
    }

    public MusicInfo() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MusicInfo)) return false;

        MusicInfo musicInfo = (MusicInfo) o;

        if (!getMusicName().equals(musicInfo.getMusicName())) return false;
        return getMusicPath().equals(musicInfo.getMusicPath());

    }

    @Override
    public int hashCode() {
        int result = getMusicName().hashCode();
        result = 31 * result + getMusicPath().hashCode();
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbums() {
        return albums;
    }

    public void setAlbums(String albums) {
        this.albums = albums;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }
}

