package bias.zochiwon_suhodae.homemade_guardian_beta.photo.common;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;

import androidx.loader.content.CursorLoader;

import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

public class PhotoDirectoryLoader extends CursorLoader {

  final String[] IMAGE_PROJECTION = {
      Media._ID,
      Media.DATA,
      Media.BUCKET_ID,
      Media.BUCKET_DISPLAY_NAME,
      Media.DATE_ADDED
  };

  public PhotoDirectoryLoader(Context context, boolean showGif) {
    super(context);
    setProjection(IMAGE_PROJECTION);
    setUri(Media.EXTERNAL_CONTENT_URI);
    setSortOrder(Media.DATE_ADDED + " DESC");
    setSelection(
        MIME_TYPE + "=? or " + MIME_TYPE + "=? " + (showGif ? ("or " + MIME_TYPE + "=?") : ""));
    String[] selectionArgs;
    if (showGif) {
      selectionArgs = new String[] { "image/jpeg", "image/png", "image/gif" };
    } else {
      selectionArgs = new String[] { "image/jpeg", "image/png" };
    }
    setSelectionArgs(selectionArgs);
  }

  private PhotoDirectoryLoader(Context context, Uri uri, String[] projection, String selection,
      String[] selectionArgs, String sortOrder) {
    super(context, uri, projection, selection, selectionArgs, sortOrder);
  }
}
