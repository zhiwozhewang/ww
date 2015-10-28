/**
 * @Title: BookUtils.java
 * @Package com.longyuan.utils
 * @author imhzwen@gmail.com   
 * @date 2014-6-12 下午5:12:39 
 * @version V1.0
 */
package com.loopeer.android.apps.lreader.utilities;


public class DecryptionUtils {

  public static String decryptionEpub(String path) {
    try {
      String destPath = path.substring(0, path.lastIndexOf('.')) + "_decryption.epub";
      FileDES fileDES = new FileDES("DSEPUB86");
      fileDES.DecryptFile(path, destPath);
      return destPath;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}
