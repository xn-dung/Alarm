package com.example.myapplication.Util;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

public final class MusicUriHelper {
    private MusicUriHelper() {
    }

    public static boolean isReadable(Context context, String uriValue) {
        if (uriValue == null || uriValue.trim().isEmpty()) {
            return false;
        }

        try (ParcelFileDescriptor descriptor =
                     context.getContentResolver().openFileDescriptor(Uri.parse(uriValue), "r")) {
            return descriptor != null;
        } catch (Exception ignored) {
            return false;
        }
    }
}
