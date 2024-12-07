package com.bitbox.osint.repository.impl

import com.bitbox.osint.domain.Scan
import com.bitbox.osint.repository.ScanRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Repository

@Repository
class ScanRepositoryImpl(
    private val mongoTemplate: MongoTemplate
) : ScanRepository {

    override fun save(scan: Scan): Scan {
        return mongoTemplate.save(scan)
    }

    override fun findById(scanId: String): Scan? {
        return mongoTemplate.findById(scanId, Scan::class.java)
    }

    override fun findAll(): List<Scan> {
        return mongoTemplate.findAll(Scan::class.java)
    }

    override fun delete(scanId: String) {
        val scan = mongoTemplate.findById(scanId, Scan::class.java)
        if (scan != null) {
            mongoTemplate.remove(scan)
        } else {
            throw IllegalArgumentException("Scan with ID $scanId does not exist")
        }
    }
}
