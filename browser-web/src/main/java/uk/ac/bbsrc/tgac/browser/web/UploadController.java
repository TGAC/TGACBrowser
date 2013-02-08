/*
 * Copyright (c) 2012. The Genome Analysis Centre, Norwich, UK
 * MISO project contacts: Robert Davey, Mario Caccamo @ TGAC
 * *********************************************************************
 *
 * This file is part of MISO.
 *
 * MISO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MISO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MISO.  If not, see <http://www.gnu.org/licenses/>.
 *
 * *********************************************************************
 */

package uk.ac.bbsrc.tgac.browser.web;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uk.ac.bbsrc.tgac.browser.core.store.FilesManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/upload")
public class UploadController {
  protected static final Logger log = LoggerFactory.getLogger(UploadController.class);

  @Autowired
    public FilesManager filesManager;



  public void setFilesManager(FilesManager filesManager) {
    this.filesManager = filesManager;
  }

//  public void setFilesManager(DnaSequenceService dnaSequenceService) {
//    this.dnaSequenceService = dnaSequenceService;
//  }

  public void uploadFile(Class type, String qualifier, MultipartFile fileItem) throws IOException {
    log.info("uploadfile 1.1");

    File dir = new File(filesManager.getFileStorageDirectory()+File.separator+type.getSimpleName().toLowerCase()+File.separator+qualifier);
        if (filesManager.checkDirectory(dir, true)) {
          log.info("Attempting to store " + dir.toString() + File.separator + fileItem.getOriginalFilename());
          fileItem.transferTo(new File(dir+File.separator+fileItem.getOriginalFilename().replaceAll("\\s", "_")));
        }
        else {
          throw new IOException("Cannot upload file - check that the directory specified in miso.properties exists and is writable");
        }
    log.info("uploadfile 1.2");

  }

  public void uploadFile(Object type, String qualifier, MultipartFile fileItem) throws IOException {
    log.info("uploadfile 1");

    uploadFile(type.getClass(), qualifier, fileItem);

    log.info("uploadfile 2");

  }

  @RequestMapping(value = "/file", method = RequestMethod.POST)
  public void uploadProjectDocument(MultipartHttpServletRequest request) throws IOException {
      String projectId = "SAM";
       log.info("uploadProjectDocument");
      for (MultipartFile fileItem : getMultipartFiles(request)) {
        log.info("uploadProjectDocument loop "+fileItem);
        uploadFile("sam", projectId, fileItem);
      }
    log.info("uploadProjectDocument loop finished");
    }

  private List<MultipartFile> getMultipartFiles(MultipartHttpServletRequest request) {
     List<MultipartFile> files = new ArrayList<MultipartFile>();
     Map<String, MultipartFile> fMap = request.getFileMap();
     for (String fileName : fMap.keySet()) {
       MultipartFile fileItem = fMap.get(fileName);
       if (fileItem.getSize() > 0) {
         files.add(fileItem);
       }
     }
     return files;
   }
}