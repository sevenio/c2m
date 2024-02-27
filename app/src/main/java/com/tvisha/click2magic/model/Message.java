package com.tvisha.click2magic.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Message {

    @SerializedName("conversation_reference_id")
    @Expose
    private String conversation_reference_id;
    @SerializedName("message_id")
    @Expose
    private int message_id;
    @SerializedName("sender_id")
    @Expose
    private int sender_id;
    @SerializedName("receiver_id")
    @Expose
    private int receiver_id;
    @SerializedName("message_type")
    @Expose
    private int message_type;
    @SerializedName("is_reply")
    @Expose
    private int is_reply;
    @SerializedName("original_message_id")
    @Expose
    private int original_message_id;
    @SerializedName("message")
    @Expose
    private Object message;
    @SerializedName("attachment")
    @Expose
    private String attachment;
    @SerializedName("attachment_name")
    @Expose
    private String attachment_name;
    @SerializedName("attachment_extension")
    @Expose
    private String attachment_extension;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("is_read")
    @Expose
    private int is_read;
    @SerializedName("is_delivered")
    @Expose
    private int is_delivered;
    @SerializedName("is_group")
    @Expose
    private int is_group;
    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("row_number")
    @Expose
    private int row_number;
    @SerializedName("dummy")
    @Expose
    private int dummy;
    @SerializedName("group_member_status")
    @Expose
    private List<Object> group_member_status = null;
    @SerializedName("reference_id")
    @Expose
    private String reference_id;

    public String getReference_id() {
        return reference_id;
    }

    public void setReference_id(String reference_id) {
        this.reference_id = reference_id;
    }

    public String getConversation_reference_id() {
        return conversation_reference_id;
    }

    public void setConversation_reference_id(String conversation_reference_id) {
        this.conversation_reference_id = conversation_reference_id;
    }

    public int getMessage_id() {
        return message_id;
    }

    public void setMessage_id(int message_id) {
        this.message_id = message_id;
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

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
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

    public int getIs_group() {
        return is_group;
    }

    public void setIs_group(int is_group) {
        this.is_group = is_group;
    }

    public String getAttachment_name() {
        return attachment_name;
    }

    public void setAttachment_name(String attachment_name) {
        this.attachment_name = attachment_name;
    }

    public String getAttachment_extension() {
        return attachment_extension;
    }

    public void setAttachment_extension(String attachment_extension) {
        this.attachment_extension = attachment_extension;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getRow_number() {
        return row_number;
    }

    public void setRow_number(int row_number) {
        this.row_number = row_number;
    }

    public int getDummy() {
        return dummy;
    }

    public void setDummy(int dummy) {
        this.dummy = dummy;
    }

    public List<Object> getGroup_member_status() {
        return group_member_status;
    }

    public void setGroup_member_status(List<Object> group_member_status) {
        this.group_member_status = group_member_status;
    }

}