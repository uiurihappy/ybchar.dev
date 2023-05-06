package com.ybcharlog.api.service.AWS;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Service
public class S3UploaderService {

	private AmazonS3Client amazonS3Client;

	@Value("${aws.s3.bucketName}")
	private String bucket;

	// MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
	@Transactional
	public String upload(MultipartFile multipartFile, String dirName) throws IOException {
		File uploadFile = convert(multipartFile)
				.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
		return upload(uploadFile, dirName);
	}

	@Transactional
	public String upload(File uploadFile, String dirName) {
		String fileName = dirName + "/" + uploadFile.getName();
		String uploadImageUrl = putS3(uploadFile, fileName);

		removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)

		return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
	}

	@Transactional
	public String putS3(File uploadFile, String fileName) {
		amazonS3Client.putObject(
				new PutObjectRequest(bucket, fileName, uploadFile)
						.withCannedAcl(CannedAccessControlList.PublicRead)	// PublicRead 권한으로 업로드 됨
		);
		return amazonS3Client.getUrl(bucket, fileName).toString();
	}

	@Transactional
	public void removeNewFile(File targetFile) {
		if(targetFile.delete()) {
			log.info("파일이 삭제되었습니다.");
		}else {
			log.info("파일이 삭제되지 못했습니다.");
		}
	}

	@Transactional
	public Optional<File> convert(MultipartFile file) throws  IOException {
		File convertFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
		if(convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}
		return Optional.empty();
	}
}
