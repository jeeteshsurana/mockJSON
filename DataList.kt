package rs.highlande.app.tatatu.data.model

/**
 * Created by Abhin.
 */
data class DataList(var id: String = "", var imagePath: String = "", var isVideo: Boolean = false, var fileName: String = "", var fileSize: Int? = 0, var imageWidth: Int = 0, var imageHeight: Int = 0, var videoDuration: Int = 0, var viewType: Int = 0, var isSelected: Boolean = false)