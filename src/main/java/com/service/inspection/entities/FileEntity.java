package com.service.inspection.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@ToString
@NoArgsConstructor
public class FileEntity {
    @Column(name = "uuid")
    private UUID fileUuid;

    @Column(name = "file_name")
    private String fileName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileEntity that = (FileEntity) o;

        if (!Objects.equals(fileUuid, that.fileUuid)) return false;
        return Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        int result = fileUuid != null ? fileUuid.hashCode() : 0;
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        return result;
    }
}
