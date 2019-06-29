package rs.highlande.app.tatatu.util.helper

import android.content.Context
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import rs.highlande.app.tatatu.data.model.DataList
import rs.highlande.app.tatatu.util.Constant
import java.net.URLConnection

/**
 * Created by Abhin.
 */

open class ImageVideoLoadHelper(val context: Context) {
    var mArrayList = ArrayList<DataList>()
    var mArrayFolderList = ArrayList<String>()

    //get the Image and Video from gallery
    fun getAllImagesAndVideos(mLoadComplete: DataLoadComplete): ImageVideoLoadHelper {
        mArrayList.clear()
        mArrayFolderList.clear()
        var absolutePathOfImage: String? = null
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.WIDTH,
            MediaStore.MediaColumns.HEIGHT,
            MediaStore.MediaColumns.DURATION
        )
        val withoutLimit = MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        val selection =
            (MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        val cursor = context.contentResolver.query(
            MediaStore.Files.getContentUri("external"),
            projection,
            selection,
            null,
            withoutLimit
        )
        val columnIndexData = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(columnIndexData)
            var bucket = cursor.getString(cursor.getColumnIndex(projection[3]))
            val fileSize = cursor.getInt(cursor.getColumnIndex(projection[4]))
            val width = cursor.getInt(cursor.getColumnIndex(projection[5]))
            val height = cursor.getInt(cursor.getColumnIndex(projection[6]))
            val duration = cursor.getInt(cursor.getColumnIndex(projection[7]))

            //Set Name The 0 -> Internal Storage
            if (bucket.equals("0", true)) {
                bucket = Constant.mInternalStorage
            }

            if (!mArrayFolderList.contains(bucket)) {
                mArrayFolderList.add(bucket)
            }

            //detect all gif files and add the all file in list
            if (!absolutePathOfImage.substring(absolutePathOfImage.lastIndexOf(".")).equals(".gif", true)) {
                val mData = DataList(bucket, absolutePathOfImage)
                mData.fileSize = fileSize
                mData.imageWidth = width
                mData.imageHeight = height
                mData.videoDuration = duration
                mData.isVideo = isVideoFormat(absolutePathOfImage)
                mData.fileName = cursor.getString(cursor.getColumnIndex(projection[1]))
                if (fileSize > 0)
                    mArrayList.add(mData)
            }
        }
        cursor.close()
        mLoadComplete.getAllData(mArrayList, mArrayFolderList)
        return this
    }

    //Check file type video format
    fun isVideoFormat(imagePath: String): Boolean {
        val extension = getExtension(imagePath)
        val mimeType = if (TextUtils.isEmpty(extension)) URLConnection.guessContentTypeFromName(imagePath)
        else MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        return mimeType != null && mimeType.startsWith("video")

    }

    //Get file Extension
    fun getExtension(path: String): String {
        val extension = MimeTypeMap.getFileExtensionFromUrl(path)
        if (!TextUtils.isEmpty(extension)) {
            return extension
        }
        return if (path.contains(".")) {
            path.substring(path.lastIndexOf(".") + 1, path.length)
        } else {
            ""
        }
    }

    interface DataLoadComplete {
        fun getAllData(mDataArrayList: ArrayList<DataList>, mDataArrayFolderList: ArrayList<String>)
    }
}