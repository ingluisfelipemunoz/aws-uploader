package com.uploader.aws_uploader.profile;

import com.uploader.aws_uploader.bucket.BucketName;
import com.uploader.aws_uploader.filestore.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserProfileService {
    private final UserProfileDataAccessService userProfileDataAccessService;
    private final FileStore fileStore;
    @Autowired
    public UserProfileService(UserProfileDataAccessService userProfileDataAccessService, FileStore fileStore) {
        this.userProfileDataAccessService = userProfileDataAccessService;
        this.fileStore = fileStore;
    }

    List<UserProfile> getUserProfiles() {
        return userProfileDataAccessService.getUserProfiles();
    }

    public void uploadUserProfileImage(UUID id, MultipartFile file) {
        System.out.println("id " + id + " - " + file.getOriginalFilename());
        // 1. Check if image is not empty
        isEmpty(file);
        // 2. If file is an image
        isImage(file);
        // 3. The user exists in our database
        UserProfile profile = getUserProfile(id);

        // 4. Grab some metadata from file if any
        Map<String, String> metadata = getMetadata(file);
        // 5. Store the image in s3 and update database (userProfileImageLink) with s3 image link
        String path = String.format("%s/%s", BucketName.PROFILE_IMAGE.getBucketName(), id);
        String fileName = String.format("%s-%s", file.getOriginalFilename(), UUID.randomUUID());
        String fullFileName = profile.getId() + "/" + fileName;
        try {
            fileStore.save2(fullFileName, Optional.of(metadata), new ByteArrayInputStream(file.getBytes()));
            profile.setUserProfileImageLink(fileName);
        } catch(Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private UserProfile getUserProfile(UUID id) {
        List<UserProfile> userProfiles = userProfileDataAccessService.getUserProfiles();
        int a = 0;
        return userProfileDataAccessService.getUserProfiles().stream().filter(userProfile -> userProfile.getId().equals(id)).findFirst().orElseThrow(() -> new IllegalStateException("User not found"));
    }

    private static Map<String, String> getMetadata(MultipartFile file) {
        Map<String, String> metadata = Map.of(
                "Content-Type", file.getContentType(),
                "Content-Length", String.valueOf(file.getSize())
        );
        return metadata;
    }

    private static void isImage(MultipartFile file) {
        if(!file.getContentType().startsWith("image/")) {
            throw new IllegalStateException("File must be an image [" + file.getContentType() + "]");
        }
    }

    private static void isEmpty(MultipartFile file) {
        if(file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file [" + file.getSize() + "]");
        }
    }

    public byte[] downloadUserProfileImage(UUID id) {
        UserProfile profile = getUserProfile(id);
        return profile.getUserProfileImageLink().map(key -> fileStore.download(BucketName.PROFILE_IMAGE.getBucketName(), String.format("%s/%s", profile.getId(), key))).orElse(new byte[0]);
    }
}
