package com.advantest.myapplication.dto;

/**
 * This is POJO class
 *
 * @author darshan.rudresh
 */
public class FileInfo {

    boolean result;
    String buildId;
    String name;
    long created;
    String target;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "result=" + result +
                ", buildId='" + buildId + '\'' +
                ", name='" + name + '\'' +
                ", created=" + created +
                ", target='" + target + '\'' +
                '}';
    }
}
