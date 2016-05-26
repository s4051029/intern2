package com.mirrorchannelth.internship.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by boss on 5/22/16.
 */
public class Image implements Parcelable {

        private String id = null;
        private String url = null;
        private String protocol = null;
        private Uri uri;
        private String mediaType;

    protected Image(Parcel in) {
        id = in.readString();
        url = in.readString();
        protocol = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
        mediaType = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Image(String url) {
            this.setUrl(url);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(protocol);
        dest.writeParcelable(uri, flags);
        dest.writeString(mediaType);
    }
}
