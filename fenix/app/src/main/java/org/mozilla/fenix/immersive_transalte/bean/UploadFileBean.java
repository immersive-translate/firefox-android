/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.bean;

import android.graphics.Bitmap;

import java.io.File;
import java.io.Serializable;

public class UploadFileBean implements Serializable {
    private Bitmap bitmap;
    private File bitmapFile;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public File getBitmapFile() {
        return bitmapFile;
    }

    public static UploadFileBean create(Bitmap bitmap, File bitmapFile) {
        UploadFileBean uploadFileBean = new UploadFileBean();
        uploadFileBean.bitmap = bitmap;
        uploadFileBean.bitmapFile = bitmapFile;
        return uploadFileBean;
    }
}
