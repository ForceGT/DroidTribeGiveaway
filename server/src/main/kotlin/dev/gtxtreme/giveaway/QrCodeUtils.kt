package dev.gtxtreme.giveaway

import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object QrCodeUtils {

    fun generateQrCode(data: String): ByteArray {
        return try {
            val qrCodeWriter = QRCodeWriter()
            val bitMatrix: BitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200)

            // Convert to PNG format
            val outputStream = ByteArrayOutputStream()
            val bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix)
            ImageIO.write(bufferedImage, "PNG", outputStream)
            outputStream.toByteArray()
        } catch (e: WriterException) {
            e.printStackTrace()
            ByteArray(0) // Return an empty byte array in case of an error
        }
    }
}