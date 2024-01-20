package com.odde.doughnut.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.odde.doughnut.entities.validators.ValidateNotePicture;
import com.odde.doughnut.models.ImageBuilder;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Pattern;
import java.io.IOException;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.multipart.MultipartFile;

@Embeddable
@ValidateNotePicture
public class NoteAccessories {

  @Getter @Setter private String url;

  @Column(name = "picture_url")
  @Getter
  @Setter
  private String pictureUrl;

  @Pattern(
      regexp = "^(((-?[0-9.]+\\s+){3}-?[0-9.]+\\s+)*((-?[0-9.]+\\s+){3}-?[0-9.]+))?$",
      message = "must be 'x y width height [x y width height...]'")
  @Column(name = "picture_mask")
  @Getter
  @Setter
  private String pictureMask;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "image_id", referencedColumnName = "id")
  @JsonIgnore
  @Getter
  @Setter
  private Image uploadPicture;

  @Column(name = "use_parent_picture")
  @Getter
  @Setter
  private Boolean useParentPicture = false;

  @JsonIgnore @Transient @Getter @Setter private MultipartFile uploadPictureProxy;

  @JsonIgnore
  public Optional<String> getNotePicture() {
    if (uploadPicture != null) {
      return Optional.of("/images/" + uploadPicture.getId() + "/" + uploadPicture.getName());
    }
    if (Strings.isBlank(pictureUrl)) return Optional.empty();
    return Optional.of(pictureUrl);
  }

  public void fetchUploadedPicture(User user) throws IOException {
    MultipartFile file = getUploadPictureProxy();
    if (file != null && !file.isEmpty()) {
      Image image = new ImageBuilder().buildImageFromUploadedPicture(user, file);
      setUploadPicture(image);
    }
  }
}
