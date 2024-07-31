package com.uploader.aws_uploader.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static com.uploader.aws_uploader.datastore.FakeUserProfileDataStore.USER_PROFILES;

@RestController
@RequestMapping("api/v1/user-profile")
public class UserProfileController {

    private final UserProfileService userProfileService;
    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public List<UserProfile> getUserProfiles() {
        return userProfileService.getUserProfiles();
    }
    @PostMapping(
            consumes = "multipart/form-data",
            produces = "application/json",
            path = "{userProfileId}/image/upload"
    )
    public void uploadUserProfileImage(@PathVariable("userProfileId") UUID id, @RequestParam("file") MultipartFile file) {
        userProfileService.uploadUserProfileImage(id, file);
    }

    @GetMapping("{userProfileId}/image/download")
    public byte[] downloadUserProfileImage(@PathVariable("userProfileId") UUID id) {
        return userProfileService.downloadUserProfileImage(id);
    }


}
