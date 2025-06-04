package com.traveljournal.domain.photo.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.traveljournal.domain.kakaoMap.port.GeoAddressLookupPort;
import com.traveljournal.domain.photo.dto.PhotoMetadataResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoMetadataService {

	private final GeoAddressLookupPort geoAddressLookupPort;

	public PhotoMetadataResponse extractMetadata(MultipartFile imageFile) {
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(imageFile.getInputStream());

			Double latitude = extractLatitude(metadata);
			Double longitude = extractLongitude(metadata);
			return PhotoMetadataResponse.of(
				extractDateTime(metadata),
				extractAddress(latitude,longitude),
				latitude,
				longitude
			);

		} catch (ImageProcessingException | IOException e) {
			return PhotoMetadataResponse.empty();
		}
	}

	private String extractDateTime(Metadata metadata) {
		ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
		if (directory != null && directory.containsTag(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL)) {
			try {
				String dateString = directory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
				if (dateString != null) {
					DateTimeFormatter exifFormatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
					LocalDateTime localDateTime = LocalDateTime.parse(dateString, exifFormatter);

					DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
					return localDateTime.format(outputFormatter);
				}


			} catch (Exception e) {
				log.debug("촬영일시 추출 실패(메타데이터 없음 또는 파싱 실패): {}", e.getMessage());
			}
		}
		return null;
	}


	private String extractAddress(Double latitude, Double longitude) {
		if (latitude != null && longitude != null) {
			return geoAddressLookupPort.getAddress(latitude, longitude);
		}
		return null;
	}

	private Double extractLatitude(Metadata metadata) {
		GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
		if (gpsDirectory != null && gpsDirectory.hasTagName(GpsDirectory.TAG_LATITUDE)) {
			try {
				return gpsDirectory.getGeoLocation().getLatitude();
			} catch (Exception e) {
				log.debug("위도 추출 실패: {}", e.getMessage());
			}
		}
		return null;
	}

	private Double extractLongitude(Metadata metadata) {
		GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
		if (gpsDirectory != null && gpsDirectory.hasTagName(GpsDirectory.TAG_LONGITUDE)) {
			try {
				return gpsDirectory.getGeoLocation().getLongitude();
			} catch (Exception e) {
				log.debug("경도 추출 실패: {}", e.getMessage());
			}
		}
		return null;
	}
}