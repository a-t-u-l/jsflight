package com.focusit.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.focusit.jsflight.player.config.Configuration;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Playing process state
 *
 * Created by dkirpichenkov on 29.04.16.
 */
@Document
public class Experiment {

    @Id
    private ObjectId id;

    @Indexed
    private Date created;

    @Indexed
    private ObjectId recordingId;

    @Indexed
    private String recordingName;

    private Configuration configuration;

    private String tag;

    private String tagHash;

    private String selectQuery = "";

    private Boolean screenshots = false;

    private int position = 0;

    private int limit = 0;

    private int steps = 0;

    private Boolean playing=false;

    private Boolean error=false;

    private String errorMessage="";

    public String getId() {
        return id.toString();
    }

    public void setId(String id) {
        this.id = new ObjectId(id);
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getRecordingId() {
        return recordingId.toString();
    }

    public void setRecordingId(String recordingId) {
        this.recordingId = new ObjectId(recordingId);
    }

    public void setRecordingId(ObjectId recordingId) {
        this.recordingId = recordingId;
    }

    public String getRecordingName() {
        return recordingName;
    }

    public void setRecordingName(String recordingName) {
        this.recordingName = recordingName;
    }

    public String getSelectQuery() {
        return selectQuery;
    }

    public void setSelectQuery(String selectQuery) {
        this.selectQuery = selectQuery;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Boolean getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(Boolean screenshots) {
        this.screenshots = screenshots;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTagHash() {
        return tagHash;
    }

    public void setTagHash(String tagHash) {
        this.tagHash = tagHash;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Boolean getPlaying() {
        return playing;
    }

    public void setPlaying(Boolean playing) {
        this.playing = playing;
    }

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}
