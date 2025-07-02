package com.example.ragdemo.repository;

import com.example.ragdemo.entity.DocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentChunkRepository extends JpaRepository<DocumentChunk, Long> {

        // Projection interface for chunk with similarity
        public interface DocumentChunkWithSimilarity {
                String getContent();

                Double getSimilarity();

                String getFileName();
        }

        @Query(value = """
                        SELECT dc.*,
                               (dc.embedding <=> CAST(:embedding AS vector)) as similarity,
                               d.file_name as fileName
                        FROM document_chunks dc
                        JOIN documents d ON dc.document_id = d.id
                        WHERE (dc.embedding <=> CAST(:embedding AS vector)) < :threshold
                        ORDER BY dc.embedding <=> CAST(:embedding AS vector)
                        LIMIT :limit
                        """, nativeQuery = true)
        List<DocumentChunkWithSimilarity> findSimilarChunks(
                        @Param("embedding") String embedding,
                        @Param("threshold") Double threshold,
                        @Param("limit") Integer limit);

        // Delete all chunks by document ID
        @Modifying
        @Query("DELETE FROM DocumentChunk dc WHERE dc.document.id = :documentId")
        void deleteByDocumentId(@Param("documentId") Long documentId);
}