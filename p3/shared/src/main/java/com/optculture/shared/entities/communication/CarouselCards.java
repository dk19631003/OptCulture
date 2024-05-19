package com.optculture.shared.entities.communication;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "carousel_cards")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CarouselCards {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private java.lang.Long id;

	@Column(name = "template_id", length = 50)
	private Long templateId;

	@Column(name = "card_media_type", length = 50)
	private String cardMediaType;//image/video

	@Column(name = "card_media_url", length = 255)
	private String cardMediaUrl;
	
	@Column(name = "body_content",columnDefinition = "MEDIUMTEXT")
	private String bodyContent;

	@Column(name = "buttons", length = 255)
	private String buttons;
	
	@Column(name = "created_date")
	private LocalDateTime createdDate;

}
