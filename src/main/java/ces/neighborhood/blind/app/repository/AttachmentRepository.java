package ces.neighborhood.blind.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ces.neighborhood.blind.app.entity.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    Attachment findByStoredFileName(String storedFileName);

}
