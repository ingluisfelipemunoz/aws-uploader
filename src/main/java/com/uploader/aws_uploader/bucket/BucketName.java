package com.uploader.aws_uploader.bucket;

public enum BucketName {
    PROFILE_IMAGE("awsuploader-spring");
    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
