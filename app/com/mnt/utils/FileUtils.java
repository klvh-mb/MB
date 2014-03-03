package com.mnt.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import play.Play;

public class FileUtils {
  
  public static Boolean isImage(String filename) {
    if(filename != null) {
      String ext = FilenameUtils.getExtension(filename);
      String[] valids = StringUtils.split(Play.application().configuration().getString("storage.images.valid-extensions"), ",");
      for (String valid : valids) {
        if(valid.equalsIgnoreCase(ext)) {
          return true;
        }
      }
    }
    return false;
  }

  public static Boolean isExternal(String filename) {
    return filename.startsWith("http");
  }

}
