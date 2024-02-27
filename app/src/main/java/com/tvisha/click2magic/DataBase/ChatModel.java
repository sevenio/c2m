package com.tvisha.click2magic.DataBase;

import android.os.Parcel;
import android.os.Parcelable;

import com.tvisha.click2magic.api.post.model.ActiveChat;
import com.tvisha.click2magic.api.post.model.Data;

public class ChatModel implements Parcelable {



     public  int id ;
     public  int sender_id=0 ;
     public  int receiver_id =0;
     public  int message_type ;
     public  int message_id ;
     public  int is_group;
     public  int is_sync ;
     public  int is_reply;
     public  int original_message_id;
     public  int is_read;
     public  int is_delivered;
     public  int is_received;
     public  int is_downloaded;

     public  String conversation_reference_id;
     public String original_message;
     public String caption;
     public String message;
     public String reference_id ;
     public String attachment;
     public  String attachment_extension;
     public  String attachment_name;
     public String created_at;
     public String name="";


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String downloadProgress;
    public String attachmentDevicePath;
    public boolean isAttachmentDownloaded=false;
    public boolean showBackGround=false;

    protected ChatModel(Parcel in) {
        this.id = ((int) in.readValue((int.class.getClassLoader())));
        this.sender_id = ((int) in.readValue((int.class.getClassLoader())));
        this.receiver_id = ((int) in.readValue((int.class.getClassLoader())));
        this.message_type = ((int) in.readValue((int.class.getClassLoader())));
        this.message_id = ((int) in.readValue((int.class.getClassLoader())));
        this.is_group = ((int) in.readValue((int.class.getClassLoader())));
        this.is_sync = ((int) in.readValue((int.class.getClassLoader())));
        this.is_reply = ((int) in.readValue((int.class.getClassLoader())));
        this.original_message_id = ((int) in.readValue((int.class.getClassLoader())));
        this.is_read = ((int) in.readValue((int.class.getClassLoader())));
        this.is_delivered = ((int) in.readValue((int.class.getClassLoader())));
        this.is_received = ((int) in.readValue((int.class.getClassLoader())));
        this.is_downloaded = ((int) in.readValue((int.class.getClassLoader())));
        this.conversation_reference_id = ((String) in.readValue((String.class.getClassLoader())));
        this.original_message = ((String) in.readValue((String.class.getClassLoader())));
        this.caption = ((String) in.readValue((String.class.getClassLoader())));
        this.message = ((String) in.readValue((String.class.getClassLoader())));
        this.reference_id = ((String) in.readValue((String.class.getClassLoader())));
        this.attachment = ((String) in.readValue((String.class.getClassLoader())));
        this.attachment_extension = ((String) in.readValue((String.class.getClassLoader())));
        this.attachment_name = ((String) in.readValue((String.class.getClassLoader())));
        this.created_at = ((String) in.readValue((String.class.getClassLoader())));
        this.downloadProgress = ((String) in.readValue((String.class.getClassLoader())));
        this.attachmentDevicePath = ((String) in.readValue((String.class.getClassLoader())));
        this.isAttachmentDownloaded = ((boolean) in.readValue((boolean.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.showBackGround = ((boolean) in.readValue((boolean.class.getClassLoader())));


    }

    public boolean isShowBackGround() {
        return showBackGround;
    }

    public void setShowBackGround(boolean showBackGround) {
        this.showBackGround = showBackGround;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(sender_id);
        dest.writeValue(receiver_id);
        dest.writeValue(message_type);
        dest.writeValue(message_id);
        dest.writeValue(is_group);
        dest.writeValue(is_sync);
        dest.writeValue(is_reply);

        dest.writeValue(original_message_id);
        dest.writeValue(is_read);
        dest.writeValue(is_delivered);
        dest.writeValue(is_received);
        dest.writeValue(is_downloaded);
        dest.writeValue(conversation_reference_id);
        dest.writeValue(original_message);
        dest.writeValue(caption);
        dest.writeValue(message);
        dest.writeValue(reference_id);
        dest.writeValue(attachment);
        dest.writeValue(attachment_extension);
        dest.writeValue(attachment_name);
        dest.writeValue(created_at);
        dest.writeValue(downloadProgress);
        dest.writeValue(attachmentDevicePath);
        dest.writeValue(isAttachmentDownloaded);
        dest.writeValue(name);
        dest.writeValue(showBackGround);
    }


    @Override
    public String toString() {
        return "ChatModel{" +
                "id=" + id +
                ", sender_id=" + sender_id +

                ", receiver_id=" + receiver_id +
                ", message_type=" + message_type +
                ", message_id=" + message_id +
                ", is_group=" + is_group +
                ", is_sync=" + is_sync +
                ", is_reply=" + is_reply +
                ", original_message_id=" + original_message_id +
                ", is_read=" + is_read +
                ", is_delivered=" + is_delivered +
                ", is_received=" + is_received +
                ", is_downloaded=" + is_downloaded +
                ", conversation_reference_id='" + conversation_reference_id + '\'' +
                ", original_message='" + original_message + '\'' +
                ", caption='" + caption + '\'' +
                ", message='" + message + '\'' +
                ", reference_id='" + reference_id + '\'' +
                ", attachment='" + attachment + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }

    public ChatModel() {
    }
    public final static Parcelable.Creator<ChatModel> CREATOR = new Creator<ChatModel>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ChatModel createFromParcel(Parcel in) {
            return new ChatModel(in);
        }

        public ChatModel[] newArray(int size) {
            return (new ChatModel[size]);
        }

    };



    @Override
    public int describeContents() {
        return 0;
    }

    public void setIs_received(int is_received) {
        this.is_received = is_received;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }



    public void setAttachmentDownloaded(boolean attachmentDownloaded) {
        isAttachmentDownloaded = attachmentDownloaded;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public int getMessage_type() {
        return message_type;
    }

    public void setMessage_type(int message_type) {
        this.message_type = message_type;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
    }

    public String getReference_id() {
        return reference_id;
    }

    public void setReference_id(String reference_id) {
        this.reference_id = reference_id;
    }

    public int getIs_group() {
        return is_group;
    }

    public void setIs_group(int is_group) {
        this.is_group = is_group;
    }

    public int getIs_sync() {
        return is_sync;
    }

    public void setIs_sync(int is_sync) {
        this.is_sync = is_sync;
    }

    public int getIs_reply() {
        return is_reply;
    }

    public void setIs_reply(int is_reply) {
        this.is_reply = is_reply;
    }

    public int getOriginal_message_id() {
        return original_message_id;
    }

    public void setOriginal_message_id(int original_message_id) {
        this.original_message_id = original_message_id;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public int getIs_delivered() {
        return is_delivered;
    }

    public void setIs_delivered(int is_delivered) {
        this.is_delivered = is_delivered;
    }

    public int getIs_downloaded() {
        return is_downloaded;
    }

    public void setIs_downloaded(int is_downloaded) {
        this.is_downloaded = is_downloaded;
    }

    public String getOriginal_message() {
        return original_message;
    }

    public void setOriginal_message(String original_message) {
        this.original_message = original_message;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getConversation_reference_id() {
        return conversation_reference_id;
    }

    public void setConversation_reference_id(String conversation_reference_id) {
        this.conversation_reference_id = conversation_reference_id;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public int getIs_received() {
        return is_received;
    }

    public String getAttachment_extension() {
        return attachment_extension;
    }

    public boolean isAttachmentDownloaded() {
        return isAttachmentDownloaded;
    }

    public void setAttachment_extension(String attachment_extension) {

        this.attachment_extension = attachment_extension;
    }

    public String getAttachment_name() {
        return attachment_name;
    }

    public void setAttachment_name(String attachment_name) {
        this.attachment_name = attachment_name;
    }
    public String getDownloadProgress() {
        return downloadProgress;
    }

    public void setDownloadProgress(String downloadProgress) {
        this.downloadProgress = downloadProgress;
    }

    public String getAttachmentDevicePath() {
        return attachmentDevicePath;
    }

    public void setAttachmentDevicePath(String attachmentDevicePath) {
        this.attachmentDevicePath = attachmentDevicePath;
    }


}



