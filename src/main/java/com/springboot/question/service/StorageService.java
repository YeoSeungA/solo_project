package com.springboot.question.service;

import org.springframework.web.multipart.MultipartFile;
// 파일 저장을 위한 서비스 인터페이스
public interface StorageService {
//    파일 저장만 하고 반환값은 없기 때문에 void => 구현체가 메서드를 오버라이드 해서 파일을 저장해야 한다.
//    store()은 파일 저장 메서드
    void store(MultipartFile file);
}
