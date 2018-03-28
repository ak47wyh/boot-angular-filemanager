package org.wyh.utils;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipal;
import java.util.*;

import static java.nio.file.attribute.AclEntryPermission.*;

public final class WinFilePermissions {
    private WinFilePermissions() { }

    private static void writeBits(StringBuilder sb, boolean r, boolean w, boolean x) {
        if (r) {
            sb.append('r');
        } else {
            sb.append('-');
        }
        if (w) {
            sb.append('w');
        } else {
            sb.append('-');
        }
        if (x) {
            sb.append('x');
        } else {
            sb.append('-');
        }
    }

    public static String toString(List<AclEntry> entries) {
        Set<AclEntryPermission> perms = null;
        StringBuilder sb = new StringBuilder(9);
        boolean or=false,ow=false,oe=false;
        boolean gr=false,gw=false,ge=false;
        boolean otherr=false,otherw=false,othere=false;
        for(AclEntry entry:entries){
            perms = entry.permissions();
            if(entry.principal().getName().startsWith("BUILTIN")){
                if(perms.contains(WRITE_DATA)){
                    or = true;
                }
                if(perms.contains(READ_DATA)){
                    ow = true;
                }
                if(perms.contains(EXECUTE)){
                    oe = true;
                }
            }else if(entry.principal().getName().startsWith("NT")){
                if(perms.contains(WRITE_ACL)){
                    if(perms.contains(WRITE_DATA)){
                        gr = true;
                    }
                    if(perms.contains(READ_DATA)){
                        gw = true;
                    }
                    if(perms.contains(EXECUTE)){
                        ge = true;
                    }
                }
            }else{
//            other
                if(perms.contains(WRITE_DATA)){
                    otherr = true;
                }
                if(perms.contains(READ_DATA)){
                    otherw = true;
                }
                if(perms.contains(EXECUTE)){
                    othere = true;
                }
            }
        }
        writeBits(sb, or, ow,oe);
        writeBits(sb, gr, gw, ge);
        writeBits(sb, otherr, otherw,othere);
        System.out.println(sb+" ==");
        return sb.toString();
    }

    private static boolean isSet(char c, char setValue) {
        if (c == setValue)
            return true;
        if (c == '-')
            return false;
        throw new IllegalArgumentException("Invalid mode");
    }
    private static boolean isR(char c) { return isSet(c, 'r'); }
    private static boolean isW(char c) { return isSet(c, 'w'); }
    private static boolean isX(char c) { return isSet(c, 'x'); }


    public static AclEntry fromString(String perms) {
        if (perms.length() != 9) {
            throw new IllegalArgumentException("Invalid mode");
        }

        AclEntry.Builder builder = AclEntry.newBuilder();
        Set<AclEntryPermission> result = EnumSet.noneOf(AclEntryPermission.class);
        try{
            if (isR(perms.charAt(0))) {
//            result.add(OWNER_READ);
                result.add(WRITE_OWNER);
                result.add(READ_DATA);
                UserPrincipal bRiceUser = FileSystems.getDefault().getUserPrincipalLookupService().lookupPrincipalByName("Administrators");
                builder.setPrincipal(bRiceUser);
            }
            if (isW(perms.charAt(1))){
//            result.add(OWNER_WRITE);
                result.add(WRITE_OWNER);
                result.add(WRITE_DATA);
                UserPrincipal bRiceUser = FileSystems.getDefault().getUserPrincipalLookupService().lookupPrincipalByName("Administrators");
                builder.setPrincipal(bRiceUser);
            }
            if (isX(perms.charAt(2))) {
//            result.add(OWNER_EXECUTE);
                result.add(WRITE_OWNER);
                result.add(EXECUTE);
                UserPrincipal bRiceUser = FileSystems.getDefault().getUserPrincipalLookupService().lookupPrincipalByName("Administrators");
                builder.setPrincipal(bRiceUser);
            }
            if (isR(perms.charAt(3))) {
//            result.add(GROUP_READ);
                result.add(WRITE_OWNER);
                result.add(WRITE_ACL);
                result.add(READ_DATA);
                UserPrincipal bRiceUser = FileSystems.getDefault().getUserPrincipalLookupService().lookupPrincipalByName("SYSTEM");
                builder.setPrincipal(bRiceUser);
            }
            if (isW(perms.charAt(4))) {
//            result.add(GROUP_WRITE);
                result.add(WRITE_OWNER);
                result.add(WRITE_ACL);
                result.add(WRITE_DATA);
                UserPrincipal bRiceUser = FileSystems.getDefault().getUserPrincipalLookupService().lookupPrincipalByName("SYSTEM");
                builder.setPrincipal(bRiceUser);
            }
            if (isX(perms.charAt(5))) {
//            result.add(GROUP_EXECUTE);
                result.add(WRITE_OWNER);
                result.add(WRITE_ACL);
                result.add(EXECUTE);
                UserPrincipal bRiceUser = FileSystems.getDefault().getUserPrincipalLookupService().lookupPrincipalByName("SYSTEM");
                builder.setPrincipal(bRiceUser);
            }
            if (isR(perms.charAt(6))) {
//            result.add(OTHERS_READ);
                result.add(READ_DATA);
            }
            if (isW(perms.charAt(7))) {
//            result.add(OTHERS_WRITE);
                result.add(WRITE_DATA);
            }
            if (isX(perms.charAt(8))) {
//            result.add(OTHERS_EXECUTE);
                result.add(EXECUTE);
            }
            builder.setPermissions(result);
            return builder.build();
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }

}
