package com.tvisha.click2magic.api.post.model;

/*public class SiteAssetData {
}*/
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SiteAssetData implements Parcelable
{

    @SerializedName("links")
    @Expose
    private List<Link> links = null;
    @SerializedName("collateral")
    @Expose
    private List<Collateral> collateral = null;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;
    @SerializedName("canned_responses")
    @Expose
    private List<CannedResponse> cannedResponses = null;
    public final static Parcelable.Creator<Data> CREATOR = new Creator<Data>() {


        @SuppressWarnings({
                "unchecked"
        })
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        public Data[] newArray(int size) {
            return (new Data[size]);
        }

    }
            ;

    protected SiteAssetData(Parcel in) {
        in.readList(this.links, (Link.class.getClassLoader()));
        in.readList(this.collateral, (Collateral.class.getClassLoader()));
        in.readList(this.images, (Image.class.getClassLoader()));
        in.readList(this.cannedResponses, (CannedResponse.class.getClassLoader()));
    }

    public SiteAssetData() {
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public List<Collateral> getCollateral() {
        return collateral;
    }

    public void setCollateral(List<Collateral> collateral) {
        this.collateral = collateral;
    }

    public List<Image> getImages() {
        return images;
    }



    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<CannedResponse> getCannedResponses() {
        return cannedResponses;
    }

    public void setCannedResponses(List<CannedResponse> cannedResponses) {
        this.cannedResponses = cannedResponses;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(links);
        dest.writeList(collateral);
        dest.writeList(images);
        dest.writeList(cannedResponses);
    }

    public int describeContents() {
        return 0;
    }

}
