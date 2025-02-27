package com.springboot.question.service;

import com.springboot.exception.StorageException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
//클라이언트가 업로드한 파일을 지정된 디렉토리에 저장하는 기능을 담당한다.
public class FileSystemStorageService implements StorageService{
    private final Path rootLocation = Paths.get("C:\\backend\\Image");;

    @Override
    public void store(MultipartFile file) {
        try{
//            업로드 된 파일이 비어 있는 경우(파일크기 0) true 반환
            if(file.isEmpty()) {
//                파일이 비어 있으면 저장할 필요가 없으므로 예외 발생, 파일을 저장할 수 없습니다.
                throw new StorageException("Failed to store empty file");
            }
//            this.rootLocation.resolve() => 파일명을 기존 경로에 추가해 전체 경로 생성
            Path destinationFile = this.rootLocation.resolve(
//           클라이언트가 업로드한 원래 파일명을 가져와 보안을 위해 .normlize를 사용해 경로를 정리.("../ 제거")
//           toAbsoulutePath를 통해 절대경로로 변환(파일이 저장될 실제 위치 반환)
                    Paths.get(file.getOriginalFilename())).normalize().toAbsolutePath();
//            destinationFile의 부모 경로를 가져온다. => 업로드 된 파일이 저장될 디렉토리를 확인
//            만댝 파일이 지정된 경로 외에 저장되려 하면 예외를 발생
            if(!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside current directory");
            }
//            파일복사. 업로드된 파일의 내용를 InputStream을 통해 읽어온다.
            try(InputStream inputStream = file.getInputStream()) {
//                Files.copy 메서드는 inputStream으로 읽은 데이터를 destinationFile 경로로 복사
//                해당 경로에 이미 파일이 존재하면 REPLACE 옵션을 통해 기존 파일을 덮어 쓴다.
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch(IOException e) {
            throw new StorageException("Failed to store file", e);
        }
    }
}

// 파일을 실제 저장할 떄는 InputStream을 통해 파일 내용을 읽어와 지정된 경로에 저장하며, 기존 파일이 있다면 덮어쓴다.
