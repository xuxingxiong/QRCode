package com.xuxx.zxing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 生成二维码
 * 
 * @author xuxx
 *
 */
public class CreateQRCode {
	private static final int BLACK = 0xFF000000;// 用于设置图案的颜色
	private static final int WHITE = 0xFFFFFFFF; // 用于背景色

	/**
	 * 创建二维码
	 * 
	 * @param content
	 *            二维码内容
	 * @param format
	 *            生成二维码的格式
	 * @param logUri
	 *            二维码中间logo的地址
	 * @param stream
	 *            二维码输出流
	 * @param size
	 *            用于设定图片大小（可变参数，宽，高）
	 * @throws IOException
	 *             抛出io异常
	 * @throws WriterException
	 *             抛出书写异常
	 */
	public static void CreatQrImage(String content, String format, String logUri, OutputStream stream, int... size)
			throws IOException, WriterException {
		int width = 430; // 二维码图片宽度 430
		int height = 430; // 二维码图片高度430

		// 如果存储大小的不为空，那么对图片的大小进行设定
		if (size.length == 2) {
			width = size[0];
			height = size[1];
		} else if (size.length == 1) {
			width = height = size[0];
		}

		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		// 指定纠错等级,纠错级别（L 7%、M 15%、Q 25%、H 30%）
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		// 内容所使用字符集编码
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		// 设置二维码边的空度，非负数
		hints.put(EncodeHintType.MARGIN, 2);

		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, // 要编码的内容
				BarcodeFormat.QR_CODE, // 编码类型
				width, // 条形码的宽度
				height, // 条形码的高度
				hints);// 生成条形码时的一些配置,此项可选
		if (logUri != null && logUri != "") {
			writeToStream(bitMatrix, format, stream, logUri);
		} else {
			MatrixToImageWriter.writeToStream(bitMatrix, format, stream);
		}
	}

	private static BufferedImage toBufferedImage(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, (matrix.get(x, y) ? BLACK : WHITE));
				// image.setRGB(x, y, (matrix.get(x, y) ? Color.YELLOW.getRGB()
				// : Color.CYAN.getRGB()));
			}
		}
		return image;
	}

	private static void writeToStream(BitMatrix matrix, String format, OutputStream stream, String logUri)
			throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		// 设置logo图标
		QRCodeFactory logoConfig = new QRCodeFactory();
		image = logoConfig.setMatrixLogo(image, logUri);
		if (!ImageIO.write(image, format, stream)) {
			throw new IOException("Could not write an image of format " + format);
		}
	}

	public static void main(String[] args) {
		String content = "..を愛しています";
		String logUri = "";
		String outFileUri = "D:/myLove.jpg";
		int[] size = new int[] { 430, 430 };
		String format = "jpg";

		try (OutputStream stream = new FileOutputStream(new File(outFileUri))) {
			CreateQRCode.CreatQrImage(content, format, logUri, stream, size);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

}
