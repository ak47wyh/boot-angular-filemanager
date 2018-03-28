package org.wyh.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

/**
 * FileUtils on spring-boot-filemanager
 *
 * @author <a href="mailto:akhuting@hotmail.com">Alex Yang</a>
 * @date 2016年08月25日 10:02
 */
public class FileUtils {

    private static List<String> ExtsDocument = Arrays.asList
            (
                    ".doc", ".docx", ".docm",
                    ".dot", ".dotx", ".dotm",
                    ".odt", ".fodt", ".rtf", ".txt",
                    ".html", ".htm", ".mht",
                    ".pdf", ".djvu", ".fb2", ".epub", ".xps"
            );

    private static List<String> ExtsSpreadsheet = Arrays.asList
            (
                    ".xls", ".xlsx", ".xlsm",
                    ".xlt", ".xltx", ".xltm",
                    ".ods", ".fods", ".csv"
            );

    private static List<String> ExtsPresentation = Arrays.asList
            (
                    ".pps", ".ppsx", ".ppsm",
                    ".ppt", ".pptx", ".pptm",
                    ".pot", ".potx", ".potm",
                    ".odp", ".fodp"
            );

    public static String getExtension(String fileName) {
        if (StringUtils.INDEX_NOT_FOUND == StringUtils.indexOf(fileName, "."))
            return StringUtils.EMPTY;
        String ext = StringUtils.substring(fileName,
                StringUtils.lastIndexOf(fileName, "."));
        return StringUtils.trimToEmpty(ext);
    }

    public static String getFileName(String header) {
        String[] tempArr1 = header.split(";");
        String[] tempArr2 = tempArr1[2].split("=");
        //获取文件名，兼容各种浏览器的写法
        return tempArr2[1].substring(tempArr2[1].lastIndexOf("\\") + 1).replaceAll("\"", "");

    }

    public static String getPermissions(Path path) throws IOException {
        String os = System.getProperty("os.name");
        if(os.startsWith("Win")){
            return getWinPermissions(path);
        }else{
            return getPosixPermissions(path);
        }
    }
    private static String getWinPermissions(Path path)throws IOException{
        AclFileAttributeView view = Files.getFileAttributeView(path,AclFileAttributeView.class);
        return WinFilePermissions.toString(view.getAcl());
    }

    private static String getPosixPermissions(Path path)throws IOException{
        PosixFileAttributeView fileAttributeView = Files.getFileAttributeView(path, PosixFileAttributeView.class);
        PosixFileAttributes readAttributes = fileAttributeView.readAttributes();
        Set<PosixFilePermission> permissions = readAttributes.permissions();
        return PosixFilePermissions.toString(permissions);
    }

    public static String setPermissions(File file, String permsCode, boolean recursive) throws IOException {
        String os = System.getProperty("os.name");
        if(os.startsWith("Win")){
            setWinPermissions(file,permsCode,recursive);
        }else{
            setPosixPermissions(file,permsCode,recursive);
        }
        return permsCode;
    }
    private static void setWinPermissions(File file, String permsCode, boolean recursive) throws IOException {
        AclFileAttributeView view = Files.getFileAttributeView(file.toPath(),AclFileAttributeView.class);
//        格式化权限编码
        AclEntry myEntry = WinFilePermissions.fromString(permsCode);
//        重置权限
        List<AclEntry> aclEntries = view.getAcl();
        aclEntries.add(myEntry);
//        设置权限
        view.setAcl(aclEntries);
        if (file.isDirectory() && recursive && file.listFiles() != null) {
            for (File f : file.listFiles()) {
                setWinPermissions(f, permsCode, true);
            }
        }
    }
    private static void setPosixPermissions(File file, String permsCode, boolean recursive) throws IOException {
        PosixFileAttributeView fileAttributeView = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class);
        fileAttributeView.setPermissions(PosixFilePermissions.fromString(permsCode));
        if (file.isDirectory() && recursive && file.listFiles() != null) {
            for (File f : file.listFiles()) {
                setPosixPermissions(f, permsCode, true);
            }
        }
    }

    public static boolean write(InputStream inputStream, File f) {
        boolean ret = false;

        try (OutputStream outputStream = new FileOutputStream(f)) {

            int read;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            ret = true;

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    public static void mkFolder(String fileName) {
        File f = new File(fileName);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    public static File mkFile(String fileName) {
        File f = new File(fileName);
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    public static void fileProber(File dirFile) {

        File parentFile = dirFile.getParentFile();
        if (!parentFile.exists()) {

            // 递归寻找上级目录
            fileProber(parentFile);

            parentFile.mkdir();
        }

    }

    public static FileType GetFileType(String fileName) {
        String ext = getExtension(fileName).toLowerCase();

        if (ExtsDocument.contains(ext))
            return FileType.Text;

        if (ExtsSpreadsheet.contains(ext))
            return FileType.Spreadsheet;

        if (ExtsPresentation.contains(ext))
            return FileType.Presentation;

        return FileType.Text;
    }
}
