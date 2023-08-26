package com.example.vkumaps.helpers;

import static android.os.ParcelFileDescriptor.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import com.example.vkumaps.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PDFRendererHelper {
    private static final String PDF_FILE_NAME = "sample.pdf"; // Tên file PDF trong thư mục raw

    public static void renderPDF(Context context, int pageIndex, OnPDFRenderListener listener) {
        try {
            // Copy file PDF từ thư mục raw vào bộ nhớ trong
            File file = copyFileFromRawToInternalStorage(context, PDF_FILE_NAME);

            if (file != null) {
                // Tạo đối tượng ParcelFileDescriptor từ file PDF
                ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);

                // Tạo đối tượng PdfRenderer từ file PDF
                PdfRenderer renderer = new PdfRenderer(fileDescriptor);

                // Lấy số lượng trang trong file PDF
                final int pageCount = renderer.getPageCount();

                // Đảm bảo chỉ hiển thị trang hợp lệ
                if (pageIndex >= 0 && pageIndex < pageCount) {
                    // Tạo một đối tượng PdfRenderer.Page để hiển thị trang cụ thể
                    PdfRenderer.Page page = renderer.openPage(pageIndex);

                    // Tạo một bitmap để chứa nội dung của trang
                    Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);

                    // Vẽ nội dung của trang lên bitmap
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    // Giải phóng trang và renderer
                    page.close();
                    renderer.close();

                    // Gọi callback để xử lý bitmap
                    listener.onPDFRendered(bitmap);
                } else {
                    // Trang không hợp lệ, thông báo lỗi
                    listener.onPDFRenderError("Invalid page index");
                }

                // Đóng file descriptor
                fileDescriptor.close();
            } else {
                // Lỗi khi sao chép file PDF
                listener.onPDFRenderError("Error copying PDF file");
            }
        } catch (IOException e) {
            e.printStackTrace();
            listener.onPDFRenderError("Error rendering PDF");
        }
    }

    private static File copyFileFromRawToInternalStorage(Context context, String fileName) throws IOException {
        InputStream inputStream = context.getResources().openRawResource(R.raw.sodo_vku);
        File file = new File(context.getFilesDir(), fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[4 * 1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        return file;
    }

    public interface OnPDFRenderListener {
        void onPDFRendered(Bitmap bitmap);

        void onPDFRenderError(String errorMessage);
    }
}
