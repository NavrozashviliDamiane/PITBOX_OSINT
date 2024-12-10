package com.bitbox.osint.service.mapper

import com.bitbox.osint.domain.ScanResult
import com.bitbox.osint.repository.ScanResultRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ScanResultService(
    private val scanResultRepository: ScanResultRepository
) {
    fun getAllScanResults(): List<ScanResult> {
        return scanResultRepository.findAll()
    }

    fun getScanResultById(id: UUID): ScanResult? {
        return scanResultRepository.findByScanId(id)
    }
}
