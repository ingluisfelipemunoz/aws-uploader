package com.uploader.aws_uploader.datastore;

import com.uploader.aws_uploader.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FakeUserProfileDataStore {

    public static final List<UserProfile> USER_PROFILES = new ArrayList<>();

    static {
        USER_PROFILES.add(new UserProfile(java.util.UUID.randomUUID(), "pedro", null));
        USER_PROFILES.add(new UserProfile(java.util.UUID.randomUUID(), "jose", null));
    }

    public List<UserProfile> getUserProfiles() {
        return USER_PROFILES;
    }
}
