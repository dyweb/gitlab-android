package io.dongyue.gitlabandroid.utils;

import android.net.Uri;



/**
 * Utility for doing various image related things
 * Created by Jawn on 9/20/2015.
 */
public class ImageUtil {

    public static Uri getAvatarUrl(String email, int size) {
        return Gravatar
                .init(email)
                .ssl()
                .size(size)
                .defaultImage(Gravatar.DefaultImage.IDENTICON)
                .build();
    }
}
