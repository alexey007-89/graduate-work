package ru.skypro.homework.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ads_images")
public class Image implements Serializable {
    @Id
    @Column(name = "id", unique = true)
    private Long id;
    private Integer fileSize;
    private String filePath;
    private String mediaType;
    @Lob
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "id_ads")
    private Ads ads;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return Objects.equals(id, image.id) && Objects.equals(fileSize, image.fileSize) && Objects.equals(filePath, image.filePath) && Objects.equals(mediaType, image.mediaType) && Arrays.equals(data, image.data) && Objects.equals(ads, image.ads);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, fileSize, filePath, mediaType, ads);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}