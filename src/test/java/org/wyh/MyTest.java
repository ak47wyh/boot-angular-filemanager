package org.wyh;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

/**
 * Created by Administrator on 2018/3/27.
 */
public class MyTest {

    Path path = null;
    @Before
    public void before(){
        path = Paths.get("E:/test/lib/12.txt");

    }

    @Test
    public void testFileUtil() throws IOException {
        FileSystem fileSystem = path.getFileSystem();
        Set<String> cc = fileSystem.supportedFileAttributeViews();
        System.out.print(cc);
    }

    @Test
    public void test1() {

        UserDefinedFileAttributeView view =
             Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);
        try {
            List<String> metaKeys = view.list();
            for (String name : metaKeys) {
                ByteBuffer buf = ByteBuffer.allocate(view.size(name));
                view.read(name, buf);
                buf.flip();
                String value = Charset.defaultCharset().decode(buf).toString();
                System.out.println(value+"  --");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test2(){

        PosixFileAttributeView fileAttributeView = Files.getFileAttributeView(path, PosixFileAttributeView.class);
        PosixFileAttributes readAttributes = null;
        try {
            readAttributes = fileAttributeView.readAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Set<PosixFilePermission> permissions = readAttributes.permissions();
    }
    @Test
    public void test3(){
        try {
            Files.deleteIfExists(path);
            Files.createFile(path);
            BasicFileAttributeView basicView = Files.getFileAttributeView(path, BasicFileAttributeView.class);
            BasicFileAttributes basicAttributes = basicView.readAttributes();
            boolean isDirectory = basicAttributes.isDirectory();
            FileTime lastModifiedTime = basicAttributes.lastModifiedTime();
            System.out.println(isDirectory);
            System.out.println(lastModifiedTime);
            PosixFileAttributeView posixView = Files.getFileAttributeView(path, PosixFileAttributeView.class);
            PosixFileAttributes posixAttributes = posixView.readAttributes();
            GroupPrincipal group = posixAttributes.group();
            Set<PosixFilePermission> permissions = posixAttributes.permissions();
            permissions.add(PosixFilePermission.OWNER_EXECUTE);
            posixView.setPermissions(permissions);
            System.out.println(group);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testacl(){
        AclFileAttributeView view = Files.getFileAttributeView(path,AclFileAttributeView.class);
        try {
            for(AclEntry e:view.getAcl()){
                System.out.println(e.permissions());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test4(){
        File file = new File("E:/test/lib/dd");
        Set perms = new HashSet();
        if(file.canExecute()){
            perms.add(PosixFilePermission.OWNER_EXECUTE);
        }
        if(file.canRead()){
            perms.add(PosixFilePermission.OWNER_READ);
        }
        if(file.canWrite()){
            perms.add(PosixFilePermission.OWNER_WRITE);
        }

        System.out.println(perms);
        try {
            FileStore fs = Files.getFileStore(path);
            printDetails(fs, AclFileAttributeView.class);
            printDetails(fs, BasicFileAttributeView.class);
            printDetails(fs, DosFileAttributeView.class);
            printDetails(fs, FileOwnerAttributeView.class);
            printDetails(fs, PosixFileAttributeView.class);
            printDetails(fs, UserDefinedFileAttributeView.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public static void printDetails(FileStore fs,
                                    Class<? extends FileAttributeView> attribClass) {
        boolean supported = fs.supportsFileAttributeView(attribClass);
        System.out.format("%s is  supported: %s%n", attribClass.getSimpleName(),
                supported);
    }

    @Test
    public void test5() throws IOException {
        String os = System.getProperty("os.name");
        System.out.println(os);

        AclFileAttributeView aclView = Files.getFileAttributeView(path,
                AclFileAttributeView.class);
        if (aclView == null) {
            System.out.format("ACL view  is not  supported.%n");
            return;
        }
        List<AclEntry> aclEntries = aclView.getAcl();
        for (AclEntry entry : aclEntries) {
            System.out.format("Principal: %s%n", entry.principal());
            System.out.format("Type: %s%n", entry.type());
            System.out.format("Permissions are:%n");

            Set<AclEntryPermission> permissions = entry.permissions();
            for (AclEntryPermission p : permissions) {
                System.out.format("%s %n", p);
            }

        }

    }





}
