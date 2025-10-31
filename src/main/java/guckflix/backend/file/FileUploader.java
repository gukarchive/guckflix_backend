package guckflix.backend.file;

import guckflix.backend.exception.NotFoundException;
import guckflix.backend.exception.RuntimeIOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 파일 업로드 클래스
 */
@Slf4j
public class FileUploader {

    public void upload(MultipartFile file, String directory, String saveFileName) {

        /** Try-with-resources */
        try (FileOutputStream fos = new FileOutputStream(FileConst.IMAGE_DIRECTORY_ROOT + "/" + directory + "/" + saveFileName)){
            fos.write(file.getBytes());
        } catch (IOException e){
             throw new RuntimeIOException("upload error about fileName : " + saveFileName);
        }
    }

    public void delete(String directory, String deleteFileName){
        Path filePath = Paths.get(FileConst.IMAGE_DIRECTORY_ROOT + "/" + directory + "/" + deleteFileName);
        try {
            Files.delete(filePath);

        } catch (NoSuchFileException e){
            log.info("File to delete does not exist. fileName : " + deleteFileName);
        } catch (IOException e) {
            throw new RuntimeIOException(e);
        }
    }
}
