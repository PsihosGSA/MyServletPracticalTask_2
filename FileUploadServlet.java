package ua.org.oa.gavrishs.controllers;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;


/**
 * Created by Anna on 03.02.2016.
 */

@WebServlet(name = "FileUploadServlet", urlPatterns = {"/upload"})
@MultipartConfig
public class FileUploadServlet extends javax.servlet.http.HttpServlet {

    private static final String SAVE_DIR = "uploadFiles";
    private static final String TEMPLATE = "/index.jsp";

    static {
        new File(SAVE_DIR).mkdir();
    }

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

        Part filePart = request.getPart("file"); // Где file это name у input <input type="file" name="file">
        String fileName = getFileName(filePart);//получаем имя файла методом getFileName(filePart)
        String appPath = request.getServletContext().getRealPath(""); //получаем путь к корневому каталогу сервера
        String savePath = appPath  + SAVE_DIR + File.separator + fileName; //создаем полный путь с именем для скачаного файла
        new File(appPath + SAVE_DIR).mkdir();
        PrintWriter writer = response.getWriter();

        try (OutputStream out = new FileOutputStream(new File(savePath));
            InputStream fileContent = filePart.getInputStream();) {

            int read = 0;
            final byte[] bytes = new byte[1024];
            while ((read = fileContent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            writer.println("New file " + fileName + " created at " + appPath + SAVE_DIR);

        } catch (FileNotFoundException fne) {
            writer.println("You either did not specify a file to upload or are "
                    + "trying to upload a file to a protected or nonexistent "
                    + "location.");
            writer.println("<br/> ERROR: " + fne.getMessage());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

        getServletContext()
                .getRequestDispatcher(TEMPLATE)
                .forward(request, response);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    private String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        for (String content : part.getHeader("content-disposition").split(";")) {
            System.out.println(content);
            if (content.trim().startsWith("filename")) {

                System.out.println(content.substring(content.indexOf('=') + 1).trim().replace("\"", ""));

                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
