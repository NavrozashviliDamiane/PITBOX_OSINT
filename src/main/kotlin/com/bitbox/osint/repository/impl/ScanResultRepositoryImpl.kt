package com.bitbox.osint.repository.impl

import com.bitbox.osint.domain.ScanResult
import com.bitbox.osint.repository.ScanResultRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class ScanResultRepositoryImpl(
    private val mongoTemplate: MongoTemplate
) : ScanResultRepository {

    override fun save(scanResult: ScanResult): ScanResult {
        return mongoTemplate.save(scanResult)
    }

    override fun findByScanId(scanId: String): ScanResult? {
        return mongoTemplate.findById(scanId, ScanResult::class.java)
    }

    override fun deleteByScanId(scanId: String) {
        val result = mongoTemplate.findById(scanId, ScanResult::class.java)
        if (result != null) {
            mongoTemplate.remove(result)
        } else {
            throw IllegalArgumentException("ScanResult with ID $scanId does not exist")
        }
    }

    override fun findAll(): List<ScanResult> {
        return mongoTemplate.findAll(ScanResult::class.java)
    }
}
